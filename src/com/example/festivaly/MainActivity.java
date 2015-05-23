package com.example.festivaly;

import com.example.festivaly.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Trieda sa zaobera vytvranim hlavneho menu
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class MainActivity extends Activity {

	private Button pridavaniePodujati;
	private Button PodujatiaVyber;
	private Button skupinyVyber;
	private Button AktualizaciaVyber;
	private Button hladajVyber;

	/**
	 * Metoda vytvori tlacidla s akciami
	 * 
	 * @param savedInstanceState Uložené dáta
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
       

        
		// zadefinovanie buttonov a pridanie im akcie
		pridavaniePodujati = (Button) findViewById(R.id.PridaniePodujatia);
		pridavaniePodujati.setOnClickListener(new OnClickListener() {

			
			
			@Override
			public void onClick(View v) {
				// otestujeme ci mame pripojenie na internet
				if (isNetworkConnected()) {

					Intent intent = new Intent(MainActivity.this,
							PridaniePodujatia.class);
					startActivity(intent);

				} else {
					Toast.makeText(
							getApplicationContext(),
							"Pre pridavanie podujati musis byt pripojeny na internet!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		skupinyVyber = (Button) findViewById(R.id.skupiny);
		skupinyVyber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the FavoritesLangagesActivity
				Intent intent = new Intent(MainActivity.this,
						VyberSkupiny.class);
				startActivity(intent);
			}
		});

		PodujatiaVyber = (Button) findViewById(R.id.podujatia);
		PodujatiaVyber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the FavoritesLangagesActivity
				Intent intent = new Intent(MainActivity.this,
						VyberPodujatia.class);
				startActivity(intent);
			}
		});

		AktualizaciaVyber = (Button) findViewById(R.id.aktualizuj);
		AktualizaciaVyber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the FavoritesLangagesActivity
				if (isNetworkConnected()) {

					try {
						Intent intent = new Intent(MainActivity.this,
								VyberAktualizovania.class);
						startActivity(intent);

					} catch (Exception e) {
						Toast.makeText(getApplicationContext(),
								"Niekde nastala chyba", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(
							getApplicationContext(),
							"Pre aktualizovanie musis byt pripojeny na internet!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		hladajVyber = (Button) findViewById(R.id.hladaj);
		hladajVyber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the FavoritesLangagesActivity
				Intent intent = new Intent(MainActivity.this,
						VyberHladania.class);
				startActivity(intent);
			}
		});

	}

	/**
	* Metoda otestuje pripojenie na internet
	* 
	* @return true ak sme pripojeny na internet, false ak nie sme pripojeny na
	*         internet
	*/
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
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
