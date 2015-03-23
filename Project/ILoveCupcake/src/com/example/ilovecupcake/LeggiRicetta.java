package com.example.ilovecupcake;

import java.io.BufferedReader;
import java.io.*;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

// legge la ricetta che è stata salvata precedentemente dall'utente
public class LeggiRicetta extends Activity {
	private String nomeRicetta;//la ricetta selezionata
	private TextView titoloT, fileText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leggi_ricetta);
		titoloT= (TextView)findViewById(R.id.textTitolo);
		fileText= (TextView)findViewById(R.id.textFile);
		fileText.setMovementMethod(new ScrollingMovementMethod());
		nomeRicetta= getIntent().getExtras().getString("nomeRicetta");// recupero il nome che mi è stato passato dall'activity ListaSalvate
		titoloT.setText(nomeRicetta.substring(0, nomeRicetta.length()-4));// per evitare di visualizzare .txt
		String filePath=this.getFilesDir().getPath().toString();// creo il path dove vengono salvate le ricette	
		filePath+="/"+nomeRicetta;// creo il path in cui leggere il file
		File file= new File(filePath);
			// legge il file contenente la ricetta e la visualizza
		try {
			FileInputStream filein= new FileInputStream( file);
			InputStreamReader inputReader= new InputStreamReader(filein);
			BufferedReader buffreader= new BufferedReader(inputReader);
			String readString= buffreader.readLine();
			String tot="";
			while (readString !=null ) {
				tot+="\n"+readString;// aggiungo a quello che ho già letto
				readString=buffreader.readLine();
				}
			fileText.setText(tot);
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
}

