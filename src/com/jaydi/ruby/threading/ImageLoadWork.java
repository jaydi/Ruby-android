package com.jaydi.ruby.threading;

import java.io.IOException;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.graphics.drawable.BitmapDrawable;

import com.jaydi.ruby.application.BitmapCache;
import com.jaydi.ruby.models.ImageDrawable;

public class ImageLoadWork extends Work<ImageDrawable> {
	private String url;
	private int width;
	private int height;

	public ImageLoadWork(String url, int width, int height) {
		super();
		this.url = url;
		this.width = width;
		this.height = height;
	}

	@Override
	public ImageDrawable work() throws IOException {
		buildResizingUrl();
		BitmapLruCache cache = BitmapCache.getBitmapCache();
		BitmapDrawable drawable = cache.get(url);
		
		if (drawable != null)
			return new ImageDrawable(drawable, false);
		else
			return new ImageDownloadWork(url).work();
	}

	private void buildResizingUrl() {
		StringBuilder sb = new StringBuilder(url);
		if (sb.indexOf("?") != -1)
			sb.append("&width=").append(width).append("&height=").append(height);
		url = sb.toString();
	}

}
