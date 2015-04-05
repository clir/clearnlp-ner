package edu.emory.clir.clearnlp.dbpedia;

import java.io.FileInputStream;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.XmlUtils;

public class OWLReader {
	static public void main(String[] args) throws Exception
	{
		String xmlFile = args[0];
		
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dFactory.newDocumentBuilder();
		Document        doc     = builder.parse(new FileInputStream(xmlFile));
		
		NodeList classes = doc.getElementsByTagName("owl:Class");
		Element owlClass, subClass;
		String rdfAbout, rdfResource;
		NodeList subClasses;
		String o = "ontology";
		PrintStream fout = IOUtils.createBufferedPrintStream(o);
		StringBuilder sb;
		
		
		
		for (int i=0; i<classes.getLength(); i++)
		{	
			sb = new StringBuilder();
			owlClass = (Element)classes.item(i);
			rdfAbout = XmlUtils.getTrimmedAttribute(owlClass, "rdf:about");
			sb.append(rdfAbout);
			subClasses = owlClass.getElementsByTagName("rdfs:subClassOf");
			
			for (int j=0; j<subClasses.getLength(); j++)
			{
				subClass    = (Element)subClasses.item(j);
				rdfResource = XmlUtils.getTrimmedAttribute(subClass, "rdf:resource");
				if (rdfResource.startsWith("http://dbpedia.org"))
				{
					sb.append("\t");
					sb.append(rdfResource);
				}
			}
			sb.append("\n");
			fout.print(sb.toString());
		}
		fout.close();
	}
}
