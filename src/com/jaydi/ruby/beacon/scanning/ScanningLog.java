package com.jaydi.ruby.beacon.scanning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.altbeacon.beacon.Beacon;

import android.content.Context;
import android.util.Log;

public class ScanningLog {

	public static void log(Context context, String msg) {
		Log.i("SL", msg);
//		logFile(context, msg, ScanningConstants.TRACK_FILE);
	}

	public static void logBeacon(Context context, Beacon beacon) {
		String msg = beacon.getId2().toString() + " " + beacon.getId3().toString();
		Log.i("SL", msg);
	}

	public static void logFile(Context context, String msg, String fileName) {
		File file = new File(fileName);

		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.append(msg + " " + getDateString());
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getDateString() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
		return f.format(new Date());
	}

}
