package com.jaydi.ruby.utils;

import android.graphics.drawable.Drawable;

import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.R;

public class UserUtils {
	public static final int GENDER_MALE = 1;
	public static final int GENDER_FEMALE = 2;

	public static final int TYPE_EMAIL = 1;
	public static final int TYPE_FACEBOOK = 2;
	public static final int TYPE_KAKAO = 3;

	public static final int STATE_ALIVE = 0;
	public static final int STATE_DEAD = 1;

	public static final int LEVEL_ROOKIE = 0;
	public static final int LEVEL_BRONZE = 1;
	public static final int LEVEL_SILVER = 2;
	public static final int LEVEL_GOLD = 3;

	public static int validate(User user) {
		if (user.getPhone() == null || user.getPhone().isEmpty())
			return 1;

		if (user.getImageKey() == null || user.getImageKey().isEmpty())
			return 2;

		if (user.getName() == null || user.getName().isEmpty())
			return 2;

		if (user.getBday() == null || user.getBday().equals(0l))
			return 2;

		if (user.getGender() == null || user.getGender().equals(0l))
			return 2;

		return 0;
	}

	public static Drawable getLevelImageDrawable(int level) {
		switch (level) {
		case 0:
			return ResourceUtils.getDrawable(R.drawable.ic_level_rookie);
		case 1:
			return ResourceUtils.getDrawable(R.drawable.ic_level_ruby);
		default:
			return ResourceUtils.getDrawable(R.drawable.ic_level_rookie);
		}
	}

	public static String getLevelName(Integer level) {
		switch (level) {
		case LEVEL_ROOKIE:
			return ResourceUtils.getString(R.string.level_rookie);
		case LEVEL_BRONZE:
			return ResourceUtils.getString(R.string.level_bronze);
		case LEVEL_SILVER:
			return ResourceUtils.getString(R.string.level_silver);
		case LEVEL_GOLD:
			return ResourceUtils.getString(R.string.level_gold);
		default:
			return ResourceUtils.getString(R.string.level_gold);
		}
	}

	public static String getLevelGuide(Integer level) {
		switch (level) {
		case LEVEL_ROOKIE:
			return ResourceUtils.getString(R.string.level_guide_rookie);
		case LEVEL_BRONZE:
			return ResourceUtils.getString(R.string.level_guide_bronze);
		case LEVEL_SILVER:
			return ResourceUtils.getString(R.string.level_guide_silver);
		case LEVEL_GOLD:
			return ResourceUtils.getString(R.string.level_guide_gold);
		default:
			return ResourceUtils.getString(R.string.level_guide_gold);
		}
	}

	public int getLimit(int level) {
		switch (level) {
		case LEVEL_ROOKIE:
			return 0;
		case LEVEL_BRONZE:
			return 10;
		case LEVEL_SILVER:
			return 25;
		case LEVEL_GOLD:
			return 100;
		default:
			return 100;
		}
	}

	public static int getLevelMax(int level) {
		switch (level) {
		case LEVEL_ROOKIE:
			return 10;
		case LEVEL_BRONZE:
			return 25;
		case LEVEL_SILVER:
			return 100;
		case LEVEL_GOLD:
			return 100;
		default:
			return 100;
		}
	}

	public static int getLevelRuby(int level) {
		switch (level) {
		case LEVEL_ROOKIE:
			return 1;
		case LEVEL_BRONZE:
			return 2;
		case LEVEL_SILVER:
			return 3;
		case LEVEL_GOLD:
			return 5;
		default:
			return 1;
		}
	}

}
