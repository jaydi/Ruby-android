package com.jaydi.ruby;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.jaydi.ruby.adapters.MineAdapter;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.RubymineParcel;

public class RubymineActivity extends BaseActivity {
	public static final String EXTRA_RUBYMINE = "com.jaydi.ruby.extras.RUBYMINE";
	public static final String EXTRA_RUBYMINE_ID = "com.jaydi.ruby.extras.RUBYMINE_ID";

	private Rubymine rubymine;
	private ListView listContents;
	private MineAdapter mineAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rubymine);
		viewId = 310;

		if (savedInstanceState == null) {
			RubymineParcel rubymineParcel = getIntent().getParcelableExtra(EXTRA_RUBYMINE);
			if (rubymineParcel == null) {
				long rubymineId = getIntent().getLongExtra(EXTRA_RUBYMINE_ID, 0l);
				getRubymine(rubymineId);
			} else
				rubymine = rubymineParcel.toRubymine();
		}

		if (rubymine != null)
			setView();
	}

	private void getRubymine(long rubymineId) {
		showProgress();
		NetworkInter.getRubymine(new ResponseHandler<Rubymine>() {

			@Override
			protected void onResponse(Rubymine res) {
				hideProgress();
				if (res == null)
					return;

				rubymine = res;
				setView();
			}

		}, rubymineId);
	}

	private void showProgress() {
		findViewById(R.id.progressbar_mine_loading).setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		findViewById(R.id.progressbar_mine_loading).setVisibility(View.GONE);
	}

	private void setView() {
		listContents = (ListView) findViewById(R.id.list_mine_contents);
		mineAdapter = new MineAdapter(this, rubymine);
		listContents.setAdapter(mineAdapter);
	}
}
