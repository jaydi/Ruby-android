package com.jaydi.ruby;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Receipt;
import com.appspot.ruby_mine.rubymine.model.Ruby;
import com.appspot.ruby_mine.rubymine.model.RubyCol;
import com.appspot.ruby_mine.rubymine.model.User;
import com.appspot.ruby_mine.rubymine.model.Userpair;
import com.appspot.ruby_mine.rubymine.model.UserpairCol;
import com.google.zxing.client.android.CaptureActivity;
import com.jaydi.ruby.beacon.scanning.ScanningListener;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.location.LocationUpdateManager;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.Logger;
import com.jaydi.ruby.utils.PushUtils;
import com.jaydi.ruby.utils.ResourceUtils;
import com.jaydi.ruby.utils.ToastUtils;
import com.jaydi.ruby.utils.UserUtils;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

public class MainMoreFragment extends MainFragment {
	private static final int QRCODE_REQUEST = 101;
	private static final String ID = "9f.vc/r";
	private View view;

	private Dialog progress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main_more, container, false);
		setProfile();
		setMenuButtons();

		return view;
	}

	private void setProfile() {
		ImageView imageProfile = (ImageView) view.findViewById(R.id.image_more_profile);
		NetworkInter.getImageCircular(null, imageProfile, LocalUser.getUser().getImageKey(), 90, 90);

		TextView textName = (TextView) view.findViewById(R.id.text_more_name);
		textName.setText(LocalUser.getUser().getName());

		refreshLevel();

		view.findViewById(R.id.button_more_social).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SocialActivity.class);
				getActivity().startActivity(intent);
			}

		});
	}

	private void setMenuButtons() {
		view.findViewById(R.id.button_more_account).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AccountActivity.class);
				getActivity().startActivity(intent);
			}

		});

		view.findViewById(R.id.button_more_mycoupons).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyCouponsActivity.class);
				getActivity().startActivity(intent);
			}

		});

		view.findViewById(R.id.button_more_qrcode).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToQrcode();
			}

		});

		view.findViewById(R.id.button_more_invite).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendInvitationKakao();
			}

		});

		view.findViewById(R.id.button_more_settings).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				getActivity().startActivity(intent);
			}

		});
	}

	@Override
	public void initContents(long rubyzoneId) {
	}

	@Override
	public void loadContents(long rubyzoneId) {
		refreshLevel();
	}

	private void refreshLevel() {
		final User user = LocalUser.getUser();

		TextView textCurrentGuide = (TextView) view.findViewById(R.id.text_more_current_level_guide);
		textCurrentGuide.setText(UserUtils.getLevelGuide(user.getLevel()));

		TextView textGoalGuide = (TextView) view.findViewById(R.id.text_more_goal_level_guide);
		textGoalGuide.setText(UserUtils.getLevelGuide(user.getLevel() + 1));

		TextView textLevelCurrent = (TextView) view.findViewById(R.id.text_more_level_current);
		textLevelCurrent.setText(UserUtils.getLevelName(user.getLevel()));

		TextView textLevelGoal = (TextView) view.findViewById(R.id.text_more_level_goal);
		textLevelGoal.setText(UserUtils.getLevelName(user.getLevel() + 1));

		NetworkInter.getUserpairs(new ResponseHandler<UserpairCol>() {

			@Override
			protected void onResponse(UserpairCol res) {
				if (res == null)
					return;

				List<Userpair> userpairs = new ArrayList<Userpair>();
				if (res.getUserpairs() != null)
					userpairs.addAll(res.getUserpairs());

				int count = userpairs.size();
				int max = UserUtils.getLevelMax(user.getLevel());
				int rate = (100 * count) / max;

				TextView textLevelGuide = (TextView) view.findViewById(R.id.text_more_level_guide);
				textLevelGuide.setText(count + ResourceUtils.getString(R.string.human_scale) + " / " + max
						+ ResourceUtils.getString(R.string.human_scale));

				View viewFilled = view.findViewById(R.id.progress_more_filled);
				viewFilled.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, rate));
			}

		}, user.getId());
	}

	private void showWaitingDialog() {
		progress = DialogUtils.showWaitingDialog(getActivity());
	}

	private void hideWaitingDialog() {
		if (progress != null && progress.isShowing())
			progress.dismiss();
	}

	private void goToQrcode() {
		showWaitingDialog();
		Intent intent = new Intent(getActivity(), CaptureActivity.class);
		intent.setAction("com.google.zxing.client.android.SCAN");
		startActivityForResult(intent, QRCODE_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		hideWaitingDialog();
		if (resultCode == Activity.RESULT_OK && requestCode == QRCODE_REQUEST)
			verifyQrcode(intent.getStringExtra("SCAN_RESULT"));
	}

	private void verifyQrcode(String barcode) {
		showWaitingDialog();

		barcode = barcode.trim();
		System.out.println(barcode);
		
		if (!barcode.startsWith(ID)) {
			hideWaitingDialog();
			ToastUtils.show(R.string.invalid_qrcode);
			return;
		}
		
		if (barcode.contains("c=")) { // visit code
			int mineId = 0;
			try {
				mineId = Integer.valueOf(barcode.substring(barcode.length() - 4, barcode.length()));
				System.out.println("mineId: " + mineId);

			} catch (NumberFormatException e) {
				hideWaitingDialog();
				ToastUtils.show(R.string.invalid_qrcode);
				return;
			}

			if (!ScanningListener.isCachedBeacon(getActivity(), mineId))
				mineRuby(mineId);
			else {
				hideWaitingDialog();
				ToastUtils.show(R.string.already_read_qrcode);
				return;
			}
		} else if (barcode.contains("r=")) { // purchase code
			long receiptId = 0;
			try {
				receiptId =  Long.valueOf(barcode.substring(barcode.indexOf("=") + 1));
				
			} catch (NumberFormatException e) {
				hideWaitingDialog();
				ToastUtils.show(R.string.invalid_qrcode);
				return;
			}
			
			claimReceipt(receiptId);
			
		} else { // invalid code
			hideWaitingDialog();
			ToastUtils.show(R.string.invalid_qrcode);
			return;
		}
	}

	private void mineRuby(long rubymineId) {
		Ruby ruby = new Ruby();
		ruby.setId(0l);
		ruby.setGiverId(0l);
		ruby.setUserId(LocalUser.getUser().getId());
		ruby.setPlanterId(rubymineId);
		ruby.setEvent(2);

		NetworkInter.mineRuby(new ResponseHandler<RubyCol>() {

			@Override
			protected void onError(int resultCode) {
				hideWaitingDialog();
				if (resultCode == BaseModel.NO_RUBY)
					ToastUtils.show(R.string.no_ruby_guide);
				else if (resultCode == BaseModel.NO_DATA)
					ToastUtils.show(R.string.invalid_qrcode);
			}

			@Override
			protected void onResponse(RubyCol res) {
				if (res != null)
					verifyRegion(res);
			}

		}, ruby);
	}

	private void verifyRegion(RubyCol rubyCol) {
		hideWaitingDialog();
		if (LocationUpdateManager.isInZone(rubyCol.getPlanter().getRubyzoneId())) {
			ScanningListener.saveExpirationTime(getActivity(), rubyCol.getPlanter().getId().intValue());
			PushUtils.pushRuby(getActivity(), rubyCol);

			// Track visit event
			Logger.log(Logger.KIND_VISIT, rubyCol.getPlanter().getId(), 0, 2);
			// Track ruby got
			Logger.log(Logger.KIND_RUBY_GOT, rubyCol.getPlanter().getId(), 0, 2);
		} else
			ToastUtils.show(R.string.wrong_region_guide);
	}

	private void claimReceipt(final long receiptId) {
		Receipt receipt = new Receipt();
		receipt.setId(receiptId);
		receipt.setUserId(LocalUser.getUser().getId());
		NetworkInter.claimReceipt(new ResponseHandler<User>() {
			
			@Override
			protected void onError(int resultCode) {
				hideWaitingDialog();
				if (resultCode == BaseModel.READ_RECEIPT)
					ToastUtils.show("Already read receipt...");
				else if (resultCode == BaseModel.NO_DATA)
					ToastUtils.show("Broken receipt...");
			}

			@Override
			protected void onResponse(User res) {
				hideWaitingDialog();
				if (res == null)
					return;
				
				float ruby = res.getRuby() - LocalUser.getUser().getRuby();
				LocalUser.setUser(res);
				
				showRubyMessage((int) ruby);

				// Track purchase
				Logger.log(Logger.KIND_PURCHASE, receiptId, 0, 0);
			}
			
		}, receipt);
	}

	protected void showRubyMessage(int ruby) {
		String msg = String.format(ResourceUtils.getString(R.string.purchase_ruby_message), ruby);
		MainActivity activity = (MainActivity) getActivity();
		activity.setRubyInfo();
		DialogUtils.showRubyDialog(activity, msg);
	}

	protected void sendInvitationKakao() {
		try {
			KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
			KakaoTalkLinkMessageBuilder mBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

			mBuilder.addText(LocalUser.getUser().getName() + ResourceUtils.getString(R.string.invitation));
			// no pending action for here
			// Action action = new AppActionBuilder().setAndroidExecuteURLParam("action=shop&shopId=" + shop.getId())
			// .setIOSExecuteURLParam("action=shop&shopId=" + shop.getId()).build();
			// mBuilder.addAppButton(ResourceUtils.getString(R.string.move_to_app), action);
			mBuilder.addAppButton(ResourceUtils.getString(R.string.move_to_app));
			kakaoLink.sendMessage(mBuilder.build(), getActivity());
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}

}
