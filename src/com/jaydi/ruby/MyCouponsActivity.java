package com.jaydi.ruby;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.appspot.ruby_mine.rubymine.model.Coupon;
import com.appspot.ruby_mine.rubymine.model.CouponCol;
import com.jaydi.ruby.adapters.CouponAdapter;
import com.jaydi.ruby.adapters.CouponAdapter.UseCouponInter;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.DialogUtils;
import com.jaydi.ruby.utils.Logger;

public class MyCouponsActivity extends BaseActivity implements UseCouponInter {
	public static final String EXTRA_USED_COUPON_ID = "com.jaydi.ruby.extras.USED_COUPON_ID";

	private List<Coupon> coupons;
	private ListView listCoupons;
	private CouponAdapter couponAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_coupons);
		viewId = 340;

		coupons = new ArrayList<Coupon>();
		listCoupons = (ListView) findViewById(R.id.list_my_coupons_coupons);
		couponAdapter = new CouponAdapter(this, coupons, this);
		listCoupons.setAdapter(couponAdapter);

		getCoupons();
	}

	private void getCoupons() {
		NetworkInter.getCoupons(new ResponseHandler<CouponCol>() {

			@Override
			protected void onResponse(CouponCol res) {
				hideProgress();
				if (res == null)
					return;

				coupons.clear();
				if (res.getCoupons() != null)
					coupons.addAll(res.getCoupons());
				refresh();
			}

		}, LocalUser.getUser().getId());
	}

	private void hideProgress() {
		findViewById(R.id.progressbar_my_coupons_loading).setVisibility(View.GONE);
	}

	private void refresh() {
		couponAdapter.notifyDataSetChanged();
	}

	@Override
	public void onUseCoupon(final Coupon coupon) {
		DialogUtils.showUseCouponDialog(this, new DialogUtils.DialogConfirmListener() {

			@Override
			public void onClickConfirm() {
				useCoupon(coupon);
			}

		});
	}

	private void useCoupon(Coupon coupon) {
		NetworkInter.useCoupon(null, coupon);

		for (Coupon result : coupons)
			if (result.getId().equals(coupon.getId()))
				result.setState(Coupon.STATE_USED);

		refresh();

		// Track coupon use
		Logger.log(Logger.KIND_COUPON_USE, coupon.getId(), 0, 0);
	}

}
