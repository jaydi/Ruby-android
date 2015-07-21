package com.jaydi.ruby;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.appspot.ruby_mine.rubymine.model.RubymineCol;
import com.jaydi.ruby.adapters.MineIntroAdapter;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.utils.JsonUtils;

public class MainIntroFragment extends MainFragment {
	private View view;
	private ListView listMines;
	private List<Rubymine> rubymines;
	private MineIntroAdapter mineAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main_intro, container, false);

		listMines = (ListView) view.findViewById(R.id.list_intro_rubymines);
		rubymines = new ArrayList<Rubymine>();
		mineAdapter = new MineIntroAdapter(getActivity(), rubymines);
		listMines.setAdapter(mineAdapter);
		
		
		
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
				mineAdapter.notifyDataSetChanged();
				loadImages();
			}

		}, rubyzoneId);
		
	}

	private void hideProgress() {
		view.findViewById(R.id.progressbar_intro_loading).setVisibility(View.GONE);
	}

	@SuppressWarnings("unchecked")
	protected void loadImages() {
		for (int i = Math.min(2, rubymines.size()); i < rubymines.size(); i++) {
			String imageKey = null;
			if (rubymines.get(i).getContents() != null) {
				String jsonContents = rubymines.get(i).getContents().getValue();
				List<String> contents = JsonUtils.fromJson(jsonContents, ArrayList.class);
				if (!contents.isEmpty())
					imageKey = contents.get(0);
			}
			NetworkInter.preloadImage(imageKey, 350, 350);
		}
	}

	@Override
	public void loadContents(long rubyzoneId) {
	}

}
