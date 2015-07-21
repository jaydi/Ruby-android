package com.jaydi.ruby;

import android.support.v4.app.FragmentActivity;

import com.facebook.AppEventsLogger;
import com.google.android.gms.analytics.Tracker;
import com.jaydi.ruby.application.RubyApplication;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.Event;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.Logger;
import com.jaydi.ruby.utils.ToastUtils;

public class BaseActivity extends FragmentActivity {
	protected long viewId;
	protected Tracker tracker;
	
	@Override
	protected void onResume() {
		super.onResume();
		// report
		RubyApplication.getInstance().activityResumed(this);

		// check network
		if (!NetworkInter.isNetworkOnline())
			DialogUtils.networkAlert(this);

		// Logs 'install' and 'app activate' App Events.
		AppEventsLogger.activateApp(this);
		
		// set GA Tracker
		tracker = RubyApplication.getInstance().getTracker();
		
		// Track view on
		Logger.log(Logger.KIND_VIEW_ON, viewId, 0, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// report
		RubyApplication.getInstance().activityPaused(this);

		// Logs 'app deactivate' App Event.
		AppEventsLogger.deactivateApp(this);
		
		// Track view off
		Logger.log(Logger.KIND_VIEW_OFF, viewId, 0, 0);
	}

	public void toast(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ToastUtils.show(msg);
			}

		});
	}

	public void dialogEvent(final Event event) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				DialogUtils.showEventDialog(BaseActivity.this, event);
			}

		});
	}
}
