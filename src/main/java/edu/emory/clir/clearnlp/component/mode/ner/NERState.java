/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.clir.clearnlp.component.mode.ner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.collection.tree.PrefixNode;
import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.collection.triple.ObjectIntIntTriple;
import edu.emory.clir.clearnlp.component.state.AbstractTagState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.ner.BILOU;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERState extends AbstractTagState
{
	/** Information from prefix-tree. */
	private List<ObjectIntIntTriple<NERInfoSet>> info_list;
	private PrefixTree<String,NERInfoSet> prefix_tree;
	/** Tags retrieved from the prefix-tree. */
	private String[] ambiguity_classes;
	private List<Set<String>> brown_clusters;
	
//	====================================== INITIALIZATION ======================================
	
	// temporary constructor
	public NERState(DEPTree tree, CFlag flag, PrefixTree<String,NERInfoSet> dictionary)
	{
		super(tree, flag);
		prefix_tree = dictionary;
		info_list = prefix_tree.getAll(d_tree.toNodeArray(), 1, DEPNode::getWordForm, true, false);
		ambiguity_classes = getAmbiguityClasses();
	}
	
	public NERState(DEPTree tree, CFlag flag, NERLexicon lexicon)
	{
		super(tree, flag);
		init(lexicon);
	}
	
	public void init(NERLexicon lexicon)
	{
		prefix_tree = lexicon.getDictionary();
		info_list = prefix_tree.getAll(d_tree.toNodeArray(), 1, DEPNode::getWordForm, true, false);
		ambiguity_classes = getAmbiguityClasses();
		initBrownClusters(lexicon.getBrownCluster());
	}
	
	private void initBrownClusters(PrefixTree<String,String[]> clusters)
	{
		brown_clusters = IntStream.range(0, t_size).mapToObj(i -> new HashSet<String>()).collect(Collectors.toList());
		Set<String> set; int i, j;
		
		for (ObjectIntIntTriple<String[]> t : clusters.getAll(d_tree.toNodeArray(), 1, DEPNode::getWordForm, true, false))
		{
			for (i=t.i1; i<=t.i2; i++)
			{
				set = brown_clusters.get(i);
				for (j=t.o.length-1; j>=0; j--) set.add(t.o[j]);
			}
		}
	}
	
	private String[] getAmbiguityClasses()
	{
		StringJoiner[] joiners = new StringJoiner[t_size];
		ObjectIntIntTriple<NERInfoSet> t;
		int i, j, size = info_list.size();
		String tag;
		
		for (i=1; i<t_size; i++)
			joiners[i] = new StringJoiner("-");
		
		for (i=0; i<size; i++)
		{
			t = info_list.get(i);
			tag = t.o.joinTags(StringConst.COLON);
			
			if (t.i1 == t.i2)
				joiners[t.i1].add(NERTag.toBILOUTag(BILOU.U, tag));
			else
			{
				joiners[t.i1].add(NERTag.toBILOUTag(BILOU.B, tag));
				joiners[t.i2].add(NERTag.toBILOUTag(BILOU.L, tag));
				
				for (j=t.i1+1; j<t.i2; j++)
					joiners[j].add(NERTag.toBILOUTag(BILOU.I, tag));
			}
		}
		
		String[] classes = new String[t_size];
		for (i=1; i<t_size; i++) classes[i] = joiners[i].toString();
		return classes;
	}
	
//	====================================== ORACLE/LABEL ======================================
	
	@Override
	protected String clearOracle(DEPNode node)
	{
		return node.clearNamedEntityTag();
	}

//	====================================== TRANSITION ======================================
	
	protected void setLabel(DEPNode node, String label)
	{
		node.setNamedEntityTag(label);
	}
	
//	====================================== FEATURES ======================================
	
	@Override
	public String getAmbiguityClass(DEPNode node)
	{
		return ambiguity_classes[node.getID()];
	}
	
	public String[] getBrownClusters(DEPNode node)
	{
		Set<String> set = brown_clusters.get(node.getID());
		String[] t = new String[set.size()];
		set.toArray(t);
		return t;
	}
	
//	====================================== DICTIONARY ======================================

	/** For training. */
	public void adjustDictionary()
	{
		IntObjectHashMap<String> goldMap = NEREval.collectNamedEntityMap(g_oracle, String::toString);
		populateDictionary(goldMap);
	}
	
	private IntObjectHashMap<ObjectIntIntTriple<NERInfoSet>> populateDictionary(IntObjectHashMap<String> goldMap)
	{
		IntObjectHashMap<ObjectIntIntTriple<NERInfoSet>> dictMap = toNERInfoMap();
		NERInfoSet list;
		int bIdx, eIdx;
		
		// add gold entries to the dictionary 
		for (ObjectIntPair<String> p : goldMap)
		{
			dictMap.remove(p.i);
			bIdx = p.i / t_size;
			eIdx = p.i % t_size;
			list = pick(prefix_tree, p.o, d_tree.toNodeArray(), bIdx, eIdx+1, DEPNode::getWordForm, 1);
			list.addCorrectCount(1);
		}
		
		for (ObjectIntPair<ObjectIntIntTriple<NERInfoSet>> p : dictMap)
			p.o.o.addCorrectCount(-1);
		
		return dictMap;
	}
	
	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	static public <T>NERInfoSet pick(PrefixTree<String,NERInfoSet> dictionary, String tag, T[] array, int beginIndex, int endIndex, Function<T,String> f, int inc)
	{
		PrefixNode<String,NERInfoSet> node = dictionary.add(array, beginIndex, endIndex, f);
		NERInfoSet set = node.getValue();
		
		if (set == null)
		{
			set = new NERInfoSet();
			node.setValue(set);
		}
		
		set.addCategory(tag);
		return set;
	}
	
	private IntObjectHashMap<ObjectIntIntTriple<NERInfoSet>> toNERInfoMap()
	{
		IntObjectHashMap<ObjectIntIntTriple<NERInfoSet>> map = new IntObjectHashMap<>();
		
		for (ObjectIntIntTriple<NERInfoSet> t : info_list)
			map.put(NEREval.getKey(t.i1, t.i2, t_size), t);

		return map;
	}
}