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

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.collection.tree.PrefixNode;
import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.collection.triple.ObjectIntIntTriple;
import edu.emory.clir.clearnlp.component.state.AbstractTagState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPFeat;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.ner.BILOU;
import edu.emory.clir.clearnlp.ner.NERInfoSet;
import edu.emory.clir.clearnlp.ner.NERTag;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERState extends AbstractTagState
{
	/** Information from prefix-tree. */
	private List<ObjectIntIntTriple<NERInfoSet>> info_list;
	private PrefixTree<String,NERInfoSet> ner_dictionary;
	private String[] ambiguity_classes;
	
//	====================================== INITIALIZATION ======================================
	
	public NERState(DEPTree tree, CFlag flag, NERLexicon lexicon)
	{
		super(tree, flag);
		init(lexicon);
	}
	
	public void init(NERLexicon lexicon)
	{
		ner_dictionary = lexicon.getDictionary();
		info_list = ner_dictionary.getAll(d_tree.toNodeArray(), 1, DEPNode::getWordForm, true, false);
		ambiguity_classes = getAmbiguityClasses();
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
		for (i=1; i<t_size; i++) classes[i] = joiners.length == 0 ? null : joiners[i].toString();
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
	
//	public void setGlobalFeatures(DEPNode node)
//	{
//		if (!node.getNamedEntityTag().equals("O")) {
//			DEPFeat feats = new DEPFeat();	
//			feats.put("g-1:f3", node.getLeftNearestSibling().getSimplifiedWordForm());
//			feats.put("g-1:f4", node.getLeftNearestSibling().getWordShape(2));
//			feats.put("g-1:d", node.getLeftNearestSibling().getLabel());
//			feats.put("g-1:n", node.getLeftNearestSibling().getNamedEntityTag());
//			
//			feats.put("g-2:f3", node.getLeftNearestSibling(1).getSimplifiedWordForm());
//			feats.put("g-2:f4", node.getLeftNearestSibling(1).getWordShape(2));
//			feats.put("g-2:d", node.getLeftNearestSibling(1).getLabel());
//			feats.put("g-2:n", node.getLeftNearestSibling(1).getNamedEntityTag());
//			
//			feats.put("g+1:f3", node.getRightNearestSibling().getSimplifiedWordForm());
//			feats.put("g+1:f4", node.getRightNearestSibling().getWordShape(2));
//			feats.put("g+1:d", node.getRightNearestSibling().getLabel());
//			feats.put("g+1:n", node.getRightNearestSibling().getNamedEntityTag());
//			
//			feats.put("g+2:f3", node.getRightNearestSibling(1).getSimplifiedWordForm());
//			feats.put("g+2:f4", node.getRightNearestSibling(1).getWordShape(2));
//			feats.put("g+2:d", node.getRightNearestSibling(1).getLabel());
//			feats.put("g+2:n", node.getRightNearestSibling(1).getNamedEntityTag());
//			
//			node.setFeats(feats);
//		}
//	}

	
//	public String[] getCooccuranceFeatures(DEPNode node)
//	{
//		String[] categories = {"PER", "LOC", "ORG", "MISC"};
//		int[] cooccurrences = new int[categories.length];
//		int i;
//		List<DEPNode> prevWords = node.getSubNodeList();
//		for (DEPNode prevWord : prevWords) {
//			for (i=0;i<categories.length;i++) {
//				if (prevWord.getNamedEntityTag().equals(categories[i])) {
//					cooccurrences[i]++;
//				}
//			}
//		}
//		StringJoiner[] joiner= new StringJoiner[categories.length];
//		String[] features = new String[categories.length];
//		for (i=0;i<categories.length;i++) {
//			joiner[i]= new StringJoiner("-");
//			joiner[i].add(categories[i])
//				.add(Double.toString(cooccurrences[i]/Math.log(prevWords.size())));
//			features[i] = joiner[i].toString();
//		}
//		return features;
//	}
	
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
			list = pick(ner_dictionary, p.o, d_tree.toNodeArray(), bIdx, eIdx+1, DEPNode::getWordForm, 1);
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

	public void getGlobalFeatures(DEPNode node) {
		// TODO Auto-generated method stub
		
	}
}
