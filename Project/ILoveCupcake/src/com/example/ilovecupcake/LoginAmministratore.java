package com.example.ilovecupcake;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
// permette di accedere come amministratore cosi da poter vedere le ricette inserite dagli utenti e passarle all'activity RicetteUtenti
public class LoginAmministratore extends Activity implements OnClickListener{
	EditText nomeAmm, pass;
	Button accedi;
	String nome, password;
	String listaNomi[];
	private static final String TAG_NOME="nome", TAG_PASS="password", TAG_SUCCESS="success", TAG_RICETTA="nomericetta";
	private static final String datiAmm="http://192.168.0.107/android_connect/datiAmministratore.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_amministratore);
		nomeAmm= (EditText)findViewById(R.id.eNomeAmm);
		pass=(EditText)findViewById(R.id.ePass);
		accedi=(Button)findViewById(R.id.baccedi);
		accedi.setOnClickListener(this);
	}
	
// vado a verificare se i dati inseriti sono corrett
	public void onClick(View v){
		nome= nomeAmm.getText().toString();
		password=pass.getText().toString();
		if ( nome== "" || password== "")
			Toast.makeText(this, "Inserisci i dati", Toast.LENGTH_SHORT).show();
		else // se ho inserito i dati 
			new VerificaUtente().execute();
	}
	
	
	class VerificaUtente extends AsyncTask<String, String, String >{
		protected String doInBackground (String... args){
			runOnUiThread(new Runnable() {
                public void run() {
                	int success;
                	try {  
                		List<NameValuePair> params = new ArrayList<NameValuePair>();
              
                		params.add(new BasicNameValuePair(TAG_NOME, nome) );//passo i dati dell'utente
                		params.add(new BasicNameValuePair(TAG_PASS, password));
                		JSONParser info= new JSONParser(datiAmm,"GET",params);
                		Thread t= new Thread(info); t.start();
                		t.join();
                		JSONObject json=info.getObj();
         	                  // leggo l'url della ricessa
         	            success= json.getInt(TAG_SUCCESS);
         	            if ( success==1){ // se sono all'amministratore leggo tutte le ricette da passare alla lista ossia visibilita=0
         	            	  JSONArray arr_nomi = json.getJSONArray(TAG_RICETTA); // JSON Array
         	             	  listaNomi= new String[arr_nomi.length()]; // lista nomi ha la stessa lungh di arr_nomi
         	                   for ( int i=0; i<arr_nomi.length(); i++){
         	                   		JSONObject c= arr_nomi.getJSONObject(i);
         	                   		listaNomi[i]=c.getString(TAG_RICETTA).toString();
         	                   }
         	                   if ( arr_nomi.length()==0) // se non ci sono ricette dell'utente
         	                	   Toast.makeText(getApplicationContext(), "Non ci sono ricette dell'utente", Toast.LENGTH_SHORT).show();
         	                   else {
         	                   //vado ad comunicare la lista delle ricette inserite dall'utente all activity RicetteUtenti
         	                	   Intent ricetteUtenti= new Intent(getApplicationContext(), RicetteUtenti.class);
         	                	   ricetteUtenti.putExtra("listaNomi", listaNomi);// passo i nomi che andranno all'activiy
         	                	   startActivity(ricetteUtenti);// chiamo l'activity 
         	                   }
         	             }
         	            else // Se non sono l'amministratore
         	            	Toast.makeText(getApplicationContext(), "Non puoi accedere ai dati privati", Toast.LENGTH_SHORT).show();
                	}catch (JSONException e) {
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
