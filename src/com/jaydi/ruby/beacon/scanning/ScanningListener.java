package com.jaydi.ruby.beacon.scanning;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Log;

import com.jaydi.ruby.beacon.BeaconUpdateManager;
import com.jaydi.ruby.user.LocalUser;

public class ScanningListener implements BeaconConsumer, RangeNotifier {
	private static final String UUID = "7ed18560-4686-43c7-a8bb-7621e22b1cc8";
	private static final String PREF_BEACON = "prefBeacon";

	private Context context;
	private BeaconManager beaconManager;
	private Set<Integer> ids;

	@Override
	public Context getApplicationContext() {
		return context;
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		return context.bindService(service, conn, flags);
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		context.unbindService(conn);
	}

	public void init(Context context) {
		this.context = context;
		beaconManager = BeaconManager.getInstanceForApplication(context);
		ids = new HashSet<Integer>();

		// start beacon service
		beaconManager.bind(this);
		// set beacon layout to search any beacon profiles
		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

		ScanningLog.log(context, "INIT");
	}

	public void term() {
		// stop beacon service
		beaconManager.unbind(this);

		ScanningLog.log(context, "TERM");
	}

	@Override
	public void onBeaconServiceConnect() {
		// attach range notifier interface which is this class
		beaconManager.setRangeNotifier(this);

		try {
			// start ranging beacons with name RubyMine
			// by setting unique UUID, detects only RubyMine beacons
			beaconManager.startRangingBeaconsInRegion(new Region("RubyMine", Identifier.parse(UUID), null, null));

			ScanningLog.log(context, "START");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
		checkRegion(beacons);
		countScanning();
	}

	private void checkRegion(Collection<Beacon> beacons) {
		// for visible beacons decide state by its distance and saved ids
		for (Beacon beacon : beacons) {
			if (!ids.contains(beacon.getId2().toInt())) {
				if (beacon.getDistance() < Double.valueOf(beacon.getId3().toString()) && LocalUser.getUser() != null
						&& LocalUser.getUser().getId() != null)
					walkIn(beacon.getId2().toInt());
			} else {
				if (beacon.getDistance() > Double.valueOf(beacon.getId3().toString()))
					walkOut(beacon.getId2().toInt());
			}

			// log detected beacon
			ScanningLog.logBeacon(context, beacon);
		}

		// if saved beacons suddenly gets invisible, treat it as walk out
		List<Integer> exitedIds = new ArrayList<Integer>();
		for (Integer id : ids) {
			boolean exists = false;
			for (Beacon beacon : beacons)
				if (id.equals(beacon.getId2().toInt()))
					exists = true;

			if (!exists)
				exitedIds.add(id);
		}

		for (Integer id : exitedIds)
			walkOut(id);
	}

	private void walkIn(Integer id) {
		ids.add(id);

		// notify walk in if beacon id is not cached
		if (!isCachedBeacon(context, id)) {
			saveExpirationTime(context, id);
			BeaconUpdateManager.handleBeaconUpdate(context, id);
		}

		logWalkIn(id);
	}

	private void logWalkIn(Integer id) {
		String msg = "region in : " + id;
		ScanningLog.log(context, msg);
	}

	private void walkOut(Integer id) {
		ids.remove(id);

		// for crash protection
		if (ids.isEmpty())
			ScanningManager.forceFlush(context);

		logWalkOut(id);
	}

	private void logWalkOut(Integer id) {
		String msg = "region out : " + id;
		ScanningLog.log(context, msg);
	}

	// for crash protection
	private int count = 0;

	// for crash protection
	private void countScanning() {
		if (++count % 1000 == 0)
			ScanningManager.forceFlush(context);
		Log.i("SL", "scan count: " + count);
	}

	public static boolean isCachedBeacon(Context context, Integer id) {
		long expirationTime = getExpirationTime(context, id);
		if (System.currentTimeMillis() < expirationTime)
			return true;
		else
			return false;
	}

	private static long getExpirationTime(Context context, Integer id) {
		SharedPreferences pref = getPref(context);
		return pref.getLong(id.toString(), 0);
	}

	public static void saveExpirationTime(Context context, Integer id) {
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
		return context.getSharedPreferences(PREF_BEACON, Context.MODE_PRIVATE);
	}
}
