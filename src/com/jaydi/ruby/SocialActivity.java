package com.jaydi.ruby;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.appspot.ruby_mine.rubymine.model.User;
import com.appspot.ruby_mine.rubymine.model.UserCol;
import com.appspot.ruby_mine.rubymine.model.Userpair;
import com.appspot.ruby_mine.rubymine.model.UserpairCol;
import com.jaydi.ruby.adapters.UserAdapter;
import com.jaydi.ruby.adapters.UserAdapter.PairUserInter;
import com.jaydi.ruby.adapters.UserpairAdapter;
import com.jaydi.ruby.adapters.UserpairAdapter.DeletePairInter;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.Logger;
import com.jaydi.ruby.utils.ToastUtils;

public class SocialActivity extends BaseActivity implements PairUserInter, DeletePairInter {
	private int preNavPosition = -1;
	private int navPosition;

	private List<User> users;
	private ListView listUsers;
	private UserAdapter userAdapter;

	private List<Userpair> userpairs;
	private ListView listUserpairs;
	private UserpairAdapter userpairAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social);
		viewId = 330;

		users = new ArrayList<User>();
		listUsers = (ListView) findViewById(R.id.list_social_users);
		userAdapter = new UserAdapter(this, users, this);
		listUsers.setAdapter(userAdapter);

		userpairs = new ArrayList<Userpair>();
		listUserpairs = (ListView) findViewById(R.id.list_social_pairs);
		userpairAdapter = new UserpairAdapter(this, userpairs, this);
		listUserpairs.setAdapter(userpairAdapter);

		// prepare navigation menu and content fragments
		navPosition = 0;

		getPairs();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateNavMenu();
		changePage();
	}
	
	@Override
	public void onPause() {
		// Track fragment view off
		Logger.log(Logger.KIND_VIEW_OFF, viewId + navPosition + 1, 0, 0);
		preNavPosition = -1;
		super.onPause();
	}

	public void changeNav(View view) {
		switch (view.getId()) {
		case R.id.button_social_nav_search:
			navPosition = 0;
			break;
		case R.id.button_social_nav_pairs:
			navPosition = 1;
			break;
		default:
			navPosition = 0;
			break;
		}

		updateNavMenu();
		changePage();

		hideSoftKeyboard();
	}

	private void updateNavMenu() {
		findViewById(R.id.button_social_nav_search).setSelected(false);
		findViewById(R.id.button_social_nav_pairs).setSelected(false);

		switch (navPosition) {
		case 0:
			findViewById(R.id.button_social_nav_search).setSelected(true);
			break;
		case 1:
			findViewById(R.id.button_social_nav_pairs).setSelected(true);
			break;
		default:
			findViewById(R.id.button_social_nav_search).setSelected(true);
			break;
		}
	}

	private void changePage() {
		findViewById(R.id.linear_social_search).setVisibility(View.GONE);
		findViewById(R.id.list_social_pairs).setVisibility(View.GONE);
		
		// Track fragment view off
		if (preNavPosition != -1)
			Logger.log(Logger.KIND_VIEW_OFF, viewId + preNavPosition + 1, 0, 0);
		preNavPosition = navPosition;

		switch (navPosition) {
		case 0:
			findViewById(R.id.linear_social_search).setVisibility(View.VISIBLE);
			break;
		case 1:
			findViewById(R.id.list_social_pairs).setVisibility(View.VISIBLE);
			break;
		default:
			findViewById(R.id.linear_social_search).setVisibility(View.VISIBLE);
			break;
		}
		
		// Track fragment view on
		Logger.log(Logger.KIND_VIEW_ON, viewId + navPosition + 1, 0, 0);
	}

	public void searchUser(View view) {
		EditText editName = (EditText) findViewById(R.id.edit_social_name);
		String name = editName.getEditableText().toString();

		NetworkInter.searchUser(new ResponseHandler<UserCol>(DialogUtils.showWaitingDialog(this)) {

			@Override
			protected void onResponse(UserCol res) {
				users.clear();
				if (res != null && res.getUsers() != null)
					users.addAll(res.getUsers());
				userAdapter.notifyDataSetChanged();
			}

		}, LocalUser.getUser().getId(), name);

		hideSoftKeyboard();
	}

	@Override
	public void onPairUser(int position) {
		User target = users.get(position);
		Userpair userpair = new Userpair();
		userpair.setUserIdA(LocalUser.getUser().getId());
		userpair.setUserIdB(target.getId());
		userpair.setUserNameA(LocalUser.getUser().getName());
		userpair.setUserNameB(target.getName());
		userpair.setUserImageKeyA(LocalUser.getUser().getImageKey());
		userpair.setUserImageKeyB(target.getImageKey());
		NetworkInter.pairUsers(new ResponseHandler<Void>() {

			@Override
			protected void onResponse(Void res) {
				getPairs();
			}

		}, userpair);

		ToastUtils.show(R.string.paired);
	}

	private void getPairs() {
		NetworkInter.getUserpairs(new ResponseHandler<UserpairCol>() {

			@Override
			protected void onResponse(UserpairCol res) {
				userpairs.clear();
				if (res != null && res.getUserpairs() != null)
					userpairs.addAll(res.getUserpairs());
				userpairAdapter.notifyDataSetChanged();
			}

		}, LocalUser.getUser().getId());
	}

	@Override
	public void onDeletePair(final int position) {
		DialogUtils.showDeletePairDialog(this, new DialogUtils.DialogConfirmListener() {

			@Override
			public void onClickConfirm() {
				Userpair pair = userpairs.get(position);
				userpairs.remove(position);
				userpairAdapter.notifyDataSetChanged();

				NetworkInter.depairUsers(null, pair.getId());

				ToastUtils.show(R.string.depaired);
			}
		});
	}

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

}
