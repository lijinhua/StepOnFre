/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-sum
 */

package com.corusen.steponfre.base;

import java.util.Locale;

//import com.corusen.accupedo.te.AnalyticsSampleApp.TrackerName;
//import com.corusen.accupedo.te.preferences.BodyWeightPreference;
//import com.corusen.accupedo.te.preferences.BodyHeightPreference;
//import com.corusen.accupedo.te.preferences.RunLengthPreference;
//import com.corusen.accupedo.te.preferences.StepLengthPreference;
import com.corusen.steponfre.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class FragmentSettings extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	public static final String ACCUPEDO_SETTINGS_RELOAD = "com.corusen.accupedo.te.ACCUPEDO_SETTINGS_RELOAD";

	private static final int DIALOG_FRAGMENT_DATE_PIKCER = 1;
	private static final int DIALOG_FRAGMENT_BODY_HEIGHT = 2;
	private static final int DIALOG_FRAGMENT_BODY_WEIGHT = 3;
	private static final int DIALOG_FRAGMENT_STEP_LENGTH = 4;
	private static final int DIALOG_FRAGMENT_RUN_LENGTH = 5;

	public static final int HEIGHT_MIN_FEET = 1;
	public static final int HEIGHT_MAX_FEET = 7;
	public static final int HEIGHT_MIN_INCH = 0;
	public static final int HEIGHT_MAX_INCH = 12;

	public static final int HEIGHT_MIN = 50;
	public static final int HEIGHT_MAX = 230;
	public static final int WEIGHT_MIN = 30;
	public static final int WEIGHT_MAX = 150;
	public static final int WALK_MIN = 25;
	public static final int WALK_MAX = 120;
	public static final int RUN_MIN = 50;
	public static final int RUN_MAX = 250;
	public static final float HEIGHT_TO_WALK = 0.4f;
	public static final float WALK_TO_RUN = 1.5f;

	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;

	private ListPreference mUnitListPreference;
	private ListPreference mLocaleListPreference;

	// private StepLengthPreference mStepLengthPreference;
	// private RunLengthPreference mRunLengthPreference;
	// private BodyWeightPreference mBodyWeightPreference;
	// private BodyHeightPreference mBodyHeightPreference;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
//		Tracker t = ((AnalyticsSampleApp) Pedometer.getInstance().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Settings");
//		t.send(new HitBuilders.AppViewBuilder().build());
	}

//	@Override  //V131
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSettings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		mPedometerSettings = new PedometerSettings(mSettings);

		addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

		mUnitListPreference = (ListPreference) getPreferenceScreen().findPreference("units");
		mLocaleListPreference = (ListPreference) getPreferenceScreen().findPreference("locale_type");

		// mStepLengthPreference = (StepLengthPreference)
		// getPreferenceScreen().findPreference("step_length");
		// mRunLengthPreference = (RunLengthPreference)
		// getPreferenceScreen().findPreference("run_length");
		// mBodyWeightPreference = (BodyWeightPreference) getPreferenceScreen()
		// .findPreference("body_weight");
		// mBodyHeightPreference = (BodyHeightPreference) getPreferenceScreen()
		// .findPreference("body_height");
		// Log.i("Settings", "Total:" + ((Integer)
		// getPreferenceScreen().getPreferenceCount()).toString());
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			initSummary(getPreferenceScreen().getPreference(i));
		}

		Preference p = findPreference("birthday");
		int year = Pedometer.mPedometerSettings.getBirthYear();
		int month = Pedometer.mPedometerSettings.getBirthMonth();
		int day = Pedometer.mPedometerSettings.getBirthDay();
		StringBuilder birthday = new StringBuilder().append(year).append("-").append(month).append("-").append(day).append(" ");
		p.setSummary(birthday);

		updateBodyHeightSummary();
		updateBodyWeightSummary();
		updateStepLengthSummary();
		updateRunLengthSummary();

		Preference datePicker = (Preference) findPreference("birthday");
		datePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(android.preference.Preference preference) {
				showDialog(DIALOG_FRAGMENT_DATE_PIKCER);
				return false;
			}
		});

		Preference bodyHeight = (Preference) findPreference("body_height");
		bodyHeight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(android.preference.Preference preference) {
				showDialog(DIALOG_FRAGMENT_BODY_HEIGHT);
				return false;
			}
		});

		Preference bodyWeight = (Preference) findPreference("body_weight");
		bodyWeight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(android.preference.Preference preference) {
				showDialog(DIALOG_FRAGMENT_BODY_WEIGHT);
				return false;
			}
		});

		Preference stepLength = (Preference) findPreference("step_length");
		stepLength.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(android.preference.Preference preference) {
				showDialog(DIALOG_FRAGMENT_STEP_LENGTH);
				return false;
			}
		});

		Preference runLength = (Preference) findPreference("run_length");
		runLength.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(android.preference.Preference preference) {
				showDialog(DIALOG_FRAGMENT_RUN_LENGTH);
				return false;
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		// actionBar.setSelectedNavigationItem(Pedometer.SPINNER_ITEM_SETTINGS);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		getActivity().sendBroadcast(new Intent(ACCUPEDO_SETTINGS_RELOAD));
	}

	@Override
	public void onDetach() {  //V131
		super.onDetach();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		getActivity().sendBroadcast(new Intent(AccuService.ACCUPEDO_SETTINGS_RELOAD));
	}


	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		float length, weight, runlength, height;
		if (key.equals("units")) {
			if (mUnitListPreference.getValue().compareTo("metric") == 0) {
				length = mPedometerSettings.getStepLength() * Utils.INCH2CM;
				runlength = mPedometerSettings.getRunLength() * Utils.INCH2CM;
				weight = mPedometerSettings.getBodyWeight() * Utils.LB2KG;
				height = mPedometerSettings.getBodyHeight() * Utils.INCH2CM;
			} else {
				length = mPedometerSettings.getStepLength() * Utils.CM2INCH;
				runlength = mPedometerSettings.getRunLength() * Utils.CM2INCH;
				weight = mPedometerSettings.getBodyWeight() * Utils.KG2LB;
				height = mPedometerSettings.getBodyHeight() * Utils.CM2INCH;
			}

			// if (mUnitListPreference.getValue().compareTo("metric") == 0) {
			// length = mStepLengthPreference.getFloat() * Utils.INCH2CM;
			// runlength = mRunLengthPreference.getFloat() * Utils.INCH2CM;
			// weight = mBodyWeightPreference.getFloat() / Utils.KG2LB;
			// } else {
			// length = mStepLengthPreference.getFloat() / Utils.INCH2CM;
			// runlength = mRunLengthPreference.getFloat() / Utils.INCH2CM;
			// weight = mBodyWeightPreference.getFloat() * Utils.KG2LB;
			// }
			// mStepLengthPreference.setFloat(length);
			// mRunLengthPreference.setFloat(runlength);

			// mBodyWeightPreference.setInteger(Math.round(weight)); //use this
			// method to update the summary
			// mBodyHeightPreference.setInteger(Math.round(height)); //use this
			// method to update the summary

			mPedometerSettings.setBodyHeight(height);
			mPedometerSettings.setBodyWeight(weight);
			mPedometerSettings.setStepLength(length);
			mPedometerSettings.setRunLength(runlength);

			updateBodyHeightSummary();
			updateBodyWeightSummary();
			updateStepLengthSummary();
			updateRunLengthSummary();
		} else if (key.equals("widget_skin_type") ) {
			Pedometer.mService.setWidgetSkinColor();
		}

		// if (key.equals(KEY_A_CHECKBOX_PREFERENCE)) {
		// mCheckBoxPreference.setSummary(sharedPreferences.getBoolean(key,
		// false) ? "Disable this setting" : "Enable this setting");
		// } else if (key.equals(KEY_AN_EDITTEXT_PREFERENCE)) {
		// mEditBoxPreference.setSummary("Current value is "
		// + sharedPreferences.getString(key, ""));
		// }

		Preference p = findPreference(key);
		updatePrefSummary(p);

		if (key.equals("locale_type")) {
			Locale locale;

			if (mLocaleListPreference.getValue().compareTo("0") == 0) {
				locale = Locale.getDefault();
			} else {
				locale = new Locale("en");
			}

			Configuration config = new Configuration();
			config.locale = locale;
			this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
			// refresh();
		}

		// if (key.equals("screen_skin_type")) {
		// Pedometer.getInstance().finish();
		//
		// Intent intent;
		// int[] array = new int[2];
		// array[0] = Pedometer.SPINNER_ITEM_SETTINGS;
		// intent = new Intent(getActivity(), Pedometer.class);
		// intent.putExtra("navigation_intent", array);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// // | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// startActivity(intent);
		// }
	}

	/**
	 * Change here for PreferenceCategory <-->PreferenceScreen
	 */
	private void initSummary(Preference p) {
		if (p instanceof PreferenceCategory) {
			PreferenceCategory pCat = (PreferenceCategory) p;
			// Log.i("Settings", "TotalPScreen:" + ((Integer)
			// pCat.getPreferenceCount()).toString());
			for (int i = 0; i < pCat.getPreferenceCount(); i++) {
				initSummary(pCat.getPreference(i));
			}
		} else {
			updatePrefSummary(p);
		}
	}

	private void updatePrefSummary(Preference p) {
		if (p instanceof ListPreference) {
			ListPreference listPref = (ListPreference) p;
			p.setSummary(listPref.getEntry());
		}
		if (p instanceof DialogPreference) {
			DialogPreference editTextPref = (DialogPreference) p;
			p.setSummary(editTextPref.getSummary());
		}
	}

	private void showDialog(int type) {
		// mStackLevel++;
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		switch (type) {
		case DIALOG_FRAGMENT_DATE_PIKCER:
			FragmentDialogBirthDatePicker dialogFragPause = new FragmentDialogBirthDatePicker();
			dialogFragPause.setTargetFragment(this, DIALOG_FRAGMENT_DATE_PIKCER);
			dialogFragPause.show(getFragmentManager().beginTransaction(), "dialog");
			break;
		case DIALOG_FRAGMENT_BODY_HEIGHT:
			FragmentDialogBodyHeight dialogHeight = new FragmentDialogBodyHeight();
			dialogHeight.setTargetFragment(this, DIALOG_FRAGMENT_BODY_HEIGHT);
			dialogHeight.show(getFragmentManager().beginTransaction(), "dialog");
			break;
		case DIALOG_FRAGMENT_BODY_WEIGHT:
			FragmentDialogBodyWeight dialogWeight = new FragmentDialogBodyWeight();
			dialogWeight.setTargetFragment(this, DIALOG_FRAGMENT_BODY_WEIGHT);
			dialogWeight.show(getFragmentManager().beginTransaction(), "dialog");
			break;
		case DIALOG_FRAGMENT_STEP_LENGTH:
			FragmentDialogStepLength dialogStep = new FragmentDialogStepLength();
			dialogStep.setTargetFragment(this, DIALOG_FRAGMENT_STEP_LENGTH);
			dialogStep.show(getFragmentManager().beginTransaction(), "dialog");
			break;
		case DIALOG_FRAGMENT_RUN_LENGTH:
			FragmentDialogRunLength dialogRun = new FragmentDialogRunLength();
			dialogRun.setTargetFragment(this, DIALOG_FRAGMENT_RUN_LENGTH);
			dialogRun.show(getFragmentManager().beginTransaction(), "dialog");
			break;
		}
	}

	void updateBodyHeightSummary() {
		Preference p = findPreference("body_height");
		float value = Pedometer.mPedometerSettings.getBodyHeight();
		String string;
		if (mPedometerSettings.isMetric()) {
			string = String.format("%d", (int) value) + getString(R.string.centimeters);
		} else {
			int feet = (int) (value / 12.0);
			int inch = (int) (value - (feet * 12));
			string = "" + feet + " " + "ft" + " " + inch + " " + getString(R.string.inches);
		}
		p.setSummary(string);
	}

	void updateBodyWeightSummary() {
		Preference p = findPreference("body_weight");
		float value = Pedometer.mPedometerSettings.getBodyWeight();
		String string;
		if (mPedometerSettings.isMetric()) {
			string = String.format("%d", (int) value) + getString(R.string.kilograms);
		} else {
			string = String.format("%d", (int) value) + getString(R.string.pounds);
		}
		p.setSummary(string);
	}

	void updateStepLengthSummary() {
		Preference p = findPreference("step_length");
		float value = Pedometer.mPedometerSettings.getStepLength();
		String string;
		if (mPedometerSettings.isMetric()) {
			string = String.format("%d", (int) value) + getString(R.string.centimeters);
		} else {
			string = String.format("%d", (int) value) + getString(R.string.inches);
		}
		p.setSummary(string);
	}

	void updateRunLengthSummary() {
		Preference p = findPreference("run_length");
		float value = Pedometer.mPedometerSettings.getRunLength();
		String string;
		if (mPedometerSettings.isMetric()) {
			string = String.format("%d", (int) value) + getString(R.string.centimeters);
		} else {
			string = String.format("%d", (int) value) + getString(R.string.inches);
		}
		p.setSummary(string);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case DIALOG_FRAGMENT_DATE_PIKCER:
			if (resultCode == Activity.RESULT_OK) {
				Preference p = findPreference("birthday");
				int year = Pedometer.mPedometerSettings.getBirthYear();
				int month = Pedometer.mPedometerSettings.getBirthMonth();
				int day = Pedometer.mPedometerSettings.getBirthDay();
				StringBuilder birthday = new StringBuilder().append(year).append("-").append(month).append("-").append(day).append(" ");
				p.setSummary(birthday);
			} else if (resultCode == Activity.RESULT_CANCELED) {
			}
			break;
		case DIALOG_FRAGMENT_BODY_HEIGHT:
			if (resultCode == Activity.RESULT_OK) {
				updateBodyHeightSummary();
			} else if (resultCode == Activity.RESULT_CANCELED) {
			}
			break;
		case DIALOG_FRAGMENT_BODY_WEIGHT:
			if (resultCode == Activity.RESULT_OK) {
				updateBodyWeightSummary();
			} else if (resultCode == Activity.RESULT_CANCELED) {
			}
			break;
		case DIALOG_FRAGMENT_STEP_LENGTH:
			if (resultCode == Activity.RESULT_OK) {
				updateStepLengthSummary();
			} else if (resultCode == Activity.RESULT_CANCELED) {
			}
			break;
		case DIALOG_FRAGMENT_RUN_LENGTH:
			if (resultCode == Activity.RESULT_OK) {
				updateRunLengthSummary();
			} else if (resultCode == Activity.RESULT_CANCELED) {
			}
			break;
		}
	}
}

// private void refresh() {
// finish();
// Intent myIntent = new Intent(this, FragmentSettings.class);
// startActivity(myIntent);
// }
//
// @Override
// public boolean onCreateOptionsMenu(Menu menu) {
// MenuInflater inflater = getMenuInflater();
// inflater.inflate(R.menu.dummy, menu);
// menu.getItem(0).setEnabled(false);
// menu.getItem(1).setEnabled(false);
// return true;
// }
//
// @Override
// public boolean onOptionsItemSelected(MenuItem item) {
// int id = item.getItemId();
// if (id == android.R.id.home) {
// Intent upIntent = new Intent(this, Pedometer.class);
// if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
// TaskStackBuilder.from(this).addNextIntent(upIntent).startActivities();
// finish();
// } else {
// NavUtils.navigateUpTo(this, upIntent);
// }
// return true;
// }
// return super.onOptionsItemSelected(item);
// }
//
// @Override
// public boolean onKeyDown(int keyCode, KeyEvent event) {
// if (keyCode == KeyEvent.KEYCODE_BACK) {
// Intent intent;
// int[] array = new int[2];
// array[0] = Pedometer.SPINNER_ITEM_PEDOMETER;
// intent = new Intent(getBaseContext(), Pedometer.class);
// intent.putExtra("navigation_intent", array);
// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
// // | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// startActivity(intent);
// return true;
// }
// return super.onKeyDown(keyCode, event);
// }
//
// public void popDialog(View v) {
// new ColorMixerDialog(this,1, onDialogSet).show();
// }
//
// private ColorMixer.OnColorChangedListener onDialogSet = new
// ColorMixer.OnColorChangedListener() {
// public void onColorChange(int argb) {
// // mixer.setColor(argb);
// // color.setText(Integer.toHexString(argb));
// }
// };
