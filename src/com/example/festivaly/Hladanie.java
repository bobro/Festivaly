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
 * Trieda sa zaobera vypisom podujati alebo skupin
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class Hladanie extends ListActivity {

	private Databaza db = null;
	private Cursor constantsCursor = null;
	private ArrayList<String> pole = new ArrayList<String>();
	private String nazov;
	private String zaner;
	private String rokKonania;
	private boolean podujatie;

	/**
	 * Metoda ktora pri zadani nazvu podjatia alebo skupiny doplna nazvy z
	 * databazy a nasledne vyhlada podujatia alebo skupiny podla zadanych
	 * parametrov na ktore sa moyeme nasledne dostat
	 * 
	 * @param savedInstanceState Uložené dáta
	 */
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vypis_podujati_skupin);

        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		// nacitame si data z intentu
		podujatie = getIntent().getExtras().getBoolean("podujatie");
		nazov = getIntent().getExtras().getString("nazov");
		zaner = getIntent().getExtras().getString("zaner");
		rokKonania = getIntent().getExtras().getString("rokKonania");

		// ak sme hladali podujatie tak si vybereme data o podujati z databazy
		// inak o skupine
		if (podujatie == true) {
			db = new Databaza(this);
			constantsCursor = db
					.getReadableDatabase()
					.rawQuery(
							"SELECT _id, nazovPodujatia, odDatum, doDatum, vstupne, miestoKonania,substr(odDatum,7,4)||'-'||substr(odDatum,4,2)||'-'||substr(odDatum,1,2) as date "
									+ "FROM podujatie "
									+ "WHERE nazovPodujatia LIKE '%"
									+ nazov
									+ "%'"
									+ " and ( odDatum LIKE '%"
									+ rokKonania
									+ "%'"
									+ " or doDatum LIKE '%"
									+ rokKonania + "%' ) order by date desc",
							null);
			// vyberem id z pola ktore sa vypisalo
			while (constantsCursor.moveToNext()) {
				pole.add(constantsCursor.getString(constantsCursor
						.getColumnIndex("_id"))); // add the item
			}

			// vypiseme data o podujatiach
			constantsCursor.moveToFirst();
			ListAdapter adapter = new CustomCursorAdapter(this, constantsCursor);
			ListView listView = (ListView) findViewById(android.R.id.list);
			listView.setAdapter(adapter);

		} else {
			db = new Databaza(this);
			constantsCursor = db.getReadableDatabase().rawQuery(
					"SELECT _id, nazovSkupiny,zanerSkupiny, krajinaPovodu "
							+ "FROM skupina " + "WHERE nazovSkupiny LIKE '%"
							+ nazov + "%'" + " and zanerSkupiny LIKE'%" + zaner
							+ "%'", null);
			// vyberem id z pola ktore sa vypisalo
			while (constantsCursor.moveToNext()) {
				pole.add(constantsCursor.getString(constantsCursor
						.getColumnIndex("_id"))); // add the item
			}

			// vypiseme data o skupinach
			constantsCursor.moveToFirst();
			// vypisem do listu
			ListAdapter adapter = new SimpleCursorAdapter(this,
					R.layout.vypis_skupin, constantsCursor, new String[] {
							Databaza.NAZOVSKUPINY, Databaza.KRAJINAPOVODU,
							Databaza.ZANERSKUPINY }, new int[] {
							R.id.NazovSkupiny, R.id.KrajinaPovodu,
							R.id.zanerSkupinydole });

			setListAdapter(adapter);
			registerForContextMenu(getListView());

		}

	}

	/**
	 * Metoda ktrora vyvola novy intent podla toho co sme stlacili
	 * 
	 * @param parent
	 *            Predok
	 * @param v
	 *            Pohlad
	 * @param position
	 *            Pozicia
	 * @param id
	 *            Idecko
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) {
		// po slaceni na item vytvorime novy intent
		if (podujatie == true) {
			Intent intent = new Intent(Hladanie.this, Podujatie.class);
			intent.putExtra("ideckoPodujatia", pole.get(position));
			startActivity(intent);
		} else {

			Intent intent = new Intent(Hladanie.this, Skupina.class);
			intent.putExtra("ideckoSkupiny", pole.get(position));
			startActivity(intent);
		}
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