package edu.emory.clir.clearnlp.ner;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 

public class changeConfigSettings {
	
	public static void main(String[] args) throws Exception{
		String configFilePath = args[0];
		String labelCutOff = args[1];
		String featureCutOff = args[2];
		String alpha = args[3];
		String rho = args[4];
		String bias = args[5];
		String average = args[6];
		String dictionaryPath = args[7];
		String dictionaryCutOff = args[8];
		
		try{
			String filepath = configFilePath;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			Node config = doc.getFirstChild();
			Node ner = doc.getElementsByTagName("ner").item(0);
			
			
			NodeList list = ner.getChildNodes();
			Node currNode;
			NamedNodeMap nodeMap;
			Node attribute;
			for(int i = 0; i<list.getLength(); i++){
				currNode = list.item(i);
				if("trainer".equals(currNode.getNodeName())){
					nodeMap = currNode.getAttributes();
					attribute = nodeMap.getNamedItem("labelCutoff");
					attribute.setTextContent(labelCutOff);
					attribute = nodeMap.getNamedItem("featureCutoff");
					attribute.setTextContent(featureCutOff);
					attribute = nodeMap.getNamedItem("alpha");
					attribute.setTextContent(alpha);
					attribute = nodeMap.getNamedItem("rho");
					attribute.setTextContent(rho);
					attribute = nodeMap.getNamedItem("bias");
					attribute.setTextContent(bias);
					attribute = nodeMap.getNamedItem("average");
					attribute.setTextContent(average);
				}
				if("dictionary_path".equals(currNode.getNodeName())){
					currNode.setTextContent(dictionaryPath);
				}
				if("dictionary_cutoff".equals(currNode.getNodeName())){
					currNode.setTextContent(dictionaryCutOff);
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);
	 
			
		}catch(IOException ioe){
			
		}
	}

}
