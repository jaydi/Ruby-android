package com.jaydi.ruby;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Rubyzone;
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
import com.jaydi.ruby.gcm.GcmManager;
import com.jaydi.ruby.location.tracking.TrackingService;
import com.jaydi.ruby.settings.Settings;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.CalUtils;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.Logger;

public class MainActivity extends BaseActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	private Rubyzone rubyzone;

	// location service variables
	private LocationClient locationClient;

	// ui variables
	private int preNavPosition = -1;
	private int navPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewId = 300;

		// prepare navigation menu and content fragments
		navPosition = 0;
		prepareFragments();

		// gcm
		GcmManager.check(this);
		// get location info to set current rubyzone
		initLocation();
		// start location tracker if auto check is on
		if (Settings.isAutoCheck(this))
			initLocationTracking();
	}

	@Override
	public void onResume() {
		super.onResume();
		setRubyInfo();
		updateNavMenu();
		changeFragment();
		loadChildContents();
	}

	@Override
	public void onPause() {
		// Track fragment view off
		Logger.log(Logger.KIND_VIEW_OFF, viewId + navPosition + 1, 0, 0);
		preNavPosition = -1;
		super.onPause();
	}

	public void setRubyInfo() {
		TextView textRubyInfo = (TextView) findViewById(R.id.text_main_ruby_info);
		textRubyInfo.setText("" + LocalUser.getUser().getRuby().intValue());
	}

	private void setTitle() {
		TextView textTitle = (TextView) findViewById(R.id.text_main_title);
		textTitle.setText(rubyzone.getName());
	}

	private void prepareFragments() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.frame_main_container_intro, new MainIntroFragment());
		ft.add(R.id.frame_main_container_collect, new MainCollectRubyFragment());
		ft.add(R.id.frame_main_container_use, new MainUseRubyFragment());
		ft.add(R.id.frame_main_container_more, new MainMoreFragment());
		ft.commit();
	}

	private void initChildContents() {
		if (rubyzone == null)
			return;

		MainFragment f;
		f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_intro);
		if (f != null)
			f.initContents(rubyzone.getId());
		f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_collect);
		if (f != null)
			f.initContents(rubyzone.getId());
		f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_use);
		if (f != null)
			f.initContents(rubyzone.getId());
		f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_more);
		if (f != null)
			f.initContents(rubyzone.getId());
	}

	public void changeNav(View view) {
		switch (view.getId()) {
		case R.id.button_main_nav_intro:
			navPosition = 0;
			break;
		case R.id.button_main_nav_collect:
			navPosition = 1;
			break;
		case R.id.button_main_nav_use:
			navPosition = 2;
			break;
		case R.id.button_main_nav_more:
			navPosition = 3;
			break;
		default:
			navPosition = 0;
			break;
		}

		updateNavMenu();
		changeFragment();
		loadChildContents();
	}

	@SuppressLint("ResourceAsColor")
	private void updateNavMenu() {
		ImageView buttonIntroImg = (ImageView) findViewById(R.id.button_main_nav_intro_img);
		TextView buttonIntroTxt = (TextView) findViewById(R.id.button_main_nav_intro_txt);
		ImageView buttonCollectImg = (ImageView) findViewById(R.id.button_main_nav_collect_img);
		TextView buttonCollectTxt = (TextView) findViewById(R.id.button_main_nav_collect_txt);
		ImageView buttonUseImg = (ImageView) findViewById(R.id.button_main_nav_use_img);
		TextView buttonUseTxt = (TextView) findViewById(R.id.button_main_nav_use_txt);
		ImageView buttonMoreImg = (ImageView) findViewById(R.id.button_main_nav_more_img);
		TextView buttonMoreTxt = (TextView) findViewById(R.id.button_main_nav_more_txt);

		buttonIntroImg.setImageResource(R.drawable.ico_01_nor);
		buttonIntroTxt.setTextColor(R.color.grey);
		buttonCollectImg.setImageResource(R.drawable.ico_02_nor);
		buttonCollectTxt.setTextColor(R.color.grey);
		buttonUseImg.setImageResource(R.drawable.ico_03_nor);
		buttonUseTxt.setTextColor(R.color.grey);
		buttonMoreImg.setImageResource(R.drawable.ico_04_nor);
		buttonMoreTxt.setTextColor(R.color.grey);

		switch (navPosition) {
		case 0:
			buttonIntroImg.setImageResource(R.drawable.ico_01_sel);
			buttonIntroTxt.setTextColor(0xffe95f4e);
			break;
		case 1:
			buttonCollectImg.setImageResource(R.drawable.ico_02_sel);
			buttonCollectTxt.setTextColor(0xffe95f4e);
			break;
		case 2:
			buttonUseImg.setImageResource(R.drawable.ico_03_sel);
			buttonUseTxt.setTextColor(0xffe95f4e);
			break;
		case 3:
			buttonMoreImg.setImageResource(R.drawable.ico_04_sel);
			buttonMoreTxt.setTextColor(0xffe95f4e);
			break;
		default:
			buttonIntroImg.setImageResource(R.drawable.ico_01_sel);
			buttonIntroTxt.setTextColor(0xffe95f4e);
			break;
		}
	}

	private void changeFragment() {
		findViewById(R.id.frame_main_container_intro).setVisibility(View.GONE);
		findViewById(R.id.frame_main_container_collect).setVisibility(View.GONE);
		findViewById(R.id.frame_main_container_use).setVisibility(View.GONE);
		findViewById(R.id.frame_main_container_more).setVisibility(View.GONE);

		if (preNavPosition != -1)
			Logger.log(Logger.KIND_VIEW_OFF, viewId + preNavPosition + 1, 0, 0);
		preNavPosition = navPosition;

		switch (navPosition) {
		case 0:
			findViewById(R.id.frame_main_container_intro).setVisibility(View.VISIBLE);
			break;
		case 1:
			findViewById(R.id.frame_main_container_collect).setVisibility(View.VISIBLE);
			break;
		case 2:
			findViewById(R.id.frame_main_container_use).setVisibility(View.VISIBLE);
			break;
		case 3:
			findViewById(R.id.frame_main_container_more).setVisibility(View.VISIBLE);
			break;
		default:
			findViewById(R.id.frame_main_container_intro).setVisibility(View.VISIBLE);
			break;
		}

		Logger.log(Logger.KIND_VIEW_ON, viewId + navPosition + 1, 0, 0);
	}

	private void loadChildContents() {
		if (rubyzone == null)
			return;

		MainFragment f;

		switch (navPosition) {
		case 0:
			f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_intro);
			break;
		case 1:
			f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_collect);
			break;
		case 2:
			f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_use);
			break;
		case 3:
			f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_more);
			break;
		default:
			f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_main_container_intro);
			break;
		}

		if (f != null)
			f.loadContents(rubyzone.getId());
	}

	private void initLocationTracking() {
		// start location tracking
		Intent intent = new Intent(RubyApplication.getInstance(), TrackingService.class);
		startService(intent);
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
		boolean enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled && Settings.isAutoCheck(this))
			DialogUtils.showEnableLocationDialog(this);

		return enabled;
	}

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
				Rubyzone rubyzone = new Rubyzone();
				double distance = Double.MAX_VALUE;
				boolean inZone = false;

				for (Rubyzone can : res) {
					double d = CalUtils.calDistance(lat, lng, can.getLat(), can.getLng());
					if (d < distance && !can.getId().equals(1l)) {
						rubyzone = can;
						distance = d;
					}

					if (d < can.getRange())
						inZone = true;
				}

				MainActivity.this.rubyzone = rubyzone;
				setTitle();
				initChildContents();

				// start beacon scanning if in zone and auto check is on
				if (inZone && Settings.isAutoCheck(MainActivity.this))
					initBeaconScanning();
			}

		});
	}

	private void initBeaconScanning() {
		ScanningManager.turnOnBluetooth();
		ScanningManager.initScanningListener(RubyApplication.getInstance());
	}

}
