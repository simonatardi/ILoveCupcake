package com.example.ilovecupcake;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
// abbiamo la lista delle ricette inserite dall'utente e cliccando andremo a passare ad un'activity il nome della ricetta da leggere
// l'activity che richiama è LeggiUtente
public class RicetteUtenti extends Activity {
	String nomeRicette[], ricettaScelta;
	ListView lista;// widget
	final ArrayList<String> arrListaNomi= new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ricette_utenti);
		nomeRicette= getIntent().getExtras().getStringArray("listaNomi"); // recupero i nomi da visualizzare nella lista
		lista= (ListView)findViewById(R.id.listaUtenti);
		for ( int i=0; i<nomeRicette.length; i++)
			arrListaNomi.add( nomeRicette[i]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, arrListaNomi );
		lista= (ListView)findViewById(R.id.listaUtenti);
   	    lista.setAdapter(adapter);
	      
		lista.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				ricettaScelta= nomeRicette[position];
	            Intent i = new Intent(RicetteUtenti.this, LeggiUtente.class);  
	            i.putExtra("nomeRicetta", ricettaScelta); // passo a leggere la ricetta. li avrò nel menu sia inserisci che cancella
	            startActivity(i);		 
	            }
			});		  
	}
}
