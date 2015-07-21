package com.jaydi.ruby.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.application.RubyApplication;

public class LocalUser {
	private static final String PREF_USER = "prefUser";
	private static final String PROPERTY_ID = "propertyId";
	private static final String PROPERTY_NAME = "propertyName";
	private static final String PROPERTY_IMAGE_KEY = "propertyImageKey";
	private static final String PROPERTY_LEVEL = "propertyLevel";
	private static final String PROPERTY_RUBY = "propertyRuby";

	private static User user;

	public static void setUser(User user) {
		LocalUser.user = user;
		saveUser();
	}

	public static User getUser() {
		if (user == null)
			loadUser();
		return user;
	}

	private static void saveUser() {
		SharedPreferences.Editor editor = getPref().edit();
		editor.putLong(PROPERTY_ID, (user.getId() == null) ? 0l : user.getId());
		editor.putString(PROPERTY_NAME, user.getName());
		editor.putString(PROPERTY_IMAGE_KEY, user.getImageKey());
		editor.putInt(PROPERTY_LEVEL, (user.getLevel() == null) ? 0 : user.getLevel());
		editor.putFloat(PROPERTY_RUBY, (user.getRuby() == null) ? 0 : user.getRuby());
		editor.commit();
	}

	private static void loadUser() {
		User user = new User();
		SharedPreferences pref = getPref();
		user.setId((pref.getLong(PROPERTY_ID, 0) != 0) ? pref.getLong(PROPERTY_ID, 0) : null);
		user.setName(pref.getString(PROPERTY_NAME, ""));
		user.setImageKey(pref.getString(PROPERTY_IMAGE_KEY, ""));
		user.setLevel(pref.getInt(PROPERTY_LEVEL, 0));
		user.setRuby(pref.getFloat(PROPERTY_RUBY, 0));
		LocalUser.user = user;
	}

	public static boolean getReady() {
		return getUser().getId() != null;
	}

	private static SharedPreferences getPref() {
		return RubyApplication.getInstance().getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
	}

}
