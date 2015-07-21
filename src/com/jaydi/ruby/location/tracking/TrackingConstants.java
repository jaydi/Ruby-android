package com.jaydi.ruby.location.tracking;

public class TrackingConstants {
	public static final long UPDATE_INTERVAL = 10 * 1000;
	public static final long FASTEST_INTERVAL = 5 * 1000;
	public static final long RESTART_ALARM_INTERVAL = 30 * 60 * 1000;
	public static final String TRACK_FILE = "/sdcard/tracked_location.txt";

	private TrackingConstants() {
		throw new AssertionError();
	}
}
