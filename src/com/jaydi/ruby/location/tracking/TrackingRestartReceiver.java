package com.jaydi.ruby.location.tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TrackingRestartReceiver extends BroadcastReceiver {
	public static final String TAG = "LocationTrackingServiceManager";
	public static final String ACTION_RESTART_TRACKING = "com.jaydi.ruby.intent.action.RESTART_TRACKING";

	@Override
	public void onReceive(Context context, Intent intent) {
		// check if it is getting the right intent
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)
				|| intent.getAction().equals(ACTION_RESTART_TRACKING))
			startSticky(context);
		else
			Log.e(TAG, "Received unexpected intent: " + intent.toString());
	}

	private void startSticky(Context context) {
		if (!TrackingService.isOnRequest) {
			Intent intent = new Intent(context, TrackingService.class);
			context.startService(intent);
		}
	}

}
