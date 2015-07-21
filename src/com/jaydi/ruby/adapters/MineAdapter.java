package com.jaydi.ruby.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.MineInfo;
import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.jaydi.ruby.R;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.JsonUtils;
import com.jaydi.ruby.utils.ResourceUtils;
import com.jaydi.ruby.utils.UserUtils;

public class MineAdapter extends BaseAdapter {
	private Context context;
	private Rubymine rubymine;
	private List<String> contents;
	private MineInfo mineInfo;
	private View infoView;

	@SuppressWarnings("unchecked")
	public MineAdapter(Context context, Rubymine rubymine) {
		super();
		this.context = context;
		this.rubymine = rubymine;

		contents = new ArrayList<String>();
		if (rubymine.getContents() != null) {
			String jsonContents = rubymine.getContents().getValue();
			contents.addAll(JsonUtils.fromJson(jsonContents, ArrayList.class));
		}

		getMineInfo();
	}

	private void getMineInfo() {
		NetworkInter.getMineInfo(new ResponseHandler<MineInfo>() {

			@Override
			protected void onResponse(MineInfo res) {
				if (res != null) {
					mineInfo = res;
					setMineInfoView();
				}
			}

		}, rubymine.getId());
	}

	@Override
	public int getCount() {
		return contents.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position < contents.size())
			return contents.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = null;

		if (position < contents.size()) {
			String content = contents.get(position);
			if (position == 0)
				view = getHeaderView(inflater, parent, content);
			else if (position % 2 == 1)
				view = getTextView(inflater, parent, content);
			else {
				view = getImageView(inflater, parent, content);
				preloadImage(position);
			}
		} else
			view = getMineInfoView(inflater, parent);

		return view;
	}

	private View getHeaderView(LayoutInflater inflater, ViewGroup parent, String imageKey) {
		View view = inflater.inflate(R.layout.adapted_mine_contents_header, parent, false);

		ImageView imageImage = (ImageView) view.findViewById(R.id.image_adapted_mine_image);
		NetworkInter.getImage(null, imageImage, imageKey, 350, 350);

		TextView textName = (TextView) view.findViewById(R.id.text_adapted_mine_name);
		textName.setText(rubymine.getName());

		TextView textType = (TextView) view.findViewById(R.id.text_adapted_mine_type);
		textType.setText(rubymine.getTypeString());

		TextView textValue = (TextView) view.findViewById(R.id.text_adapted_mine_value);
		textValue.setText("" + UserUtils.getLevelRuby(LocalUser.getUser().getLevel()) + ResourceUtils.getString(R.string.ruby_scale));

		return view;
	}

	private View getTextView(LayoutInflater inflater, ViewGroup parent, String text) {
		if (text != null && !text.isEmpty()) {
			View view = inflater.inflate(R.layout.adapted_mine_contents_text_layout, parent, false);
			TextView textView = (TextView) view.findViewById(R.id.text_adapted_mine_contents_text);
			textView.setText(text);
			return view;
		} else
			return inflater.inflate(R.layout.dummy_layout, parent, false);
	}

	private View getImageView(LayoutInflater inflater, ViewGroup parent, String imageKey) {
		View view = inflater.inflate(R.layout.adapted_mine_contents_image_layout, parent, false);
		ImageView imageView = (ImageView) view.findViewById(R.id.image_adapted_mine_contents_image);
		NetworkInter.getImage(null, imageView, imageKey, 350, 350);
		return view;
	}

	private void preloadImage(int position) {
		position += 2;
		if (position < contents.size())
			NetworkInter.preloadImage(contents.get(position), 350, 350);
	}

	private View getMineInfoView(LayoutInflater inflater, ViewGroup parent) {
		infoView = inflater.inflate(R.layout.adapted_mine_contents_footer, parent, false);

		TextView textName = (TextView) infoView.findViewById(R.id.text_adapted_mine_name);
		textName.setText(rubymine.getName());

		TextView textType = (TextView) infoView.findViewById(R.id.text_adapted_mine_type);
		textType.setText(rubymine.getTypeString());

		if (mineInfo != null)
			setMineInfoView();

		return infoView;
	}

	private void setMineInfoView() {
		if (infoView == null)
			return;

		TextView textAddress = (TextView) infoView.findViewById(R.id.text_adapted_mine_address);
		textAddress.setText(mineInfo.getAddress());

		TextView textPhone = (TextView) infoView.findViewById(R.id.text_adapted_mine_phone);
		textPhone.setText(mineInfo.getPhone());

		TextView textTime = (TextView) infoView.findViewById(R.id.text_adapted_mine_time);
		textTime.setText(mineInfo.getTime());

		TextView textMenu = (TextView) infoView.findViewById(R.id.text_adapted_mine_menu);
		textMenu.setText(mineInfo.getMenu());

		infoView.findViewById(R.id.image_adapted_mine_map).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToMap();
			}

		});

		infoView.findViewById(R.id.image_adapted_mine_phone).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToPhone();
			}

		});
	}

	private void goToMap() {
		String uri = String.format(Locale.KOREAN, "geo:%f,%f?z=17&q=%f,%f", mineInfo.getLat(), mineInfo.getLng(), mineInfo.getLat(),
				mineInfo.getLng());
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
		boolean isIntentSafe = activities.size() > 0;

		if (isIntentSafe)
			context.startActivity(intent);
		else
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps")));
	}

	private void goToPhone() {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + mineInfo.getPhone()));
		context.startActivity(intent);
	}

}
