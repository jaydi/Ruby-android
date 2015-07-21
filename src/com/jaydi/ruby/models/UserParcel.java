package com.jaydi.ruby.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appspot.ruby_mine.rubymine.model.User;

public class UserParcel implements Parcelable {
	private long id;
	private String regId;
	private long socialId;
	private String name;
	private String phone;
	private String email;
	private String password;
	private String imageKey;
	private long bday;
	private int gender;
	private int level;
	private float ruby;
	private int type;
	private int state;
	private int verCode;
	private boolean isPaired;

	public UserParcel() {
		super();
	}

	public UserParcel(User user) {
		id = (user.getId() == null) ? 0l : user.getId();
		regId = user.getRegId();
		socialId = (user.getSocialId() == null) ? 0l : user.getSocialId();
		name = user.getName();
		phone = user.getPhone();
		email = user.getEmail();
		password = user.getPassword();
		imageKey = user.getImageKey();
		bday = (user.getBday() == null) ? 0l : user.getBday();
		;
		gender = (user.getGender() == null) ? 0 : user.getGender();
		level = (user.getLevel() == null) ? 0 : user.getLevel();
		ruby = (user.getRuby() == null) ? 0f : user.getRuby();
		type = (user.getType() == null) ? 0 : user.getType();
		state = (user.getState() == null) ? 0 : user.getState();
		verCode = (user.getVerCode() == null) ? 0 : user.getVerCode();
		isPaired = (user.getPaired() == null) ? false : user.getPaired();
	}

	public User toUser() {
		User user = new User();
		user.setId(id);
		user.setRegId(regId);
		user.setSocialId(socialId);
		user.setName(name);
		user.setPhone(phone);
		user.setEmail(email);
		user.setPassword(password);
		user.setImageKey(imageKey);
		user.setBday(bday);
		user.setGender(gender);
		user.setBday(bday);
		user.setGender(gender);
		user.setLevel(level);
		user.setRuby(ruby);
		user.setType(type);
		user.setState(state);
		user.setVerCode(verCode);
		user.setPaired(isPaired);
		return user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public Long getSocialId() {
		return socialId;
	}

	public void setSocialId(Long socialId) {
		this.socialId = socialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public long getBday() {
		return bday;
	}

	public void setBday(long bday) {
		this.bday = bday;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getRuby() {
		return ruby;
	}

	public void setRuby(float ruby) {
		this.ruby = ruby;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getVerCode() {
		return verCode;
	}

	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}

	public boolean isPaired() {
		return isPaired;
	}

	public void setPaired(boolean isPaired) {
		this.isPaired = isPaired;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(regId);
		dest.writeLong(socialId);
		dest.writeString(name);
		dest.writeString(phone);
		dest.writeString(email);
		dest.writeString(password);
		dest.writeString(imageKey);
		dest.writeLong(bday);
		dest.writeInt(gender);
		dest.writeInt(level);
		dest.writeFloat(ruby);
		dest.writeInt(type);
		dest.writeInt(state);
		dest.writeInt(verCode);
		boolean[] isPairedArray = new boolean[] { isPaired };
		dest.writeBooleanArray(isPairedArray);
	}

	public UserParcel(Parcel source) {
		id = source.readLong();
		regId = source.readString();
		socialId = source.readLong();
		name = source.readString();
		phone = source.readString();
		email = source.readString();
		password = source.readString();
		imageKey = source.readString();
		bday = source.readLong();
		gender = source.readInt();
		level = source.readInt();
		ruby = source.readFloat();
		type = source.readInt();
		state = source.readInt();
		verCode = source.readInt();
		boolean[] isPairedArray = new boolean[1];
		source.readBooleanArray(isPairedArray);
		isPaired = isPairedArray[0];
	}

	public static final Parcelable.Creator<UserParcel> CREATOR = new Parcelable.Creator<UserParcel>() {

		@Override
		public UserParcel createFromParcel(Parcel source) {
			return new UserParcel(source);
		}

		@Override
		public UserParcel[] newArray(int size) {
			return new UserParcel[size];
		}

	};

}
