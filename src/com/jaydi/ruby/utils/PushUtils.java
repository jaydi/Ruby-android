package com.jaydi.ruby.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.appspot.ruby_mine.rubymine.model.RubyCol;
import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.BaseActivity;
import com.jaydi.ruby.EventNoticeActivity;
import com.jaydi.ruby.MainActivity;
import com.jaydi.ruby.R;
import com.jaydi.ruby.RubyNoticeActivity;
import com.jaydi.ruby.application.RubyApplication;
import com.jaydi.ruby.models.Event;
import com.jaydi.ruby.models.RubyParcel;
import com.jaydi.ruby.models.RubymineParcel;
import com.jaydi.ruby.user.LocalUser;

public class PushUtils {

	public static void vibrate() {
		Vibrator vibrator = (Vibrator) RubyApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(600);
	}

	private static int getPhoneState(Context context) {
		int state = 0;
		if (isScreenOn(context))
			state++;
		if (isAppOnScreen(context))
			state++;
		return state;
	}

	private static boolean isScreenOn(Context context) {
		return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
	}

	private static boolean isAppOnScreen(Context context) {
		SharedPreferences pref = context.getSharedPreferences(RubyApplication.PREF_APP, Context.MODE_PRIVATE);
		return pref.getBoolean(RubyApplication.PROPERTY_ON_SCREEN, false);
	}

	public static void pushRuby(Context context, RubyCol rubyCol) {
		int state = getPhoneState(context);
		switch (state) {
		case 0:
			popupRuby(context, rubyCol);
			break;
		case 1:
			notifyRuby(context, rubyCol);
			break;
		case 2:
			popupRuby(context, rubyCol);
			break;
		}
	}

	public static void popupRuby(Context context, RubyCol rubyCol) {
		Intent intent = new Intent(context, RubyNoticeActivity.class);
		intent = setRubyColExtras(intent, rubyCol);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

		notifyRuby(context, rubyCol);

		vibrate();
	}

	public static void notifyRuby(Context context, RubyCol rubyCol) {
		Intent intent = new Intent(context, RubyNoticeActivity.class);
		intent = setRubyColExtras(intent, rubyCol);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setAutoCancel(true);
		builder.setSmallIcon(R.drawable.ic_logo);
		builder.setContentTitle(ResourceUtils.getString(R.string.app_name));
		builder.setContentText(String.format(ResourceUtils.getString(R.string.ruby_message), rubyCol.getPlanter().getName()));
		builder.setContentIntent(pendingIntent);

		NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.notify(rubyCol.getPlanter().getId().intValue(), builder.build());

		vibrate();
	}

	private static Intent setRubyColExtras(Intent intent, RubyCol rubyCol) {
		int rubiesSize = (rubyCol.getRubies() == null) ? 0 : rubyCol.getRubies().size();
		RubyParcel[] rubyParcels = new RubyParcel[rubiesSize];
		for (int i = 0; i < rubyParcels.length; i++)
			rubyParcels[i] = new RubyParcel(rubyCol.getRubies().get(i));

		int giversSize = (rubyCol.getGivers() == null) ? 0 : rubyCol.getGivers().size();
		RubymineParcel[] giverParcels = new RubymineParcel[giversSize];
		for (int i = 0; i < giverParcels.length; i++)
			giverParcels[i] = new RubymineParcel(rubyCol.getGivers().get(i));

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(RubyNoticeActivity.EXTRA_RUBIES, rubyParcels);
		intent.putExtra(RubyNoticeActivity.EXTRA_PLANTER, new RubymineParcel(rubyCol.getPlanter()));
		intent.putExtra(RubyNoticeActivity.EXTRA_GIVERS, giverParcels);

		return intent;
	}

	public static void toastRuby(Context context, RubyCol rubyCol) {
		BaseActivity activity = RubyApplication.getInstance().getOnScreenActivity();
		if (activity != null)
			activity.toast(String.format(ResourceUtils.getString(R.string.ruby_message), rubyCol.getPlanter().getName()));
		notifyRuby(context, rubyCol);

		vibrate();
	}

	public static void pushLevelChange(Context context, boolean change) {
		String msg = "";
		if (change)
			msg = ResourceUtils.getString(R.string.level_change_message_up);
		else
			msg = ResourceUtils.getString(R.string.level_change_message_down);

		int state = getPhoneState(context);
		if (state < 2)
			notifyLevelChange(context, msg);
		else
			toastLevelChange(context, msg);
	}

	private static void notifyLevelChange(Context context, String msg) {
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setAutoCancel(true);
		builder.setSmallIcon(R.drawable.ic_logo);
		builder.setContentTitle(ResourceUtils.getString(R.string.app_name));
		builder.setContentText(msg);
		builder.setContentIntent(pendingIntent);

		NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.notify(1, builder.build());

		vibrate();
	}

	private static void toastLevelChange(Context context, String msg) {
		BaseActivity activity = RubyApplication.getInstance().getOnScreenActivity();
		if (activity != null)
			activity.toast(msg);
		notifyLevelChange(context, msg);

		vibrate();
	}

	public static void pushEvent(Context context, Event event) {
		int state = getPhoneState(context);
		switch (state) {
		case 0:
			popupEvent(context, event);
			break;
		case 1:
			notifyEvent(context, event);
			break;
		case 2:
			toastEvent(context, event);
			break;
		}

		if (event.getRuby() > 0) {
			User user = LocalUser.getUser();
			user.setRuby(user.getRuby() + event.getRuby());
			LocalUser.setUser(user);
		}
	}

	private static void popupEvent(Context context, Event event) {
		Intent intent = new Intent(context, EventNoticeActivity.class);
		intent.putExtra(EventNoticeActivity.EXTRA_EVENT, event);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

		notifyEvent(context, event);

		vibrate();
	}

	private static void notifyEvent(Context context, Event event) {
		Intent intent = new Intent(context, EventNoticeActivity.class);
		intent.putExtra(EventNoticeActivity.EXTRA_EVENT, event);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setAutoCancel(true);
		builder.setSmallIcon(R.drawable.ic_logo);
		builder.setContentTitle(ResourceUtils.getString(R.string.app_name));
		builder.setContentText(event.getMessage());
		builder.setContentIntent(pendingIntent);

		NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.notify(2, builder.build());

		vibrate();
	}

	private static void toastEvent(Context context, Event event) {
		BaseActivity activity = RubyApplication.getInstance().getOnScreenActivity();
		if (activity != null)
			activity.dialogEvent(event);

		vibrate();
	}
}
