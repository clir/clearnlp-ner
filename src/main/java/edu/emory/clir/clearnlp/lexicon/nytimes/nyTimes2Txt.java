package edu.emory.clir.clearnlp.lexicon.nytimes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.CharConst;
import edu.emory.clir.clearnlp.util.constant.StringConst;

public class nyTimes2Txt implements nyTimesHTML {



	public static void createTxtFiles(File sourcePath, String targetPath) throws IOException {
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
			//Ignore .ds store file
			if(htmlFile.getName().charAt(0)==CharConst.PERIOD) continue;
			sb.setLength(0);
			jsoupDoc = Jsoup.parse(htmlFile, UTF, " ");
			pdate =  jsoupDoc.getElementsByAttributeValue(NAME,PDATE).get(0).attr(CONTENT);
			title = jsoupDoc.getElementsByTag(TITLE).get(0).text();
			sb.append(targetPath);
			sb.append(StringConst.BW_SLASH);
			sb.append(pdate);
			sb.append(StringConst.UNDERSCORE);
			sb.append(title);
			fileWriter = IOUtils.createBufferedPrintStream(sb.toString());
			sentences = jsoupDoc.getElementsByClass(BODY);
			for(Element sentence:sentences){
				fileWriter.println(sentence.text());
			}
			fileWriter.flush();
			fileWriter.close();
		}
		}
		

	}
	//Reads config file where each line is SourcePath \t TargetPath
	//These paths are where the html files to where you want to store the txt files


	static public void main(String[] args) throws Exception{
		BufferedReader configReader = IOUtils.createBufferedReader(args[0]);
		String sourcePath,targetPath,line;
		while((line=configReader.readLine())!=null){
			sourcePath=line.substring(0,line.indexOf(StringConst.TAB)+1);
			targetPath=line.substring(line.indexOf(StringConst.TAB)+1, line.length());
			createTxtFiles(new File(sourcePath.trim()), targetPath.trim());
		}
	}



}
