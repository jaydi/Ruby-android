package com.jaydi.ruby.beacon;

import android.content.Context;

import com.appspot.ruby_mine.rubymine.model.Ruby;
import com.appspot.ruby_mine.rubymine.model.RubyCol;
import com.jaydi.ruby.connection.ResponseHandler;
import com.jaydi.ruby.connection.network.NetworkInter;
import com.jaydi.ruby.user.LocalUser;
import com.jaydi.ruby.utils.Logger;
import com.jaydi.ruby.utils.PushUtils;

public class BeaconUpdateManager {

	public static void handleBeaconUpdate(Context context, int id) {
		mineRuby(context, id);
	}

	private static void mineRuby(final Context context, int id) {
		Ruby ruby = new Ruby();
		ruby.setId(0l);
		ruby.setGiverId(0l);
		ruby.setPlanterId(Long.valueOf(id));
		ruby.setUserId(LocalUser.getUser().getId());
		ruby.setEvent(2);

		NetworkInter.mineRuby(new ResponseHandler<RubyCol>(context.getMainLooper()) {

			@Override
			protected void onError(int resultCode) {
			}

			@Override
			protected void onResponse(RubyCol res) {
				if (res == null)
					return;

				onRubyFound(context, res);
			}

		}, ruby);

		// Track visit event
		Logger.log(Logger.KIND_VISIT, id, 0, 1);
	}

	private static void onRubyFound(Context context, RubyCol rubyCol) {
		PushUtils.pushRuby(context, rubyCol);
		
		// Track ruby got
		Logger.log(Logger.KIND_RUBY_GOT, rubyCol.getPlanter().getId(), 0, 1);
	}

}
