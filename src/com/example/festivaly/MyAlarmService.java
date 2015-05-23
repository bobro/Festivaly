package com.example.festivaly;

// http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/

import com.example.festivaly.R;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

/**
 * Trieda sa zaobera vytvaranim notifikacii
 * 
 * @author Matus Bobrovcan 5ZI037
 * 
 */
public class MyAlarmService extends Service

{
	private NotificationManager mManager;
	private Databaza db = null;
	private Cursor constantsCursor = null;
	private int notificationId;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	/**
	 * Metoda pri vytvoreni pripoji databazy k aktivite
	 */
	@Override
	public void onCreate() {

		super.onCreate();
		// pripojime sa na databazu
		db = new Databaza(this);
		notificationId = 0;
	}

	/**
	 * Pri vyvolani aktivity prebera id podujatia a vytvara notifikaciu a ztusi
	 * staru poziadavku
	 * 
	 * @param intent Intent
	 * @param startId Idecko
	 */
	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		try {
			// zistime ci intent ma idecko na podujatie
			notificationId = Integer.parseInt(intent.getData()
					.getSchemeSpecificPart());

			Log.d("MyAlarm", "onStart" + notificationId);

			// System.out.println("idecko: " + notificationId);
		} catch (Exception e) {

		}
		if (notificationId != 0) {
			// ak ma ine ako 0 ktoreje defaultne tak nacitame informacie o danom
			// festivale

			constantsCursor = db.getReadableDatabase().rawQuery(
					"SELECT podujatie._id, nazovPodujatia " + "FROM podujatie"
							+ " WHERE podujatie._id = " + notificationId
							+ " ORDER BY podujatie._id", null);

			String nazov = "";

			while (constantsCursor.moveToNext()) {

				nazov = constantsCursor.getString(constantsCursor
						.getColumnIndex("nazovPodujatia"));

			}

			// vytvorime notifikaciu s nazvom podujatia a obrazkom aplikacie
			mManager = (NotificationManager) this.getApplicationContext()
					.getSystemService(
							this.getApplicationContext().NOTIFICATION_SERVICE);

			Intent intent1 = new Intent(this.getApplicationContext(),
					VyberPodujatia.class);

			Notification notification = new Notification(
					R.drawable.ic_launcher, "Nov˝ festival!",
					System.currentTimeMillis());

			intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);

			PendingIntent pendingNotificationIntent = PendingIntent
					.getActivity(this.getApplicationContext(), notificationId,
							intent1, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			notification.setLatestEventInfo(this.getApplicationContext(),
					"Podujatia", "Dnes sa zacÌna festival: " + nazov
							+ "!\nNenechaj si ho ujsù.",
					pendingNotificationIntent);

			// zrusime notifikaciu aby sa uz nezobrazovala
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.cancel(pendingNotificationIntent);

			mManager.notify(0, notification);

		}
	}
	/**
	 * MetÛda, ktor· sa vykon· pred ukonËenÌm aktivity
	 * uzavrie kurzor a datab·zu
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}