package com.example.festivaly;

import java.io.FileInputStream;

import com.example.festivaly.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Trieda sa zaobera vypisom itemov do listviewu
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class CustomCursorAdapter extends CursorAdapter {
	private LayoutInflater mLayoutInflater;

	/**
	 * Metoda nacita informacie o podujati
	 * 
	 * @param context kontext	
	 * @param c	kurzor
	 */
	@SuppressWarnings("deprecation")
	public CustomCursorAdapter(Context context, Cursor c) {
		super(context, c);
		mLayoutInflater = LayoutInflater.from(context);
	}

	/**
	 * Metoda vytvara novy view s infomaciami o podujati
	 * 
	 * @param context 	Kontext
	 * @param cursor	Kurzor
	 * @param parent	ViewGrup
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = mLayoutInflater
				.inflate(R.layout.vypis_podujati, parent, false);
		return v;
	}

	/**
	 * Metoda vypsie data z databazy do prkov ktore mame zadafinovane v layoute
	 * 
	 * @param v			Pohlad
	 * @param context	Kontext
	 * @param c			Kurzor
	 */
	@Override
	public void bindView(View v, Context context, Cursor c) {
		String nazov = c.getString(c
				.getColumnIndexOrThrow(Databaza.NAZOVPODUJATIA));
		String miesto = c.getString(c
				.getColumnIndexOrThrow(Databaza.MIESTOKONANIA));
		String odDatum = c.getString(c.getColumnIndexOrThrow(Databaza.ODDATUM));
		String doDatum = c.getString(c.getColumnIndexOrThrow(Databaza.DODATUM));
		String vstupne = c.getString(c.getColumnIndexOrThrow(Databaza.VSTUPNE));
		String idObrazka = c.getString(c.getColumnIndexOrThrow("_id"));

		// vlozime si nase data do layoutu

		TextView nazovList = (TextView) v.findViewById(R.id.NazovDacohoVypis);
		nazovList.setText(nazov);
		TextView miestoList = (TextView) v.findViewById(R.id.NazovMiestaVypis);
		miestoList.setText(miesto);
		TextView odDatumList = (TextView) v.findViewById(R.id.spodny2);
		odDatumList.setText(odDatum);
		TextView doDatumList = (TextView) v.findViewById(R.id.spodny4);
		doDatumList.setText(doDatum);
		TextView vstupneList = (TextView) v.findViewById(R.id.spodny6);
		vstupneList.setText(vstupne);

		// nacitame si obrazok podla id podujatia
		Bitmap obrazok = null;
		try {
			FileInputStream fis = context.openFileInput("" + idObrazka);
			obrazok = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (Exception e) {
			Log.d("chyba", "Nacitavanie obrazka");

		}
		ImageView obrazokList = (ImageView) v.findViewById(R.id.obrazokList);
		if (obrazok == null) {
			obrazokList.setImageResource(R.drawable.noimage);
		} else {
			obrazokList.setImageBitmap(obrazok);
		}

	}

}
