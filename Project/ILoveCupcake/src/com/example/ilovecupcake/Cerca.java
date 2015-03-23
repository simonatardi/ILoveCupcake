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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;
import android.os.AsyncTask;
// mi permette di cercare una ricetta tramite il nome o una lista di ingredienti di cui dò le dosi. Chiama l'actiivty ListaNomi
public class Cerca extends Activity implements OnClickListener {

	private Button bingredienti;
	private EditText nome_ric, uova,latte,farina,ingredienti;
	String nome,ingredientiPrinc;// Rcietta da cercare, ingredienti principali
	private static String url_ingrRicette = "http://192.168.0.107/android_connect/getPerDosi.php"; // torna le ricette che rispettano i dati che sono passati dall'utente
	private static String url_nomiricette="http://192.168.0.107/android_connect/cerca_nome_simile.php";// nomi simili a quelli dati dall'utente
   boolean nomeOingr=true;// true indica la riceerca per nome, false tramite ingredienti
	String[] listaNomi;
	int nUova, qLatte, nfarina;	
    private static final String TAG_SUCCESS = "success",TAG_NOME = "nome", TAG_FARINA= "farina", TAG_UOVA="uova", TAG_LATTE="latte", TAG_PRINCI= "ingrediente";
		@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cerca);
        nome_ric=(EditText)findViewById(R.id.nome_ricetta);
        uova=(EditText)findViewById(R.id.e_uova);
        latte=(EditText)findViewById(R.id.e_latte);
        farina=(EditText)findViewById(R.id.efarina);
        ingredienti=(EditText)findViewById(R.id.e_ingrediente);
        		
        nome_ric.setImeOptions(EditorInfo.IME_ACTION_GO); // quando ho aggiunto i dati permette di togliere la tastiera tramite il bottone fatto
        nome_ric.setOnEditorActionListener(new OnEditorActionListener(){// quello che succede quando vado a pigiare il tasto fatto
    	  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    	         if (actionId == EditorInfo.IME_ACTION_GO) {
    	        	nome = nome_ric.getText().toString();// leggo ciò che l'utente vuole cercare
    	        	if (nome.equals(""))// se nn ha inserito nulla
    					Toast.makeText(getApplicationContext(), "Nome ricetta non inserito", Toast.LENGTH_LONG).show();
    				else { // cerco in background
    					nomeOingr=true; // indico che la ricerca è per nome
    					new TrovaNomiRicetta().execute(); //mando asynctask in esecuzione
    					return true;
    				}
    			}// i metodi onKey restituiscono sempre un booleano x indicare che l'evento è stato gestito
    			return false;
    	        
    	    }
 
      });
        bingredienti=(Button)findViewById(R.id.c_ingredienti);
        bingredienti.setOnClickListener(this);
	}
		
		

	   // comunica con il db tramite php ee visualizza eventualmente la lista dei nomi compatibili con quello cercato
		//tale lista verrà passata all'activity che si occuperà di visualizzare la lista
	class TrovaNomiRicetta extends AsyncTask<String , String, String > {
			
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			}
			
		protected String doInBackground (String... args){
			runOnUiThread(new Runnable() {
				public void run() {
					int success;
					try { 	
	            	  // Building Parameters
	            		JSONParser met;
	            		List<NameValuePair> params = new ArrayList<NameValuePair>();
	            
	            		if ( nomeOingr== true) { // effettuo la ricerca per nome 
	            				params.add(new BasicNameValuePair(TAG_NOME, nome) );
	           	 // richiamare in un thread la classe jsonParser
	            				met= new JSONParser(url_nomiricette,"GET",params);
	            		}
	            		else { // eseguo la ricerca per ingredienti
	            			params.add(new BasicNameValuePair(TAG_FARINA, Integer.toString(nfarina)) );
	            			params.add(new BasicNameValuePair(TAG_UOVA, Integer.toString(nUova)) );
	            			params.add(new BasicNameValuePair(TAG_LATTE, Integer.toString(qLatte)) );
	            			params.add(new BasicNameValuePair(TAG_PRINCI, ingredientiPrinc) );
	            	 // richiamare in un thread la classe jsonParser
	            			met= new JSONParser(url_ingrRicette,"GET",params);
	            		}
	              
	            	//bisogna richiamare in un thread la classe jsonParser
	            	    Thread t= new Thread(met); t.start();
	            	    t.join(); // il thread aspetta che il thread t termini
	                	JSONObject json=met.getObj();	         
	            	    success= json.getInt(TAG_SUCCESS);
	            	    if ( success==1){// se l'operazione ha avuto successo
	            	    	JSONArray arr_nomi = json.getJSONArray(TAG_NOME); // JSON Array
	            	    	listaNomi= new String[arr_nomi.length()]; // lista nomi ha la stessa lungh di arr_nomi
	            	    	for ( int i=0; i<arr_nomi.length(); i++){ // per tutte le ricette trovate
	            	    		JSONObject c= arr_nomi.getJSONObject(i);
	            	    		listaNomi[i]=c.getString(TAG_NOME).toString();// vado ad inserire il nome trovato nella listaNomi
	            	    	}
	            	  	//vado ad comunicare la lista dei nomi simili delle ricette all activity listaNomi
	            	    	Intent actlistaNomi= new Intent(getApplicationContext(), ListaNomi.class);// visualizza tutta la lista
	            	    	actlistaNomi.putExtra("listaNomi", listaNomi);// passo i nomi che andranno nella lista all'activiy
	            	    	startActivity(actlistaNomi);// chiamo l'activity che visualizza la lista
	            	    } else if ( success ==0) // se non c'è nessuna ricetta compatibile
	            	    	Toast.makeText(getApplicationContext(), "Nessuna ricetta trovata", Toast.LENGTH_LONG).show();
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
		
		// si occupa di andare ad eseguire la ricerca in base alle dosi degli ingredienti
		public void onClick(View v){
				if (v.getId()== R.id.c_ingredienti){
					// se l'utente non ha inzializzato i valori vado ad inserisci dei valori molto alti in modo da non andare a scartare dei record validi
					nUova= uova.getText().toString().equals("") ? 20 : Integer.parseInt(uova.getText().toString()) ;
					qLatte= latte.getText().toString().equals("") ? 1000 : Integer.parseInt(latte.getText().toString());
					nfarina= farina.getText().toString().equals("") ? 1000 : Integer.parseInt(farina.getText().toString());
					ingredientiPrinc= ingredienti.getText().toString().equals("") ? "NULL" : ingredienti.getText().toString();
	 			
					if ( nUova== 20 && qLatte==1000 && (nfarina==1000) && (ingredientiPrinc== "NULL" )) // se non è stato inserito alcun dato dall'utente
						Toast.makeText(getApplicationContext(), "Inserire la quantità di almeno 1 ingrediente", Toast.LENGTH_SHORT).show();
					else { // è stato inserito almeno un parametro per la ricerca
						nomeOingr=false;
		 			
	 				new TrovaNomiRicetta().execute(); //mando asynctask in esecuzione che manderà in esecuzione la classe json
					}
 	 		}
		}
}		