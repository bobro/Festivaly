package com.example.festivaly;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Trieda sa zaobera vytvaranim databazy
 * 
 * @author Matus Bobrovcan 5ZI037 
 * 
 */
public class Databaza extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "databazafestivaly";
	// tablka podujatie
	static final String NAZOVPODUJATIA = "nazovPodujatia";
	static final String POPISPODUJATIA = "popisPodujatia";
	static final String WEB = "web";
	static final String MIESTOKONANIA = "miestoKonania";
	static final String ODDATUM = "odDatum";
	static final String DODATUM = "doDatum";
	static final String VSTUPNE = "vstupne";
	static final String LATITUDE = "latitude";
	static final String LONGITUDE = "longitude";

	// tabulka skupina
	static final String NAZOVSKUPINY = "nazovSkupiny";
	static final String KRAJINAPOVODU = "krajinaPovodu";
	static final String ROKZALOZENIASKUPINY = "rokZalozeniaSkupiny";
	static final String POPISSKUPINY = "popisSkupiny";
	static final String ZANERSKUPINY = "zanerSkupiny";

	// tabulka recenzie
	static final String PREZYVKA = "prezyvka";
	static final String HLAVICKA = "hlavicka";
	static final String POPISRECENZIE = "popisRecenzie";
	static final String POCETHVIEZD = "pocetHviezd";
	static final String PODUJATIE_ID = "podujatie_id";

	// tabulka castProgramu
	static final String POPISCASTIPROGRAMU = "popisCastiProgramu";
	static final String DATUMCASTIPROGRAMU = "datumCastiProgramu";

	// tabulka clenSkupiny
	static final String MENO = "meno";
	static final String ROKVSTUPU = "rokVstupu";
	static final String POZICIA = "pozicia";
	static final String SKUPINA_ID = "skupina_id";

	// tabulka album
	static final String NAZOVALBUMU = "nazovAlbumu";
	static final String ROKVYDANIA = "rokVydania";

	// tabulka info
	static final String DATUMAKTUALIZACIE = "datumAktualizacie";
	static final String POCETPODUJATI = "pocetPodujati";
	static final String POCETSKUPIN = "pocetSkupin";
	static final String POCETRECENZII = "pocetRecenzii";

	// vytvorenie databazy
	public Databaza(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	/**
	 * Metoda vytvori nove entiti do danej databazy
	 * 
	 * @param db	databaza
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// vytvorenie tabulky podujatie
		db.execSQL("CREATE TABLE podujatie (" + "_id INTEGER PRIMARY KEY , "
				+ "nazovPodujatia TEXT, " + "miestoKonania TEXT, "
				+ "popisPodujatia TEXT, " + "web TEXT, "
				+ "odDatum TEXT,vstupne DOUBLE, " + "doDatum TEXT,"
				+ "latitude DOUBLE," + "longitude DOUBLE);");

		// vytvorenie tabulky skupina
		db.execSQL("CREATE TABLE skupina (" + "_id INTEGER PRIMARY KEY , "
				+ "nazovSkupiny TEXT, " + "krajinaPovodu TEXT, "
				+ "rokZalozeniaSkupiny INTEGER, " + "popisSkupiny TEXT,"
				+ "zanerSkupiny TEXT);");

		// vytvorenie tabulky recenzia
		db.execSQL("CREATE TABLE recenzia (" + "_id INTEGER PRIMARY KEY , "
				+ "prezyvka TEXT," + "hlavicka TEXT, " + "popisRecenzie TEXT, "
				+ "pocetHviezd INTEGER," + "podujatie_id INTEGER,"
				+ "FOREIGN KEY (podujatie_id) REFERENCES podujatie(_id));");

		// vytvorenie tabulky castProgramu
		db.execSQL("CREATE TABLE castProgramu (" + "_id INTEGER PRIMARY KEY , "
				+ "popisCastiProgramu TEXT," + "datumCastiProgramu TEXT,"
				+ "podujatie_id INTEGER,"
				+ "FOREIGN KEY (podujatie_id) REFERENCES podujatie(_id));");

		// vytvorenie tabulky album
		db.execSQL("CREATE TABLE album (" + "_id INTEGER PRIMARY KEY , "
				+ "nazovAlbumu TEXT," + "rokVydania INTEGER, "
				+ "skupina_id INTEGER,"
				+ "FOREIGN KEY (skupina_id) REFERENCES skupina(_id));");

		// vytvorenie tabulky clenSkupiny
		db.execSQL("CREATE TABLE clenSkupiny (" + "_id INTEGER PRIMARY KEY , "
				+ "meno TEXT," + "rokVstupu INTEGER, " + "pozicia TEXT,"
				+ "skupina_id INTEGER,"
				+ "FOREIGN KEY (skupina_id) REFERENCES skupina(_id));");

		// tabulka skupina_podujatie
		db.execSQL("CREATE TABLE skupina_podujatie ("
				+ "_id INTEGER PRIMARY KEY , " + "podujatie_id INTEGER , "
				+ "skupina_id INTEGER, "
				+ "FOREIGN KEY (podujatie_id) REFERENCES podujatie(_id),"
				+ "FOREIGN KEY (skupina_id) REFERENCES skupina(_id));");

		// tabulka aktualizacia
		db.execSQL("CREATE TABLE aktualizacia (" + "_id INTEGER PRIMARY KEY , "
				+ "datumAktualizacie TEXT, " + "pocetPodujati INTEGER, "
				+ "pocetSkupin INTEGER, " + "pocetRecenzii INTEGER);");
		/*
		 * naplnenie zakladu tabulky skupina
		 * 
		 * ContentValues skupinaObsah = new ContentValues();
		 * 
		 * skupinaObsah.put(NAZOVSKUPINY, "hex"); skupinaObsah.put(ZANERSKUPINY,
		 * "rock"); skupinaObsah.put(KRAJINAPOVODU, "Slovensko");
		 * skupinaObsah.put(ROKZALOZENIASKUPINY, "1992");
		 * skupinaObsah.put(POPISSKUPINY, "dake keci blbosti a tak");
		 * db.insert("skupina", null, skupinaObsah);
		 * 
		 * skupinaObsah.put(NAZOVSKUPINY, "Ine Kafe");
		 * skupinaObsah.put(ZANERSKUPINY, "rock");
		 * skupinaObsah.put(KRAJINAPOVODU, "Slovensko");
		 * skupinaObsah.put(ROKZALOZENIASKUPINY, "1993");
		 * skupinaObsah.put(POPISSKUPINY, "dake keci blbosti a tak");
		 * db.insert("skupina", null, skupinaObsah); // naplnenie album
		 * 
		 * ContentValues albumObsah = new ContentValues();
		 * albumObsah.put(NAZOVALBUMU, "album1 pre 1 skupinu");
		 * albumObsah.put(ROKVYDANIA, "2154"); albumObsah.put(SKUPINA_ID, 1);
		 * db.insert("album", null, albumObsah);
		 * 
		 * albumObsah.put(NAZOVALBUMU, "album2 pre 1 skupinu");
		 * albumObsah.put(ROKVYDANIA, "2154"); albumObsah.put(SKUPINA_ID, 1);
		 * db.insert("album", null, albumObsah);
		 * 
		 * albumObsah.put(NAZOVALBUMU, "album1 pre 2 skupinu");
		 * albumObsah.put(ROKVYDANIA, "2154"); albumObsah.put(SKUPINA_ID, 2);
		 * db.insert("album", null, albumObsah);
		 * 
		 * albumObsah.put(NAZOVALBUMU, "album2 pre 2 skupinu");
		 * albumObsah.put(ROKVYDANIA, "2154"); albumObsah.put(SKUPINA_ID, 2);
		 * db.insert("album", null, albumObsah);
		 * 
		 * // naplnanie clenSkupiny ContentValues clenSkupinyObsah = new
		 * ContentValues(); clenSkupinyObsah.put(MENO, "clen1 pre 1 skupinu");
		 * clenSkupinyObsah.put(ROKVSTUPU, "2154");
		 * clenSkupinyObsah.put(SKUPINA_ID, 1); db.insert("clenSkupiny", null,
		 * clenSkupinyObsah);
		 * 
		 * clenSkupinyObsah.put(MENO, "clen2 pre 1 skupinu");
		 * clenSkupinyObsah.put(ROKVSTUPU, "2154");
		 * clenSkupinyObsah.put(SKUPINA_ID, 1); db.insert("clenSkupiny", null,
		 * clenSkupinyObsah);
		 * 
		 * clenSkupinyObsah.put(MENO, "clen1 pre 2 skupinu");
		 * clenSkupinyObsah.put(ROKVSTUPU, "2154");
		 * clenSkupinyObsah.put(SKUPINA_ID, 2); db.insert("clenSkupiny", null,
		 * clenSkupinyObsah);
		 * 
		 * clenSkupinyObsah.put(MENO, "clen2 pre 2 skupinu");
		 * clenSkupinyObsah.put(ROKVSTUPU, "2154");
		 * clenSkupinyObsah.put(SKUPINA_ID, 2); db.insert("clenSkupiny", null,
		 * clenSkupinyObsah);
		 * 
		 * clenSkupinyObsah.put(MENO, "clen3 pre 2 skupinu");
		 * clenSkupinyObsah.put(ROKVSTUPU, "2154");
		 * clenSkupinyObsah.put(SKUPINA_ID, 2); db.insert("clenSkupiny", null,
		 * clenSkupinyObsah);
		 * 
		 * // naplnenie zakladu tabulky podujatie ContentValues podujatieObsah =
		 * new ContentValues();
		 * 
		 * podujatieObsah.put(NAZOVPODUJATIA, "Bazant Pohoda");
		 * podujatieObsah.put(POPISPODUJATIA, "daco daco");
		 * podujatieObsah.put(WEB, "www.asds.ds"); podujatieObsah.put(VSTUPNE,
		 * "20e"); podujatieObsah.put(ODDATUM, "1.1.1950");
		 * podujatieObsah.put(DODATUM, "5.1.1950");
		 * podujatieObsah.put(MIESTOKONANIA, "Trencin"); db.insert("podujatie",
		 * null, podujatieObsah);
		 * 
		 * podujatieObsah.put(NAZOVPODUJATIA, "Top fest");
		 * podujatieObsah.put(POPISPODUJATIA, "daco daco");
		 * podujatieObsah.put(WEB, "www.asds.ds"); podujatieObsah.put(ODDATUM,
		 * "1.1.1950"); podujatieObsah.put(DODATUM, "5.1.1950");
		 * podujatieObsah.put(VSTUPNE, "30e"); podujatieObsah.put(MIESTOKONANIA,
		 * "Piestany"); db.insert("podujatie", null, podujatieObsah);
		 * 
		 * // naplnenie zakladu tabulky skupina-podujatie ContentValues
		 * skupina_podujatieObsah = new ContentValues();
		 * skupina_podujatieObsah.put(PODUJATIE_ID, 1);
		 * skupina_podujatieObsah.put(SKUPINA_ID, 1);
		 * db.insert("skupina_podujatie", null, skupina_podujatieObsah);
		 * 
		 * skupina_podujatieObsah.put(PODUJATIE_ID, 1);
		 * skupina_podujatieObsah.put(SKUPINA_ID, 2);
		 * db.insert("skupina_podujatie", null, skupina_podujatieObsah);
		 * 
		 * skupina_podujatieObsah.put(PODUJATIE_ID, 2);
		 * skupina_podujatieObsah.put(SKUPINA_ID, 1);
		 * db.insert("skupina_podujatie", null, skupina_podujatieObsah);
		 * 
		 * skupina_podujatieObsah.put(PODUJATIE_ID, 2);
		 * skupina_podujatieObsah.put(SKUPINA_ID, 2);
		 * db.insert("skupina_podujatie", null, skupina_podujatieObsah);
		 * 
		 * db.execSQL("PRAGMA foreign_keys = ON;");
		 * 
		 * // naplnenie zakladu tabulky recenzia ContentValues recenzieObsah =
		 * new ContentValues();
		 * 
		 * recenzieObsah.put(PREZYVKA, "milan"); recenzieObsah.put(HLAVICKA,
		 * "v poho"); recenzieObsah.put(POPISRECENZIE, "dalo sa to ");
		 * recenzieObsah.put(POCETHVIEZD, "5"); recenzieObsah.put(PODUJATIE_ID,
		 * 1); db.insert("recenzia", null, recenzieObsah);
		 * 
		 * recenzieObsah.put(PREZYVKA, "juro"); recenzieObsah.put(HLAVICKA,
		 * "tak tak"); recenzieObsah.put(POPISRECENZIE,
		 * "vyborne sa niekto bavil urcite"); recenzieObsah.put(POCETHVIEZD,
		 * "1"); recenzieObsah.put(PODUJATIE_ID, 1); db.insert("recenzia", null,
		 * recenzieObsah);
		 * 
		 * recenzieObsah.put(PREZYVKA, "juro"); recenzieObsah.put(HLAVICKA,
		 * "tak tak"); recenzieObsah.put(POPISRECENZIE,
		 * "vyborne sa niekto bavil urcite"); recenzieObsah.put(POCETHVIEZD,
		 * "1"); recenzieObsah.put(PODUJATIE_ID, 1); db.insert("recenzia", null,
		 * recenzieObsah);
		 * 
		 * recenzieObsah.put(PREZYVKA, "juro"); recenzieObsah.put(HLAVICKA,
		 * "tak tak"); recenzieObsah.put(POPISRECENZIE,
		 * "vyborne sa niekto bavil urcite"); recenzieObsah.put(POCETHVIEZD,
		 * "1"); recenzieObsah.put(PODUJATIE_ID, 1); db.insert("recenzia", null,
		 * recenzieObsah);
		 * 
		 * // naplnenie zakladu tabulky recenzia ContentValues castProgramuObsah
		 * = new ContentValues(); castProgramuObsah.put(POPISCASTIPROGRAMU,
		 * "1 hra daco"); castProgramuObsah.put(DATUMCASTIPROGRAMU,
		 * "24.5.2012"); castProgramuObsah.put(PODUJATIE_ID, 1);
		 * db.insert("castProgramu", null, castProgramuObsah);
		 * 
		 * 
		 * castProgramuObsah.put(POPISCASTIPROGRAMU, "2 hra daco");
		 * castProgramuObsah.put(DATUMCASTIPROGRAMU, "24.5.2012");
		 * castProgramuObsah.put(PODUJATIE_ID, 1);
		 * 
		 * db.insert("castProgramu", null, castProgramuObsah);
		 * 
		 * castProgramuObsah.put(POPISCASTIPROGRAMU, "1 hra daco");
		 * castProgramuObsah.put(DATUMCASTIPROGRAMU, "24.5.2012");
		 * castProgramuObsah.put(PODUJATIE_ID, 2); db.insert("castProgramu",
		 * null, castProgramuObsah);
		 * 
		 * 
		 * castProgramuObsah.put(POPISCASTIPROGRAMU, "2 hra daco");
		 * castProgramuObsah.put(DATUMCASTIPROGRAMU, "24.5.2012");
		 * castProgramuObsah.put(PODUJATIE_ID, 2); db.insert("castProgramu",
		 * null, castProgramuObsah);
		 * 
		 * // insert do tabulky info ContentValues infoObsah = new
		 * ContentValues(); infoObsah.put(DATUMAKTUALIZACIE, "5.5.2000");
		 * infoObsah.put(POCETPODUJATI, 4); infoObsah.put(POCETRECENZII, 5);
		 * infoObsah.put(POCETSKUPIN, 6); db.insert("aktualizacia", null,
		 * infoObsah);
		 */

	}

	/**
	 * Metoda dropne vsetky entity a vytvori nove
	 * 
	 * @param db  			databaza
	 * @param oldVersion 	cislo starej verzie
	 * @param newVersion 	cislo novej verzie
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		android.util.Log.w("skupina_podujatie",
				"Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS skupina_podujatie");

		android.util.Log.w("clenSkupiny",
				"Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS clenSkupiny");

		android.util.Log.w("album",
				"Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS album");

		android.util.Log.w("recenzia",
				"Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS recenzia");

		android.util.Log.w("castProgramu",
				"Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS castProgramu");

		android.util.Log.w("podujatie",
				"Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS podujatie");

		android.util.Log.w("skupina",
				"Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS skupina");

		onCreate(db);
	}
}