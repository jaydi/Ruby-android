package com.jaydi.ruby.utils;

public class CalUtils {
	public static double calDistance(double latX, double lngX, double latY, double lngY) {
		double scale = 6400 * 1000 * (Math.PI / 180);
		double dX = scale * Math.abs((latX - latY));
		double dY = scale * Math.abs((lngX - lngY));
		return Math.sqrt(dX * dX + dY * dY);
	}
}
