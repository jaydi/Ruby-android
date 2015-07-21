package com.jaydi.ruby.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaydi.ruby.R;
import com.jaydi.ruby.application.RubyApplication;

public class ToastUtils {

	public static void show(String msg) {
		final Context context = RubyApplication.getInstance().getApplicationContext();
		final Toast toast = new Toast(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView msgView = (TextView) inflater.inflate(R.layout.toast_simple_layout, null, false);
		msgView.setText(msg);

		toast.setView(msgView);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void show(int resId) {
		show(ResourceUtils.getString(resId));
	}

	public static void showBoughtGem(Context context) {
		final Toast toast = new Toast(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toast_bought_gem_layout, null, false);

		toast.setView(view);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
