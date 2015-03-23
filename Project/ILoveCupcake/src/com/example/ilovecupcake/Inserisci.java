package com.example.ilovecupcake;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
// permette di far inserire all'utente una nuova ricetta nel db, sarà poi l'amministratore a decidere se è accettabile 
public class Inserisci extends Activity implements OnClickListener {
	EditText num,unit,ingr,url,proce, persone,ricetta;
	Button aggiungi,salva;
	Vector<String > uniIngr= new Vector<String>();// conterranno le unità degli ingredienti per creare la ricetta
	Vector<String > ingredienti= new Vector<String>();// i nomi degli incredienti
	Vector<Integer> quantIngr= new Vector<Integer>();// le quantità degli ingredienti
	String procedimento, urlVideo, nomericetta;
	int numpersone;// numero di porzioni con quella dose
	
	String urlInserisciIngrPost ="http://192.168.0.107/android_connect/inseriscoIngredientiPost.php";// url del file.php per inserire il record
	String urlInserisci ="http://192.168.0.107/android_connect/inseriscoRicetta.php";// url del file.php per inserire il record
	private static final String TAG_SUCCESS = "success", TAG_NOME = "nome",TAG_PROCE="procedimento",TAG_PERSONE="persone";
	private static final String TAG_INGR="nomeingr", TAG_URL="indirizzo", TAG_UNI="unita", TAG_QUA="quantita";
	
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inserisci);
	// tutte le edit utilizzate
		num=(EditText)findViewById(R.id.eNum);
		unit=(EditText)findViewById(R.id.eUni);
		url=(EditText)findViewById(R.id.eVideo);		
		ingr=(EditText)findViewById(R.id.eIngrediente);
		proce=(EditText)findViewById(R.id.eProce);
		proce.setMovementMethod(new ScrollingMovementMethod());
			persone=(EditText)findViewById(R.id.epersone);
		ricetta=(EditText)findViewById(R.id.eRicetta);
		 
	// bottoni
		aggiungi=(Button)findViewById(R.id.bAggiungi);
		salva=(Button)findViewById(R.id.bsalva);
		aggiungi.setOnClickListener(this);// permette di aggiungere l'ingrediente alla lista degli ingredienti
		salva.setOnClickListener(this);// salva l'intera ricetta
	}
	
	// gestisce i bottoni salva e aggiungi
	public void onClick(View v){
		if (v.getId()==R.id.bAggiungi){// aggiungi gli ingredienti
			if ( !ingr.getText().toString().equals("") && !num.getText().toString().equals("") ){// se l'utente ha aggiunto l'ingrediente ed almeno la quantita(unita può essere null)
				ingredienti.add(ingr.getText().toString());// aggiungo agli altri ingredienti
				quantIngr.add( Integer.parseInt(num.getText().toString())); // aggiungo alle altre quantita
				uniIngr.add( unit.getText().toString().equals("") ? "NULL" : unit.getText().toString() ) ;
				ingr.setText("");  num.setText(""); unit.setText("");// ripulisco le edit per facilitare gli inserimenti dell'utente
				Toast.makeText(getApplicationContext(), "L'ingrediente è stato inserito", Toast.LENGTH_SHORT).show();
			}
			else // non sono stati inserite info sufficienit per aggiungere l'ingrediente
				Toast.makeText(getApplicationContext(), "Inserire dati dell'ingrediente", Toast.LENGTH_LONG).show();
		}// fine del bottone aggiungi
		
		else if ( v.getId()== R.id.bsalva){ // salvare ricetta
			if(( ingredienti.size()==0) || (proce.getText().toString().equals("")) || ricetta.getText().toString().equals(""))// se non ho inserito alcun ingrediente oppuer nn ho inserito il procedimento
				Toast.makeText(getApplicationContext(), "Ingredienti insufficienti o procedimento non inserito", Toast.LENGTH_SHORT).show();
			else { // salvo la ricetta
				nomericetta=ricetta.getText().toString();
				numpersone= persone.getText().toString() == null ? 0 : 12; // se nn ho inserito un numero di persone di default assegnerò 12 
				procedimento= proce.getText().toString();// salvo il procedimento
				urlVideo= url.getText().toString()==null  ? "NULL" : url.getText().toString();
				new RicettaDb().execute();// eseguo la chiamata a json per aggiungere la ricetta al db
			}
		}
	}
	
	

// si occuperà di inserire il record nel db. assegnando visibilita=0 finchè l'amministratore non cambiarà tale valore
	class RicettaDb extends AsyncTask<String , String, String > {
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			}
		
		protected String doInBackground (String... args){
			runOnUiThread(new Runnable() {
                public void run() {
                	int success;
                	try{
                	// parametri per inserire la ricetta nel db
                	 List<NameValuePair> params = new ArrayList<NameValuePair>();
     	            params.add(new BasicNameValuePair(TAG_NOME,nomericetta ) );
     	            params.add(new BasicNameValuePair(TAG_URL, urlVideo) );
     	            params.add(new BasicNameValuePair (TAG_PROCE, procedimento));
   	              	params.add(new BasicNameValuePair(TAG_PERSONE, Integer.toString(numpersone)) );
   	            	
   	              	JSONParser met= new JSONParser(urlInserisci,"GET",params);     	     
   	              	Thread t= new Thread(met); t.start();
   	              	t.join();
   	              	JSONObject json=met.getObj();
   	              	success= json.getInt(TAG_SUCCESS);
   	              	if ( success==0)
   	              		Toast.makeText(getApplicationContext(), "La ricetta non è stata inserita", Toast.LENGTH_SHORT).show();
   	              	else {// solo se la ricetta è stata inserita posso aggiungere gli ingredienti ( rispettando vincolo FOREIGN KEY)
   	              		params.add(new BasicNameValuePair(TAG_NOME,nomericetta ) );
   	              		for ( int i=0; i< ingredienti.size(); i++) { // eseguo questo for per passare al file.php gli ingredienti
   	              			params.add(new BasicNameValuePair(TAG_INGR,ingredienti.elementAt(i) ) );
   	              			params.add(new BasicNameValuePair(TAG_UNI,uniIngr.elementAt(i) ) );
   	              			params.add(new BasicNameValuePair(TAG_QUA,quantIngr.elementAt(i).toString() ) );
        	 
   	              			JSONParser tingr= new JSONParser(urlInserisciIngrPost,"POST",params);   	     
   	              			Thread th= new Thread(tingr); th.start();
   	              			th.join();
   	              			JSONObject n=met.getObj();
   	              			success= n.getInt(TAG_SUCCESS);
   	              		}
   	              		Toast.makeText(getApplicationContext(), "Ricetta inserita", Toast.LENGTH_SHORT).show();
   	              	}
                } catch (JSONException e) {
                	e.printStackTrace();
                	}catch (InterruptedException e) {// quando esco dal join del thread
                		e.printStackTrace();
    				}
                }
			});
     	 return null;
		}
    }
        	
       		
        		
}	
		
		
 	   
		


			