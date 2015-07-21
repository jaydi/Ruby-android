package com.jaydi.ruby;

import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Coupon;
import com.appspot.ruby_mine.rubymine.model.Gem;
import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.GemParcel;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.ResourceUtils;
import com.jaydi.ruby.utils.ToastUtils;

public class GemActivity extends BaseActivity {
	public static final String EXTRA_GEM = "com.jaydi.ruby.extras.GEM";

	private Gem gem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gem);
		viewId = 320;

		if (savedInstanceState == null) {
			GemParcel gemParcel = getIntent().getParcelableExtra(EXTRA_GEM);
			gem = gemParcel.toGem();
		}

		setRubyInfo();
		showGem();
	}

	private void setRubyInfo() {
		TextView textGuide = (TextView) findViewById(R.id.text_gem_guide);
		textGuide.setText(String.format(ResourceUtils.getString(R.string.ruby_info_guide_1), LocalUser.getUser().getName()));

		TextView textRuby = (TextView) findViewById(R.id.text_gem_ruby);
		textRuby.setText(" " + LocalUser.getUser().getRuby().intValue() + ResourceUtils.getString(R.string.ruby_scale));
	}

	private void showGem() {
		ImageView imageImage = (ImageView) findViewById(R.id.image_gem_image);
		NetworkInter.getImage(null, imageImage, gem.getImageKey(), 360, 240);

		TextView textName = (TextView) findViewById(R.id.text_gem_name);
		textName.setText("[" + gem.getRubymineName() + "] " + gem.getName());

		TextView textValue = (TextView) findViewById(R.id.text_gem_value);
		textValue.setText("" + gem.getValue());
		
		TextView textDesc = (TextView) findViewById(R.id.text_gem_desc);
		textDesc.setText("" + gem.getDesc());
	}

	public void goToRubymine(View view) {
		Intent intent = new Intent(this, RubymineActivity.class);
		intent.putExtra(RubymineActivity.EXTRA_RUBYMINE_ID, gem.getRubymineId());
		startActivity(intent);
	}

	public void buyGem(View view) {
		if (LocalUser.getUser().getRuby() >= gem.getValue())
			DialogUtils.showBuyGemDialog(this, new DialogUtils.DialogConfirmListener() {

				@Override
				public void onClickConfirm() {
					redeemCoupon();
				}
			});
		else
			DialogUtils.showNeedRubyDialog(this);
	}

	private void redeemCoupon() {
		Coupon coupon = new Coupon();
		coupon.setUserId(LocalUser.getUser().getId());
		coupon.setGemId(gem.getId());
		coupon.setName(gem.getName());
		coupon.setRubymineName(gem.getRubymineName());
		coupon.setImageKey(gem.getImageKey());
		coupon.setDesc(gem.getDesc());
		coupon.setExpirationDate(getExpirationDate());

		NetworkInter.redeemCoupon(new ResponseHandler<User>(DialogUtils.showWaitingDialog(this)) {

			@Override
			protected void onResponse(User res) {
				if (res == null)
					return;

				LocalUser.setUser(res);
				ToastUtils.showBoughtGem(GemActivity.this);
			}

		}, coupon);
	}

	private long getExpirationDate() {
		long et = new Date().getTime() + 1000 * 60 * 60 * 24 * 60; // 60 days
		return et;
	}

}
