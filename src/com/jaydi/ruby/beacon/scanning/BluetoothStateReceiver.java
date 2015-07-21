package com.jaydi.ruby.beacon.scanning;

import java.util.Date;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothStateReceiver extends BroadcastReceiver {
	private static final String TAG = "BSR";
	private static final long SUSPICIOUSLY_SHORT_BLUETOOTH_OFF_INTERVAL_MILLIS = 600l;

	private static long lastBluetoothOffTime = 0l;
	private static long lastBluetoothTurningOnTime = 0l;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			switch (state) {
			case BluetoothAdapter.ERROR:
				Log.d(TAG, "Bluetooth state is ERROR");
				break;
			case BluetoothAdapter.STATE_OFF:
				Log.d(TAG, "Bluetooth state is OFF");
				ScanningManager.termScanningListener();
				lastBluetoothOffTime = new Date().getTime();
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				Log.d(TAG, "Bluetooth state is TURNING_OFF");
				break;
			case BluetoothAdapter.STATE_ON:
				Log.d(TAG, "Bluetooth state is ON");
				Log.d(TAG, "Bluetooth was turned off for " + (lastBluetoothTurningOnTime - lastBluetoothOffTime) + " milliseconds");
				if (lastBluetoothTurningOnTime - lastBluetoothOffTime < SUSPICIOUSLY_SHORT_BLUETOOTH_OFF_INTERVAL_MILLIS) {
					Log.e(TAG, "Bluetooth crash detected");
					ScanningManager.startRecovery(context);
				}
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				lastBluetoothTurningOnTime = new Date().getTime();
				Log.d(TAG, "Bluetooth state is TURNING_ON");
				break;
			}
		}
	}

}
