package com.jaydi.ruby.connection;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jaydi.ruby.R;
import com.jaydi.ruby.application.RubyApplication;
import com.jaydi.ruby.models.ImageDrawable;
import com.jaydi.ruby.utils.BitmapUtils;

public class DrawableResponseHandler extends ResponseHandler<ImageDrawable> {
	private Handler secondHandler;
	private ImageView imageView;
	private boolean isCircular;

	public DrawableResponseHandler(Handler secondHandler, ImageView imageView, boolean isCircular) {
		super();
		this.secondHandler = secondHandler;
		this.imageView = imageView;
		this.isCircular = isCircular;
	}

	@Override
	protected void onResponse(ImageDrawable res) {
		if (secondHandler != null)
			secondHandler.sendEmptyMessage(0);

		if (imageView == null || res == null)
			return;

		if (res.isFadeIn()) {
			Animation fadeIn = AnimationUtils.loadAnimation(RubyApplication.getInstance(), R.anim.fade_in);
			imageView.startAnimation(fadeIn);
		}

		if (isCircular) {
			Bitmap b = res.getDrawable().getBitmap();
			imageView.setImageBitmap(BitmapUtils.cropCircularView(b));
		} else
			imageView.setImageDrawable(res.getDrawable());
	}
}
