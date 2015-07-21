package com.jaydi.ruby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jaydi.ruby.models.Event;

public class EventNoticeActivity extends BaseActivity {
	public static final String EXTRA_EVENT = "com.jaydi.ruby.extras.EVENT";

	private Event event;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_notice);
		viewId = 500;

		if (savedInstanceState == null)
			event = getIntent().getParcelableExtra(EXTRA_EVENT);

		turnOn();
		showEvent();
	}

	private void turnOn() {
		if (!((PowerManager) getSystemService(Context.POWER_SERVICE)).isScreenOn())
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
							| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	private void showEvent() {
		TextView textMsg = (TextView) findViewById(R.id.text_event_notice_message);
		textMsg.setText(event.getMessage());
	}

	public void goToMain(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
