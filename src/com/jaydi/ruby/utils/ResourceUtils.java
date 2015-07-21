package com.jaydi.ruby.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.jaydi.ruby.application.RubyApplication;

public class ResourceUtils {
	public static String getString(int resId) {
		return RubyApplication.getInstance().getResources().getString(resId);
	}

	public static Drawable getDrawable(int resId) {
		return RubyApplication.getInstance().getResources().getDrawable(resId);
	}

	public static int convertDpToPixel(int dpValue) {
		Resources r = RubyApplication.getInstance().getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
	}

	public static String getImageUrlFromKey(String key) {
		if (key == null || key.isEmpty())
			return "";
		if (key.startsWith("http"))
			return key;
		else
			return "https://ruby-mine.appspot.com/image?blob-key=" + key;
	}
}
