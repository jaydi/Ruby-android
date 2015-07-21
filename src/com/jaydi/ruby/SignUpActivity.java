package com.jaydi.ruby;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.StringUtils;
import com.jaydi.ruby.utils.ToastUtils;
import com.jaydi.ruby.utils.UserUtils;

public class SignUpActivity extends BaseActivity {
	private String email;
	private String password;
	private String passwordC;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		viewId = 220;
	}

	public void signUp(View view) {
		EditText editEmail = (EditText) findViewById(R.id.edit_sign_up_email);
		email = editEmail.getEditableText().toString();

		EditText editPassword = (EditText) findViewById(R.id.edit_sign_up_password);
		password = editPassword.getEditableText().toString();

		EditText editPasswordC = (EditText) findViewById(R.id.edit_sign_up_password_confirm);
		passwordC = editPasswordC.getEditableText().toString();

		if (email.isEmpty() || !validateEmail(email)) {
			ToastUtils.show(R.string.input_email_guide);
			return;
		}

		if (password.length() < 6) {
			ToastUtils.show(R.string.input_password_guide);
			return;
		}

		if (!password.equals(passwordC)) {
			ToastUtils.show(R.string.password_not_match_guide);
			return;
		}

		insertUser();
	}

	private boolean validateEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	private void insertUser() {
		User user = new User();
		user.setEmail(email);
		user.setPassword(StringUtils.encryptSHA256(password));
		user.setType(UserUtils.TYPE_EMAIL);

		NetworkInter.insertUser(new ResponseHandler<User>(DialogUtils.showWaitingDialog(this)) {

			@Override
			protected void onError(int resultCode) {
				if (resultCode == BaseModel.DUP_EMAIL)
					ToastUtils.show(R.string.dup_email_guide);
			}

			@Override
			protected void onResponse(User res) {
				if (res == null)
					return;

				LocalUser.setUser(res);
				goToPhone();
			}

		}, user);
	}

	private void goToPhone() {
		Intent intent = new Intent(this, PhoneActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

}
