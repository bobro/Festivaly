package com.example.festivaly;

import java.util.ArrayList;

import com.example.festivaly.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Trieda sa zaobera vypisom podujati
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class VyberPodujatia extends ListActivity {

	private Databaza db = null;
	private Cursor constantsCursor = null;
	private ArrayList<String> podujatia = new ArrayList<String>();

	
	/**
	 * Metoda vypise do listu vsetky podujatia podla datumu konania
	 * 
	 * @param savedInstanceState	Uložené dáta
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vypis_podujati_skupin);

        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		// nacitam databazu do kurzora
		db = new Databaza(this);
		constantsCursor = db
				.getReadableDatabase()
				.rawQuery(
						"SELECT _id, nazovPodujatia, odDatum, doDatum, miestoKonania,vstupne,substr(odDatum,7,4)||'-'||substr(odDatum,4,2)||'-'||substr(odDatum,1,2) as date "
								+ "FROM podujatie ORDER BY date DESC", null);
		// vyberem id z pola ktore sa vypisalo

		while (constantsCursor.moveToNext()) {
			podujatia.add(constantsCursor.getString(constantsCursor
					.getColumnIndex("_id"))); // add the item

		}

		ListAdapter adapter = new CustomCursorAdapter(this, constantsCursor);
		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(adapter);

	}
	/**
	 * Metoda ktora vytvori novú aktivitu o vybranom podujati
	 * 
	 * @param parent	Predok
	 * @param v			Pohlad
	 * @param position	Pozícia
	 * @param id		Idecko
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Intent intent = new Intent(VyberPodujatia.this, Podujatie.class);

		intent.putExtra("ideckoPodujatia", podujatia.get(position));

		startActivity(intent);

	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	this.finish();
	    	overridePendingTransition(R.layout.slide_in, R.layout.slide_out);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Metoda ktora urci co sa stane po stlaceni tlacitka back
	 * 
	 */
	public void onBackPressed() {
        // Write your code here
	 this.finish();
    	overridePendingTransition(R.layout.slide_in, R.layout.slide_out);
        super.onBackPressed();
    }
	/**
	 * Metóda, ktorá sa vykoná pred ukonèením aktivity
	 * uzavrie kurzor a databázu
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		constantsCursor.close();
		db.close();

	}
}