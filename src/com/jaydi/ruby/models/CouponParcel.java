package com.jaydi.ruby.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appspot.ruby_mine.rubymine.model.Coupon;

public class CouponParcel implements Parcelable {
	private long id;
	private long userId;
	private String name;
	private String rubymineName;
	private String imageKey;
	private String desc;
	private long expirationDate;

	public CouponParcel(Coupon coupon) {
		super();
		id = coupon.getId();
		userId = coupon.getUserId();
		name = coupon.getName();
		rubymineName = coupon.getRubymineName();
		imageKey = coupon.getImageKey();
		desc = coupon.getDesc();
		expirationDate = coupon.getExpirationDate();
	}

	public Coupon toCoupon() {
		Coupon coupon = new Coupon();
		coupon.setId(id);
		coupon.setUserId(userId);
		coupon.setName(name);
		coupon.setRubymineName(rubymineName);
		coupon.setImageKey(imageKey);
		coupon.setDesc(desc);
		coupon.setExpirationDate(expirationDate);
		return coupon;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(userId);
		dest.writeString(name);
		dest.writeString(rubymineName);
		dest.writeString(imageKey);
		dest.writeString(desc);
		dest.writeLong(expirationDate);
	}

	public CouponParcel(Parcel source) {
		super();
		id = source.readLong();
		userId = source.readLong();
		name = source.readString();
		rubymineName = source.readString();
		imageKey = source.readString();
		desc = source.readString();
		expirationDate = source.readLong();
	}

	public static final Parcelable.Creator<CouponParcel> CREATOR = new Parcelable.Creator<CouponParcel>() {

		@Override
		public CouponParcel createFromParcel(Parcel source) {
			return new CouponParcel(source);
		}

		@Override
		public CouponParcel[] newArray(int size) {
			return new CouponParcel[size];
		}

	};
}
