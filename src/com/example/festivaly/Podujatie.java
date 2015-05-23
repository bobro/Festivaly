package com.example.festivaly;

import java.io.FileInputStream;
import java.util.ArrayList;

import com.example.festivaly.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Trieda sa zaobera vypisom informacii o podujati
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class Podujatie extends ListActivity {

	private Button pridanieRecenzie;
	private Databaza db = null;
	private Cursor constantsCursor = null;
	private ArrayList<String> recenzie = new ArrayList<String>();
	private String NazovPodujatia;
	private String idecko;
	private Button mapaUbytovanie;
	private Button mapaZaujimavosi;
	private String stranka;
	private String latitude;
	private String longitude;
	private ImageView logoFestivalu;

	/**
	 * Metoda vytvori taby a nacita informacie o podujati a vypise ich
	 * 
	 * @param savedInstanceState Uložené dáta
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.podujatie);

        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		TextView nazovFestivalu = (TextView) findViewById(R.id.NazovFestivaluVypis);
		TextView odDatum = (TextView) findViewById(R.id.DatumOdFestivaluVypis);
		TextView doDatum = (TextView) findViewById(R.id.DatumDoFestivaluVypis);
		TextView popis = (TextView) findViewById(R.id.PopisfestivaluVypis);
		TextView miestoKonania = (TextView) findViewById(R.id.miestoKonaniaVypis);
		TextView web = (TextView) findViewById(R.id.webVypis);
		TextView hodnotenie = (TextView) findViewById(R.id.hodnotenieVypis);
		TextView program = (TextView) findViewById(R.id.ProgramVypis);

		logoFestivalu = (ImageView) findViewById(R.id.obrazok);

		TabHost tabs = (TabHost) findViewById(R.id.tabPoduajtie);

		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("tag1");

		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.apptheme_tab_indicator_holo, tabs.getTabWidget(),
				false);
		TextView title = (TextView) tabIndicator
				.findViewById(android.R.id.title);
		title.setText("  Info");
		ImageView icon = (ImageView) tabIndicator
				.findViewById(android.R.id.icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.info));

		spec.setContent(R.id.info);
		spec.setIndicator(tabIndicator);
		tabs.addTab(spec);

		spec = tabs.newTabSpec("tag3");
		spec.setContent(R.id.program);

		tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.apptheme_tab_indicator_holo, tabs.getTabWidget(),
				false);
		title = (TextView) tabIndicator.findViewById(android.R.id.title);
		title.setText("  Program");
		icon = (ImageView) tabIndicator.findViewById(android.R.id.icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.list));

		spec.setIndicator(tabIndicator);
		tabs.addTab(spec);

		spec = tabs.newTabSpec("tag4");
		spec.setContent(R.id.recenzie);

		tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.apptheme_tab_indicator_holo, tabs.getTabWidget(),
				false);
		title = (TextView) tabIndicator.findViewById(android.R.id.title);
		title.setText("  Recenzie");
		icon = (ImageView) tabIndicator.findViewById(android.R.id.icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.recenzia));

		spec.setIndicator(tabIndicator);
		tabs.addTab(spec);

		// preberanie idcka
		idecko = getIntent().getExtras().getString("ideckoPodujatia");

		db = new Databaza(this);
		constantsCursor = db
				.getReadableDatabase()
				.rawQuery(
						"SELECT podujatie._id, nazovPodujatia, popisPodujatia, miestoKonania,web, doDatum, odDatum, "
								+ "latitude,longitude "
								+ "FROM podujatie	"
								+ " WHERE podujatie._id = "
								+ idecko
								+ " ORDER BY nazovPodujatia", null);
		// vyberem id z pola ktore sa vypisalo

		while (constantsCursor.moveToNext()) {

			nazovFestivalu.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("nazovPodujatia")));
			odDatum.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("odDatum")));
			doDatum.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("doDatum")));
			popis.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("popisPodujatia")));
			miestoKonania.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("miestoKonania")));
			stranka = constantsCursor.getString(constantsCursor
					.getColumnIndex("web"));
			web.setText(stranka);
			NazovPodujatia = constantsCursor.getString(constantsCursor
					.getColumnIndex("nazovPodujatia"));

			latitude = constantsCursor.getString(constantsCursor
					.getColumnIndex("latitude"));
			longitude = constantsCursor.getString(constantsCursor
					.getColumnIndex("longitude"));
		}

		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT _id, popisCastiProgramu, datumCastiProgramu,podujatie_id "
						+ "FROM castProgramu	" + " WHERE podujatie_id = "
						+ idecko + " ORDER BY datumCastiProgramu", null);
		// vyberem id z pola ktore sa vypisalo

		String programCursor = "";

		while (constantsCursor.moveToNext()) {

			programCursor += constantsCursor.getString(constantsCursor
					.getColumnIndex("popisCastiProgramu")) + "\n";
		}

		program.setText(programCursor);

		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT recenzia._id, AVG(pocetHviezd) " + "FROM recenzia "
						+ "WHERE recenzia.podujatie_id = " + idecko + ""
						+ " ORDER BY recenzia._id", null);
		// vyberem id z pola ktore sa vypisalo
		while (constantsCursor.moveToNext()) {
			hodnotenie.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("AVG(pocetHviezd)"))); // add the item
		}

		// nacitanie listu recenzii
		db = new Databaza(this);
		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT recenzia._id, recenzia.hlavicka, recenzia.pocetHviezd,prezyvka "
						+ "FROM recenzia " + "WHERE recenzia.podujatie_id = "
						+ idecko + "" + " ORDER BY recenzia._id", null);
		// vyberem id z pola ktore sa vypisalo
		while (constantsCursor.moveToNext()) {
			recenzie.add(constantsCursor.getString(constantsCursor
					.getColumnIndex("_id"))); // add the item
		}

		// vypisem do listu
		@SuppressWarnings("deprecation")
		ListAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.vypis_recenzii, constantsCursor, new String[] {
						Databaza.HLAVICKA, Databaza.POCETHVIEZD,
						Databaza.PREZYVKA }, new int[] { R.id.HlavickaRecenzie,
						R.id.pocetHviezd, R.id.autorRecenzie });

		setListAdapter(adapter);
		registerForContextMenu(getListView());

		pridanieRecenzie = (Button) findViewById(R.id.PriddajRecenziu);
		pridanieRecenzie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isNetworkConnected()) {

					Intent intent = new Intent(Podujatie.this,
							PridanieRecenzie.class);
					intent.putExtra("nazovPodujatia", NazovPodujatia);
					intent.putExtra("idPodujatia", idecko);
					startActivity(intent);

				} else {
					Toast.makeText(
							getApplicationContext(),
							"Pre pridavanie recenzii musis byt pripojeny na internet!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		web = (Button) findViewById(R.id.webVypis);
		web.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isNetworkConnected()) {
					// start the FavoritesLangagesActivity
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
							.parse("http://" + stranka));
					startActivity(intent);

				} else {
					Toast.makeText(
							getApplicationContext(),
							"Pre prezeranie podujatia musis byt pripojeny na internet!",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		mapaUbytovanie = (Button) findViewById(R.id.mapaUbytovanie);
		mapaUbytovanie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isNetworkConnected()) {
					String urlAddress = "https://www.google.sk/maps/search/accommodation/@"
							+ latitude + "," + longitude + ",13z/data=!3m1!4b1";
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(urlAddress));
					startActivity(intent);

				} else {
					Toast.makeText(
							getApplicationContext(),
							"Pre pozeranie na mape musis byt pripojeny na internet!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		mapaZaujimavosi = (Button) findViewById(R.id.mapaZaujimavosti);
		mapaZaujimavosi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isNetworkConnected()) {
					String urlAddress = "https://www.google.sk/maps/search/zaujimavosti/@"
							+ latitude + "," + longitude + ",13z/data=!3m1!4b1";
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(urlAddress));
					startActivity(intent);

				} else {
					Toast.makeText(
							getApplicationContext(),
							"Pre pozeranie na mape musis byt pripojeny na internet!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		FileInputStream fis;
		Bitmap obrazok = null;
		try {
			fis = openFileInput("" + idecko);
			obrazok = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (Exception e) {
			Log.d("chyba", "Nacitavanie obrazka");

		}
		if (obrazok == null) {
			logoFestivalu.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.noimage));
		} else {
			logoFestivalu.setImageBitmap(obrazok);
		}

	}

	/**
	 * Metoda vyvara novy intent na recenziu
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) {
		Intent intent = new Intent(Podujatie.this, Recenzia.class);
		intent.putExtra("ideckoRecenzie", recenzie.get(position));
		startActivity(intent);
	}

	/**
	 * Metoda testuje pripojenie na internet
	 * 
	 * @return true ak je pripojeny, false ak nie
	 */
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
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
	 * Metóda, ktorá sa vykona pri obnoveni aktivity
	 * nacita celu aktivitu odznova kvoli aktualizovani recenzii
	 */
	@Override
	protected void onResume() {
		super.onResume();
		onCreate(null);
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

}
