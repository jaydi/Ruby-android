package com.jaydi.ruby.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;

public class BitmapUtils {
	public static int longSide = 720;
	public static int middleSide = 540;
	public static int shortSide = 360;

	public static Bitmap decodeStreamScaledDown(InputStream stream, int reqWidth, int reqHeight) {
		byte[] data = new byte[0];
		try {
			data = IOUtils.toByteArray(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	public static Bitmap decodeFileScaledDown(String path, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}

	private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		final int width = options.outWidth;
		final int height = options.outHeight;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = (heightRatio < widthRatio) ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap fixOrientation(String imagePath, Bitmap result) {
		int orientation = 1;

		try {
			ExifInterface exif = new ExifInterface(imagePath);
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			result = rotate(result, 90);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			result = rotate(result, 180);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			result = rotate(result, 270);
			break;
		}

		return result;
	}

	private static Bitmap rotate(Bitmap result, int rotation) {
		Matrix matrix = new Matrix();
		matrix.setRotate(rotation, (float) result.getWidth() / 2, (float) result.getHeight() / 2);

		return Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
	}

	public static Bitmap cropCircularView(Bitmap bitmap) {
		if (bitmap == null)
			return null;

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		float radius;

		if (bitmap.getWidth() > bitmap.getHeight())
			radius = bitmap.getHeight() / 2;
		else
			radius = bitmap.getWidth() / 2;

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
}
