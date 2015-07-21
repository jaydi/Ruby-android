package com.jaydi.ruby.threading;

import java.io.IOException;

import uk.co.senab.bitmapcache.BitmapLruCache;

import com.jaydi.ruby.application.BitmapCache;

public class ImagePreloadWork extends Work<Object> {
	private String url;
	private int width;
	private int height;

	public ImagePreloadWork(String url, int width, int height) {
		super();
		this.url = url;
		this.width = width;
		this.height = height;
	}

	@Override
	public Object work() throws IOException {
		buildResizingUrl();
		BitmapLruCache cache = BitmapCache.getBitmapCache();
		if (!cache.contains(url))
			new ImageDownloadWork(url).work();

		return null;
	}

	private void buildResizingUrl() {
		StringBuilder sb = new StringBuilder(url);
		if (sb.indexOf("?") != -1)
			sb.append("&width=").append(width).append("&height=").append(height);
		url = sb.toString();
	}

}
