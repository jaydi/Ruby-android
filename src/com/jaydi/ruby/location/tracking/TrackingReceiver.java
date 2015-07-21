package com.jaydi.ruby.location.tracking;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.location.LocationClient;
import com.jaydi.ruby.location.LocationUpdateManager;
import com.jaydi.ruby.utils.CalUtils;

public class TrackingReceiver extends BroadcastReceiver {
	private static final String PREFERENCE_LOC = "preference_loc";
	private static final String PROPERTY_LAT = "latitude";
	private static final String PROPERTY_LNG = "longitude";
	private static final String PROPERTY_TIME = "created_at";

	@Override
	public void onReceive(Context context, Intent intent) {
		// extract location object from the intent
		Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
		// deliver location information to handling function
		handleLocation(context, location);
	}

	private void handleLocation(Context context, Location location) {
		// hand location over to manager, if it passes filter
		if (filterLocation(context, location))
			LocationUpdateManager.handleLocationUpdate(context, location);

		// report location update
		TrackingLog.logLocation(context, location);
	}

	private boolean filterLocation(Context context, Location location) {
		// get last location update information
		SharedPreferences pref = getLocationPreferences(context);
		double preLat = Double.valueOf(pref.getString(PROPERTY_LAT, "0.0"));
		double preLng = Double.valueOf(pref.getString(PROPERTY_LNG, "0.0"));
		long preTime = pref.getLong(PROPERTY_TIME, 0);

		// save location
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(PROPERTY_LAT, String.valueOf(location.getLatitude()));
		editor.putString(PROPERTY_LNG, String.valueOf(location.getLongitude()));
		editor.putLong(PROPERTY_TIME, new Date().getTime());
		editor.commit();

		// return true if no last location information exists
		if (preLat == 0 && preLng == 0 && preTime == 0)
			return true;

		// calculate distance, interval time and speed
		// to detect outlier point
		double distance = CalUtils.calDistance(preLat, preLng, location.getLatitude(), location.getLongitude());
		double time = (new Date().getTime() - preTime) / 1000;
		double speed = distance / time;

		// return false if moved distance is under 5m
		if (distance < 5.0d)
			return false;

		// return false if speed is over 20m/s
		// it means that we judge this point to be outlier
		// even if the user is actually moving at speed of 20m/s, it is no worth pushing a pin to that fast moving user
		if (speed > 20.0d)
			return false;

		// else return true
		return true;
	}

	private static SharedPreferences getLocationPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCE_LOC, Context.MODE_PRIVATE);
	}
}
