package com.example.ilovecupcake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	
	 private Button cerca,inserisci,salvate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        cerca=(Button)findViewById(R.id.b_cerca);
	    cerca.setOnClickListener(this); 
	        
	    inserisci=(Button)findViewById(R.id.b_inserisci);
	    inserisci.setOnClickListener(this);
        
	    salvate=(Button)findViewById(R.id.b_salvate);
	    salvate.setOnClickListener(this);
    }
    
 // gestisce gli eventi relativi ai bottoni
 	public void onClick(View v){
 		if (v.getId()== R.id.b_cerca){
 			Intent cercare= new Intent(MainActivity.this, Cerca.class);
             startActivity(cercare);
     	}
 		else if (v.getId()==R.id.b_salvate) {
             Intent listaSalvate= new Intent(MainActivity.this, ListaSalvate.class);
             startActivity(listaSalvate);
         }
 		else {// inserimenti
 			Intent inserisci= new Intent(MainActivity.this, Inserisci.class);
 			startActivity(inserisci);
 		}
 	}
 	
 // qui c'è la voce che ci permette di accedere come amministratore
 	@Override 
 	public boolean onCreateOptionsMenu( Menu menu){
 		MenuInflater inflater=getMenuInflater();// recupera un riferito ad un inflate di menu
 			// ossia un servizio del sistema in grado di modellare la struttura dell'oggetto
 		inflater.inflate(R.menu.menuamm, menu);//ho la voce amministratore
 		return true;
 	}
 	
 // gestisce il menu, recupera la voce selezonata e svolge le operazioni
 	public boolean onOptionsItemSelected (MenuItem item){
 		Intent i= new Intent (this, LoginAmministratore.class);
 		startActivity(i);
		return false;
 		}

}
