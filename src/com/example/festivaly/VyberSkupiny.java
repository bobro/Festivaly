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
import android.widget.SimpleCursorAdapter;

/**
 * Trieda sa zaobera vypisom skupin
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class VyberSkupiny extends ListActivity {

	private Databaza db = null;
	private Cursor constantsCursor = null;
	private ArrayList<String> skupiny = new ArrayList<String>();

	/**
	 * Metoda vypise vsetky skupiny do listu
	 * 
	 * @param savedInstanceState	Uložené dáta
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vypis_podujati_skupin);

		
        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		// nacitam databazu do kurzora
		db = new Databaza(this);
		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT _id, nazovSkupiny, krajinaPovodu, zanerSkupiny "
						+ "FROM skupina ORDER BY nazovSkupiny", null);
		// vyberem id z pola ktore sa vypisalo
		while (constantsCursor.moveToNext()) {
			skupiny.add(constantsCursor.getString(constantsCursor
					.getColumnIndex("_id"))); // add the item
		}

		constantsCursor.moveToFirst();
		// vypisem do listu
		ListAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.vypis_skupin, constantsCursor, new String[] {
						Databaza.NAZOVSKUPINY, Databaza.KRAJINAPOVODU,
						Databaza.ZANERSKUPINY }, new int[] { R.id.NazovSkupiny,
						R.id.KrajinaPovodu, R.id.zanerSkupinydole });

		setListAdapter(adapter);
		registerForContextMenu(getListView());

	}
	/**
	 * Metoda ktora vytvori novú aktivitu o vybranej skupine
	 * 
	 * @param parent	Predok
	 * @param v			Pohlad
	 * @param position	Pozícia
	 * @param id		Idecko
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Intent intent = new Intent(VyberSkupiny.this, Skupina.class);

		intent.putExtra("ideckoSkupiny", skupiny.get(position));

		startActivity(intent);
	}

	/**
	 * Metoda ktora reaguje na stlacenie buttonu v toolbare
	 * @param item - polozka ktoraje stlacena
	 */
	@Override
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