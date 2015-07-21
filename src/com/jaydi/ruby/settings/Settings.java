package com.jaydi.ruby.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	public static void setDefault(Context context) {
		setAutoCheck(context, true);
	}

	public static void setAutoCheck(Context context, boolean isAutoCheck) {
		SharedPreferences pref = getPref(context);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("keyAutoCheck", isAutoCheck);
		editor.commit();
	}

	public static boolean isAutoCheck(Context context) {
		SharedPreferences pref = getPref(context);
		return pref.getBoolean("keyAutoCheck", true);
	}

	private static SharedPreferences getPref(Context context) {
		return context.getSharedPreferences("prefAppSettings", Context.MODE_PRIVATE);
	}
}
