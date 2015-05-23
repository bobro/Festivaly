package com.example.festivaly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.provider.Settings.Secure;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.example.festivaly.R;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Trieda sa zaobera pridavanim recenzii
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class PridanieRecenzie extends Activity {

	private String prezyvka;
	private String hlavicka;
	private String popisRecenzie;
	private String podujatie_id;
	private String android_id;
	private int pocetHviezd;
	private Button OdoslatRecenziu;
	private Databaza db;

	/**
	 * Metoda posiela poziadavku na server pre pridanie recenzie
	 * 
	 * @param savedInstanceState
	 *            Uložené dáta
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pridajrecenziu);

        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		db = new Databaza(this);

		final EditText prezyvkaEditText = (EditText) findViewById(R.id.RecenziaPrezyvkaText);
		final EditText nadpisEditText = (EditText) findViewById(R.id.RecenziaNadpisEdit);
		final EditText popisEditText = (EditText) findViewById(R.id.RecenziaPopisText);
		android_id = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);

		String Nazov = getIntent().getExtras().getString("nazovPodujatia");
		podujatie_id = getIntent().getExtras().getString("idPodujatia");
		TextView nazovFestivalu = (TextView) findViewById(R.id.RecenziaNazovVypis);
		nazovFestivalu.setText(Nazov);

		OdoslatRecenziu = (Button) findViewById(R.id.RecenziaOdoslat);
		OdoslatRecenziu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				RatingBar bar = (RatingBar) findViewById(R.id.RecenziaRating);
				pocetHviezd = (int) bar.getRating();

				prezyvka = prezyvkaEditText.getText().toString();
				hlavicka = nadpisEditText.getText().toString();
				popisRecenzie = popisEditText.getText().toString();

				if (prezyvka.compareTo("") == 0) {
					Toast.makeText(getApplicationContext(),
							"Musis zadat prezyvku!", Toast.LENGTH_LONG).show();
				} else {

					Thread vlakno = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost(
										"http://www.festival.vv.si/pridanieRecenzie.php");

								List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
										6);
								nameValuePairs.add(new BasicNameValuePair(
										"prezyvka", "" + prezyvka));
								nameValuePairs.add(new BasicNameValuePair(
										"hlavicka", "" + hlavicka));
								nameValuePairs.add(new BasicNameValuePair(
										"popisRecenzie", "" + popisRecenzie));
								nameValuePairs.add(new BasicNameValuePair(
										"pocetHviezd", "" + pocetHviezd));
								nameValuePairs.add(new BasicNameValuePair(
										"podujatie_id", "" + podujatie_id));
								nameValuePairs.add(new BasicNameValuePair(
										"kluc", "400"));
								nameValuePairs.add(new BasicNameValuePair(
										"android_id", "" + android_id));
								httppost.setEntity(new UrlEncodedFormEntity(
										nameValuePairs));

								httpclient.execute(httppost);

							} catch (ClientProtocolException e) {

							} catch (IOException e) {

							}

						}
					});

					ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo ni = cm.getActiveNetworkInfo();
					if (ni == null) {

						Toast.makeText(getApplicationContext(),
								"Nie je prístup na internet!",
								Toast.LENGTH_LONG).show();

					} else {
						vlakno.start();

						db.getReadableDatabase().execSQL(
								"Insert into recenzia(prezyvka,hlavicka,popisRecenzie,"
										+ "pocetHviezd,podujatie_id)"
										+ " values('" + prezyvka + "','"
										+ hlavicka + "','" + popisRecenzie
										+ "'," + pocetHviezd + ","
										+ podujatie_id + ")");

						Toast.makeText(getApplicationContext(),
								"Pokus o pridanie recenzie prebehol úspešne!",
								Toast.LENGTH_LONG).show();

						prezyvkaEditText.setText("");
						nadpisEditText.setText("");
						popisEditText.setText("");
					}

				}
			}
		});

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
	
	
}
