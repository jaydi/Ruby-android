package com.jaydi.ruby.application;

import java.io.File;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.content.Context;
import android.os.Environment;

public class BitmapCache {
	private static final int DISK_CACHE_MAX_SIZE = 20 * 1024 * 1024; // 10Mb
	private static final String DISK_CACHE_SUBDIR = "rubyimages";

	private static BitmapLruCache cache;

	public static void init(Context context) {
		BitmapLruCache.Builder builder = new BitmapLruCache.Builder(context);
		builder = builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize(40f);
		builder = builder.setDiskCacheEnabled(true).setDiskCacheLocation(getCacheLocation(context)).setDiskCacheMaxSize(DISK_CACHE_MAX_SIZE);
		builder = builder.setRecyclePolicy(BitmapLruCache.RecyclePolicy.ALWAYS);
		cache = builder.build();
	}

	private static File getCacheLocation(Context context) {
		File cacheLocation;

		// If we have external storage use it for the disk cache. Otherwise we use
		// the cache dir
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			cacheLocation = new File(Environment.getExternalStorageDirectory() + File.separator + DISK_CACHE_SUBDIR);
		else
			cacheLocation = new File(context.getCacheDir().getPath() + File.separator + DISK_CACHE_SUBDIR);

		cacheLocation.mkdirs();

		return cacheLocation;
	}

	public static BitmapLruCache getBitmapCache() {
		return cache;
	}
}
