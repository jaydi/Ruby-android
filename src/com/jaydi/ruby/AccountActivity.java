package com.jaydi.ruby;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.UserParcel;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.ResourceUtils;
import com.jaydi.ruby.utils.UserUtils;

public class AccountActivity extends BaseActivity {
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		viewId = 350;
	}

	@Override
	public void onResume() {
		super.onResume();
		// load and refresh user information on resume
		getUser();
	}

	private void getUser() {
		NetworkInter.getUser(new ResponseHandler<User>() {

			@Override
			protected void onResponse(User res) {
				if (res != null) {
					user = res;
					showUserInfo();
				}
			}

		}, LocalUser.getUser().getId());
	}

	private void showUserInfo() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(user.getBday());
		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH) + 1;
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);

		String strMonth = "00" + mMonth;
		strMonth = strMonth.substring(strMonth.length() - 2, strMonth.length());
		String strDay = "00" + mDay;
		strDay = strDay.substring(strDay.length() - 2, strDay.length());

		((TextView) findViewById(R.id.tv_account_birth)).setText(mYear + "." + strMonth + "." + strDay);
		((TextView) findViewById(R.id.tv_account_gender)).setText((user.getGender() == 1) ? ResourceUtils.getString(R.string.male) : ResourceUtils
				.getString(R.string.female));
		((TextView) findViewById(R.id.tv_account_phone)).setText(user.getPhone());
		((TextView) findViewById(R.id.tv_account_email)).setText(user.getEmail());

		if (user.getType().equals(UserUtils.TYPE_FACEBOOK))
			((TextView) findViewById(R.id.tv_account_facebook)).setText(R.string.connected);
		else if (user.getType().equals(UserUtils.TYPE_KAKAO))
			((TextView) findViewById(R.id.tv_account_kakao)).setText(R.string.connected);
	}

	public void goToEditProfile(View view) {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.EXTRA_FROM_EDIT, true);
		intent.putExtra(ProfileActivity.EXTRA_USER, new UserParcel(user));
		startActivity(intent);
	}
}
