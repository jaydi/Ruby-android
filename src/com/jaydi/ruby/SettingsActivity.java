package com.jaydi.ruby;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.appspot.ruby_mine.rubymine.model.Rubyzone;
import com.appspot.ruby_mine.rubymine.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.jaydi.ruby.application.RubyApplication;
import com.jaydi.ruby.beacon.scanning.ScanningManager;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.database.DatabaseInter;
import com.jaydi.ruby.location.tracking.TrackingService;
import com.jaydi.ruby.settings.Settings;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.CalUtils;
import com.jaydi.ruby.utils.ResourceUtils;

public class SettingsActivity extends BaseActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	// location service variables
	private LocationClient locationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		viewId = 360;

		Button buttonAutoCheck = (Button) findViewById(R.id.button_settings_auto_check);
		if (Settings.isAutoCheck(this))
			buttonAutoCheck.setText(ResourceUtils.getString(R.string.auto_check_on));
		else
			buttonAutoCheck.setText(ResourceUtils.getString(R.string.auto_check_off));
	}

	public void toggleAutoCheck(View view) {
		Button buttonAutoCheck = (Button) findViewById(R.id.button_settings_auto_check);

		if (Settings.isAutoCheck(this)) {
			Settings.setAutoCheck(this, false);
			stopServices();
			buttonAutoCheck.setText(ResourceUtils.getString(R.string.auto_check_off));
		} else {
			Settings.setAutoCheck(this, true);
			startServices();
			buttonAutoCheck.setText(ResourceUtils.getString(R.string.auto_check_on));
		}
	}

	public void logOut(View view) {
		stopServices();
		LocalUser.setUser(new User());

		Intent intent = new Intent(this, LoadingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void startServices() {
		startLocationTracking();
		initLocation();
	}

	private void stopServices() {
		stopBeaconScanning();
		stopLocationTracking();
	}

	private void startLocationTracking() {
		Intent intent = new Intent(RubyApplication.getInstance(), TrackingService.class);
		startService(intent);
	}

	private void stopLocationTracking() {
		Intent intent = new Intent(RubyApplication.getInstance(), TrackingService.class);
		stopService(intent);
	}

	private void stopBeaconScanning() {
		ScanningManager.turnOffBluetooth();
		ScanningManager.termScanningListener();
	}

	private void initLocation() {
		if (servicesConnected() && locationEnabled()) { // if google service available start fuse location service
			locationClient = new LocationClient(this, this, this);
			locationClient.connect();
		} else
			// else load links on temporary location
			setRubyzone(0d, 0d);
	}

	private boolean servicesConnected() {
		// check if google play service is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (ConnectionResult.SUCCESS == resultCode)
			return true;
		else
			return false;
	}

	private boolean locationEnabled() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	// get current location update and start beacon scanning when turn on auto check

	@Override
	public void onConnected(Bundle bundle) {
		requestLocationUpdate();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// retry
		locationClient.connect();
	}

	@Override
	public void onDisconnected() {
	}

	private void requestLocationUpdate() {
		// create location request object
		LocationRequest locationRequest = LocationRequest.create();
		// use power accuracy balanced priority
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// set interval
		locationRequest.setInterval(1000 * 3);
		// set fastest interval
		locationRequest.setFastestInterval(1000 * 1);
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		removeLocationUpdate();
		setRubyzone(location.getLatitude(), location.getLongitude());
	}

	private void removeLocationUpdate() {
		locationClient.removeLocationUpdates(this);
	}

	private void setRubyzone(final double lat, final double lng) {
		DatabaseInter.getRubyzones(this, new ResponseHandler<List<Rubyzone>>() {

			@Override
			protected void onResponse(List<Rubyzone> res) {
				boolean inZone = false;
				for (Rubyzone can : res) {
					double d = CalUtils.calDistance(lat, lng, can.getLat(), can.getLng());
					if (d < can.getRange())
						inZone = true;
				}

				// start beacon scanning if in zone and auto check is on
				if (inZone && Settings.isAutoCheck(SettingsActivity.this))
					initBeaconScanning();
			}

		});
	}

	private void initBeaconScanning() {
		ScanningManager.turnOnBluetooth();
		ScanningManager.initScanningListener(RubyApplication.getInstance());
	}
}
