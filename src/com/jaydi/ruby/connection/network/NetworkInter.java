package com.jaydi.ruby.connection.network;

import java.io.IOException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.ImageView;

import com.appspot.ruby_mine.rubymine.Rubymine;
import com.appspot.ruby_mine.rubymine.model.Coupon;
import com.appspot.ruby_mine.rubymine.model.Log;
import com.appspot.ruby_mine.rubymine.model.Receipt;
import com.appspot.ruby_mine.rubymine.model.Ruby;
import com.appspot.ruby_mine.rubymine.model.RubyCol;
import com.appspot.ruby_mine.rubymine.model.User;
import com.appspot.ruby_mine.rubymine.model.Userpair;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.jaydi.ruby.application.RubyApplication;
import com.jaydi.ruby.connection.DrawableResponseHandler;
import com.jaydi.ruby.models.ImageDrawable;
import com.jaydi.ruby.threading.ImageFileLoadWork;
import com.jaydi.ruby.threading.ImageLoadWork;
import com.jaydi.ruby.threading.ImagePreloadWork;
import com.jaydi.ruby.threading.ImageUploadWork;
import com.jaydi.ruby.threading.ThreadManager;
import com.jaydi.ruby.threading.Work;
import com.jaydi.ruby.utils.ResourceUtils;

public class NetworkInter {
	private static Rubymine service;

	static {
		Rubymine.Builder rBuilder = new Rubymine.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		service = rBuilder.build();
	}

	public static boolean isNetworkOnline() {
		boolean status = false;

		try {
			ConnectivityManager cm = (ConnectivityManager) RubyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return status;
	}

	public static void uploadImage(Handler handler, String path) {
		ThreadManager.execute(new ImageUploadWork(path), handler);
	}

	public static void preloadImage(String url, int width, int height) {
		ThreadManager.execute(new ImagePreloadWork(url, width, height), null);
	}

	public static void getImage(Handler handler, ImageView imageView, String url, int width, int height) {
		getImageCropping(handler, imageView, url, width, height, false);
	}

	public static void getImageCircular(Handler handler, ImageView imageView, String url, int width, int height) {
		getImageCropping(handler, imageView, url, width, height, true);
	}

	private static void getImageCropping(Handler handler, ImageView imageView, String url, int width, int height, boolean isCircular) {
		if (url == null || url.isEmpty())
			return;

		Work<ImageDrawable> work = null;
		if (url.startsWith("/"))
			work = new ImageFileLoadWork(url, width, height);
		else if (url.startsWith("http"))
			work = new ImageLoadWork(url, width, height);
		else
			work = new ImageLoadWork(ResourceUtils.getImageUrlFromKey(url), width, height);

		if (work != null)
			ThreadManager.execute(work, new DrawableResponseHandler(handler, imageView, isCircular));
	}

	public static <T> void insertUser(Handler handler, final User user) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().insert(user).execute();
			}

		}, handler);
	}

	public static <T> void getUser(Handler handler, final long id) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().get(id).execute();
			}

		}, handler);
	}

	public static <T> void updateUser(Handler handler, final User user) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().update(user).execute();
			}

		}, handler);
	}

	public static <T> void startUserFacebook(Handler handler, final User user) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().start().facebook(user).execute();
			}

		}, handler);
	}

	public static <T> void startUserKakao(Handler handler, final User user) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().start().kakao(user).execute();
			}

		}, handler);
	}

	public static <T> void startUserEmail(Handler handler, final User user) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().start().email(user).execute();
			}

		}, handler);
	}

	public static <T> void insertPhone(Handler handler, final User user) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().phone().insert(user).execute();
			}

		}, handler);
	}

	public static <T> void verifyPhone(Handler handler, final User user) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().phone().verify(user).execute();
			}

		}, handler);
	}

	public static <T> void searchUser(Handler handler, final long id, final String name) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.users().search(id, name).execute();
			}

		}, handler);
	}

	public static <T> void getUserpairs(Handler handler, final long userId) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.userpairs().list(userId).execute();
			}

		}, handler);
	}

	public static <T> void pairUsers(Handler handler, final Userpair userpair) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.userpairs().pair(userpair).execute();
			}

		}, handler);
	}

	public static <T> void depairUsers(Handler handler, final long id) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.userpairs().delete(id).execute();
			}

		}, handler);
	}

	public static <T> void getRubyzones(Handler handler) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.rubyzones().list().execute();
			}

		}, handler);
	}

	public static <T> void getRubymine(Handler handler, final long id) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.rubymines().get(id).execute();
			}

		}, handler);
	}

	public static <T> void getRubymines(Handler handler, final long rubyzoneId) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.rubymines().list(rubyzoneId).execute();
			}

		}, handler);
	}

	public static <T> void getMineInfo(Handler handler, final long id) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.mineinfos().get(id).execute();
			}

		}, handler);
	}

	public static <T> void getMineGems(Handler handler, final long rubymineId) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.gems().list().mine(rubymineId).execute();
			}

		}, handler);
	}

	public static <T> void getZoneGems(Handler handler, final long rubyzoneId) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.gems().list().zone(rubyzoneId).execute();
			}

		}, handler);
	}

	public static <T> void insertCoupon(Handler handler, final Coupon coupon) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.coupons().insert(coupon).execute();
			}

		}, handler);
	}

	public static <T> void getCoupons(Handler handler, final long userId) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.coupons().list(userId).execute();
			}

		}, handler);
	}

	public static <T> void useCoupon(Handler handler, final Coupon coupon) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.coupons().use(coupon).execute();
			}

		}, handler);
	}
	
	public static <T> void redeemCoupon(Handler handler, final Coupon coupon) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.coupons().redeem(coupon).execute();
			}

		}, handler);
	}

	public static <T> void mineRuby(Handler handler, final Ruby ruby) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.rubies().mine(ruby).execute();
			}

		}, handler);
	}

	public static <T> void claimRuby(Handler handler, final RubyCol rubyCol) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.rubies().claim(rubyCol).execute();
			}

		}, handler);
	}
	
	public static <T> void claimReceipt(Handler handler, final Receipt receipt) {
		ThreadManager.execute(new Work<T>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.receipts().claim(receipt).execute();
			}
			
		}, handler);
	}
	
	public static <T> void log(final Log log) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				return (T) service.logs().insert(log).execute();
			}
			
		}, null);
	}
}
