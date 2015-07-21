package com.jaydi.ruby.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.appspot.ruby_mine.rubymine.model.Text;

public class RubymineParcel implements Parcelable {
	private long id;
	private long rubyzoneId;
	private String name;
	private String contents;
	private int type;
	private String typeString;
	private float ruby;
	private float value;

	public RubymineParcel(Rubymine rubymine) {
		super();
		id = rubymine.getId();
		rubyzoneId = rubymine.getRubyzoneId();
		name = rubymine.getName();
		contents = (rubymine.getContents() == null) ? null : rubymine.getContents().getValue();
		type = rubymine.getType();
		typeString = rubymine.getTypeString();
		ruby = rubymine.getRuby();
		value = rubymine.getValue();
	}

	public Rubymine toRubymine() {
		Rubymine rubymine = new Rubymine();
		rubymine.setId(id);
		rubymine.setRubyzoneId(rubyzoneId);
		rubymine.setName(name);
		Text textContents = new Text();
		textContents.setValue(contents);
		rubymine.setContents(textContents);
		rubymine.setType(type);
		rubymine.setTypeString(typeString);
		rubymine.setRuby(ruby);
		rubymine.setValue(value);
		return rubymine;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(rubyzoneId);
		dest.writeString(name);
		dest.writeString(contents);
		dest.writeInt(type);
		dest.writeString(typeString);
		dest.writeFloat(ruby);
		dest.writeFloat(value);
	}

	public RubymineParcel(Parcel source) {
		super();
		id = source.readLong();
		rubyzoneId = source.readLong();
		name = source.readString();
		contents = source.readString();
		type = source.readInt();
		typeString = source.readString();
		ruby = source.readFloat();
		value = source.readFloat();
	}

	public static final Parcelable.Creator<RubymineParcel> CREATOR = new Parcelable.Creator<RubymineParcel>() {

		@Override
		public RubymineParcel createFromParcel(Parcel source) {
			return new RubymineParcel(source);
		}

		@Override
		public RubymineParcel[] newArray(int size) {
			return new RubymineParcel[size];
		}

	};

}
