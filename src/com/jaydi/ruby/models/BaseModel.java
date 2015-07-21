package com.jaydi.ruby.models;

public class BaseModel {
	public static final int NO_DATA = 1;
	public static final int DUP_EMAIL = 101;
	public static final int DUP_PHONE = 102;
	public static final int DUP_NAME = 103;
	public static final int WRONG_VER_CODE = 104;
	public static final int NO_RUBY = 201;
	public static final int NO_GEM = 301;
	public static final int READ_RECEIPT = 401;
	public static final int SEVER_ERROR = 500;

	private int resultCode;

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

}
