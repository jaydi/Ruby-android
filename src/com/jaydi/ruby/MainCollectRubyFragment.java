package com.jaydi.ruby;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.appspot.ruby_mine.rubymine.model.RubymineCol;
import com.jaydi.ruby.adapters.MinePagerAdapter;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;

public class MainCollectRubyFragment extends MainFragment implements OnPageChangeListener {
	private View view;
	private List<Rubymine> rubymines;
	private ViewPager minePager;
	private MinePagerAdapter minePagerAdapter;

	private int precursor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main_collect_ruby, container, false);

		rubymines = new ArrayList<Rubymine>();
		minePager = (ViewPager) view.findViewById(R.id.pager_collect_ruby_mines);
		minePagerAdapter = new MinePagerAdapter(getFragmentManager(), rubymines);
		minePager.setAdapter(minePagerAdapter);
		minePager.setOnPageChangeListener(this);
		
		
		return view;
	}

	@Override
	public void initContents(long rubyzoneId) {
		NetworkInter.getRubymines(new ResponseHandler<RubymineCol>() {

			@Override
			protected void onResponse(RubymineCol res) {
				hideProgress();
				if (res == null || res.getRubymines() == null)
					return;

				rubymines.clear();
				rubymines.addAll(res.getRubymines());
				refreshContents();
			}

		}, rubyzoneId);
	}

	private void hideProgress() {
		view.findViewById(R.id.progressbar_collect_ruby_loading).setVisibility(View.GONE);
	}

	private void refreshContents() {
		minePagerAdapter.notifyDataSetChanged();
		minePager.setCurrentItem(0);
		precursor = 0;
	}

	@Override
	public void loadContents(long rubyzoneId) {
		preloadImages(minePager.getCurrentItem());
	}

	@Override
	public void onPageSelected(int position) {
		if (position > precursor) {
			precursor = position;
			preloadImages(position);
		}
	}

	private void preloadImages(int current) {
		// if (rubymines.size() > current + 1)
		// loadImages(rubymines.get(current + 1));
	}

	// @SuppressWarnings("unchecked")
	// private void loadImages(Rubymine rubymine) {
	// if (rubymine.getContents() != null) {
	// String jsonContents = rubymine.getContents().getValue();
	// List<String> contents = JsonUtils.fromJson(jsonContents, ArrayList.class);
	//
	// for (int i = 0; i < Math.min(contents.size(), 5); i++)
	// if (i % 2 == 0)
	// NetworkInter.getImage(null, null, contents.get(i), 350, 350);
	// }
	// }

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

}
