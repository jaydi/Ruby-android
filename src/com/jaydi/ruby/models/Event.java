package com.jaydi.ruby.models;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
	private long id;
	private long userId;
	private long targetId;
	private String message;
	private int type;
	private float ruby;

	public Event() {
		super();
	}

	public Event(Bundle extras) {
		super();
		id = Long.valueOf(extras.getString("id"));
		userId = Long.valueOf(extras.getString("userId"));
		if (extras.getString("targetId") != null)
			targetId = Long.valueOf(extras.getString("targetId"));
		try {
			message = URLDecoder.decode(extras.getString("message"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			message = "";
		}
		type = Integer.valueOf(extras.getString("type"));
		ruby = Float.valueOf(extras.getString("ruby"));
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTargetId() {
		return targetId;
	}

	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getRuby() {
		return ruby;
	}

	public void setRuby(float ruby) {
		this.ruby = ruby;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(userId);
		dest.writeLong(targetId);
		dest.writeString(message);
		dest.writeInt(type);
		dest.writeFloat(ruby);
	}

	public Event(Parcel source) {
		super();
		id = source.readLong();
		userId = source.readLong();
		targetId = source.readLong();
		message = source.readString();
		type = source.readInt();
		ruby = source.readFloat();
	}

	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

		@Override
		public Event createFromParcel(Parcel source) {
			return new Event(source);
		}

		@Override
		public Event[] newArray(int size) {
			return new Event[size];
		}
	};

}
