package edu.emory.clir.clearnlp.lexicon.nytimes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import edu.emory.clir.clearnlp.util.IOUtils;


public class nyTimesUrl {


	static public void main(String[] args) throws Exception{
		//q  = query term 
		//fq = filter query fq=news_desk:("Sports" "Foreign") returns sports desk or foreign
		//fl = limits field in search results web_url snippet lead_paragraph abstract print_page blog source multimedia headline keywords pub_date document_type news_desk byline type_of_material _id word_count
		//begin_date = begin date
		//end_date = end date 
		//sort (by default sort by relevance to query but can be newest | oldest)
		//callback
		//response_format response format (.json | .jsonp)
		//filter  (Sports news desk)
		
		// ADD GENRES
		List<String> filters = new ArrayList<>();
		filters.add("&fq=news_desk:(\"sports\")&sort=oldest&fl=web_url");
		filters.add("&fq=news_desk:(\"Business\")&sort=oldest&fl=web_url");
		filters.add("&fq=section_name:(\"books\")&sort=oldest&fl=web_url");
		filters.add("&q=Review&fq=section_name:(\"Movies\")&sort=oldest&fl=web_url");
		filters.add("&fq=section_name:(\"technology\")&sort=oldest&fl=web_url");
		filters.add("&fq=section_name:(\"world\")&sort=oldest&fl=web_url");
		//Begin date
		String begindate = "&begin_date=2014";
		
		//api-key
		String api_key ="&api-key=e9021729d847504e154cd091f1ac1ecf:6:71762791";
		//Base url
		String base= "http://api.nytimes.com/svc/search/v2/articlesearch.json?";
		
		// Add calender Month,Amount of days
		Map<String,Integer> calender = new HashMap<>();
//		calender.put("01", 31);
//		calender.put("02",28);
//		calender.put("03",31);
//		calender.put("04",5);
		calender.put("08",31);
		calender.put("09",30);
		calender.put("10",31);
		calender.put("11",30);
		calender.put("12",31);


		
		int i = 1, j;
		List<String> urls;
		StringBuilder sb = new StringBuilder();
		StringBuilder tag = new StringBuilder();
		for(j = 0; j<filters.size(); j++){
			PrintStream fout = IOUtils.createBufferedPrintStream(args[j]);
			fout.println("<html>");
			fout.println("<body>");
			for(String month: calender.keySet()){
			i = 1;
			while(i<calender.get(month)){
			sb.setLength(0);
			sb.append(base);
			sb.append(filters.get(j));
			if(Integer.parseInt(month)>4){
			sb.append(begindate);
			}else{
			sb.append("&begin_date=2015");
			}
			sb.append(month);
			if(i<10){
			sb.append("0");
			}
			sb.append(Integer.toString(i));
			sb.append(api_key);
			urls = getUrls(sb.toString(),j);
			for(String s: urls){
				tag.setLength(0);
				tag.append("<a href=\"");
				tag.append(s);
				tag.append("\">a</a>");
				fout.println(tag.toString());
			}
			i++;
			Thread.sleep(1000);
			}
			}
			fout.println("</body>");
			fout.println("</html>");
			fout.flush();
			fout.close();
		}
		

	}

	private static List<String> getUrls(String link, int j) throws IOException {
		List<String> urls = new ArrayList<>();
		try {
			URL url = new URL(link);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();
			JsonParser jp = new JsonParser(); //from gson
			JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); 
			Gson temp = new Gson();
			container re = temp.fromJson(temp.toJson(root.getAsJsonObject()),container.class);
			web_url[] tempArr = re.getResponse().getDocs();
			
			for(web_url u:tempArr){
				if(j==2){
					if(u.getUrl().substring(u.getUrl().lastIndexOf("/")-6, u.getUrl().lastIndexOf("/")).equals("review")){
						urls.add(u.getUrl());
					}
					else{
						continue;
					}
				}
				urls.add(u.getUrl());
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return urls;
	}
}
