package com.jaydi.ruby.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.R;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;

public class UserAdapter extends BaseAdapter {
	private Context context;
	private List<User> users;
	private PairUserInter pairUserInter;

	public UserAdapter(Context context, List<User> users, PairUserInter pairUserInter) {
		super();
		this.context = context;
		this.users = users;
		this.pairUserInter = pairUserInter;
	}

	public interface PairUserInter {
		abstract void onPairUser(int position);
	}

	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return users.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final User user = users.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.adapted_user_layout, parent, false);

		ImageView imageImage = (ImageView) view.findViewById(R.id.image_adapted_user_image);
		NetworkInter.getImageCircular(null, imageImage, user.getImageKey(), 100, 100);

		TextView textName = (TextView) view.findViewById(R.id.text_adapted_user_name);
		textName.setText(user.getName());

		Log.i("uA", "paired: " + user.getPaired() + "/" + user.getName());

		if (!user.getPaired() && !user.getId().equals(LocalUser.getUser().getId())) {
			final Button buttonAdd = (Button) view.findViewById(R.id.button_adapted_user_add);
			buttonAdd.setVisibility(View.VISIBLE);
			buttonAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					buttonAdd.setVisibility(View.GONE);
					pairUserInter.onPairUser(position);
				}

			});
		}

		return view;
	}

}
