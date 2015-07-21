package com.jaydi.ruby;

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

public class EmailActivity extends BaseActivity {
	private String email;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email);
		viewId = 210;
	}

	public void signIn(View view) {
		EditText editEmail = (EditText) findViewById(R.id.edit_email_email);
		email = editEmail.getEditableText().toString();

		EditText editPassword = (EditText) findViewById(R.id.edit_email_password);
		password = editPassword.getEditableText().toString();

		if (email.isEmpty() || password.isEmpty()) {
			ToastUtils.show(R.string.input_info_guide);
			return;
		}

		startUserEmail();
	}

	private void startUserEmail() {
		User user = new User();
		user.setEmail(email);
		user.setPassword(StringUtils.encryptSHA256(password));

		NetworkInter.startUserEmail(new ResponseHandler<User>(DialogUtils.showWaitingDialog(this)) {

			@Override
			protected void onError(int resultCode) {
				if (resultCode == BaseModel.NO_DATA)
					ToastUtils.show(R.string.no_email_guide);
			}

			@Override
			protected void onResponse(User res) {
				if (res == null)
					return;

				LocalUser.setUser(res);
				goToMain();
			}

		}, user);
	}

	private void goToMain() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	public void goToSignUp(View view) {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}
}
