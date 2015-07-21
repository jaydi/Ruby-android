package com.jaydi.ruby.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appspot.ruby_mine.rubymine.model.Ruby;

public class RubyParcel implements Parcelable {
	private long id;
	private long userId;
	private long giverId;
	private long planterId;
	private float value;
	private long createdAt;
	private int event;
	
	public RubyParcel(Ruby ruby) {
		id = ruby.getId();
		userId = ruby.getUserId();
		giverId = ruby.getGiverId();
		planterId = ruby.getPlanterId();
		value = ruby.getValue();
		createdAt = ruby.getCreatedAt();
		event = ruby.getEvent();
	}

	public Ruby toRuby() {
		Ruby ruby = new Ruby();
		ruby.setId(id);
		ruby.setUserId(userId);
		ruby.setGiverId(giverId);
		ruby.setPlanterId(planterId);
		ruby.setValue(value);
		ruby.setCreatedAt(createdAt);
		ruby.setEvent(event);
		return ruby;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(userId);
		dest.writeLong(giverId);
		dest.writeLong(planterId);
		dest.writeFloat(value);
		dest.writeLong(createdAt);
		dest.writeInt(event);
	}

	public RubyParcel(Parcel source) {
		super();
		id = source.readLong();
		userId = source.readLong();
		giverId = source.readLong();
		planterId = source.readLong();
		value = source.readFloat();
		createdAt = source.readLong();
		event = source.readInt();
	}

	public static final Parcelable.Creator<RubyParcel> CREATOR = new Parcelable.Creator<RubyParcel>() {

		@Override
		public RubyParcel createFromParcel(Parcel source) {
			return new RubyParcel(source);
		}

		@Override
		public RubyParcel[] newArray(int size) {
			return new RubyParcel[size];
		}

	};

}
