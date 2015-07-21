package com.jaydi.ruby.application;

import java.util.HashMap;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.jaydi.ruby.BaseActivity;
import com.jaydi.ruby.R;

public class RubyApplication extends Application {
	public static final String PREF_APP = "prefApp";
	public static final String PROPERTY_ON_SCREEN = "propertyOnScreen";

	private static RubyApplication instance;
	private static BaseActivity onScreenActivity;

	private GoogleAnalytics analytics;
	private HashMap<TrackerId, Tracker> trackers = new HashMap<TrackerId, Tracker>();

	public enum TrackerId {
		APP_TRACKER
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		// init bitmap cache
		BitmapCache.init(this);
		// init GA
		initAnalytics();
	}

	@Override
	public void onTerminate() {
		instance = null;
		super.onTerminate();
	}

	public static RubyApplication getInstance() {
		return instance;
	}

	public void activityResumed(BaseActivity activity) {
		onScreenActivity = activity;
		SharedPreferences pref = getPref();
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(PROPERTY_ON_SCREEN, true);
		editor.commit();
	}

	public void activityPaused(BaseActivity activity) {
		onScreenActivity = null;
		SharedPreferences pref = getPref();
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(PROPERTY_ON_SCREEN, false);
		editor.commit();
	}

	public BaseActivity getOnScreenActivity() {
		return onScreenActivity;
	}

	private synchronized void initAnalytics() {
		analytics = GoogleAnalytics.getInstance(this);
		analytics.enableAutoActivityReports(this);

		Tracker t = analytics.newTracker(R.xml.app_tracker);
		t.enableAdvertisingIdCollection(true);
		trackers.put(TrackerId.APP_TRACKER, t);
	}

	public synchronized Tracker getTracker() {
		return trackers.get(TrackerId.APP_TRACKER);
	}

	public static int getAppVersion() {
		try {
			PackageInfo packageInfo = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private SharedPreferences getPref() {
		return getSharedPreferences(PREF_APP, MODE_PRIVATE);
	}

}
