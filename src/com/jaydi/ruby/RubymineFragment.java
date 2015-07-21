package com.jaydi.ruby;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.jaydi.ruby.adapters.MineAdapter;

public class RubymineFragment extends Fragment {
	private Rubymine rubymine;

	public void setRubymine(Rubymine rubymine) {
		this.rubymine = rubymine;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("RF", "create fragment of " + rubymine.getName());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ListView view = (ListView) inflater.inflate(R.layout.fragment_rubymine, container, false);

		MineAdapter adapter = new MineAdapter(getActivity(), rubymine);
		view.setAdapter(adapter);
		// loadImages();

		return view;
	}

	// @SuppressWarnings("unchecked")
	// private void loadImages() {
	// if (rubymine.getContents() != null) {
	// String jsonContents = rubymine.getContents().getValue();
	// List<String> contents = JsonUtils.fromJson(jsonContents, ArrayList.class);
	// for (int i = Math.min(2, contents.size()); i < contents.size(); i++)
	// if (i % 2 == 0)
	// NetworkInter.getImage(null, null, contents.get(i), 350, 350);
	// }
	// }
}
