package com.jaydi.ruby.adapters;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.appspot.ruby_mine.rubymine.model.Rubymine;
import com.jaydi.ruby.RubymineFragment;

public class MinePagerAdapter extends FragmentStatePagerAdapter {
	private List<Rubymine> rubymines;
	SparseArray<Fragment> currentFragments = new SparseArray<Fragment>();

	public MinePagerAdapter(FragmentManager fm, List<Rubymine> rubymines) {
		super(fm);
		this.rubymines = rubymines;
	}

	@Override
	public int getCount() {
		return rubymines.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
		RubymineFragment f = new RubymineFragment();
		f.setRubymine(rubymines.get(position));		
		return f;
	}

	@Override
	public String getPageTitle(int position) {
		return rubymines.get(position).getName();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = (Fragment) super.instantiateItem(container, position);
		currentFragments.put(position, fragment);
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		currentFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	public Fragment getCurrentFragment(int position) {
		return currentFragments.get(position);
	}

}
