package com.jaydi.ruby.connection;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.api.client.json.GenericJson;
import com.jaydi.ruby.models.BaseModel;
import com.jaydi.ruby.utils.JsonUtils;

public abstract class ResponseHandler<T> extends Handler {
	private Dialog dialog;

	public ResponseHandler() {
		super();
	}

	public ResponseHandler(Dialog dialog) {
		super();
		this.dialog = dialog;
	}

	public ResponseHandler(Looper looper) {
		super(looper);
	}

	public ResponseHandler(Looper looper, Dialog dialog) {
		super(looper);
		this.dialog = dialog;
	}

	@Override
	public void handleMessage(Message m) {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		processResponse(m.obj);
	}

	@SuppressWarnings("unchecked")
	private void processResponse(Object res) {
		if (res == null)
			onResponse((T) res);

		try {
			String json = ((GenericJson) res).toString();
			BaseModel bmRes = JsonUtils.fromJson(json, BaseModel.class);

			errorCheck(bmRes);

		} catch (ClassCastException e) {
			onResponse((T) res);
		} catch (NullPointerException e) {
			onResponse((T) res);
		}
	}

	@SuppressWarnings("unchecked")
	private void errorCheck(BaseModel bmRes) {
		int resultCode = bmRes.getResultCode();
		if (resultCode != 0)
			onError(resultCode);
		else
			onResponse((T) bmRes);
	}

	protected void onError(int resultCode) {
	}

	protected abstract void onResponse(T res);
}
