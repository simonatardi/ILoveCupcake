package com.example.ilovecupcake;

import java.io.*;
import java.util.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

// calcola le dosi degli ingredienti di una data ricetta in base al numero di persone indicato dall'utente
public class CalcolaIngredienti extends Activity implements OnClickListener{
	String nomeRicetta,infoIngr=" ", procedimento; //nomeRicetta letto dall'activity precedente, infoIngr indica tutte info sugli ingredienti
	TextView tNome,tIngred; //tNome conterrà il nome della ricetta, tIngred conterrà tutte le info sugli ingredienti
	int nPersone, nPersoneRicetta; //il numero di perosne (ottenuto dalla editText). nPersoneRicetta è quella ottenuta dalla richesta
	EditText numPersone;// contiene il numero di persone indicate dall'utente
	private static final String TAG_SUCCESS="success", TAG_NOME="nome", TAG_INGREDIENTI="nomeingr",TAG_QUANTITA="quantita",TAG_UNITA="unita", TAG_PERSONE="persone", TAG_PROCEDIMENTO="procedimento";
	private static String ingrRicetta="http://192.168.0.107/android_connect/get_info_Ricetta.php";// get ingrricetta
	private Button salvaDosi;
	Vector <String> ingr= new Vector<String>();// conterrà tutti gli ingredienti 
	Vector <String> unita= new Vector<String>();// conterrà tutte le unita
	Vector <Integer> quantita = new Vector<Integer>(); // conterrà tutte le quantita
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calcola_ingredienti);
		
		salvaDosi= (Button)findViewById(R.id.salvaDosi); // permette di salvare in un file le dosi
		salvaDosi.setOnClickListener(this);
		nomeRicetta= getIntent().getExtras().getString("nomeRicetta"); // ottengo il nome dall'activity
		tNome= (TextView)findViewById(R.id.tNome);
		tNome.setText(nomeRicetta); // visualizzo il nome della ricetta
		tIngred=(TextView)findViewById(R.id.tIngred);// inserirò le info degli ingredienti
		tIngred.setMovementMethod(new ScrollingMovementMethod());// in modo da rendere la text scrollable se gli ingredienti sono molti
		numPersone=(EditText)findViewById(R.id.eNumpersone);// contiene il numero di persone
		numPersone.setImeOptions(EditorInfo.IME_ACTION_GO); // quando ho digitato il numero di persone toglie tastiera col bottone fatto
	   //legge il numero di persone e se valido richiama la classe che si occuperà di andare ad ottenre le info sugli ingredienti
		numPersone.setOnEditorActionListener(new OnEditorActionListener(){// indica cosa accade quando pigio fatto
	    	  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	    	         if (actionId == EditorInfo.IME_ACTION_GO) {
	    	        	if ( numPersone.getText().toString().equals(""))// nn ha digitato nulla
	    					Toast.makeText(getApplicationContext(), "Indica il numero di persone", Toast.LENGTH_LONG).show();
	    				else { // cerco in background
	    					nPersone = Integer.parseInt(numPersone.getText().toString());// leggo numero Persone indicate dall'utente
		    	        	new GetIngredienti().execute(); //mando asynctask in esecuzione
	    					return true;
	    				}
	    			}// i metodi onKey restituiscono sempre un booleano x indicare che l'evento è stato gestito
	    			return false;
	    	    }
	      });
	}
	
	
	class GetIngredienti extends AsyncTask<String , String, String > {
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
                		List<NameValuePair> params = new ArrayList<NameValuePair>();
                		params.add(new BasicNameValuePair(TAG_NOME, nomeRicetta) );//passo il nome della ricetta di cui voglio le info
                		JSONParser info= new JSONParser(ingrRicetta,"GET",params);
                		Thread t= new Thread(info); t.start();
                		t.join();
                		JSONObject json=info.getObj();
	                 	success= json.getInt(TAG_SUCCESS);
	                    if ( success==1){// se l'operazione ha successo
	                    	procedimento= json.getString(TAG_PROCEDIMENTO);
		                 	nPersoneRicetta= json.getInt(TAG_PERSONE);// ottengo il numero di persone per cui la dose è indicata
		                 	JSONArray arr_ingre = json.getJSONArray(TAG_INGREDIENTI); 
		                 	JSONArray arr_quant= json.getJSONArray(TAG_QUANTITA);
		                 	JSONArray arr_unita= json.getJSONArray(TAG_UNITA);
		                 	for ( int i=0; i<arr_ingre.length(); i++){
		                 		JSONObject c= arr_ingre.getJSONObject(i);
		                 		JSONObject q= arr_quant.getJSONObject(i);
		                 		JSONObject u= arr_unita.getJSONObject(i);
		                 		ingr.add( c.getString(TAG_INGREDIENTI).toString());// memorizzo l'ingrediente
		                 		unita.add( u.getString(TAG_UNITA).toString());// memorizzo l'unita
		                 		quantita.add( q.getInt(TAG_QUANTITA)); // memorizzo quantita 
		                 	}
		           // Quando ho letto tutto contatto il metodo che si occuperà di calcolare le dosi x il num di persone idnicato
		                 	calcola();
	                    }  else // nessun ingrediente trovato
	                    		Toast.makeText(getApplicationContext(), "Nessun ingrediente trovato", Toast.LENGTH_LONG).show();
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
	
	// calcola le dosi per il numero di persone indicate
	public void calcola(){
		float indice=(float) nPersone/nPersoneRicetta; // vado a dividere il numero di persone indicito dall'utente con quello della rcetta
		int nuovaQuantita; // la quantita in base al numero di persone
		for ( int i=0; i< quantita.size(); i++){// ricalcolo le quantita
			nuovaQuantita= (int) ( (float)quantita.elementAt(i) * indice); //  indice mi permette di ottenere la dose giusta
			if (nuovaQuantita==0 )
				nuovaQuantita= 1;// in modo da nn avere di un dato ingrediente la quantita 0
			// vado a crearmi la stringa che sarà poi visualizzata nella text
			if (unita.elementAt(i) != "null" )// verifico se l'unità è presente o vale null. in questo caso nn la stampo
    			infoIngr+="\n"+nuovaQuantita+" "+unita.elementAt(i)+" "+ingr.elementAt(i)+"\n";
    		else 
    			infoIngr+="\n"+nuovaQuantita+" "+ingr.elementAt(i)+"\n";
		}
		tIngred.setText(infoIngr);
	}

	
	// quando pigio su salva permette di salvare le dosi calcolate in un file
	public void onClick(View v){
		// devo salvare le dosi contenute nella textView
		if ( infoIngr != " "){
			String filePath=this.getFilesDir().getPath().toString()+"/"+nomeRicetta+" dosi per "+nPersone+ ".txt";
			FileOutputStream fileout;
			File file= new File(filePath);
			try {
				if ( file.exists())// se già esiste
					Toast.makeText(getApplicationContext(), "La ricetta già presente", Toast.LENGTH_LONG).show(); // se già ho il file	
				else {
					file.createNewFile();
					fileout= new FileOutputStream( file, false);
					OutputStreamWriter outputWriter= new OutputStreamWriter(fileout);
					outputWriter.write(infoIngr);
					outputWriter.write("\n\n"+ procedimento);
					outputWriter.close();
					Toast.makeText(getApplicationContext(), "La ricetta è stata salvata", Toast.LENGTH_LONG).show();
				}
			} catch (FileNotFoundException e) { 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else // se non ho calcolato gli ingredienti
			Toast.makeText(getApplicationContext(), "Ingredienti non calcolati", Toast.LENGTH_SHORT).show();
	}
	
}
