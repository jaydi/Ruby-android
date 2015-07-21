package com.jaydi.ruby;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Ruby;
import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.jaydi.ruby.models.RubyParcel;
import com.jaydi.ruby.models.RubymineParcel;
import com.jaydi.ruby.utils.ResourceUtils;

public class RubyNoticeActivity extends BaseActivity {
	public static final String EXTRA_RUBIES = "com.jaydi.ruby.extras.RUBIES";
	public static final String EXTRA_PLANTER = "com.jaydi.ruby.extras.PLANTER";
	public static final String EXTRA_GIVERS = "com.jaydi.ruby.extras.GIVERS";

	private List<Ruby> rubies;
	private Rubymine planter;
	private List<Rubymine> givers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ruby_notice);
		viewId = 400;

		if (savedInstanceState == null) {
			Parcelable[] rubyParcels = getIntent().getParcelableArrayExtra(EXTRA_RUBIES);
			rubies = new ArrayList<Ruby>();
			for (int i = 0; i < rubyParcels.length; i++)
				rubies.add(((RubyParcel) rubyParcels[i]).toRuby());

			RubymineParcel planterParcel = getIntent().getParcelableExtra(EXTRA_PLANTER);
			planter = planterParcel.toRubymine();

			Parcelable[] giverParcels = getIntent().getParcelableArrayExtra(EXTRA_GIVERS);
			givers = new ArrayList<Rubymine>();
			for (int i = 0; i < giverParcels.length; i++)
				givers.add(((RubymineParcel) giverParcels[i]).toRubymine());
		}

		showRubyInfo();
		turnOn();
	}

	private void showRubyInfo() {
		TextView textInfo = (TextView) findViewById(R.id.text_ruby_notice_message);
		textInfo.setText(String.format(ResourceUtils.getString(R.string.ruby_message), planter.getName()));
	}

	private void turnOn() {
		if (!((PowerManager) getSystemService(Context.POWER_SERVICE)).isScreenOn())
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
							| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	public void goToRecommend(View view) {
		Intent intent = new Intent(this, RecommendActivity.class);

		RubyParcel[] rubyParcels = new RubyParcel[rubies.size()];
		for (int i = 0; i < rubies.size(); i++)
			rubyParcels[i] = new RubyParcel(rubies.get(i));

		RubymineParcel[] giverParcels = new RubymineParcel[givers.size()];
		for (int i = 0; i < givers.size(); i++)
			giverParcels[i] = new RubymineParcel(givers.get(i));

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(RecommendActivity.EXTRA_RUBIES, rubyParcels);
		intent.putExtra(RecommendActivity.EXTRA_PLANTER, new RubymineParcel(planter));
		intent.putExtra(RecommendActivity.EXTRA_GIVERS, giverParcels);

		startActivity(intent);
	}
}
