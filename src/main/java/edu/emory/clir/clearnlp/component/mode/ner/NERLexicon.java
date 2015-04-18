/**
 * Copyright 2014, Emory University
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

import java.io.ObjectInputStream;
import java.io.Serializable;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.ngram.Bigram;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.constant.StringConst;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERLexicon implements Serializable
{
	private static final long serialVersionUID = 3816259878124239839L;
	private PrefixTree<String,NERInfoSet> ne_dictionary;
	private PrefixTree<String,String[]> ne_cluster;
	private Bigram<String,String> dict_counts;
	private int dictionary_cutoff;
	
	public NERLexicon(NERConfiguration configuration)
	{
		setDictionaryCutoff(configuration.getDictionaryCutoff());
		dict_counts = new Bigram<>();
		
		if (configuration.getDictionaryPath() != null) setDictionary(getNEDictionary(configuration.getLanguage(), configuration.getDictionaryPath()));
		else setDictionary(new PrefixTree<>());
		
		if (configuration.getBrownClusterPath() != null) setBrownCluster(getBrownClusters(configuration.getBrownClusterPath()));
		else setBrownCluster(new PrefixTree<>());
	}
	
	public void collect(DEPTree tree)
	{
		DEPNode[] nodes = tree.toNodeArray();
		IntObjectHashMap<String> map = NEREval.collectNamedEntityMap(nodes, DEPNode::getNamedEntityTag);
		int bIdx, eIdx, size = tree.size();
		
		for (ObjectIntPair<String> p : map)
		{
			bIdx = p.i / size;
			eIdx = p.i % size;
			if (p.o.equals("MISC"))
				dict_counts.add(p.o, Joiner.join(nodes, StringConst.SPACE, bIdx, eIdx+1, DEPNode::getWordForm));
		}
	}
	
	public void populateDictionary()
	{
		NERInfoSet set;
		String[]   array;
		
		for (String type : dict_counts.getBigramSet())
		{
			for (ObjectIntPair<String> p : dict_counts.toList(type, dictionary_cutoff))
			{
				array = Splitter.splitSpace(p.o);
				set = NERState.pick(ne_dictionary, type, array, 0, array.length, String::toString, p.i);
				set.addCorrectCount(p.i);
			}
		}
		
		dict_counts = null;
	}
	
	public PrefixTree<String,NERInfoSet> getDictionary()
	{
		return ne_dictionary;
	}
	
	public void setDictionary(PrefixTree<String,NERInfoSet> dictionary)
	{
		ne_dictionary = dictionary;
	}
	
	public PrefixTree<String,String[]> getBrownCluster()
	{
		return ne_cluster;
	}
	
	public void setBrownCluster(PrefixTree<String,String[]> cluster)
	{
		ne_cluster = cluster;
	}
	
	public int getDictionaryCutoff()
	{
		return dictionary_cutoff;
	}
	
	public void setDictionaryCutoff(int cutoff)
	{
		dictionary_cutoff = cutoff;
	}
	
	@SuppressWarnings("unchecked")
	static public PrefixTree<String,NERInfoSet> getNEDictionary(TLanguage language, ObjectInputStream in)
	{
		BinUtils.LOG.info("Loading named entity dictionary.\n");
		PrefixTree<String,NERInfoSet> tree = null;
		
		try
		{
			tree = (PrefixTree<String,NERInfoSet>)in.readObject();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return tree;
	}
	
	static public PrefixTree<String,NERInfoSet> getNEDictionary(TLanguage language, String modelPath)
	{
		return getNEDictionary(language, NLPUtils.getObjectInputStream(modelPath));
	}
	
	@SuppressWarnings("unchecked")
	static public PrefixTree<String,String[]> getBrownClusters(ObjectInputStream in)
	{
		BinUtils.LOG.info("Loading brown clusters.\n");
		PrefixTree<String,String[]> tree = null;
		
		try
		{
			tree = (PrefixTree<String,String[]>)in.readObject();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return tree;
	}
	
	static public PrefixTree<String,String[]> getBrownClusters(String modelPath)
	{
		return getBrownClusters(NLPUtils.getObjectInputStream(modelPath));
	}
}
