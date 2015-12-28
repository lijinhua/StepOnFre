/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *  http://stackoverflow.com/questions/3748568/how-can-i-launch-androids-email-activity-with-an-attachment-attached-in-the-emai
 *  
 *   *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *  Cleaned step detection algorithm
 *  Sensitivity 0.1, 0.11, 0.12, 0.14, 0.18
 *  Added Pause/resume buttons
 *  Check mIsSensorRegisterd for pause/resume buttons 
 *  Commented LE version functions: limited count,days, link to the market, expiration message
 *  
 *  updateWidget at TripA mode 5/18/2011
 *  category.LAUNCHER property at .Settings in Manifest xml
 *  Added Help information (link to video instruction) in the setting screen
 *  Set "singleInstance" property for Activity in Manifest xml. Allows "back" and "home" buttons
 *  http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
 *  
 *  Key hash for android-Fabcebook app
 *  use this command to generate key hash: 
 *  go to the AndroidMarketUpload folder, then
 *  keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
 *  or
 *  keytool -exportcert -alias accupedo -keystore Corusen|openssl sha1 -binary|openssl base64
 *  sLXTnqa0Q8Xram05j70YjEv5hUE
 *  138032342998022
 */

package com.corusen.steponfre.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.corusen.steponfre.R;

public class FragmentDialogBodyHeight extends DialogFragment {

	private PedometerSettings mPedometerSettings;
	private boolean mIsMetric;
	private float mValue;
	private int mFeet;
	private int mInch;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		mPedometerSettings = new PedometerSettings(settings);
		mValue = mPedometerSettings.getBodyHeight();
		mIsMetric = mPedometerSettings.isMetric();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view;
		NumberPicker mNumberPicker;
		NumberPicker mNumberPicker1;
		NumberPicker mNumberPicker2;

		if (mIsMetric) {
			// mUnitText = getString(R.string.kilograms);
			view = inflater.inflate(R.layout.dark_fragment_dialog_number_picker, null);
			mNumberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
			mNumberPicker.setMinValue(FragmentSettings.HEIGHT_MIN);
			mNumberPicker.setMaxValue(FragmentSettings.HEIGHT_MAX);
			mNumberPicker.setValue((int) mValue);

			mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
					mValue = (float) newVal;
				}
			});
		} else {
			// mUnitText = getString(R.string.pounds);
			view = inflater.inflate(R.layout.dark_fragment_dialog_number_picker_ftinch, null);
			mNumberPicker1 = (NumberPicker) view.findViewById(R.id.number_picker1);
			mNumberPicker2 = (NumberPicker) view.findViewById(R.id.number_picker2);
			mNumberPicker1.setMinValue(FragmentSettings.HEIGHT_MIN_FEET);
			mNumberPicker1.setMaxValue(FragmentSettings.HEIGHT_MAX_FEET);
			mNumberPicker2.setMinValue(FragmentSettings.HEIGHT_MIN_INCH);
			mNumberPicker2.setMaxValue(FragmentSettings.HEIGHT_MAX_INCH);

			mFeet = (int) (mValue / 12.0);
			mInch = (int) (mValue - (mFeet * 12));
			mNumberPicker1.setValue(mFeet);
			mNumberPicker2.setValue(mInch);

			mNumberPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
					mFeet = newVal;
				}
			});

			mNumberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
					mInch = newVal;
				}
			});

		}

		builder.setView(view).setTitle(getString(R.string.body_height_setting_title))
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (mIsMetric) mPedometerSettings.setBodyHeight(mValue);
						else mPedometerSettings.setBodyHeight(mFeet * 12 + mInch);

						getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
					}
				}).setNegativeButton(getString(R.string.cancelled), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

		return builder.create();
	}
}
