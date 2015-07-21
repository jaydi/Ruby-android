package com.jaydi.ruby.utils;

import java.util.Date;

import com.appspot.ruby_mine.rubymine.model.Log;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;

public class Logger {
	public static final int KIND_VISIT = 100;
	public static final int KIND_RUBY_GOT = 106;
	public static final int KIND_RUBY = 101;
	public static final int KIND_AD_GOT = 102;
	public static final int KIND_AD_SEEN = 103;
	public static final int KIND_PURCHASE = 104;
	public static final int KIND_COUPON_USE = 105;

	public static final int KIND_VIEW_ON = 200;
	public static final int KIND_VIEW_OFF = 201;

	public static final int KIND_USER_PAIR = 300;
	public static final int KIND_USER_DEPAIR = 301;

	private static Log getEmptyLog() {
		Log log = new Log();
		log.setUserId(LocalUser.getUser().getId());
		log.setCreatedAt(new Date().getTime());
		return log;
	}

	private static void sendLog(Log log) {
		NetworkInter.log(log);
	}

	public static void log(int kind, long targetId, long refer, int type) {
		Log log = getEmptyLog();
		log.setKind(kind);
		log.setTargetId(targetId);
		log.setRefer(refer);
		log.setType(type);
		sendLog(log);
	}
}
