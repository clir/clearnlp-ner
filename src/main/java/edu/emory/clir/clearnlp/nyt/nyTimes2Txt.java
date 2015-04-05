package edu.emory.clir.clearnlp.nyt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.emory.clir.clearnlp.util.IOUtils;

public class nyTimes2Txt {


	//Read all files in a folder path 
	//For each file use jsoup to create the article 
	//Write the article to the article folder 
	//<meta name="pdate" content="20150119" />
	//<p class="story-body-text story-content"
	


	static public void main(String[] args) throws Exception{
		BufferedReader configReader = IOUtils.createBufferedReader(args[0]);
		String sourcePath,targetPath,line;
		while((line=configReader.readLine())!=null){
			sourcePath=line.substring(0,line.indexOf("\t")+1);
			targetPath=line.substring(line.indexOf("\t")+1, line.length());
			createTxtFiles(new File(sourcePath.trim()), targetPath.trim());
		}
	}


	private static void createTxtFiles(File sourcePath, String targetPath) throws IOException {
		Document jsoupDoc;
		String pdate,title;
		Elements sentences;
		PrintStream fileWriter;
		StringBuilder sb = new StringBuilder();
		//Create directories if they don't exist
		if (!Files.isDirectory(Paths.get(targetPath))) {
			new File(targetPath).mkdirs();
		}
		if(sourcePath.listFiles()!=null){
		for(File htmlFile:sourcePath.listFiles()){
			//Ignore .ds store files
			if(htmlFile.getName().charAt(0)=='.') continue;
			sb.setLength(0);
			jsoupDoc = Jsoup.parse(htmlFile, "UTF-8", " ");
			pdate =  jsoupDoc.getElementsByAttributeValue("name", "pdate").get(0).attr("content");
			title = jsoupDoc.getElementsByTag("title").get(0).text();
			sb.append(targetPath);
			sb.append("/");
			sb.append(pdate);
			sb.append("_");
			sb.append(title);
			fileWriter = IOUtils.createBufferedPrintStream(sb.toString());
			sentences = jsoupDoc.getElementsByClass("story-body-text");
			for(Element sentence:sentences){
				fileWriter.println(sentence.text());
			}
			fileWriter.flush();
			fileWriter.close();
		}
		}
		

	}


}
