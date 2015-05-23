package com.example.festivaly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import android.widget.Toast;

/**
 * Trieda sa zaobera pridavanim podujati
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class PridaniePodujatia extends Activity {

	private Button OdoslanieNovehoPodujatia;
	private String nazov;
	private String datumOd;
	private String datumDo;
	private String adresa;
	private String email;
	private String web;
	private String popis;

	/**
	 * Metoda posle poziadavku na server pre pridanie podujatia
	 * 
	 * @param savedInstanceState
	 *            Uložené dáta
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pridajpodujatie);

        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		final EditText nazovPodujatia = (EditText) findViewById(R.id.PosliEditNazov);
		final EditText datumOdEditText = (EditText) findViewById(R.id.PosliEditDatumOd);
		final EditText datumDoEditText = (EditText) findViewById(R.id.PosliEditDatumDo);
		final EditText emailEditText = (EditText) findViewById(R.id.PosliEditEmail);
		final EditText webEditText = (EditText) findViewById(R.id.PosliEditWeb);
		final EditText adresaEditText = (EditText) findViewById(R.id.PosliEditAdresa);
		final EditText popisEditText = (EditText) findViewById(R.id.PosliEditPopis);

		OdoslanieNovehoPodujatia = (Button) findViewById(R.id.PridajPodujatie);
		OdoslanieNovehoPodujatia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				nazov = nazovPodujatia.getText().toString();
				datumOd = datumOdEditText.getText().toString();
				datumDo = datumDoEditText.getText().toString();
				adresa = adresaEditText.getText().toString();
				email = emailEditText.getText().toString();
				web = webEditText.getText().toString();
				popis = popisEditText.getText().toString();

				// zistime ci je nieco zadane v nazve a adrese ak nie tak
				if (nazov.compareTo("") == 0 || adresa.compareTo("") == 0) {
					Toast.makeText(getApplicationContext(),
							"Musis zadat aspon nazov a adresu !",
							Toast.LENGTH_LONG).show();
				} else {
					// pripojime sa na strnku

					Thread vlakno = new Thread(new Runnable() {
						@Override
						public void run() {

							try {

								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost(
										"http://www.festival.vv.si/pridaniePodujatia.php");

								// povolime vykonanie v hlavnom vlakne

								// vlozime do pola data ktore chceme poslat
								List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
										8);
								nameValuePairs.add(new BasicNameValuePair(
										"nazov", "" + nazov));
								nameValuePairs.add(new BasicNameValuePair(
										"datumOd", "" + datumOd));
								nameValuePairs.add(new BasicNameValuePair(
										"datumDo", "" + datumDo));
								nameValuePairs.add(new BasicNameValuePair(
										"adresa", "" + adresa));
								nameValuePairs.add(new BasicNameValuePair(
										"email", "" + email));
								nameValuePairs.add(new BasicNameValuePair(
										"web", "" + web));
								nameValuePairs.add(new BasicNameValuePair(
										"popis", "" + popis));
								nameValuePairs.add(new BasicNameValuePair(
										"kluc", "500"));
								httppost.setEntity(new UrlEncodedFormEntity(
										nameValuePairs));

								// posleme data metodou POST serveru
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

						Toast.makeText(getApplicationContext(),
								"Pokus o pridanie podujatia prebehol úspešne",
								Toast.LENGTH_LONG).show();

						nazovPodujatia.setText("");
						datumOdEditText.setText("");
						datumDoEditText.setText("");
						emailEditText.setText("");
						webEditText.setText("");
						adresaEditText.setText("");
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
