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

import com.appspot.ruby_mine.rubymine.model.Gem;
import com.jaydi.ruby.GemActivity;
import com.jaydi.ruby.R;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.GemParcel;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.ResourceUtils;

public class GemAdapter extends BaseAdapter {
	private Context context;
	private List<Gem> gems;
	private List<Gem> underGems;
	private List<Gem> overGems;
	private int partType;
	private int underCount;
	private int overCount;

	public GemAdapter(Context context, List<Gem> gems) {
		super();
		this.context = context;
		this.gems = gems;
	}

	@Override
	public int getCount() {
		compareGems();
		return count();
	}

	private void compareGems() {
		underGems = new ArrayList<Gem>();
		overGems = new ArrayList<Gem>();

		int uv = LocalUser.getUser().getRuby().intValue();
		for (Gem gem : gems)
			if (gem.getValue().intValue() <= uv)
				underGems.add(gem);
			else
				overGems.add(gem);
	}

	private int count() {
		partType = 0;
		int count = 0;

		if (!underGems.isEmpty()) {
			partType++;
			count++;
			underCount = ((underGems.size() / 2) + (underGems.size() % 2));
			count += underCount;
		}

		if (!overGems.isEmpty()) {
			partType += 2;
			count++;
			overCount = ((overGems.size() / 2) + (overGems.size() % 2));
			count += overCount;
		}

		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		switch (partType) {
		case 1:
			if (position == 0)
				view = getGemHeader(inflater, parent, false);
			else
				view = getGemView(inflater, parent, position - 1, false);
			break;
		case 2:
			if (position == 0)
				view = getGemHeader(inflater, parent, true);
			else
				view = getGemView(inflater, parent, position - 1, true);
			break;
		case 3:
			if (position == 0)
				view = getGemHeader(inflater, parent, false);
			else if (position > 0 && position <= underCount)
				view = getGemView(inflater, parent, position - 1, false);
			else if (position == underCount + 1)
				view = getGemHeader(inflater, parent, true);
			else
				view = getGemView(inflater, parent, position - (2 + underCount), true);
			break;
		}

		return view;
	}

	private View getGemHeader(LayoutInflater inflater, ViewGroup parent, boolean isOver) {
		View view = inflater.inflate(R.layout.adapted_gem_header, parent, false);

		TextView textGuide = (TextView) view.findViewById(R.id.text_adapted_gem_header_guide);
		TextView textCount = (TextView) view.findViewById(R.id.text_adapted_gem_header_count);
		if (isOver) {
			textGuide.setText(ResourceUtils.getString(R.string.use_gem_guide_over));
			textCount.setText("" + overGems.size());
		} else {
			textGuide.setText(ResourceUtils.getString(R.string.use_gem_guide_under));
			textCount.setText("" + underGems.size());
		}

		return view;
	}

	private View getGemView(LayoutInflater inflater, ViewGroup parent, int position, boolean isOver) {
		final View view = inflater.inflate(R.layout.adapted_gem_layout, parent, false);
		final Gem gem1;
		final Gem gem2;
		if (isOver) {
			gem1 = overGems.get(position * 2);
			gem2 = (overGems.size() > (position * 2 + 1)) ? overGems.get(position * 2 + 1) : null;
		} else {
			gem1 = underGems.get(position * 2);
			gem2 = (underGems.size() > (position * 2 + 1)) ? underGems.get(position * 2 + 1) : null;
		}

		view.findViewById(R.id.frame_adapted_gem_box1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToGem(gem1);
			}

		});

		ImageView image1 = (ImageView) view.findViewById(R.id.image_adapted_gem_image_1);
		NetworkInter.getImage(null, image1, gem1.getImageKey(), 165, 165);

		TextView name1 = (TextView) view.findViewById(R.id.text_adapted_gem_name_1);
		name1.setText("[" + gem1.getRubymineName() + "] " + gem1.getName());

		TextView value1 = (TextView) view.findViewById(R.id.text_adapted_gem_value_1);
		value1.setText("" + gem1.getValue());

		if (gem2 != null) {
			view.findViewById(R.id.frame_adapted_gem_box2).setVisibility(View.VISIBLE);
			view.findViewById(R.id.frame_adapted_gem_box2).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goToGem(gem2);
				}

			});

			ImageView image2 = (ImageView) view.findViewById(R.id.image_adapted_gem_image_2);
			NetworkInter.getImage(null, image2, gem2.getImageKey(), 165, 165);

			TextView name2 = (TextView) view.findViewById(R.id.text_adapted_gem_name_2);
			name2.setText("[" + gem2.getRubymineName() + "] " + gem2.getName());

			TextView value2 = (TextView) view.findViewById(R.id.text_adapted_gem_value_2);
			value2.setText("" + gem2.getValue());
		}

		return view;
	}

	private void goToGem(Gem gem) {
		Intent intent = new Intent(context, GemActivity.class);
		intent.putExtra(GemActivity.EXTRA_GEM, new GemParcel(gem));
		context.startActivity(intent);
	}

}
