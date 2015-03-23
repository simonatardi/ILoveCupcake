package com.example.ilovecupcake;

import java.io.*;
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
import android.view.*;
import android.widget.*;

// qyesta classse si occupa di andare a visualizzare le informazioni riguardanti la ricetta scelta 
// ciò che verrà visualizzato dipende dalla voce che viene scelta dal menu
// le activity correlate sono: Video e CalcolaIngredienti
public class VisualizzaRicetta extends Activity {
	private static String infoRicetta="http://192.168.0.107/android_connect/get_info_ricetta.php";
    private static final String TAG_INGREDIENTI="nomeingr", TAG_PROCEDIMENTO="procedimento",TAG_NOME="nome",TAG_SUCCESS="success", TAG_QUANTITA="quantita";
	private static final String TAG_UNITA="unita", TAG_VIDEO="indirizzo", TAG_PERSONE="persone";
	private String nomeRicetta,infoIngr="\n", procedimento, video=null;;
	int persone;
	VideoView vVideo;
	TextView sIngre,textIngr, textProc, sProce;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visualizza_ricetta);
		
		sIngre= (TextView)findViewById(R.id.Ingredienti); // contiene il testo "ingredienti"
		textIngr= (TextView)findViewById(R.id.listaIngredienti);// conterrà gli ingredienti
		sProce=(TextView)findViewById(R.id.Procedimento); //contiene il testo "procedimento"
		textProc= (TextView)findViewById(R.id.proce);// conterrà il procedimento
		// cerco le informazioni sulla ricetta che mi viene passata dall'activity ListaNomi
		nomeRicetta=getIntent().getExtras().getString("nomeRicetta");// recupero il nome della ricetta selezionata
		new InfoRicetta().execute(); //manco asynctask in esecuzione	
 	}
		
	// ottengo le inforazioni riguardanti la ricetta
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
                	sProce.setVisibility(View.GONE); textProc.setVisibility(View.GONE);// rendo invisibili le informazioni degli ingredienti
      			   // Building Parameters
            	    List<NameValuePair> params = new ArrayList<NameValuePair>();
            	    params.add(new BasicNameValuePair(TAG_NOME, nomeRicetta) );//passo il nome della ricetta di cui voglio le info
            	    JSONParser info= new JSONParser(infoRicetta,"GET",params);// prendo tutte le informazioni della ricetta
            	    Thread t= new Thread(info); t.start();
            	    t.join();
            	    JSONObject json=info.getObj();
            	    success= json.getInt(TAG_SUCCESS);
            	    if ( success==1){//se l'operazione è andata bene prendo tutte le info e le memorizzo
            	    	procedimento= json.getString(TAG_PROCEDIMENTO);
            	    	textProc.setText(procedimento); // inserisco il procedimento nella textView
            		    persone=json.getInt(TAG_PERSONE); 
            		    sIngre.setText( sIngre.getText().toString()+" per "+ persone +" persone");
            		    video=json.getString(TAG_VIDEO);
            		    JSONArray arr_ingre = json.getJSONArray(TAG_INGREDIENTI); // JSON Array
            		    JSONArray arr_quant= json.getJSONArray(TAG_QUANTITA);
            		    JSONArray arr_unita= json.getJSONArray(TAG_UNITA);
            		    for ( int i=0; i<arr_ingre.length(); i++){ // leggo tutti gli elementi dell'array
            			    JSONObject c= arr_ingre.getJSONObject(i);
                		    JSONObject q= arr_quant.getJSONObject(i);
                		    JSONObject u= arr_unita.getJSONObject(i);
                		  // creo la stringa degli ingredienti che sarà visualizzata 
                		    if (u.getString(TAG_UNITA).toString() != "null" )// verifico se l'unità è presente o vale null. in questo caso nn la stampo
                		    	infoIngr+="\n"+q.getString(TAG_QUANTITA).toString()+" "+u.getString(TAG_UNITA).toString()+" "+c.getString(TAG_INGREDIENTI).toString()+"\n";
                     		else 
                	     		infoIngr+="\n"+q.getString(TAG_QUANTITA).toString()+" "+c.getString(TAG_INGREDIENTI).toString()+"\n";
            		     } // quando sono terminati gli elementi inserisco la stringa in textIngr
		           textIngr.setText(infoIngr);
		           }  else if ( success ==0) {// devo visualizzare la lista degli ingredienti e il rpocedimetno
	            	     Toast.makeText(getApplicationContext(), "Nessun procedimento trovato", Toast.LENGTH_LONG).show();
	            	     }
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
	
	// riguarda il menu e oncreate... verrà invocato solo alla creazione dell'activity
	@Override 
	public boolean onCreateOptionsMenu( Menu menu){
		MenuInflater inflater=getMenuInflater();// recupera un riferito ad un inflate di menu
			// ossia un servizio del sistema in grado di modellare la struttura dell'oggetto
		inflater.inflate(R.menu.menuvisualizza, menu);//il primo parametro è xml del menu,2° oggetto di menu da configurare
		return true;// true se il menu è attivo
	}
	
	
	// gestisce il menu, recupera la voce selezonata e svolge le operazioni
	public boolean onOptionsItemSelected (MenuItem item){
		int id= item.getItemId(); // recupera la voce selezionata
		switch (id){
		case R.id.MenuIngredienti: { // se voglio visualizzare gli ingredienti
			sProce.setVisibility(View.GONE);textProc.setVisibility(View.GONE);// rendo invisibili questi elementi
			sIngre.setVisibility(View.VISIBLE); textIngr.setVisibility(View.VISIBLE);// rendo gli ingredienti visibili
			break;
			}
		case R.id.MenuProcedimento:{ // se voglio visualizzare il procedimento
			sIngre.setVisibility(View.GONE); textIngr.setVisibility(View.GONE); // rendo gli ingredienti invisibili
			sProce.setVisibility(View.VISIBLE); textProc.setVisibility(View.VISIBLE);// visualizzzo il procedimento
			break;
			}
		case R.id.MenuVideo: {
			if ( video ==null)
				Toast.makeText(getApplicationContext(), "Nessun video presente per questa ricetta", Toast.LENGTH_LONG).show();
			else {// lo visualizzo
				Intent visualizzaVideo=new  Intent(VisualizzaRicetta.this, Video.class);
				visualizzaVideo.putExtra("video", video);// passo l'url del video da visualizzare
				startActivity(visualizzaVideo);
				break;
				}
			}
		case R.id.MenuSalva: {// devo salvare gli ingredienti e il procedimento in un file
				String nomefile=nomeRicetta+".txt";
				String filePath=this.getFilesDir().getPath().toString()+"/"+nomefile;// definisco il path assoluto dove inserire la ricetta
				FileOutputStream fileout;
				File file= new File(filePath);
				 try {
					if ( file.exists()){
						Toast.makeText(getApplicationContext(), "La ricetta già presente", Toast.LENGTH_LONG).show(); // se già ho il file	
					}
					else {
						file.createNewFile();
						fileout= new FileOutputStream( file, false);
						OutputStreamWriter outputWriter= new OutputStreamWriter(fileout);
					    outputWriter.write(infoIngr);// scrivo gli ingredienti e il procedimento nel file
						outputWriter.write("\n\n"+ procedimento);
						outputWriter.close();
						Toast.makeText(getApplicationContext(), "La ricetta è stata salvata", Toast.LENGTH_LONG).show();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Non è possibile salvare ricetta", Toast.LENGTH_LONG).show(); // se già ho il file
				} catch (IOException e) {// per il writer
					e.printStackTrace();
				}  
				  break;
			}
		case R.id.MenuPersone: {// per calcolare gli ingredienti
				Intent calcIngr= new Intent (VisualizzaRicetta.this, CalcolaIngredienti.class);
				calcIngr.putExtra("nomeRicetta",nomeRicetta ); // passo all'activity la ricetta per cui vogliamo calcolare gli ingredienti
				startActivity(calcIngr);
			break; 
			}
		} 
		
		return false;		
	}

}

