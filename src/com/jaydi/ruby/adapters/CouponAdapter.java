package com.jaydi.ruby.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Coupon;
import com.jaydi.ruby.R;
import com.jaydi.ruby.connection.network.NetworkInter;

public class CouponAdapter extends BaseAdapter {
	private Context context;
	private List<Coupon> coupons;
	private UseCouponInter useCouponInter;

	public interface UseCouponInter {
		abstract void onUseCoupon(Coupon coupon);
	}

	public CouponAdapter(Context context, List<Coupon> coupons, UseCouponInter useCouponInter) {
		super();
		this.context = context;
		this.coupons = coupons;
		this.useCouponInter = useCouponInter;
	}

	@Override
	public int getCount() {
		return coupons.size();
	}

	@Override
	public Object getItem(int position) {
		return coupons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return coupons.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Coupon coupon = coupons.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.adapted_coupon_layout, parent, false);

		ImageView imageImage = (ImageView) view.findViewById(R.id.image_adapted_coupon_image);
		NetworkInter.getImage(null, imageImage, coupon.getImageKey(), 90, 90);

		TextView textName = (TextView) view.findViewById(R.id.text_adapted_coupon_name);
		textName.setText("[" + coupon.getRubymineName() + "] " + coupon.getName());

		if (coupon.getState().equals(Coupon.STATE_UNUSED)) {
			Button buttonUse = (Button) view.findViewById(R.id.button_adapted_coupon_use);
			buttonUse.setVisibility(View.VISIBLE);
			buttonUse.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					view.findViewById(R.id.button_adapted_coupon_use).setVisibility(View.GONE);
					view.findViewById(R.id.button_adapted_coupon_cancel).setVisibility(View.VISIBLE);
					view.findViewById(R.id.view_adapted_coupon_line).setVisibility(View.VISIBLE);
					view.findViewById(R.id.linear_adpated_coupon_use).setVisibility(View.VISIBLE);
				}

			});

			Button buttonCancel = (Button) view.findViewById(R.id.button_adapted_coupon_cancel);
			buttonCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					view.findViewById(R.id.button_adapted_coupon_cancel).setVisibility(View.GONE);
					view.findViewById(R.id.view_adapted_coupon_line).setVisibility(View.GONE);
					view.findViewById(R.id.linear_adpated_coupon_use).setVisibility(View.GONE);
					view.findViewById(R.id.button_adapted_coupon_use).setVisibility(View.VISIBLE);
				}

			});

			Button buttonConfirm = (Button) view.findViewById(R.id.button_adapted_coupon_confirm);
			buttonConfirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					useCouponInter.onUseCoupon(coupon);
				}

			});
		} else if (coupon.getState().equals(Coupon.STATE_EXPIRED))
			view.findViewById(R.id.text_adapted_coupon_expired).setVisibility(View.VISIBLE);
		else if (coupon.getState().equals(Coupon.STATE_USED))
			view.findViewById(R.id.text_adapted_coupon_used).setVisibility(View.VISIBLE);

		return view;
	}

}
