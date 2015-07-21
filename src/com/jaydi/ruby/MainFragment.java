package com.jaydi.ruby;

import android.support.v4.app.Fragment;

public abstract class MainFragment extends Fragment {
	public abstract void initContents(long rubyzoneId);
	public abstract void loadContents(long rubyzoneId);
}
