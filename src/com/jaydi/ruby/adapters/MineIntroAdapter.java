package com.jaydi.ruby.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.jaydi.ruby.R;
import com.jaydi.ruby.RubymineActivity;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.RubymineParcel;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.JsonUtils;
import com.jaydi.ruby.utils.ResourceUtils;
import com.jaydi.ruby.utils.UserUtils;

public class MineIntroAdapter extends BaseAdapter {
	private Context context;
	private List<Rubymine> rubymines;

	public MineIntroAdapter(Context context, List<Rubymine> rubymines) {
		super();
		this.context = context;
		this.rubymines = rubymines;
	}

	@Override
	public int getCount() {
		return rubymines.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0)
			return null;
		else
			return rubymines.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		if (position == 0)
			return 0;
		else
			return rubymines.get(position - 1).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view;

		if (position == 0)
			view = getHeaderView(inflater, parent);
		else
			view = getMineIntroView(inflater, parent, position - 1);

		return view;
	}

	private View getHeaderView(LayoutInflater inflater, ViewGroup parent) {
		return inflater.inflate(R.layout.adapted_mine_intro_header, parent, false);
	}

	@SuppressWarnings("unchecked")
	private View getMineIntroView(LayoutInflater inflater, ViewGroup parent, int position) {
		final Rubymine rubymine = rubymines.get(position);
		View view = inflater.inflate(R.layout.adapted_mine_intro_layout, parent, false);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToRubymine(rubymine);
			}

		});

		String introImageKey = null;
		if (rubymine.getContents() != null) {
			String jsonContents = rubymine.getContents().getValue();
			List<String> contents = JsonUtils.fromJson(jsonContents, ArrayList.class);
			if (!contents.isEmpty())
				introImageKey = contents.get(0);
		}

		ImageView imageImage = (ImageView) view.findViewById(R.id.image_adapted_mine_image);
		NetworkInter.getImage(null, imageImage, introImageKey, 350, 350);

		TextView textName = (TextView) view.findViewById(R.id.text_adapted_mine_name);
		textName.setText(rubymine.getName());

		TextView textType = (TextView) view.findViewById(R.id.text_adapted_mine_type);
		textType.setText(rubymine.getTypeString());

		TextView textValue = (TextView) view.findViewById(R.id.text_adapted_mine_value);
		textValue.setText("" + UserUtils.getLevelRuby(LocalUser.getUser().getLevel()) + ResourceUtils.getString(R.string.ruby_scale));

		// view.findViewById(R.id.image_adpated_mine_map).setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// ToastUtils.show("mine map");
		// }
		//
		// });

		return view;
	}

	private void goToRubymine(Rubymine rubymine) {
		Intent intent = new Intent(context, RubymineActivity.class);
		intent.putExtra(RubymineActivity.EXTRA_RUBYMINE, new RubymineParcel(rubymine));
		context.startActivity(intent);
	}

}
