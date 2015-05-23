package com.example.festivaly;

import java.util.ArrayList;

import com.example.festivaly.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;

/**
 * Trieda sa zaobera vypisom skupiny
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class Skupina extends ListActivity {

	private Databaza db = null;
	private Cursor constantsCursor = null;
	private ArrayList<String> podujatia = new ArrayList<String>();

	/**
	 * Metoda nacita data z databazy o skupinách a vypise ich
	 * 
	 * @param savedInstanceState Uložené dáta
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skupina);

        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		// zadafinovanie TexViewov
		TextView nazovSkupiny = (TextView) findViewById(R.id.NazovSkupinyVypis);
		TextView zanerSkupiny = (TextView) findViewById(R.id.zanerSkupinyVypis);
		TextView clenoviaSkupiny = (TextView) findViewById(R.id.clenoviaSkupinyVypis);
		TextView zalozenieSkupiny = (TextView) findViewById(R.id.rokZalozeniaVypis);
		TextView krajinaPovoduSkupiny = (TextView) findViewById(R.id.krajinaPovoduVypis);
		TextView popisSkupiny = (TextView) findViewById(R.id.PopisSkupinyVypis);
		TextView albumySkupiny = (TextView) findViewById(R.id.SkupinaAlbumyVypis);

		// vytvorenie tabov
		TabHost tabs = (TabHost) findViewById(R.id.tabSkupiny);

		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("tag1");

		spec.setContent(R.id.infoSkupiny);
		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.apptheme_tab_indicator_holo, tabs.getTabWidget(),
				false);
		TextView title = (TextView) tabIndicator
				.findViewById(android.R.id.title);
		title.setText("  Info");
		ImageView icon = (ImageView) tabIndicator
				.findViewById(android.R.id.icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.info));

		spec.setIndicator(tabIndicator);
		tabs.addTab(spec);

		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.albumySkupiny);

		tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.apptheme_tab_indicator_holo, tabs.getTabWidget(),
				false);
		title = (TextView) tabIndicator.findViewById(android.R.id.title);
		title.setText("  Albumy");
		icon = (ImageView) tabIndicator.findViewById(android.R.id.icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.list));

		spec.setIndicator(tabIndicator);
		tabs.addTab(spec);

		spec = tabs.newTabSpec("tag3");
		spec.setContent(R.id.PodujatiaSkupiny);

		tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.apptheme_tab_indicator_holo, tabs.getTabWidget(),
				false);
		title = (TextView) tabIndicator.findViewById(android.R.id.title);
		title.setText(" Podujatia");
		icon = (ImageView) tabIndicator.findViewById(android.R.id.icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.podujatie));

		spec.setIndicator(tabIndicator);
		tabs.addTab(spec);

		// vyebreme si id skupiny z intentu
		String idecko = getIntent().getExtras().getString("ideckoSkupiny");

		// vzbereme data z databazy o skupine
		db = new Databaza(this);
		constantsCursor = db
				.getReadableDatabase()
				.rawQuery(
						"SELECT  skupina._id, nazovSkupiny, zanerSkupiny, krajinaPovodu, rokZalozeniaSkupiny, popisSkupiny "
								+ "FROM skupina "
								+ " WHERE skupina._id = "
								+ idecko + " " + " order BY nazovSkupiny", null);
		// vyberem id z pola ktore sa vypisalo

		String clenoviaSkupinyCursor = "";

		// vlozime ich do TextViewov
		while (constantsCursor.moveToNext()) {

			nazovSkupiny.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("nazovSkupiny")));
			zanerSkupiny.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("zanerSkupiny")));
			zalozenieSkupiny.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("rokZalozeniaSkupiny")));
			popisSkupiny.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("popisSkupiny")));
			krajinaPovoduSkupiny
					.setText(constantsCursor.getString(constantsCursor
							.getColumnIndex("krajinaPovodu")));
		}

		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT  _id,nazovAlbumu,rokVydania,skupina_id "
						+ "FROM album " + " WHERE skupina_id = " + idecko + " "
						+ " order BY rokVydania", null);
		// vyberem id z pola ktore sa vypisalo

		String albumyCursor = "";

		while (constantsCursor.moveToNext()) {

			albumyCursor += constantsCursor.getString(constantsCursor
					.getColumnIndex("rokVydania")) + " - ";
			albumyCursor += constantsCursor.getString(constantsCursor
					.getColumnIndex("nazovAlbumu")) + "\n";
		}

		albumySkupiny.setText(albumyCursor);

		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT  meno " + "FROM clenSkupiny " + " WHERE skupina_id = "
						+ idecko + " order BY meno", null);
		while (constantsCursor.moveToNext()) {

			clenoviaSkupinyCursor += constantsCursor.getString(constantsCursor
					.getColumnIndex("meno")) + "\n";

		}
		clenoviaSkupiny.setText(clenoviaSkupinyCursor);

		constantsCursor = db
				.getReadableDatabase()
				.rawQuery(
						"SELECT podujatie._id, podujatie.nazovPodujatia, podujatie.odDatum ,podujatie.vstupne, podujatie.doDatum, podujatie.miestoKonania,substr(odDatum,7,4)||'-'||substr(odDatum,4,2)||'-'||substr(odDatum,1,2) as date "
								+ "FROM skupina "
								+ "INNER JOIN skupina_podujatie ON skupina_podujatie.skupina_id = skupina._id "
								+ "INNER JOIN podujatie ON skupina_podujatie.podujatie_id = podujatie._id "
								+ "WHERE skupina_podujatie.skupina_id = "
								+ idecko + "" + " ORDER BY date desc", null);
		// vyberem id z pola ktore sa vypisalo
		while (constantsCursor.moveToNext()) {
			podujatia.add(constantsCursor.getString(constantsCursor
					.getColumnIndex("_id"))); // add the item
		}
		// vlozime si idecka podujati do pola pre pripad ze by sme chceli prejst
		// na podujatie

		constantsCursor.moveToFirst();
		ListAdapter adapter = new CustomCursorAdapter(this, constantsCursor);
		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(adapter);

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
	 * Metoda ktora vytvori novy aktivitu o vybranom podujati
	 * 
	 * @param parent	Predok
	 * @param v			Pohlad
	 * @param position	Pozícia
	 * @param id		Idecko
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Intent intent = new Intent(Skupina.this, Podujatie.class);
		intent.putExtra("ideckoPodujatia", podujatia.get(position));
		startActivity(intent);
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
