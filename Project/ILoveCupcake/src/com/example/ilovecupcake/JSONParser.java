package com.example.ilovecupcake;
import java.io.*;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
 // si occupa di 
public class JSONParser implements Runnable {
	private  static InputStream is = null;
    private volatile static JSONObject jObj = null;
    private static String json = "";
    private  String url="", method; 
    private List<NameValuePair>params;
    public JSONObject getObj(){return jObj;}// ritorna l'oggetto
    // con il costruttoe inizializzo le variabili
    JSONParser( String url, String method, List<NameValuePair>params)  {
    	this.url=url; this.method=method; this.params=params; 
    	}
    
    public void makeHttpRequest(){	
        try {
        	if(method == "POST"){
        		HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }else if(method == "GET"){
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }           
         } catch (UnsupportedEncodingException e) {
        	e.printStackTrace(); 
        	} catch (ClientProtocolException e) {
        		e.printStackTrace();
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(  is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) 
                sb.append(line + "\n");
            
            is.close();
            json = sb.toString();
            
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        
        try {
            jObj = new JSONObject(json);// try parse the string to a JSON object
        } catch (JSONException e) {
            Log.v("JSON Parser", "Error parsing data " + e.toString());
        }
     }

    // quando creo un thread il metodo run richiamerà makeHttpRequest
  	public void run(){
   		makeHttpRequest();
   	}
          
}