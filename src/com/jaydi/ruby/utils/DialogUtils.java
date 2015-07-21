package com.jaydi.ruby.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jaydi.ruby.R;
import com.jaydi.ruby.models.Event;

public class DialogUtils {

	public interface DialogConfirmListener {
		public abstract void onClickConfirm();
	}

	public interface BirthdayPickerListener {
		public void onBirthdaySet(int year, int month, int day);
	}

	public static void networkAlert(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Network Error");
		builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Activity) context).finish();
			}

		});
		builder.show();
	}

	public static Dialog showWaitingDialog(Context context) {
		ProgressDialog progressDlg = new ProgressDialog(context);
		progressDlg.setMessage(ResourceUtils.getString(R.string.waiting_message));
		progressDlg.setCancelable(false);
		progressDlg.show();
		return progressDlg;
	}

	public static void showBirthdayPicker(Context context, final BirthdayPickerListener listener) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.dialog_birthday_picker_layout, null, false);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		Button buttonCancel = (Button) view.findViewById(R.id.button_birthday_picker_confirm);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_birthday_picker_date);
				listener.onBirthdaySet(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
			}

		});
	}

	public static void showNeedRubyDialog(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_need_ruby_layout, null, false);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		Button buttonCancel = (Button) view.findViewById(R.id.button_need_ruby_confirm);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});
	}

	public static void showBuyGemDialog(Context context, final DialogConfirmListener buyCouponListener) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_buy_gem_layout, null, false);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		Button buttonCancel = (Button) view.findViewById(R.id.button_buy_gem_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});

		Button buttonConfirm = (Button) view.findViewById(R.id.button_buy_gem_confirm);
		buttonConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				buyCouponListener.onClickConfirm();
			}

		});
	}

	public static void showUseCouponDialog(Context context, final DialogConfirmListener buyCouponListener) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_use_coupon_layout, null, false);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		Button buttonCancel = (Button) view.findViewById(R.id.button_use_coupon_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});

		Button buttonConfirm = (Button) view.findViewById(R.id.button_use_coupon_confirm);
		buttonConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				buyCouponListener.onClickConfirm();
			}

		});
	}

	public static void showDeletePairDialog(Context context, final DialogConfirmListener deletePairListener) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_delete_pair_layout, null, false);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		Button buttonCancel = (Button) view.findViewById(R.id.button_delete_pair_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});

		Button buttonConfirm = (Button) view.findViewById(R.id.button_delete_pair_confirm);
		buttonConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				deletePairListener.onClickConfirm();
			}

		});
	}

	public static void showEventDialog(Context context, Event event) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_event_layout, null, false);

		TextView textMsg = (TextView) view.findViewById(R.id.text_dialog_event_message);
		textMsg.setText(event.getMessage());

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		Button buttonCancel = (Button) view.findViewById(R.id.button_dialog_event_confirm);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});
	}

	public static void showEnableLocationDialog(final Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_enable_location_layout, null, false);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		Button buttonCancel = (Button) view.findViewById(R.id.button_dialog_enable_location_confirm);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}

		});
	}

	public static void showRubyDialog(Context context, String msg) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_event_layout, null, false);

		TextView textMsg = (TextView) view.findViewById(R.id.text_dialog_event_message);
		textMsg.setText(msg);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final Dialog dialog = builder.show();

		Button buttonCancel = (Button) view.findViewById(R.id.button_dialog_event_confirm);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});
	}
}
