package com.jaydi.ruby.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Userpair;
import com.jaydi.ruby.R;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;

public class UserpairAdapter extends BaseAdapter {
	private Context context;
	private List<Userpair> userpairs;
	private DeletePairInter deletePairInter;

	public interface DeletePairInter {
		abstract void onDeletePair(int position);
	}

	public UserpairAdapter(Context context, List<Userpair> userpairs, DeletePairInter deletePairInter) {
		super();
		this.context = context;
		this.userpairs = userpairs;
		this.deletePairInter = deletePairInter;
	}

	@Override
	public int getCount() {
		return userpairs.size();
	}

	@Override
	public Object getItem(int position) {
		return userpairs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return userpairs.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Userpair userpair = userpairs.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.adapted_userpair_layout, parent, false);

		ImageView imageImage = (ImageView) view.findViewById(R.id.image_adapted_userpair_image);
		TextView textName = (TextView) view.findViewById(R.id.text_adapted_userpair_name);

		if (userpair.getUserIdA().equals(LocalUser.getUser().getId())) {
			NetworkInter.getImageCircular(null, imageImage, userpair.getUserImageKeyB(), 100, 100);
			textName.setText(userpair.getUserNameB());
		} else {
			NetworkInter.getImageCircular(null, imageImage, userpair.getUserImageKeyA(), 100, 100);
			textName.setText(userpair.getUserNameA());
		}

		TextView textRemove = (TextView) view.findViewById(R.id.text_adapted_userpair_remove);
		textRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deletePairInter.onDeletePair(position);
			}

		});

		return view;
	}

}
