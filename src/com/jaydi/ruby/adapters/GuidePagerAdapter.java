package com.jaydi.ruby.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jaydi.ruby.GuideFragment;

public class GuidePagerAdapter extends FragmentStatePagerAdapter {

	public GuidePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
		GuideFragment f = new GuideFragment(position);
		return f;
	}

}
