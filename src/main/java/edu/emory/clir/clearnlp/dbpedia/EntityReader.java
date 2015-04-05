package edu.emory.clir.clearnlp.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;

import edu.emory.clir.clearnlp.util.IOUtils;

public class EntityReader {



	static public void main(String[] args) throws Exception{
		String o = args[0];
		String instanceFile = args[1];
		String redirectFile = args[2];
		//Read ontology File
		DBGraph dbGraph = new DBGraph();
		createDBGraph(dbGraph,o);
		EntityGraph eGraph = new EntityGraph();


		//Read Instance Type File
		BufferedReader Breader = new BufferedReader(new FileReader(new File(instanceFile)));
		String line, title, instance, alias;
		Entity currEntity;
		int count = 0,beginIndex,endIndex;
		while((line=Breader.readLine())!=null){	
			beginIndex = (line.lastIndexOf("/", line.indexOf(">")));
			endIndex = line.indexOf(">");
			if(beginIndex !=-1 && endIndex !=-1){
				title = line.substring(beginIndex+1, endIndex);
				beginIndex = line.lastIndexOf("<");
				endIndex = line.lastIndexOf("/");
				if(line.substring(beginIndex, endIndex+1).equals("<http://dbpedia.org/ontology/")){
					instance = (line.substring(beginIndex, line.lastIndexOf(">")));
					currEntity = new Entity(title);
					currEntity.addInstance(instance);
					eGraph.addEntity(currEntity);
				}
			}
		}

		Breader.close();


		//Read Redirect Links
		Breader = new BufferedReader(new FileReader(new File(redirectFile)));
		while((line=Breader.readLine())!=null){	
			beginIndex = (line.lastIndexOf("/", line.indexOf(">")));
			endIndex = line.indexOf(">");
			if(beginIndex !=-1 && endIndex !=-1){
				alias = line.substring(beginIndex+1, endIndex);
				beginIndex = line.lastIndexOf("<");
				endIndex = line.lastIndexOf("/");
				if(line.substring(beginIndex, endIndex+1).equals("<http://dbpedia.org/resource/")){
					title = (line.substring(beginIndex, line.lastIndexOf(">")));
					count= eGraph.addAlias(title, alias);
					if(count==-1){
						System.out.println("No title " + title + " for this alias" + alias);
					}
				}
			}
		}	
		Breader.close();
		
		
		//Write to file format Entity Title \t RedirectLink1 \n RedirectLink2 \t Ontology1 \n Ontology2 
		o = "fout.txt";
		PrintStream fout = IOUtils.createBufferedPrintStream(o);
		StringBuilder sb;
		Map<String,Entity> entity_map = eGraph.getEntity_map();
		for(Entry<String,Entity> entry : entity_map.entrySet()){
			sb = new StringBuilder();
			sb.append(entry.getKey());
			sb.append("\t");
			for(String aliases:entry.getValue().getAliases()){
				sb.append(aliases);
				sb.append("\n");
			}
			sb.append("\t");
			for(String instances:entry.getValue().getAllInstance()){
				sb.append(instances);
				sb.append("\n");
			}
			fout.print(sb.toString());
		}
		fout.close();

		
		


	}

	//53 unique ontology classes
	private static void createDBGraph(DBGraph dbGraph, String ontologyFile) throws Exception {
		BufferedReader ontologyReader = new BufferedReader(new FileReader(new File(ontologyFile)));
		String line;
		DBNode currNode;
		DBNode supNode;
		while((line=ontologyReader.readLine())!=null){
			if(line.indexOf("\t")!=-1){
				currNode = new DBNode(line.substring(0, line.indexOf("\t")+1));
				supNode = new DBNode(line.substring(line.indexOf("\t")+1, line.length()));
				currNode.setSuperClass(supNode);
				dbGraph.addNode(currNode);
				dbGraph.addNode(supNode);		
			}
			else{
				currNode = new DBNode(line);
				dbGraph.addNode(currNode);
			}
		}
		ontologyReader.close();
	}



}
