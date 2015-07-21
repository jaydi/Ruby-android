package com.jaydi.ruby.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.jaydi.ruby.R;

public class StringUtils {
	public static final int MINE_TYPE_RESTAURANT = 1;
	public static final int MINE_TYPE_CAFE = 2;
	public static final int MINE_TYPE_BAR = 3;
	public static final int MINE_TYPE_ETC = 100;

	public static String encryptSHA256(String st) {
		if (st == null)
			return "";

		String encryptedData = "";
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			sha.update(st.getBytes());
			byte[] digest = sha.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++)
				sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
			encryptedData = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			encryptedData = null;
		}

		return encryptedData;
	}

	public static String getMineTypeString(int type) {
		switch (type) {
		case MINE_TYPE_ETC:
			return ResourceUtils.getString(R.string.mine_type_etc);
		case MINE_TYPE_RESTAURANT:
			return ResourceUtils.getString(R.string.mine_type_restaurant);
		case MINE_TYPE_CAFE:
			return ResourceUtils.getString(R.string.mine_type_cafe);
		case MINE_TYPE_BAR:
			return ResourceUtils.getString(R.string.mine_type_bar);
		default:
			return ResourceUtils.getString(R.string.mine_type_etc);
		}
	}

}
