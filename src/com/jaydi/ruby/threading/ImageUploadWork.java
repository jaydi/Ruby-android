package com.jaydi.ruby.threading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;

import com.jaydi.ruby.utils.BitmapUtils;

public class ImageUploadWork extends Work<String> {
	private final String uploadUrlRequestUrl = "https://ruby-mine.appspot.com/image";
	private String path;

	public ImageUploadWork(String path) {
		super();
		this.path = path;
	}

	@Override
	public String work() throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet uploadUrlRequest;
		HttpPost uploadRequest;
		HttpResponse response;

		try {
			uploadUrlRequest = new HttpGet(uploadUrlRequestUrl);
			response = client.execute(uploadUrlRequest);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("response not ok: " + response.getStatusLine().getStatusCode());
				return null;
			}

			HttpEntity resultEntity = response.getEntity();
			if (resultEntity == null)
				return "";

			InputStream is = resultEntity.getContent();
			String uploadUrl = IOUtils.toString(is);

			Bitmap bitmap = BitmapUtils.decodeFileScaledDown(path, 300, 300);
			bitmap = BitmapUtils.fixOrientation(path, bitmap);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
			byte[] byteArray = stream.toByteArray();

			File file = new File("/sdcard/tempfile.jpeg");
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(byteArray);
			fos.close();

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			FileBody fileBody = new FileBody(file, "application/octet-stream");
			multipartEntity.addPart("image", fileBody);

			uploadRequest = new HttpPost(uploadUrl);
			uploadRequest.setEntity(multipartEntity);
			response = client.execute(uploadRequest);

			file.delete();

			statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("response not ok: " + response.getStatusLine().getStatusCode());
				return null;
			}

			resultEntity = response.getEntity();
			if (resultEntity == null)
				return "";

			is = resultEntity.getContent();
			String imageKey = IOUtils.toString(is);

			return imageKey;

		} catch (IllegalArgumentException e) {
			return "";
		} catch (IllegalStateException e) {
			return "";
		}
	}
}
