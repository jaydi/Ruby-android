package com.jaydi.ruby.gcm;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.ruby_mine.rubymine.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jaydi.ruby.application.RubyApplication;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;

public class GcmManager {
	private static final String SENDER_ID = "100213433938";
	private static final String PREF_GCM = "prefGcm";
	private static final String PROPERTY_REG_ID = "propRegId";
	private static final String PROPERTY_APP_VERSION = "propAppVer";

	public static void check(Context context) {
		if (checkPlayServices(context)) {
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
			String regId = getRegId(context);

			if (regId.isEmpty()) {
				registerInBackground(context, gcm);
			}
		}
	}

	private static boolean checkPlayServices(Context context) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (!GooglePlayServicesUtil.isUserRecoverableError(resultCode))
				Log.i("GCM", "This device is not supported.");
			return false;
		}
		return true;
	}

	private static void registerInBackground(final Context context, final GoogleCloudMessaging gcm) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String regId = "";
				try {
					regId = gcm.register(SENDER_ID);

				} catch (IOException e) {
				}

				return regId;
			}

			@Override
			protected void onPostExecute(String regId) {
				if (!regId.isEmpty()) {
					sendRegistrationIdToBackend(regId);
					saveRegistrationId(context, regId);
				}
			}

		}.execute();
	}

	private static void sendRegistrationIdToBackend(String regId) {
		User user = LocalUser.getUser();
		user.setRegId(regId);
		NetworkInter.updateUser(null, user);
	}

	private static void saveRegistrationId(Context context, String regId) {
		SharedPreferences prefs = getPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID + LocalUser.getUser().getId(), regId);
		editor.putInt(PROPERTY_APP_VERSION, RubyApplication.getAppVersion());
		editor.commit();
	}

	public static String getRegId(Context context) {
		SharedPreferences prefs = getPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID + LocalUser.getUser().getId(), "");
		if (registrationId.isEmpty())
			return "";

		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, 0);
		int currentVersion = RubyApplication.getAppVersion();
		if (registeredVersion != currentVersion)
			return "";

		return registrationId;
	}

	private static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREF_GCM, Context.MODE_PRIVATE);
	}

}
