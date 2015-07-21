package com.jaydi.ruby;

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
import com.jaydi.ruby.utils.ToastUtils;

public class PhoneActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone);
		viewId = 230;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// LocalUser.setUser(new User());
	}

	public void getVerCode(View view) {
		EditText editPhone = (EditText) findViewById(R.id.edit_phone_phone);
		String phone = editPhone.getEditableText().toString();
		phone = formatPhoneNumber(phone);

		if (phone == null) {
			ToastUtils.show(R.string.wrong_phone_guide);
			return;
		}

		User user = LocalUser.getUser();
		user.setPhone(phone);

		NetworkInter.insertPhone(new ResponseHandler<User>(DialogUtils.showWaitingDialog(this)) {

			@Override
			protected void onError(int resultCode) {
				if (resultCode == BaseModel.DUP_PHONE)
					ToastUtils.show(R.string.dup_phone_guide);
			}

			@Override
			protected void onResponse(User res) {
				if (res == null)
					return;

				goToVerCode();
			}

		}, user);
	}

	private static String formatPhoneNumber(String s) {
		String r, r2;
		// �닽�옄留� �궓湲곌린
		r = s.replaceAll("\\D", "");

		// 泥リ��옄媛� 0�씠嫄곕굹 82濡� �떆�옉�븯怨�, 洹몃떎�쓬�씠 10, 11, 16, 18以� �븯�굹�씤媛�
		String regEx = "^(0|82)(10|11|16|18|19)(.{7,8})";
		if (!Pattern.matches(regEx, r))
			return null;

		// 82濡� �떆�옉�븯寃� 諛붽씀湲�
		r2 = r.replaceAll(regEx, "+82$2$3");
		return r2;
	}

	private void goToVerCode() {
		Intent intent = new Intent(this, VerCodeActivity.class);
		startActivity(intent);
		finish();
	}

	public void resetMain(View view) {
		LocalUser.setUser(new User());

		Intent intent = new Intent(this, SignInActivity.class);
		startActivity(intent);
		finish();
	}

}
