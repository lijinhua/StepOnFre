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

public class FragmentDialogRunLength extends DialogFragment {

	private PedometerSettings mPedometerSettings;
	private float mValue;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		mPedometerSettings = new PedometerSettings(settings);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dark_fragment_dialog_number_picker, null);
		NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);

		mValue = mPedometerSettings.getRunLength();
		boolean isMetric = mPedometerSettings.isMetric();
		if (isMetric) {
			numberPicker.setMinValue(FragmentSettings.RUN_MIN);
			numberPicker.setMaxValue(FragmentSettings.RUN_MAX);
		} else {
			numberPicker.setMinValue((int) ((float) FragmentSettings.RUN_MIN * Utils.CM2INCH));
			numberPicker.setMaxValue((int) ((float) FragmentSettings.RUN_MAX * Utils.CM2INCH));
		}

		numberPicker.setValue((int)mValue);
		numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
				mValue = (float) newVal;
			}
		});

		builder.setView(view).setTitle(getString(R.string.run_length_setting))
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// update here
						mPedometerSettings.setRunLength(mValue);
						getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
					}
				}).setNegativeButton(getString(R.string.cancelled), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});

		return builder.create();
	}
}


//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.content.DialogInterface;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//
//import com.corusen.steponfre.R;
//
//public class FragmentDialogRunLength extends DialogFragment {
//
//	private SharedPreferences mSettings;
//	private PedometerSettings mPedometerSettings;
//	private boolean mIsMetric;
//	private TextView mTextDetail;
//	private TextView mTextValue;
//	private String mUnitText;
//	private float mValue;
//
//	private SeekBar mSeekBar = null;
//
//	@Override
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		mSettings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
//		mPedometerSettings = new PedometerSettings(mSettings);
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//		LayoutInflater inflater = getActivity().getLayoutInflater();
//		View view = inflater.inflate(R.layout.dark_fragment_dialog_seekbar, null);
//		mTextDetail = (TextView) view.findViewById(R.id.textDetail);
//		mTextValue = (TextView) view.findViewById(R.id.textValue);
//		mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
//
//		// update here
//		mTextDetail.setText(getString(R.string.run_length_setting));
//		mValue = mPedometerSettings.getRunLength();
//		mIsMetric = mPedometerSettings.isMetric();
//		if (mIsMetric) {
//			mUnitText = getString(R.string.centimeters);
//			mSeekBar.setMax(FragmentSettings.RUN_MAX - FragmentSettings.RUN_MIN);
//			mSeekBar.setProgress((int) mValue - FragmentSettings.RUN_MIN);
//		} else {
//			mUnitText = getString(R.string.inches);
//			mSeekBar.setMax((int) ((float) (FragmentSettings.RUN_MAX - FragmentSettings.RUN_MIN) * Utils.CM2INCH));
//			mSeekBar.setProgress((int) mValue - (int) ((float) FragmentSettings.RUN_MIN * Utils.CM2INCH));
//		}
//
//		updateText((int) mValue);
//
//		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//			int progresschange;
//
//			public void onProgressChanged(SeekBar arg0, int progress, boolean fromTouch) {
//				if (mIsMetric) {
//					progresschange = progress + FragmentSettings.RUN_MIN;
//				} else {
//					progresschange = progress + (int) ((float) FragmentSettings.RUN_MIN * Utils.CM2INCH);
//				}
//				updateText(progresschange);
//			}
//
//			public void onStartTrackingTouch(SeekBar arg0) {
//			}
//
//			public void onStopTrackingTouch(SeekBar arg0) {
//				mValue = (float) progresschange;
//				updateText(progresschange);
//			}
//		});
//
//		builder.setView(view).setTitle(getString(R.string.run_length_setting)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				// update here
//				mPedometerSettings.setRunLength(mValue);
//				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
//			}
//		}).setNegativeButton(getString(R.string.cancelled), new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//
//			}
//		});
//
//		return builder.create();
//	}
//
//	// update here
//	void updateText(int progresschange) {
//		if (mIsMetric) {
//			mTextValue.setText("" + progresschange + " " + mUnitText);
//		} else {
//			mTextValue.setText("" + progresschange + " " + mUnitText);
//		}
//	}
//}
