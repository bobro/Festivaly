package com.example.festivaly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Trieda sa zaobera primanim spravy pre vytvorenie notifikacie
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class MyReceiver extends BroadcastReceiver {

	/**
	 * Metoda prime intent a vytvori novu aktivitu pre vyvolanie notifikacie
	 * 
	 * @param context 	Kontext
	 * @param intent	Intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// ziskame id podujatia z intentu ktory nam pride servisom
		int notificationId = Integer.parseInt(intent.getData()
				.getSchemeSpecificPart());
		// String id = intent.getStringExtra("id");

		Log.d("MyReceiver", "" + notificationId);

		Intent service1 = new Intent(context, MyAlarmService.class);
		// vlozime do noveho intentu id a posleme na vykonanie
		service1.setData(Uri.parse("timer:" + notificationId));
		context.startService(service1);

	}

}
