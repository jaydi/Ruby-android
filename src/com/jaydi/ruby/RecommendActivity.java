package com.jaydi.ruby;

import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.appspot.ruby_mine.rubymine.model.Ruby;
import com.appspot.ruby_mine.rubymine.model.RubyCol;
import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.appspot.ruby_mine.rubymine.model.User;
import com.jaydi.ruby.adapters.MinePagerAdapter;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.models.RubyParcel;
import com.jaydi.ruby.models.RubymineParcel;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.Logger;
import com.jaydi.ruby.utils.ResourceUtils;

public class RecommendActivity extends BaseActivity implements OnPageChangeListener {
	public static final String EXTRA_RUBIES = "com.jaydi.ruby.extras.RUBIES";
	public static final String EXTRA_PLANTER = "com.jaydi.ruby.extras.PLANTER";
	public static final String EXTRA_GIVERS = "com.jaydi.ruby.extras.GIVERS";

	private List<Ruby> rubies;
	private Rubymine planter;
	private List<Rubymine> givers;
	private ViewPager minePager;
	private MinePagerAdapter minePagerAdapter;

	// Track
	private List<Integer> shownPositions = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend);
		viewId = 410;

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

		minePager = (ViewPager) findViewById(R.id.pager_recommend_mines);
		minePagerAdapter = new MinePagerAdapter(getSupportFragmentManager(), givers);
		minePager.setAdapter(minePagerAdapter);
		minePager.setOnPageChangeListener(this);

		removeNotification();
		claimRubies();

		// Track given ads
		for (int i = 0; i < givers.size(); i++)
			Logger.log(Logger.KIND_AD_GOT, givers.get(i).getId(), planter.getId(), i + 1);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Track first ad shown
		if (givers.size() > 0) {
			Logger.log(Logger.KIND_AD_SEEN, givers.get(0).getId(), planter.getId(), 1);
			shownPositions.add(0);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		if (!shownPositions.contains(position)) {
			// Track ad shown
			Logger.log(Logger.KIND_AD_SEEN, givers.get(position).getId(), planter.getId(), position + 1);
			// cache shown ad
			shownPositions.add(position);
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void removeNotification() {
		NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.cancel(planter.getId().intValue());
	}

	private void claimRubies() {
		RubyCol rubyCol = new RubyCol();
		rubyCol.setRubies(rubies);
		rubyCol.setPlanter(planter);
		rubyCol.setGivers(givers);

		NetworkInter.claimRuby(new ResponseHandler<User>() {

			@Override
			protected void onResponse(User res) {
				if (res == null)
					return;

				LocalUser.setUser(res);
				showMessage();
			}

		}, rubyCol);
	}

	private void showMessage() {
		float value = 0.0f;
		for (Ruby ruby : rubies)
			value += ruby.getValue();

		findViewById(R.id.linear_recommend_mining).setVisibility(View.GONE);
		findViewById(R.id.linear_recommend_mining_done).setVisibility(View.VISIBLE);

		TextView textValue = (TextView) findViewById(R.id.text_recommend_value);
		textValue.setText(" " + ((int) value) + ResourceUtils.getString(R.string.ruby_scale));

		TextView textRuby = (TextView) findViewById(R.id.text_recommend_ruby);
		textRuby.setText(" " + LocalUser.getUser().getRuby().intValue() + ResourceUtils.getString(R.string.ruby_scale));

		Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		fadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				findViewById(R.id.linear_recommend_mining_done).setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

		});
		findViewById(R.id.linear_recommend_mining_done).setAnimation(fadeOut);

		// Track ruby event
		Logger.log(Logger.KIND_RUBY, planter.getId(), (long) value, rubies.get(0).getEvent());
	}

}
