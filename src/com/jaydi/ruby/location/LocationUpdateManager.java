package com.jaydi.ruby.location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.appspot.ruby_mine.rubymine.model.Ruby;
import com.appspot.ruby_mine.rubymine.model.RubyCol;
import com.appspot.ruby_mine.rubymine.model.Rubyzone;
import com.jaydi.ruby.beacon.scanning.ScanningManager;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.database.DatabaseInter;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.location.tracking.TrackingLog;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.CalUtils;
import com.jaydi.ruby.utils.PushUtils;

public class LocationUpdateManager {
	private static final String PREF_LOCATION = "prefLocation";

	private static Location lastLocation;
	private static Set<Long> idSet = new HashSet<Long>();

	public static void handleLocationUpdate(Context context, Location location) {
		// save updated location, don't know if it will work as expected...
		lastLocation = location;

		// check if ruby zone is nearby
		getRubyZones(context, location);
	}

	private static void getRubyZones(final Context context, final Location location) {
		DatabaseInter.getRubyzones(context, new ResponseHandler<List<Rubyzone>>() {

			@Override
			protected void onResponse(List<Rubyzone> res) {
				checkNearbyRubyZone(context, location, res);
			}

		});
	}

	private static void checkNearbyRubyZone(Context context, Location location, List<Rubyzone> rubyzones) {
		List<Long> ids = new ArrayList<Long>();
		for (Rubyzone rubyzone : rubyzones)
			if (CalUtils.calDistance(rubyzone.getLat(), rubyzone.getLng(), location.getLatitude(), location.getLongitude()) < rubyzone.getRange())
				ids.add(rubyzone.getId());

		for (Long id : ids)
			if (!idSet.contains(id)) {
				idSet.add(id);
				walkIn(context, id);
			}

		List<Long> outIds = new ArrayList<Long>();
		for (Long id : idSet)
			if (!ids.contains(id)) {
				outIds.add(id);
				walkOut(context, id);
			}
		idSet.removeAll(outIds);
	}

	private static void walkIn(Context context, Long id) {
		// // save bluetooth state
		// ScanningManager.saveBluetoothState(context);
		// turn on bluetooth
		ScanningManager.turnOnBluetooth();
		// turn on scanning if user is in zone
		ScanningManager.initScanningListener(context);

		if (!isCachedLocation(context, id)) {
			mineRuby(context, id);
			saveExpirationTime(context, id);
		}

		logWalkIn(context, id);
	}

	private static void mineRuby(final Context context, Long id) {
		Ruby ruby = new Ruby();
		ruby.setId(0l);
		ruby.setGiverId(0l);
		ruby.setPlanterId(id);
		ruby.setUserId(LocalUser.getUser().getId());
		ruby.setEvent(1);

		NetworkInter.mineRuby(new ResponseHandler<RubyCol>(context.getMainLooper()) {

			@Override
			protected void onError(int resultCode) {
			}

			@Override
			protected void onResponse(RubyCol res) {
				if (res == null)
					return;

				onRubyFound(context, res);
			}

		}, ruby);
	}

	private static void onRubyFound(Context context, RubyCol rubyCol) {
		PushUtils.pushRuby(context, rubyCol);
	}

	private static void logWalkIn(Context context, Long id) {
		String msg = "region in : " + id;
		TrackingLog.logMsg(context, msg);
	}

	private static void walkOut(Context context, Long id) {
		// turn off scanning if user is out of any zone
		ScanningManager.termScanningListener();
		// turn off bluetooth if needed
		// if (!ScanningManager.wasBluetoothOn(context))
		ScanningManager.turnOffBluetooth();

		logWalkOut(context, id);
	}

	private static void logWalkOut(Context context, Long id) {
		String msg = "region out : " + id;
		TrackingLog.logMsg(context, msg);
	}

	public static Location getLastLocation() {
		return lastLocation;
	}

	public static boolean isInZone(long rubyzoneId) {
		return idSet.contains(rubyzoneId);
	}

	private static boolean isCachedLocation(Context context, Long id) {
		long expirationTime = getExpirationTime(context, id);
		if (System.currentTimeMillis() < expirationTime)
			return true;
		else
			return false;
	}

	private static long getExpirationTime(Context context, Long id) {
		SharedPreferences pref = getPref(context);
		return pref.getLong(id.toString(), 0);
	}

	private static void saveExpirationTime(Context context, Long id) {
		// cache beacon detection, expiration time is the midnight of today
		SharedPreferences pref = getPref(context);
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(id.toString(), getMidnightTime());
		editor.commit();
	}

	private static long getMidnightTime() {
		Calendar c = Calendar.getInstance(Locale.KOREA);
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTimeInMillis();
	}

	private static SharedPreferences getPref(Context context) {
		return context.getSharedPreferences(PREF_LOCATION, Context.MODE_PRIVATE);
	}

}
