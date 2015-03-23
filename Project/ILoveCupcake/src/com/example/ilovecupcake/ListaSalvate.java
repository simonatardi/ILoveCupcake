package com.example.ilovecupcake;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

// torna la lista delle ricette salvate dall'utente nel suo dispostivo
// se devo leggere il File richiamo l'activity LeggiRicetta
public class ListaSalvate extends Activity {
	final ArrayList<String> arrListaNomi= new ArrayList<String>();
	private ListView lista;
	String[] filename;
	String nomeRicetta; // nomeRicetta selezionata
	private boolean leggi=true; // se ho leggi=true devo leggere, se dal menu seleziono cancella imposto leggi a false
	File dir;// contiene il path della directory contenente le ricette salvate
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_salvate);
		lista=(ListView)findViewById(R.id.listaSalvate);
		String filePath=this.getFilesDir().getPath().toString();// creo il path dove vengono salvate le ricette	
		dir= new File(filePath);// punto a quella directory
		final String[]filename=dir.list();// ottengo tutti i file contenuti
		if ( filename.length==0)// se non ci sono file
			Toast.makeText(getApplicationContext(), "Non hai salvato nessuna Ricetta", Toast.LENGTH_LONG).show();
		// se ci sono file creo la lista delle ricette
		for ( int i=0; i<filename.length; i++)
			arrListaNomi.add(filename[i].substring( 0, filename[i].length()-4) );// nomi che saranno visualizzati
			
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( ListaSalvate.this, android.R.layout.simple_list_item_1, arrListaNomi );
	    lista.setAdapter(adapter);
	    lista.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
            	nomeRicetta= filename[position]; // ottengo il nome selezionato
            	if ( leggi==false){ // devo cancellare 
            		File file= new File(dir+"/"+nomeRicetta);
            		file.delete(); // cancella il file
        			Toast.makeText(getApplicationContext(), "La ricetta è stata cancellata", Toast.LENGTH_LONG).show();
            	}
            	else { // se la ricetta deve essere letta
            		Intent actlegg = new Intent(ListaSalvate.this, LeggiRicetta.class);  
            		actlegg.putExtra("nomeRicetta", nomeRicetta); // passo a LeggiRicetta il file da aprire
            		startActivity(actlegg);
                    }
                }
	       });	  
	}
	
	
	// riguarda il menu dove ho le voci leggi e cancella
	@Override 
	public boolean onCreateOptionsMenu( Menu menu){
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.menufile, menu);
		return true;
	}
		
		// gestisce il menu, recupera la voce selezonata e svolge le operazioni
	public boolean onOptionsItemSelected (MenuItem item){
		int id= item.getItemId(); // recupera la voce selezionata
		switch (id){
		case R.id.MenuLeggi: { leggi=true;// se voglio leggere la ricetta
							break;
						}
		case R.id.MenuCancella: { leggi=false;// se voglio cancellare
							break;
						}
				}
		return false; 
	}
	
}
