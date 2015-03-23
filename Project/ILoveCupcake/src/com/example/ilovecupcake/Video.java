package com.example.ilovecupcake;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import javax.xml.parsers.*;
import org.w3c.dom.*;

// andrà a visualizzare la videoricetta. essendo gli url riferiti a youtube dovrò calcolare l url
public class Video extends Activity {
	String urlYt,videoUrl;// urlYt passato dall'activity precedente,videoUrl è quello calcolato
	VideoView vVideo;
	private MediaController controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		urlYt=getIntent().getExtras().getString("video");// recupero all'activity precedente l'url del video
		vVideo= (VideoView)findViewById(R.id.video);
	    controller = new MediaController(this);
	    vVideo.setMediaController(controller);
	    new YourAsyncTask().execute();
		}
	
	private class YourAsyncTask extends AsyncTask<Void, Void, Void>{
         @Override
         protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected Void doInBackground(Void... params){
        	try {
                videoUrl=getRTSPVideoUrl(urlYt);// richiama il metodo che dovrà calcolare l'url
                 }
            catch (Exception e) {
             }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        	super.onPostExecute(result);
            vVideo.setVideoURI(Uri.parse(videoUrl));// mi permette di visualizzare il video
            vVideo.requestFocus();
            vVideo.start();          
           //    controller.show();
        }
    }

	// il metodo che mi permette di ottenere l'url partendo all'url di youtube
   	public String getRTSPVideoUrl(String urlYoutube) {
   		try {
            String gdy = "http://gdata.youtube.com/feeds/api/videos/";
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String id = extractYoutubeId(urlYoutube);
            URL url = new URL(gdy + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Document doc = dBuilder.parse(connection.getInputStream());
            Element el = doc.getDocumentElement();
            NodeList list = el.getElementsByTagName("media:content");
            String cursor = urlYoutube;
            for (int i = 0; i < list.getLength(); i++) {
            	Node node = list.item(i);
            	if (node != null) {
            		NamedNodeMap nodeMap = node.getAttributes();
            		HashMap<String, String> maps = new HashMap<String, String>();
            		for (int j = 0; j < nodeMap.getLength(); j++) {
            			Attr att = (Attr) nodeMap.item(j);
            			maps.put(att.getName(), att.getValue());
            		}
            		if (maps.containsKey("yt:format")) {
            			String f = maps.get("yt:format");
            			if (maps.containsKey("url"))
            				cursor = maps.get("url");
            			if (f.equals("1"))
            				return cursor;
            		}
            	}
            }
            return cursor;
        } catch (Exception ex) {
           	return urlYoutube;
        }
   	}

   public String extractYoutubeId(String url) throws MalformedURLException {
	   String query = new URL(url).getQuery();
       String[] param = query.split("&");
       String id = null;
       for (String row : param) {
    	   String[] param1 = row.split("=");
           if (param1[0].equals("v")) {
        	   id = param1[1];
            }
      }
       return id;
      }
}

