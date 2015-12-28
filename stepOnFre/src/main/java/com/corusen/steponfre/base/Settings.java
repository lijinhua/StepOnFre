/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-sum
 */

//package com.corusen.steponfre.base;
//
//import java.util.Locale;
//
//import com.corusen.steponfre.base.AnalyticsSampleApp.TrackerName;
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.preferences.BodyWeightPreference;
//import com.corusen.steponfre.preferences.RunLengthPreference;
//import com.corusen.steponfre.preferences.StepLengthPreference;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//
//import android.app.ActionBar;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
//import android.content.res.Configuration;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.preference.DialogPreference;
//import android.preference.ListPreference;
//import android.preference.Preference;
//import android.preference.PreferenceActivity;
//import android.preference.PreferenceCategory;
//import android.preference.PreferenceManager;
//import android.support.v4.app.NavUtils;
//import android.support.v4.app.TaskStackBuilder;
////import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//
//public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
//	private static final String TAG = "Settings";
//	public static final String ACCUPEDO_SETTINGS_RELOAD = "com.corusen.steponfre.ACCUPEDO_SETTINGS_RELOAD";
//	private ListPreference mUnitListPreference;
//	private ListPreference mLocaleListPreference;
//	private StepLengthPreference mStepLengthPreference;
//	private RunLengthPreference mRunLengthPreference;
//	private BodyWeightPreference mBodyWeightPreference;
//
//	private ActionBar actionBar;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		addPreferencesFromResource(R.xml.preferences);
//		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//		mUnitListPreference = (ListPreference) getPreferenceScreen().findPreference("units");
//		mLocaleListPreference = (ListPreference) getPreferenceScreen().findPreference("locale_type");
//		mStepLengthPreference = (StepLengthPreference) getPreferenceScreen().findPreference("step_length");
//		mRunLengthPreference = (RunLengthPreference) getPreferenceScreen().findPreference("run_length");
//		mBodyWeightPreference = (BodyWeightPreference) getPreferenceScreen().findPreference("body_weight");
//		//Log.i("Settings", "Total:" + ((Integer) getPreferenceScreen().getPreferenceCount()).toString());
//		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
//			initSummary(getPreferenceScreen().getPreference(i));
//		}
//
//		actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setHomeButtonEnabled(true);
//		actionBar.setDisplayShowTitleEnabled(true);
//		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(AccuService.mScreenAcitionBarColor)));
//	}
//
//	@Override
//	public void onStart() {
//		super.onStart();
//		Tracker t = ((AnalyticsSampleApp) this.getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Settings");
//		t.send(new HitBuilders.AppViewBuilder().build());
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//	}
//
//	@Override
//	protected void onRestart() {
//		super.onRestart();
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
//		// actionBar.setSelectedNavigationItem(Pedometer.SPINNER_ITEM_SETTINGS);
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
//		this.sendBroadcast(new Intent(ACCUPEDO_SETTINGS_RELOAD));
//	}
//
//	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//
//		if (key.equals("units")) {
//			Float length, weight, runlength;
//
//			if (mUnitListPreference.getValue().compareTo("metric") == 0) {
//				length = mStepLengthPreference.getFloat() * Utils.INCH2CM;
//				runlength = mRunLengthPreference.getFloat() * Utils.INCH2CM;
//				weight = mBodyWeightPreference.getFloat() / Utils.KG2LB;
//			} else {
//				length = mStepLengthPreference.getFloat() / Utils.INCH2CM;
//				runlength = mRunLengthPreference.getFloat() / Utils.INCH2CM;
//				weight = mBodyWeightPreference.getFloat() * Utils.KG2LB;
//			}
//
//			mStepLengthPreference.setFloat(length);
//			mRunLengthPreference.setFloat(runlength);
//			mBodyWeightPreference.setFloat(weight);
//		}
//
//		// if (key.equals(KEY_A_CHECKBOX_PREFERENCE)) {
//		// mCheckBoxPreference.setSummary(sharedPreferences.getBoolean(key,
//		// false) ? "Disable this setting" : "Enable this setting");
//		// } else if (key.equals(KEY_AN_EDITTEXT_PREFERENCE)) {
//		// mEditBoxPreference.setSummary("Current value is "
//		// + sharedPreferences.getString(key, ""));
//		// }
//
//		updatePrefSummary(findPreference(key));
//
//		if (key.equals("locale_type")) {
//			Locale locale;
//
//			if (mLocaleListPreference.getValue().compareTo("0") == 0) {
//				locale = Locale.getDefault();
//			} else {
//				locale = new Locale("en");
//			}
//
//			Configuration config = new Configuration();
//			config.locale = locale;
//			this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
//			refresh();
//		}
//
//	}
//
//	/**
//	 * Change here for PreferenceCategory <-->PreferenceScreen
//	 */
//	private void initSummary(Preference p) {
//		if (p instanceof PreferenceCategory) {
//			PreferenceCategory pCat = (PreferenceCategory) p;
//			//Log.i("Settings", "TotalPScreen:" + ((Integer) pCat.getPreferenceCount()).toString());
//			for (int i = 0; i < pCat.getPreferenceCount(); i++) {
//				initSummary(pCat.getPreference(i));
//			}
//		} else {
//			updatePrefSummary(p);
//		}
//	}
//
//	private void updatePrefSummary(Preference p) {
//		if (p instanceof ListPreference) {
//			ListPreference listPref = (ListPreference) p;
//			p.setSummary(listPref.getEntry());
//		}
//		if (p instanceof DialogPreference) {
//			DialogPreference editTextPref = (DialogPreference) p;
//			p.setSummary(editTextPref.getSummary());
//		}
//	}
//
//	private void refresh() {
//		finish();
//		Intent myIntent = new Intent(this, Settings.class);
//		startActivity(myIntent);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.dummy, menu);
//		menu.getItem(0).setEnabled(false);
//		menu.getItem(1).setEnabled(false);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//		if (id == android.R.id.home) {
//			Intent upIntent = new Intent(this, Pedometer.class);
//			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//				TaskStackBuilder.from(this).addNextIntent(upIntent).startActivities();
//				finish();
//			} else {
//				NavUtils.navigateUpTo(this, upIntent);
//			}
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent intent;
//			int[] array = new int[2];
//			array[0] = Pedometer.SPINNER_ITEM_PEDOMETER;
//			intent = new Intent(getBaseContext(), Pedometer.class);
//			intent.putExtra("navigation_intent", array);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//			startActivity(intent);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//}
