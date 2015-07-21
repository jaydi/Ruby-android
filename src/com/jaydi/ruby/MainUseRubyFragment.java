package com.jaydi.ruby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Gem;
import com.appspot.ruby_mine.rubymine.model.GemCol;
import com.jaydi.ruby.adapters.GemAdapter;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.ResourceUtils;

public class MainUseRubyFragment extends MainFragment {
	private View view;
	private List<Gem> gems;
	private ListView listGems;
	private GemAdapter gemAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main_use_ruby, container, false);

		gems = new ArrayList<Gem>();
		listGems = (ListView) view.findViewById(R.id.list_use_ruby_gems);
		gemAdapter = new GemAdapter(getActivity(), gems);
		listGems.setAdapter(gemAdapter);
		
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		setRubyInfo();
	}

	private void setRubyInfo() {
		TextView textGuide = (TextView) view.findViewById(R.id.text_use_ruby_guide);
		textGuide.setText(String.format(ResourceUtils.getString(R.string.ruby_info_guide_1), LocalUser.getUser().getName()));

		TextView textRuby = (TextView) view.findViewById(R.id.text_use_ruby_ruby);
		textRuby.setText(" " + LocalUser.getUser().getRuby().intValue() + ResourceUtils.getString(R.string.ruby_scale));
	}

	@Override
	public void initContents(long rubyzoneId) {
		NetworkInter.getZoneGems(new ResponseHandler<GemCol>() {

			@Override
			protected void onResponse(GemCol res) {
				hideProgress();
				if (res == null || res.getGems() == null)
					return;

				gems.clear();
				gems.addAll(res.getGems());
				refresh();
			}

		}, rubyzoneId);
	}

	private void hideProgress() {
		view.findViewById(R.id.progressbar_use_ruby_loading).setVisibility(View.GONE);
	}

	private void refresh() {
		sortGems();
		gemAdapter.notifyDataSetChanged();
		// loadImages();
	}

	private void sortGems() {
		Collections.sort(gems, new Comparator<Gem>() {

			@Override
			public int compare(Gem lhs, Gem rhs) {
				if (lhs.getValue().intValue() > rhs.getValue().intValue())
					return 1;
				else if (lhs.getValue().intValue() < rhs.getValue().intValue())
					return -1;
				else
					return 0;
			}

		});
	}

	// private void loadImages() {
	// for (Gem gem : gems)
	// NetworkInter.getImage(null, null, gem.getImageKey(), 165, 165);
	// }

	@Override
	public void loadContents(long rubyzoneId) {
	}

}
