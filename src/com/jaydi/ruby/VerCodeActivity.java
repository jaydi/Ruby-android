package com.jaydi.ruby;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.models.UserParcel;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.ToastUtils;

public class VerCodeActivity extends BaseActivity {
	private Dialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ver_code);
		viewId = 240;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//LocalUser.setUser(new User());
	}

	public void confirmVerCode(View view) {
		progress = DialogUtils.showWaitingDialog(this);

		EditText editVerCode = (EditText) findViewById(R.id.edit_ver_code_code);
		String verCode = editVerCode.getEditableText().toString();

		User user = LocalUser.getUser();
		user.setVerCode(Integer.valueOf(verCode));

		NetworkInter.verifyPhone(new ResponseHandler<User>() {
			
			@Override
			protected void onError(int resultCode) {
				if (progress != null && progress.isShowing())
					progress.dismiss();
				
				if (resultCode == BaseModel.WRONG_VER_CODE)
					ToastUtils.show(R.string.wrong_ver_code_guide);
			}

			@Override
			protected void onResponse(User res) {
				if (res == null)
					return;

				getUserInfo();
			}

		}, user);
	}

	private void getUserInfo() {
		NetworkInter.getUser(new ResponseHandler<User>() {

			@Override
			protected void onResponse(User res) {
				goToProfile(res);
			}

		}, LocalUser.getUser().getId());
	}

	private void goToProfile(User user) {
		if (progress != null && progress.isShowing())
			progress.dismiss();

		Intent intent = new Intent(this, ProfileActivity.class);
		if (user != null)
			intent.putExtra(ProfileActivity.EXTRA_USER, new UserParcel(user));
		startActivity(intent);
		finish();
	}

}
