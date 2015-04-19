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

import java.io.InputStream;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.XmlUtils;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERConfiguration extends AbstractConfiguration
{
	private String[] cluster_paths;
	private String dictionary_path;
	private int collect_cutoff;
	private Set<String> collect_labels;
	
//	============================== Initialization ==============================
	
	public NERConfiguration()
	{
		super(NLPMode.ner);
	}
	
	public NERConfiguration(InputStream in)
	{
		super(in, NLPMode.ner);
		initXml();
	}
	
	private void initXml()
	{
		Element eMode = getModeElement();
		
		String dictPath = getPath("dictionary_path");
		String collectLabels = XmlUtils.getTrimmedTextContent(XmlUtils.getFirstElementByTagName(eMode, "collect_labels"));
		int collectCutoff = XmlUtils.getIntegerTextContent(XmlUtils.getFirstElementByTagName(eMode, "collect_cutoff"));
		
		initClusterPaths();
		setDictionaryPath(dictPath);
		setCollectCutoff(collectCutoff);
		setCollectLabelSet(DSUtils.toHashSet(Splitter.splitCommas(collectLabels)));
	}
	
	private void initClusterPaths()
	{
		Element eCluster = getFirstElement("cluster_paths");
		NodeList list = eCluster.getElementsByTagName("cluster_path");
		int i, len = list.getLength();
		cluster_paths = new String[len];
		
		for (i=0; i<len; i++)
			cluster_paths[i] = XmlUtils.getTrimmedTextContent((Element)list.item(i));
	}
	
	public String getDictionaryPath()
	{
		return dictionary_path;
	}
	
	public String[] getClusterPaths()
	{
		return cluster_paths;
	}
	
	public int getCollectCutoff()
	{
		return collect_cutoff;
	}
	
	public Set<String> getCollectLabelSet()
	{
		return collect_labels;
	}
	
	public void setDictionaryPath(String path)
	{
		dictionary_path = path;
	}
	
	public void setClusterPaths(String[] paths)
	{
		cluster_paths = paths;
	}

	public void setCollectCutoff(int dictionaryCutoff)
	{
		collect_cutoff = dictionaryCutoff;
	}
	
	public void setCollectLabelSet(Set<String> set)
	{
		collect_labels = set;
	}
}
