package com.jaydi.ruby;

import java.util.Arrays;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.appspot.ruby_mine.rubymine.model.User;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.UserUtils;
import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.MeResponseCallback;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.exception.KakaoException;

public class SignInActivity extends BaseActivity {
	// facebook
	private UiLifecycleHelper uiHelper;
	private StatusCallback facebookSessionStatusCallback = new StatusCallback() {

		@Override
		public void call(com.facebook.Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}

	};

	// kakao
	private final SessionCallback kakaoSessionCallback = new SessionCallback() {

		@Override
		public void onSessionOpened() {
			getKakaoUserData();
		}

		@Override
		public void onSessionClosed(KakaoException exception) {
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		viewId = 200; 

		// facebook
		com.facebook.widget.LoginButton buttonFacebook = (com.facebook.widget.LoginButton) findViewById(R.id.button_sign_in_facebook);
		buttonFacebook.setReadPermissions(Arrays.asList("email", "user_birthday"));
		uiHelper = new UiLifecycleHelper(this, facebookSessionStatusCallback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("Signin", "onResume");

		// facebook
		com.facebook.Session session = com.facebook.Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();

		// kakao
		if (com.kakao.Session.initializeSession(this, kakaoSessionCallback)) {
			// nothing to do
		} else if (com.kakao.Session.getCurrentSession().isOpened())
			getKakaoUserData();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("Signin", "onActivityResult");
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("Signin", "onPause");
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("Signin", "onDestroy");
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("Signin", "onSaveState");
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(com.facebook.Session session, SessionState state, Exception exception) {
		if (state.isOpened())
			getFacebookUserData(session);
	}

	private boolean requested;

	private void getFacebookUserData(final com.facebook.Session session) {
		if (!requested) {
			new Request(session, "/me", null, HttpMethod.GET, new Request.Callback() {

				@Override
				public void onCompleted(Response response) {
					GraphObject go = response.getGraphObject();
					String socialId = go.getProperty("id").toString();
					// String name = go.getProperty("first_name").toString();
					String imageKey = "http://graph.facebook.com/" + socialId + "/picture?style=small";
					String email = (go.getProperty("email") != null) ? go.getProperty("email").toString() : "";
					String bday = (go.getProperty("birthday") != null) ? go.getProperty("birthday").toString() : "";
					String gender = (go.getProperty("gender") != null) ? go.getProperty("gender").toString() : "";

					User user = new User();
					user.setSocialId(Long.valueOf(socialId));
					// user.setName(name);
					user.setEmail(email);
					user.setImageKey(imageKey);
					user.setBday(getBdayFromString(bday));
					user.setGender((!gender.isEmpty()) ? (gender.equals("male") ? UserUtils.GENDER_MALE : UserUtils.GENDER_FEMALE) : 0);
					user.setType(UserUtils.TYPE_FACEBOOK);

					startUserFacebook(session, user);
				}

			}).executeAsync();

			requested = true;
		}
	}

	protected Long getBdayFromString(String bday) {
		if (bday == null || bday.isEmpty())
			return 0l;

		String[] values = bday.split("/");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, Integer.valueOf(values[0]) - 1);
		c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(values[1]));
		c.set(Calendar.YEAR, Integer.valueOf(values[2]));
		return c.getTimeInMillis();
	}

	protected void startUserFacebook(com.facebook.Session session, User user) {
		session.closeAndClearTokenInformation();

		NetworkInter.startUserFacebook(new ResponseHandler<User>(DialogUtils.showWaitingDialog(this)) {

			@Override
			protected void onResponse(User res) {
				if (res == null)
					return;

				LocalUser.setUser(res);
				switch (UserUtils.validate(res)) {
				case 0:
					goToMain();
					break;
				case 1:
					goToPhone();
					break;
				case 2:
					goToProfile();
					break;
				}
			}

		}, user);
	}

	private void getKakaoUserData() {
		UserManagement.requestMe(new MeResponseCallback() {

			@Override
			protected void onSuccess(final UserProfile userProfile) {
				User user = new User();
				user.setSocialId(userProfile.getId());
				// user.setName(userProfile.getNickname());
				user.setImageKey(userProfile.getThumbnailImagePath());
				user.setType(UserUtils.TYPE_KAKAO);

				startUserKakao(user);
			}

			@Override
			protected void onNotSignedUp() {
			}

			@Override
			protected void onSessionClosedFailure(final APIErrorResult errorResult) {
			}

			@Override
			protected void onFailure(final APIErrorResult errorResult) {
			}

		});

	}

	protected void startUserKakao(User user) {
		UserManagement.requestLogout(new LogoutResponseCallback() {

			@Override
			protected void onSuccess(final long userId) {
			}

			@Override
			protected void onFailure(final APIErrorResult apiErrorResult) {
			}

		});

		NetworkInter.startUserKakao(new ResponseHandler<User>(DialogUtils.showWaitingDialog(this)) {

			@Override
			protected void onResponse(User res) {
				if (res == null)
					return;

				LocalUser.setUser(res);
				switch (UserUtils.validate(res)) {
				case 0:
					goToMain();
					break;
				case 1:
					goToPhone();
					break;
				case 2:
					goToProfile();
					break;
				}
			}

		}, user);
	}

	public void goToEmail(View view) {
		Intent intent = new Intent(this, EmailActivity.class);
		startActivity(intent);
	}

	protected void goToMain() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	protected void goToProfile() {
		Intent intent = new Intent(this, ProfileActivity.class);
		startActivity(intent);
		finish();
	}

	private void goToPhone() {
		Intent intent = new Intent(this, PhoneActivity.class);
		startActivity(intent);
		finish();
	}
}
