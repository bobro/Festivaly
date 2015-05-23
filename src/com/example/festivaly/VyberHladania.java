package com.example.festivaly;

import java.util.ArrayList;

import com.example.festivaly.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Trieda sa zaobera hladanim podujati alebo skupin
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class VyberHladania extends Activity implements TextWatcher {

	private Button hladanieSkupin;
	private Button hladaniePodujati;
	private AutoCompleteTextView editPodujatie;
	private AutoCompleteTextView editSkupina;

	private Databaza db = null;
	private Cursor constantsCursor = null;

	/**
	 * Metoda vytvori 2 taby v ktorych bude moct uzivatel zadat informacie o
	 * skupine aleho podujati ktore hlada, nacita do poli podujatia a skupiny
	 * ktore bude automaticky doplnat pri pisani, po zadani vytvori novu
	 * aktivitu s listom podujati alebo skupin ktore nasla
	 * 
	 * @param savedInstanceState 	Uložené dáta
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hladanie);

        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		final ArrayList<String> poleSkupin = new ArrayList<String>();
		final ArrayList<String> polePodujati = new ArrayList<String>();
		final EditText zaner = (EditText) findViewById(R.id.HladanieZadanieZanruSkupiny);
		final EditText rokKonania = (EditText) findViewById(R.id.HladanieZadanieRokuFestivalu);

		db = new Databaza(this);
		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT _id, nazovPodujatia " + "FROM podujatie "
						+ " ORDER BY nazovPodujatia", null);
		// vyberem id z pola ktore sa vypisalo

		while (constantsCursor.moveToNext()) {
			polePodujati.add(constantsCursor.getString(constantsCursor
					.getColumnIndex("nazovPodujatia")));

		}

		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT _id, nazovSkupiny " + "FROM skupina "
						+ " ORDER BY nazovSkupiny", null);
		// vyberem id z pola ktore sa vypisalo

		while (constantsCursor.moveToNext()) {
			poleSkupin.add(constantsCursor.getString(constantsCursor
					.getColumnIndex("nazovSkupiny")));

		}

		editPodujatie = (AutoCompleteTextView) findViewById(R.id.editPodujatia);
		editPodujatie.addTextChangedListener(this);

		editPodujatie.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, polePodujati));

		editSkupina = (AutoCompleteTextView) findViewById(R.id.editSkupina);
		editSkupina.addTextChangedListener(this);

		editSkupina.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, poleSkupin));

		hladaniePodujati = (Button) findViewById(R.id.HladajFestival);
		hladaniePodujati.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the FavoritesLangagesActivity
				Intent intent = new Intent(VyberHladania.this, Hladanie.class);
				intent.putExtra("podujatie", true);
				intent.putExtra("nazov", editPodujatie.getText().toString());
				intent.putExtra("rokKonania", rokKonania.getText().toString());

				startActivity(intent);
			}
		});

		hladanieSkupin = (Button) findViewById(R.id.HladajSkupinu);
		hladanieSkupin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the FavoritesLangagesActivity
				Intent intent = new Intent(VyberHladania.this, Hladanie.class);
				intent.putExtra("podujatie", false);
				intent.putExtra("nazov", editSkupina.getText().toString());
				intent.putExtra("zaner", zaner.getText().toString());
				startActivity(intent);
			}
		});

		TabHost tabs = (TabHost) findViewById(R.id.tabhost);

		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("tag1");

		spec.setContent(R.id.tab1);

		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.apptheme_tab_indicator_holo, tabs.getTabWidget(),
				false);
		TextView title = (TextView) tabIndicator
				.findViewById(android.R.id.title);
		title.setText("  Podujatie");
		ImageView icon = (ImageView) tabIndicator
				.findViewById(android.R.id.icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.podujatie));

		spec.setIndicator(tabIndicator);

		tabs.addTab(spec);

		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.tab2);

		tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.apptheme_tab_indicator_holo, tabs.getTabWidget(),
				false);
		title = (TextView) tabIndicator.findViewById(android.R.id.title);
		title.setText("  Skupina");
		icon = (ImageView) tabIndicator.findViewById(android.R.id.icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.skupina));

		spec.setIndicator(tabIndicator);
		tabs.addTab(spec);

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	public void afterTextChanged(Editable s) {

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
