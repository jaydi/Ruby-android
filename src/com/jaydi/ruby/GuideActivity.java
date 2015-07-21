package com.jaydi.ruby;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.jaydi.ruby.adapters.GuidePagerAdapter;

public class GuideActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		viewId = 110;

		ViewPager pager = (ViewPager) findViewById(R.id.pager_guide_pager);
		GuidePagerAdapter guidePagerAdapter = new GuidePagerAdapter(getSupportFragmentManager());
		pager.setAdapter(guidePagerAdapter);
	}
}
