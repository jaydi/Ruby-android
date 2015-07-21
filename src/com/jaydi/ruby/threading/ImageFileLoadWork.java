package com.jaydi.ruby.threading;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.jaydi.ruby.application.RubyApplication;
import com.jaydi.ruby.models.ImageDrawable;
import com.jaydi.ruby.utils.BitmapUtils;

public class ImageFileLoadWork extends Work<ImageDrawable> {
	private String path;
	private int width;
	private int height;

	public ImageFileLoadWork(String path, int width, int height) {
		super();
		this.path = path;
		this.width = width;
		this.height = height;
	}

	@Override
	public ImageDrawable work() throws IOException {
		Bitmap bitmap = BitmapUtils.decodeFileScaledDown(path, width, height);
		bitmap = BitmapUtils.fixOrientation(path, bitmap);
		BitmapDrawable drawable = new BitmapDrawable(RubyApplication.getInstance().getResources(), bitmap);
		return new ImageDrawable(drawable, true);
	}
}
