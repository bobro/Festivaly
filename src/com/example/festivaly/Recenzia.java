package com.example.festivaly;

import java.io.FileInputStream;
import com.example.festivaly.R;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Trieda sa zaobera vypisom recenzie
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class Recenzia extends Activity {

	private Databaza db = null;
	private Cursor constantsCursor = null;

	/**
	 * Metoda nacita data z databazy o recenzii a vypise ich
	 * 
	 * @param savedInstanceState Uložené dáta
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recenzia);

        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		TextView nazovPodujatia = (TextView) findViewById(R.id.RecenziaNazovPodujatiaVypis);
		TextView Autor = (TextView) findViewById(R.id.RecenziaAutorRecenzieVypis);
		TextView hlavicka = (TextView) findViewById(R.id.RecenziahlavickaVypis);
		TextView popis = (TextView) findViewById(R.id.RecenziaPopisVypis);
		RatingBar rating = (RatingBar) findViewById(R.id.RecenziaRatingVypis);
		ImageView obrazokRecenzie = (ImageView) findViewById(R.id.obrazokRecenzia);

		String idecko = getIntent().getExtras().getString("ideckoRecenzie");

		db = new Databaza(this);
		constantsCursor = db
				.getReadableDatabase()
				.rawQuery(
						"SELECT recenzia._id, podujatie.nazovPodujatia, prezyvka,podujatie._id, hlavicka, popisRecenzie,pocetHviezd "
								+ "FROM recenzia "
								+ "INNER JOIN podujatie on podujatie._id=recenzia.podujatie_id "
								+ "WHERE recenzia._id = "
								+ idecko
								+ " ORDER BY recenzia._id", null);
		// vyberem id z pola ktore sa vypisalo

		int idPodujatia = 0;
		while (constantsCursor.moveToNext()) {

			nazovPodujatia.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("nazovPodujatia")));
			Autor.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("prezyvka")));
			hlavicka.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("hlavicka")));
			popis.setText(constantsCursor.getString(constantsCursor
					.getColumnIndex("popisRecenzie")));
			rating.setRating(Integer.parseInt(constantsCursor
					.getString(constantsCursor.getColumnIndex("pocetHviezd"))));
			idPodujatia = Integer.parseInt(constantsCursor
					.getString(constantsCursor.getColumnIndex("_id")));
		}

		FileInputStream fis;
		Bitmap obrazok = null;
		try {
			fis = openFileInput("" + idPodujatia);
			obrazok = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (Exception e) {
			Log.d("chyba", "Nacitavanie obrazka");

		}
		if (obrazok == null) {
			obrazokRecenzie.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.noimage));
		} else {
			obrazokRecenzie.setImageBitmap(obrazok);
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
