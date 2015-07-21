package com.jaydi.ruby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.appspot.ruby_mine.rubymine.model.RubyzoneCol;
import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.application.RubyApplication;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.database.DatabaseInter;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.UserUtils;

public class LoadingActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		viewId = 100;
		
		getRubyzones();
		getUserReady();
	}

	private void getRubyzones() {
		NetworkInter.getRubyzones(new ResponseHandler<RubyzoneCol>() {

			@Override
			protected void onResponse(final RubyzoneCol rubyzoneCol) {
				DatabaseInter.deleteRubyzoneAll(RubyApplication.getInstance(), new ResponseHandler<Void>() {

					@Override
					protected void onResponse(Void res) {
						DatabaseInter.addRubyzones(RubyApplication.getInstance(), null, rubyzoneCol.getRubyzones());
					}

				});
			}

		});
	}

	private void getUserReady() {
		if (isFirstStart())
			goToGuideDelayed();
		else if (!LocalUser.getReady())
			goToSignInDelayed();
		else
			refreshUser();
	}

	private boolean isFirstStart() {
		SharedPreferences pref = getSharedPreferences("RubyApp", Context.MODE_PRIVATE);
		boolean isFirstStart = pref.getBoolean("isFirstStart", true);

		if (isFirstStart) {
			SharedPreferences.Editor editor = pref.edit();
			editor.putBoolean("isFirstStart", false);
			editor.commit();
		}

		return isFirstStart;
	}

	@SuppressLint("HandlerLeak")
	private void goToGuideDelayed() {
		new Handler() {

			@Override
			public void handleMessage(Message m) {
				Intent intent = new Intent(LoadingActivity.this, GuideActivity.class);
				startActivity(intent);
			}

		}.sendEmptyMessageDelayed(0, 1000);
	}

	@SuppressLint("HandlerLeak")
	private void goToSignInDelayed() {
		new Handler() {

			@Override
			public void handleMessage(Message m) {
				Intent intent = new Intent(LoadingActivity.this, SignInActivity.class);
				startActivity(intent);
			}

		}.sendEmptyMessageDelayed(0, 1000);
	}

	private void refreshUser() {
		NetworkInter.getUser(new ResponseHandler<User>() {

			@Override
			protected void onError(int resultCode) {
				goToSignInDelayed();
			}

			@Override
			protected void onResponse(User res) {
				if (res != null) {
					LocalUser.setUser(res);
					switch (UserUtils.validate(res)) {
					case 0:
						goToMainDelayed();
						break;
					case 1:
						goToPhone();
						break;
					case 2:
						goToProfile();
						break;
					}
				}
			}

		}, LocalUser.getUser().getId());
	}

	protected void goToPhone() {
		Intent intent = new Intent(this, PhoneActivity.class);
		startActivity(intent);
	}

	protected void goToProfile() {
		Intent intent = new Intent(this, ProfileActivity.class);
		startActivity(intent);
	}

	@SuppressLint("HandlerLeak")
	private void goToMainDelayed() {
		new Handler() {

			@Override
			public void handleMessage(Message m) {
				Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
				startActivity(intent);
			}

		}.sendEmptyMessageDelayed(0, 1000);
	}

}
