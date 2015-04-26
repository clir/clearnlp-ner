package lexicon.freebase;

import java.io.FileInputStream;
import java.util.Properties;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;



public class query {
  public static void main(String[] args) {
    try {
      HttpTransport httpTransport = new NetHttpTransport();
      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
      JsonParser parser = new JsonParser();
      GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
      url.put("query", "Cee Lo Green");
      url.put("filter", "(all type:/music/artist created:\"The Lady Killer\")");
      url.put("limit", "10");
      url.put("indent", "true");
      url.put("key", "AIzaSyB2kTgReRq2Up9j_zdZF6ejPUWTBBGxNlU");
      HttpRequest request = requestFactory.buildGetRequest(url);
      HttpResponse httpResponse = request.execute();
      JsonObject response = (JsonObject)parser.parse(httpResponse.parseAsString());
      JsonArray results = (JsonArray)response.get("result");
      for (Object result : results) {
        System.out.println(JsonPath.read(result,"$.name").toString());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}