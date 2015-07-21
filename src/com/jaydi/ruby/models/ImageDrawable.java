package com.jaydi.ruby.models;

import android.graphics.drawable.BitmapDrawable;

public class ImageDrawable {
	private BitmapDrawable drawable;
	private boolean fadeIn;

	public ImageDrawable(BitmapDrawable drawable, boolean fadeIn) {
		super();
		this.drawable = drawable;
		this.fadeIn = fadeIn;
	}

	public BitmapDrawable getDrawable() {
		return drawable;
	}

	public void setDrawable(BitmapDrawable drawable) {
		this.drawable = drawable;
	}

	public boolean isFadeIn() {
		return fadeIn;
	}

	public void setFadeIn(boolean fadeIn) {
		this.fadeIn = fadeIn;
	}

}
