package com.example.ilovecupcake;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
// andrò a leggere una ricetta dell'utente ( scelta dalla lista precedente) e potrò cancellarla o inserirla nel db

public class LeggiUtente extends Activity {
	private static String infoRicetta="http://192.168.0.107/android_connect/get_info_ricetta.php";// ottenere le info 
	private static final String inserisco="http://192.168.0.107/android_connect/inseriscoRecord.php";// per inserire il record set visiblita=1
	private static String cancello="http://192.168.0.107/android_connect/cancelloRecord.php";// cancellare record
	private static final String TAG_INGREDIENTI="nomeingr",TAG_PROCEDIMENTO="procedimento",TAG_NOME="nome";
	private static final String TAG_SUCCESS="success",TAG_QUANTITA="quantita",TAG_UNITA="unita";
	boolean insert; // se devo inserire record assume valore true altrimenti false
	String infoIngr="\n", procedimento,nomeRicetta;// nomeRicetta da leggere;
	TextView ingr, proc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leggi_utente);
		nomeRicetta= getIntent().getExtras().getString("nomeRicetta");// leggo il nome della ricetta da leggere
		ingr= (TextView)findViewById(R.id.tIngredienti);
		new InfoRicetta().execute(); //mando inforicetta che estende asynctask in esecuzione	
	}
	
	// ottiene le inforazioni riguardanti la ricetta
	class InfoRicetta extends AsyncTask<String , String, String > {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			}
		
		protected String doInBackground (String... args){
			runOnUiThread(new Runnable() {
				public void run() {
					int success;
	                try {
	                	List<NameValuePair> params = new ArrayList<NameValuePair>();
	                	params.add(new BasicNameValuePair(TAG_NOME, nomeRicetta) );//passo il nome della ricetta di cui voglio le info
	                	JSONParser info= new JSONParser(infoRicetta,"GET",params);
	                	Thread t= new Thread(info); t.start();
	                	t.join();
	                	JSONObject json=info.getObj();
	                	success= json.getInt(TAG_SUCCESS);
	                	if ( success==1){// ottengo le info
	                		procedimento= json.getString(TAG_PROCEDIMENTO);
	                		JSONArray arr_ingre = json.getJSONArray(TAG_INGREDIENTI); 
	                		JSONArray arr_quant= json.getJSONArray(TAG_QUANTITA);
	                		JSONArray arr_unita= json.getJSONArray(TAG_UNITA);
	                		for ( int i=0; i<arr_ingre.length(); i++){
	                			JSONObject c= arr_ingre.getJSONObject(i);
	                			JSONObject q= arr_quant.getJSONObject(i);
	                			JSONObject u= arr_unita.getJSONObject(i);
	                		
	                			if (u.getString(TAG_UNITA).toString() != "null" )
	                				infoIngr+="\n"+q.getString(TAG_QUANTITA).toString()+" "+u.getString(TAG_UNITA).toString()+" "+c.getString(TAG_INGREDIENTI).toString()+"\n";
	                			else 
	                				infoIngr+="\n"+q.getString(TAG_QUANTITA).toString()+" "+c.getString(TAG_INGREDIENTI).toString()+"\n";
	                		}
	                		ingr.setText(nomeRicetta+"\n\n"+infoIngr+ "\n"+procedimento);// ingr ora contiene tutte le info da visualizzare

	                	}  else if ( success ==0) // non sono state trovate le informazioni
	                		Toast.makeText(getApplicationContext(), "Nessuna informazione trovata", Toast.LENGTH_LONG).show();
		            } catch (JSONException e) {
		            	e.printStackTrace();
		            	} catch (InterruptedException e) {// quando esco dal join del thread
		            		e.printStackTrace();
		            	}
				}
			});
			return null;
		}
	}
		
	@Override 
	public boolean onCreateOptionsMenu( Menu menu){
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.menuutente, menu);
		return true;// true se il menu è attivo
	}
		
	//verifico la voce che è stata selezionata dal menu
	public boolean onOptionsItemSelected (MenuItem item){
		int id= item.getItemId(); // recupera la voce selezionata
		switch (id){
		case R.id.MenuInsert: { // se voglio inserire effettivamente ila ricetta nel db
			insert=true;
			new CambiaRecord().execute();
			break;
			}
		case R.id.MenuDelete:{ // se voglio cancellare la ricetta dell'utente
			insert=false;
			new CambiaRecord().execute();
			break;
			}
		}
		return false;// ritorna sempre un boolean		
	}
		
		
	class CambiaRecord extends AsyncTask< String,String,String >{	
		protected String doInBackground (String... args){
			runOnUiThread(new Runnable() {
                public void run() {
                	try { 
                		List<NameValuePair> params = new ArrayList<NameValuePair>();
                		params.add(new BasicNameValuePair(TAG_NOME, nomeRicetta) );//passo il nome della ricetta su cui vogliamo fare le operazioni
                		JSONParser record;
                		if ( insert==true)// isnerisco
                			record= new JSONParser(inserisco,"GET",params);
                		else 
                			record= new JSONParser(cancello, "GET", params);
                		Thread tR= new Thread(record); tR.start();
                		tR.join();
                		JSONObject json=record.getObj();
                		int success= json.getInt(TAG_SUCCESS);
                		if ( success==1)
                			Toast.makeText(getApplicationContext(), "Operazione eseguita con successo", Toast.LENGTH_SHORT).show();
                		else 
                			Toast.makeText(getApplicationContext(), "Operazione fallita", Toast.LENGTH_LONG).show();
                	} catch (JSONException e) {
                		e.printStackTrace();
                		} catch (InterruptedException e) {// quando esco dal join del thread
                			e.printStackTrace();
                		}
                }
			});
			return null;
		}
	}
}
