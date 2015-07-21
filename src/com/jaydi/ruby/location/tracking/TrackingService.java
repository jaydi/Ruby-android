package com.jaydi.ruby.location.tracking;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class TrackingService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	public static boolean isOnRequest = false;

	private LocationClient locationClient;
	private LocationRequest locationRequest;
	private PendingIntent locationIntent;

	// flag that indicates if a request is ongoing
	private boolean inProgress;

	private boolean servicesAvailable = false;

	private IBinder binder = new LocalBinder();

	public class LocalBinder extends Binder {
		public TrackingService getServerInstance() {
			return TrackingService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("TS", "CREATE");

		isOnRequest = false;
		inProgress = false;
		// create location request object
		locationRequest = LocationRequest.create();
		// use power accuracy balanced priority
		locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		// set interval
		locationRequest.setInterval(TrackingConstants.UPDATE_INTERVAL);
		// set fastest interval
		locationRequest.setFastestInterval(TrackingConstants.FASTEST_INTERVAL);

		servicesAvailable = servicesConnected();

		// create location client using enclosing class as interfaces
		locationClient = new LocationClient(this, this, this);
	}

	private boolean servicesConnected() {
		Log.i("TS", "SERVICE CONNECTED");

		// check if google play service is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (ConnectionResult.SUCCESS == resultCode)
			return true;
		else
			return false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i("TS", "START");

		if (!servicesAvailable || inProgress)
			return START_STICKY;

		setLocationClient();
		if (!locationClient.isConnected() || !locationClient.isConnecting() && !inProgress) {
			TrackingLog.logMsg(this, "Service Started");
			inProgress = true;
			locationClient.connect();
		}

		return START_STICKY;
	}

	private void setLocationClient() {
		if (locationClient == null)
			locationClient = new LocationClient(this, this, this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.i("TS", "LOCATION CLIENT CREATE");

		// set the receiver which gets location update
		Intent intent = new Intent(this, TrackingReceiver.class);
		locationIntent = PendingIntent.getBroadcast(this, 3143, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		// request location update to location client
		locationClient.requestLocationUpdates(locationRequest, locationIntent);
		// set working state setting to be true
		isOnRequest = true;
		// start restart alarm
		setRestartAlarm(true);
		TrackingLog.logMsg(this, "Service Connected");
	}

	private void setRestartAlarm(boolean flag) {
		// restart manager will get broadcast from alarm
		Intent intent = new Intent(this, TrackingRestartReceiver.class);
		intent.setAction(TrackingRestartReceiver.ACTION_RESTART_TRACKING);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 12491, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// set alarm repeating every 30 minutes
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if (flag)
			manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000,
					TrackingConstants.RESTART_ALARM_INTERVAL, alarmIntent);
		else
			manager.cancel(alarmIntent);
	}

	@Override
	public void onDisconnected() {
		Log.i("TS", "LOCATION CLIENT DISCONNECTED");
		// turn off request flag
		inProgress = false;
		// destroy current location client
		locationClient = null;
		// set working state setting to be false
		isOnRequest = false;
		// cancel restart alarm
		setRestartAlarm(false);
		TrackingLog.logMsg(this, "Service Disconnected");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		inProgress = false;

		/*
		 * if result error has a resolution, it is possible that google play services can handle it
		 * let google play services activity to resolve error
		 */
	}

	@Override
	public void onDestroy() {
		Log.i("TS", "DESTROY");
		// turn off request flag
		inProgress = false;
		if (servicesAvailable && locationClient != null) {
			// destroy current location client
			if (locationIntent != null)
				locationClient.removeLocationUpdates(locationIntent);
			locationClient = null;
		}
		// set working state setting to be false
		isOnRequest = false;
		// cancel restart alarm
		setRestartAlarm(false);

		TrackingLog.logMsg(this, "Service Stopped");

		super.onDestroy();
	}

}
