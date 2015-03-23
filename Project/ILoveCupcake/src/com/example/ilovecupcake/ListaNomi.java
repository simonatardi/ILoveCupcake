package com.example.ilovecupcake;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;
// permette di visualizzare la lista dei nomi possibili richiesti dall'utente e di sceglierne uno di essi. Mi porta all'activity VisualizzaRicetta
public class ListaNomi extends Activity {
	private String nomeRicetta; // ricetta da cercare 
	ListView lista;// widget ListView
	String[] listaNomi; 
	String[] listaIngredienti;
	final ArrayList<String> arrListaNomi= new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_nomi);

		listaNomi= getIntent().getExtras().getStringArray("listaNomi");// recupero i nomi che mi sono passati dall'activity Cerca 
		for ( int i=0; i<listaNomi.length; i++)// aggiungo tutti arrayList
			arrListaNomi.add(listaNomi[i]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( ListaNomi.this, android.R.layout.simple_list_item_1, arrListaNomi );
		lista= (ListView)findViewById(R.id.lista);
   	    lista.setAdapter(adapter);
	   // ho creato la lista e posso scegliere un elemento
   	    lista.setOnItemClickListener(new OnItemClickListener() {
   	    	public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	           	nomeRicetta= listaNomi[position];// ottengo il nome della ricetta scelta
	       	    Intent i = new Intent(ListaNomi.this, VisualizzaRicetta.class);  
	            i.putExtra("nomeRicetta", nomeRicetta); // passo a visualizza la ricetta di cui l'utente vuole le informazioni 
	            startActivity(i);		
	       }
	    });		  
	}
}


