package com.jaydi.ruby.beacon.scanning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScanningCommandReceiver extends BroadcastReceiver {
	public static final String ACTION_STOP_FLUSHING = "com.jaydi.ruby.intent.action.STOP_FLUSHING";
	public static final String ACTION_RESTART_DELAYED = "com.jaydi.ruby.intent.action.RESTART_DELAYED";
	public static final String ACTION_START_SCANNING = "com.jaydi.ruby.intent.action.START_SCANNING";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_STOP_FLUSHING))
			ScanningManager.finishFlush(context);

		if (intent.getAction().equals(ACTION_RESTART_DELAYED))
			ScanningManager.restartDelayed(context);

		if (intent.getAction().equals(ACTION_START_SCANNING)) {
			ScanningManager.turnOnBluetooth();
			ScanningManager.initScanningListener(context);
		}
	}

}