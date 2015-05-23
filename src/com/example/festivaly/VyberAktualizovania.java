package com.example.festivaly;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.festivaly.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Trieda sa zaobera aktualizovanim databazy
 * 
 * @author Matus Bobrovcan 5ZI037
 *
 */
public class VyberAktualizovania extends Activity {

	private Databaza db = null;
	private Cursor constantsCursor = null;
	private String datumAktualizacii = "1.1.1940";
	private String pocetPodujati = "0";
	private String pocetSkupin = "0";
	private String pocetRecenzii = "0";
	private String dnesnyDatum = "0";
	private TextView datumAktualizacieTextview;
	private TextView pocetPodujatiTextview;
	private TextView pocetSkupinTextview;
	private TextView pocetRecenziiTextview;

	private PendingIntent pendingIntent;
	private boolean error=false;

	private boolean pom = false;

	private String downloadUrl = "http://festival.vv.si/obrazky/";

	private ArrayList<Integer> ideckaChybaju = new ArrayList<Integer>();
	private ArrayList<Integer> idStarych = new ArrayList<Integer>();
	private ArrayList<Integer> idNovych = new ArrayList<Integer>();

	private String nazov = null;
	private String urlObrazku;

	/**
	 * Metoda ktora zmaze vsetky data z databaze a nacita nove z internetovej
	 * databazy, stiahne potrebne obrazky
	 * 
	 * @param savedInstanceState Uložené dáta
	 */
	@SuppressLint("SimpleDateFormat") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aktualizovanie);
		
        this.overridePendingTransition(R.layout.slide_in,
                R.layout.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
		datumAktualizacieTextview = (TextView) findViewById(R.id.NaposledyAktualizovanieVypis);
		pocetPodujatiTextview = (TextView) findViewById(R.id.pocetPodujatiVypis);
		pocetSkupinTextview = (TextView) findViewById(R.id.pocetSkupinVypis);
		pocetRecenziiTextview = (TextView) findViewById(R.id.pocetRecenziiVypis);

		
		db = new Databaza(this);

		// WebServer Request URL
		String serverURL = "http://festival.vv.si/podujatia.php";

		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT _id, datumAktualizacie, pocetPodujati, pocetSkupin, pocetRecenzii "
						+ "FROM aktualizacia order by _id asc", null);

		while (constantsCursor.moveToNext()) {

			datumAktualizacii = (constantsCursor.getString(constantsCursor
					.getColumnIndex("datumAktualizacie")));
			pocetPodujati = constantsCursor.getString(constantsCursor
					.getColumnIndex("pocetPodujati"));
			pocetSkupin = constantsCursor.getString(constantsCursor
					.getColumnIndex("pocetSkupin"));
			pocetRecenzii = constantsCursor.getString(constantsCursor
					.getColumnIndex("pocetRecenzii"));

		}

		constantsCursor = db.getReadableDatabase().rawQuery(
				"SELECT _id FROM podujatie ", null);

		while (constantsCursor.moveToNext()) {

			idStarych.add(constantsCursor.getInt(constantsCursor
					.getColumnIndex("_id")));
		}

		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		dnesnyDatum = df.format(Calendar.getInstance().getTime());

		datumAktualizacii += " / ";
		pocetPodujati += " / ";
		pocetSkupin += " / ";
		pocetRecenzii += " / ";

		new LongOperation().execute(serverURL);
		
		

	}

	
	/**
	 * Trieda sa zaobera aktualizovanim databazy
	 * 
	 * @author Matus Bobrovcan 5ZI037
	 *
	 */
	protected class LongOperation extends AsyncTask<String, Void, Void> {

		

	//	private final HttpClient Client = new DefaultHttpClient();
		private String Content;
		private String Error = null;
		private ProgressDialog Dialog = new ProgressDialog(
				VyberAktualizovania.this);
		String data = "";

		/**
		 * Metoda ktora vyvola dialogove okno
		 */
		protected void onPreExecute() {

			Dialog.setMessage("Prosim èakajte..");
			Dialog.show();
			
			int currentOrientation = getResources().getConfiguration().orientation;
			if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			}
			else {
			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			}

		}

		/**
		 * Metoda ktora sa spoji so serverom a prebere data v tvare json objektu
		 * 
		 * @param urls	linky z ktorých sa stahujú dáta
		 */
		protected Void doInBackground(String... urls) {

			BufferedReader reader = null;

			try {

				URL url = new URL(urls[0]);

				// pripojenie
				URLConnection conn = url.openConnection();

				// nacitanie dat

				reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				// citanie odpovede
				while ((line = reader.readLine()) != null) {

					sb.append(line + " ");
				}

				// premena odpovede na String
				Content = sb.toString();
			} catch (Exception ex) {
				Error = ex.getMessage();
				error=true;
			} finally {
				try {

					reader.close();

				} catch (Exception ex) {
					Log.e("citanie", "chyba: " + ex);
					error=true;
					
				}
			}

			if (Error != null) {
				Log.d("PostExecute","chyba");
				error=true;

			} else {



		//		String OutputData = "";
				JSONObject jsonResponse;

				try {
					// premazanie tabuliek
					db.getReadableDatabase().execSQL(
							"delete from skupina_podujatie");
					db.getReadableDatabase().execSQL("delete from album");
					db.getReadableDatabase().execSQL("delete from clenSkupiny");
					db.getReadableDatabase().execSQL("delete from recenzia");
					db.getReadableDatabase()
							.execSQL("delete from castProgramu");
					db.getReadableDatabase().execSQL("delete from skupina");
					db.getReadableDatabase().execSQL("delete from podujatie");


					jsonResponse = new JSONObject(Content);

					/********************************** naplnanie tabulky podujatie **********************/

					JSONArray podujatie = jsonResponse
							.optJSONArray("podujatie");

					

					int lengthJsonArr = podujatie.length();

					for (int i = 0; i < lengthJsonArr; i++) {
						
						JSONObject jsonChildPodujatie = podujatie
								.getJSONObject(i);
			

						int idInsertPodujatie = jsonChildPodujatie
								.getInt("idI");
						String nazovPodujatiaInsert = jsonChildPodujatie
								.getString("nazovPodujatiaI");
						String popisPodujatiaInsert = jsonChildPodujatie
								.getString("popisPodujatiaI");
						String webInsert = jsonChildPodujatie.getString("webI");
						String miestoKonaniaInsert = jsonChildPodujatie
								.getString("miestoKonaniaI");
						String odDatumInsert = jsonChildPodujatie
								.getString("odDatumI");
						String doDatumInsert = jsonChildPodujatie
								.getString("doDatumI");
						double doVstupneInsert = jsonChildPodujatie
								.getDouble("vstupneI");
						double latitudeInsert = jsonChildPodujatie
								.getDouble("latitudeI");
						double longitudeInsert = jsonChildPodujatie
								.getDouble("longitudeI");

						db.getReadableDatabase()
								.execSQL(
										"Insert into podujatie(_id,nazovPodujatia,popisPodujatia,web,"
												+ "odDatum,doDatum,miestoKonania,vstupne,latitude,longitude)"
												+ " values("
												+ idInsertPodujatie + ",'"
												+ nazovPodujatiaInsert + "','"
												+ popisPodujatiaInsert + "','"
												+ webInsert + "','"
												+ odDatumInsert + "','"
												+ doDatumInsert + "','"
												+ miestoKonaniaInsert + "',"
												+ doVstupneInsert + ","
												+ latitudeInsert + ","
												+ longitudeInsert + ")");

					}

					/***************************** naplnanie tabulky skupiny *********************/

					JSONArray skupiny = jsonResponse.optJSONArray("skupina");


					lengthJsonArr = skupiny.length();

					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildSkupiny = skupiny.getJSONObject(i);


						int idInsertSkupina = jsonChildSkupiny.getInt("idI");
						String nazovSkupinyInsert = jsonChildSkupiny
								.getString("nazovSkupinyI");
						String krajinaPovoduInsert = jsonChildSkupiny
								.getString("krajinaPovoduI");
						int rokZalozeniaInsert = jsonChildSkupiny
								.getInt("rokZalozeniaSkupinyI");
						String popisSkupinyInsert = jsonChildSkupiny
								.getString("popisSkupinyI");
						String zanerSkupinyInsert = jsonChildSkupiny
								.getString("zanerSkupinyI");

						db.getReadableDatabase().execSQL(
								"Insert into skupina(_id,nazovSkupiny,krajinaPovodu,rokZalozeniaSkupiny,"
										+ "popisSkupiny,zanerSkupiny)"
										+ " values(" + idInsertSkupina + ",'"
										+ nazovSkupinyInsert + "','"
										+ krajinaPovoduInsert + "',"
										+ rokZalozeniaInsert + ",'"
										+ popisSkupinyInsert + "','"
										+ zanerSkupinyInsert + "')");

					}

					/************************************** naplnanie tabulky castProgramu ************************/

					JSONArray castProgramu = jsonResponse
							.optJSONArray("castProgramu");


					lengthJsonArr = castProgramu.length();

					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildPodujatie = castProgramu
								.getJSONObject(i);


						int idInsertCastProgramu = jsonChildPodujatie
								.getInt("idI");
						String popisCastiProgramuInsert = jsonChildPodujatie
								.getString("popisCastiProgramuI");
						String datumCastiProgramuInsert = jsonChildPodujatie
								.getString("datumCastiProgramuI");
						int podujatie_idInsert = jsonChildPodujatie
								.getInt("podujatie_idI");

						db.getReadableDatabase().execSQL(
								"Insert into castProgramu(_id,popisCastiProgramu,"
										+ "datumCastiProgramu,podujatie_id)"
										+ " values(" + idInsertCastProgramu
										+ ",'" + popisCastiProgramuInsert
										+ "','" + datumCastiProgramuInsert
										+ "'," + podujatie_idInsert + ")");

					}
					/************************************** naplnanie tabulky recenzia ************************/

					JSONArray recenzia = jsonResponse.optJSONArray("recenzia");


					lengthJsonArr = recenzia.length();

					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildPodujatie = recenzia
								.getJSONObject(i);


						int idInsertRecenzia = jsonChildPodujatie.getInt("idI");
						String prezyvkaInsert = jsonChildPodujatie
								.getString("prezyvkaI");
						String hlavickaInsert = jsonChildPodujatie
								.getString("hlavickaI");
						String popisRecenzieInsert = jsonChildPodujatie
								.getString("popisRecenzieI");
						int pocetHviezdInsert = jsonChildPodujatie
								.getInt("pocetHviezdI");
						int podujatie_idInsert = jsonChildPodujatie
								.getInt("podujatie_idI");

						db.getReadableDatabase().execSQL(
								"Insert into recenzia(_id,prezyvka,hlavicka,popisRecenzie,"
										+ "pocetHviezd,podujatie_id)"
										+ " values(" + idInsertRecenzia + ",'"
										+ prezyvkaInsert + "','"
										+ hlavickaInsert + "','"
										+ popisRecenzieInsert + "',"
										+ pocetHviezdInsert + ","
										+ podujatie_idInsert + ")");

					}

					/************************************** naplnanie tabulky clenSkupiny ************************/

					JSONArray clenSkupiny = jsonResponse
							.optJSONArray("clenSkupiny");


					lengthJsonArr = clenSkupiny.length();

					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildPodujatie = clenSkupiny
								.getJSONObject(i);


						int idInsertClenSkupiny = jsonChildPodujatie
								.getInt("idI");
						String menoInsert = jsonChildPodujatie
								.getString("menoI");
						int rokVstupuInsert = jsonChildPodujatie
								.getInt("rokVstupuI");
						String poziciaInsert = jsonChildPodujatie
								.getString("poziciaI");
						int skupina_idInsert = jsonChildPodujatie
								.getInt("skupina_idI");

						db.getReadableDatabase().execSQL(
								"Insert into clenSkupiny(_id,meno,rokVstupu,pozicia,"
										+ "skupina_id)" + " values("
										+ idInsertClenSkupiny + ",'"
										+ menoInsert + "'," + rokVstupuInsert
										+ ",'" + poziciaInsert + "',"
										+ skupina_idInsert + ")");

					}

					/************************************** naplnanie tabulky album ************************/

					JSONArray album = jsonResponse.optJSONArray("album");


					lengthJsonArr = album.length();

					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildPodujatie = album.getJSONObject(i);


						int idInsertAlbum = jsonChildPodujatie.getInt("idI");
						String nazovAlbumuInsert = jsonChildPodujatie
								.getString("nazovAlbumuI");
						int rokVydaniaInsert = jsonChildPodujatie
								.getInt("rokVydaniaI");
						int skupina_idInsert = jsonChildPodujatie
								.getInt("skupina_idI");

						db.getReadableDatabase().execSQL(
								"Insert into album(_id,nazovAlbumu,rokVydania,"
										+ "skupina_id)" + " values("
										+ idInsertAlbum + ",'"
										+ nazovAlbumuInsert + "',"
										+ rokVydaniaInsert + ","
										+ skupina_idInsert + ")");

					}

					/************************************** naplnanie tabulky skupina_podujatie ************************/

					JSONArray skupina_podujatie = jsonResponse
							.optJSONArray("skupina_podujatie");


					lengthJsonArr = skupina_podujatie.length();

					for (int i = 0; i < lengthJsonArr; i++) {
					
						JSONObject jsonChildPodujatie = skupina_podujatie
								.getJSONObject(i);


						int idInsertSkupina_Podujatie = jsonChildPodujatie
								.getInt("idI");
						int podujatie_idInsert = jsonChildPodujatie
								.getInt("podujatie_idI");
						int skupina_idInsert = jsonChildPodujatie
								.getInt("skupina_idI");

						db.getReadableDatabase().execSQL(
								"Insert into skupina_podujatie(_id,podujatie_id,skupina_id)"
										+ " values("
										+ idInsertSkupina_Podujatie + ","
										+ podujatie_idInsert + ","
										+ skupina_idInsert + ")");

					}


					int pocetSkupinDoTabulky = 0;
					int pocetRecenziiDoTabulky = 0;
					
					/************************** pridanie aktualneho poctu podujati ***************/
					constantsCursor = db
							.getReadableDatabase()
							.rawQuery(
									"SELECT _id "
											+ "FROM podujatie  ORDER BY nazovPodujatia",
									null);

					while (constantsCursor.moveToNext()) {
						idNovych.add(constantsCursor.getInt(constantsCursor
								.getColumnIndex("_id")));

					}

					/************************** pridanie aktualneho poctu skupin ***************/
					constantsCursor = db.getReadableDatabase().rawQuery(
							"SELECT _id, count(_id) " + "FROM skupina", null);

					while (constantsCursor.moveToNext()) {

						pocetSkupinDoTabulky = constantsCursor
								.getInt(constantsCursor
										.getColumnIndex("count(_id)"));

					}
					/************************** pridanie aktualneho poctu recenzii ***************/
					constantsCursor = db.getReadableDatabase().rawQuery(
							"SELECT _id, count(_id) " + "FROM recenzia", null);

					while (constantsCursor.moveToNext()) {

						pocetRecenziiDoTabulky = constantsCursor
								.getInt(constantsCursor
										.getColumnIndex("count(_id)"));

					}

					datumAktualizacii += dnesnyDatum;
					pocetPodujati += idNovych.size();
					pocetSkupin += pocetSkupinDoTabulky;
					pocetRecenzii += pocetRecenziiDoTabulky;

					/*************************** aktualizacia tabulky aktualizacii ***********************/

					db.getReadableDatabase().execSQL(
							"Insert into aktualizacia(datumAktualizacie,pocetPodujati,"
									+ "pocetSkupin,pocetRecenzii) values('"
									+ dnesnyDatum + "'," + idNovych.size()
									+ "," + pocetSkupinDoTabulky + ","
									+ pocetRecenziiDoTabulky + ")");

					/***************************/
					

					ideckaChybaju.clear();
					boolean pomPodujatia;
					for (int i = 0; i < idNovych.size(); i++) {
						pomPodujatia = true;
						for (int j = 0; j < idStarych.size(); j++) {

							if (idNovych.get(i) == idStarych.get(j)) {
								pomPodujatia = false;

							}

						}
						if (pomPodujatia == true) {
							ideckaChybaju.add(idNovych.get(i));

						}
					}

					// vyvaranei notifikacii pre nove podujatia a stahovanie obrazkov
					ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
					AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					ExecutorService executor = Executors.newFixedThreadPool(3);

					for (int i = 0; i < ideckaChybaju.size(); i++) {
						while (pom) {
						}
						pom = true;
						urlObrazku = downloadUrl + ideckaChybaju.get(i)
								+ ".png";
						nazov = Integer.toString(ideckaChybaju.get(i));
						Thread myThread = new Thread(new Stahovanie());
						executor.execute(myThread);

						String pomocna = "" + ideckaChybaju.get(i);
						constantsCursor = db.getReadableDatabase().rawQuery(
								"SELECT podujatie._id, nazovPodujatia, odDatum "
										+ "FROM podujatie	"
										+ " WHERE podujatie._id = " + pomocna
										+ " ORDER BY podujatie._id", null);

						String odDatum = "";

						while (constantsCursor.moveToNext()) {

							odDatum = constantsCursor.getString(constantsCursor
									.getColumnIndex("odDatum"));

						}
						constantsCursor.close();
						Calendar calendar = Calendar.getInstance();

						calendar.set(Calendar.MONTH,
								Integer.parseInt(odDatum.substring(3, 5)) - 1);
						calendar.set(Calendar.YEAR,
								Integer.parseInt(odDatum.substring(6, 10)));
						calendar.set(Calendar.DAY_OF_MONTH,
								Integer.parseInt(odDatum.substring(0, 2)));
						calendar.set(Calendar.HOUR_OF_DAY, 8);
						calendar.set(Calendar.MINUTE, ideckaChybaju.get(i));
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.AM_PM, Calendar.AM);


						if (calendar.after(Calendar.getInstance())) {
							Intent myIntent = new Intent(
									VyberAktualizovania.this, MyReceiver.class);
					

							myIntent.setData(Uri.parse("timer:"
									+ ideckaChybaju.get(i)));
							System.out.println("" + ideckaChybaju.get(i));
							pendingIntent = PendingIntent.getBroadcast(
									VyberAktualizovania.this,
									ideckaChybaju.get(i), myIntent,
									Intent.FLAG_GRANT_READ_URI_PERMISSION);

							alarmManager.cancel(pendingIntent);
							alarmManager.set(AlarmManager.RTC,
									calendar.getTimeInMillis(), pendingIntent);

							intentArray.add(pendingIntent);

						}
					}
					executor.shutdown();
					// testujem ci uz skoncilo stahovanie obrazkov
					while (!executor.isTerminated()) {
					}

					
					

				} catch (JSONException e) {
					error=true;
					e.printStackTrace();
				}

			}
		
		
		
			return null;
		}

		/**
		 * Metoda nacitane data rozparsuje a povklada do databazy a stiahne obrazky k novym festivalom 
		 */
		protected void onPostExecute(Void unused) {
			
			
			datumAktualizacieTextview.setText(datumAktualizacii);
			pocetPodujatiTextview.setText(pocetPodujati);
			pocetSkupinTextview.setText(pocetSkupin);
			pocetRecenziiTextview.setText(pocetRecenzii);
			
			Dialog.dismiss();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			if(error){
				
				Toast.makeText(
						getApplicationContext(),
						"Niekde nastala chyba!\nSkús aktualizova neskôr!",
						Toast.LENGTH_SHORT).show();
			}else{
				
				Toast.makeText(
						getApplicationContext(),
						"Aktualizácia prebehla úspesne!",
						Toast.LENGTH_SHORT).show();
			}
	}
	/**
	 * Trieda ktora ma za ulohu stihnut obrazky
	 * @author matus
	 *
	 */
	private class Stahovanie implements Runnable {

		@Override
		public void run() {
			downloadBitmap(urlObrazku, nazov);
			pom = false;

		}
	}

	/**
	 * Metoda ktora sa pripoji na server a stiahne obrazok a ulozi ho do vnutornej pameti telefonu
	 * 
	 * @param url	Url adresa na ktorej sa nachádza obrázok
	 * @param nazov	Názov obrázku
	 */
	private void downloadBitmap(String url, String nazov) {
	
		final DefaultHttpClient client = new DefaultHttpClient();
		Bitmap bitmap = null;
		
		final HttpGet getRequest = new HttpGet(url);
		try {

			HttpResponse response = client.execute(getRequest);

			
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w("Img", "Error " + statusCode
						+ "Chyba pri stahovani obrazku z" + url);

			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {

					inputStream = entity.getContent();

					bitmap = BitmapFactory.decodeStream(inputStream);

					FileOutputStream fos;
					try {
						fos = openFileOutput(nazov, Context.MODE_PRIVATE);
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
						fos.close();

					} catch (Exception e) {

					}

				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
					Log.e("stahovanie", "ukoncene stahovanie");
				}
			}

		} catch (Exception e) {
			
			getRequest.abort();
			Log.e("Img", "Chyba pri stahovani obrazku" + url + e.toString());
		}
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
