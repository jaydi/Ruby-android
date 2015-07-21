package com.jaydi.ruby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.jaydi.ruby.utils.ResourceUtils;

public class GuideFragment extends Fragment {
	private int position;

	public GuideFragment(int position) {
		super();
		this.position = position;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guide, container, false);

		ImageView imageImage = (ImageView) view.findViewById(R.id.image_guide_image);
		ImageView imageText = (ImageView) view.findViewById(R.id.image_guide_text);

		switch (position) {
		case 0:
			imageImage.setImageDrawable(ResourceUtils.getDrawable(R.drawable.guide_1_phone));
			imageText.setImageDrawable(ResourceUtils.getDrawable(R.drawable.guide_1_text));
			break;
		case 1:
			imageImage.setImageDrawable(ResourceUtils.getDrawable(R.drawable.guide_2_phone));
			imageText.setImageDrawable(ResourceUtils.getDrawable(R.drawable.guide_2_text));
			break;
		case 2:
			imageImage.setImageDrawable(ResourceUtils.getDrawable(R.drawable.guide_3_phone));
			imageText.setImageDrawable(ResourceUtils.getDrawable(R.drawable.guide_3_text));
			break;
		case 3:
			imageImage.setImageDrawable(ResourceUtils.getDrawable(R.drawable.guide_4_phone));
			imageText.setImageDrawable(ResourceUtils.getDrawable(R.drawable.guide_4_text));
			setStartButton(view);
			break;
		}

		return view;
	}

	private void setStartButton(View view) {
		Button buttonStart = (Button) view.findViewById(R.id.button_guide_start);
		buttonStart.setVisibility(View.VISIBLE);
		buttonStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SignInActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
			}

		});
	}

}
