/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.corusen.steponfre.chart;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.GraphicalView;

import com.corusen.steponfre.base.AnalyticsSampleApp;
import com.corusen.steponfre.R;
import com.corusen.steponfre.base.AccuService;
import com.corusen.steponfre.base.Pedometer;
import com.corusen.steponfre.base.PedometerSettings;
import com.corusen.steponfre.base.Utils;
import com.corusen.steponfre.base.AnalyticsSampleApp.TrackerName;
import com.corusen.steponfre.database.Constants;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StatisticsActivity extends FragmentActivity {

	public static final int MAX_NUMBER_LAPS = 20;
	public static final String TYPE = "type";
	private SharedPreferences mSettings;
	private static PedometerSettings mPedometerSettings;
	private static int mWeekFormat;

	private static Calendar mCurrent;
	private static Calendar mToday;
	private static Calendar mFirstDay;

	private static GraphicalView mChartView;
	private static int mNumberDays;

	private ActionBar actionBar;

	private static Boolean mIsMetric;
	private static float mfDistanceFactor;
	public static String msDistanceUnit;
	// private AdView adView;

	// static MyDB mDB;

	static List<double[]> mX = new ArrayList<double[]>();
	static List<double[]> mValues = new ArrayList<double[]>();
	DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

	/**
	 * The {@link android.support.v4.view.ViewPager} that will display the
	 * object collection.
	 */
	ViewPager mViewPager;

	private AdView adView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.light_collection_statistics);

		mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(
				getSupportFragmentManager());

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(AccuService.mScreenAcitionBarColor)));

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDemoCollectionPagerAdapter);

		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);
		mWeekFormat = mPedometerSettings.getWeekFormat();

		// JS V115
		final UncaughtExceptionHandler defaultHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				if (thread.getName().startsWith("AdWorker")) {
					Log.w("ADMOB", "AdWorker thread thrown an exception.", ex);
				} else if (defaultHandler != null) {
					defaultHandler.uncaughtException(thread, ex);
				} else {
					throw new RuntimeException(
							"No default uncaught exception handler.", ex);
				}
			}
		});

		if (Pedometer.getInstance() != null) {
			LocationManager coarseLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			Location coarseLocation = coarseLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Date birthday = Pedometer.mPedometerSettings.getBirthDate();
			int gender = Pedometer.mPedometerSettings.getGender();

			adView = new AdView(this);
			adView.setAdSize(AdSize.SMART_BANNER);
			adView.setAdUnitId(Constants.ACCUPEDO_ADMOB_ID);
			LinearLayout layout = (LinearLayout) findViewById(R.id.adView);
			layout.addView(adView);

			AdRequest adRequest = new AdRequest.Builder().setLocation(coarseLocation).setGender(gender).setBirthday(birthday).build();
			adView.loadAd(adRequest);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.statistics_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent upIntent = new Intent(this, Pedometer.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.from(this).addNextIntent(upIntent)
						.startActivities();
				finish();
			} else {
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		} else if (id == R.id.menu_to_chart) {
			sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
			Intent intent = new Intent(getBaseContext(),
					com.corusen.steponfre.chart.ChartActivity.class);

			startActivity(intent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class DemoCollectionPagerAdapter extends
			FragmentStatePagerAdapter {

		public DemoCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new DemoObjectFragment();
			Bundle args = new Bundle();
			args.putInt(DemoObjectFragment.ARG_OBJECT, i); // i+1 Our object is
															// just an
															// integer :-P
			// Log.i("Chart_Fragment", "..." + ((Integer) i).toString());
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {

			int count;

			count = mNumberDays;

			return count;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Calendar current = (Calendar) mToday.clone();
			String string;
			current.add(Calendar.DATE, -(mNumberDays - 1 - position));
			string = DateFormat.format("E, MMM dd", current).toString();
			return string;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			return container;
		}
	}

	public static class DemoObjectFragment extends Fragment {

		public static final String ARG_OBJECT = "object";
		View mRootView;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(AccuService.mScreenFragmentStatistics,
					container, false);
			Bundle args = getArguments();
			int position = args.getInt(ARG_OBJECT);

			mCurrent = (Calendar) mToday.clone();
			mCurrent.add(Calendar.DATE, -(mNumberDays - 1 - position));

			updateHourlyChart();
			return mRootView;
		}

		private void updateHourlyChart() {
			LinearLayout layout = (LinearLayout) mRootView
					.findViewById(R.id.chart_hourly);

			IChart1 mChart = new HourBarChart1();

			boolean mScreenLarge = true;
			setDayHourData();
			if (mChartView != null) {
				layout.removeView(mChartView);
			}
			mChartView = mChart.graphicalView(this, mX, mValues, mScreenLarge);
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}

		public void setDayHourData() {
			int i, steps, hour, minute, goalsteps;
			float calories, distance, percent;
			long steptime;

			int[] lapsteps = new int[MAX_NUMBER_LAPS];
			float[] lapdistance = new float[MAX_NUMBER_LAPS];
			float[] lapcalories = new float[MAX_NUMBER_LAPS];

			double[] dSteps = new double[25];
			double[] hSteps = new double[25];

			for (i = 0; i <= 24; i++) {
				dSteps[i] = 0.0;
				hSteps[i] = 0.0;
			}

			for (i = 0; i < MAX_NUMBER_LAPS; i++) {
				lapsteps[i] = 0;
				lapdistance[i] = 0.00f;
				lapcalories[i] = 0.0f;
			}

			Pedometer.mDB.open();
			Cursor c1 = Pedometer.mDB.queryLapMaxStepsForDay(
					mCurrent.get(Calendar.YEAR),
					mCurrent.get(Calendar.MONTH) + 1,
					mCurrent.get(Calendar.DATE));

			i = 0;
			int count = c1.getCount();
			if (c1.moveToFirst()) {
				do {
					lapsteps[i] = c1.getInt(c1
							.getColumnIndex(Constants.KEY_LAPSTEPS));
					lapcalories[i] = c1.getFloat(c1
							.getColumnIndex(Constants.KEY_LAPCALORIES));
					lapdistance[i] = c1.getFloat(c1
							.getColumnIndex(Constants.KEY_LAPDISTANCE));
					i++;
					if (i >= MAX_NUMBER_LAPS) {
						count = MAX_NUMBER_LAPS;
						break;
					}
				} while (c1.moveToNext());
				c1.close();
			}

			TableLayout table = (TableLayout) mRootView
					.findViewById(R.id.TableLayout01);
			// table.setStretchAllColumns(true); //cause FC

			for (i = 0; i < count; i++) {
				TableRow row = new TableRow(this.getActivity());
				TextView tv1 = new TextView(this.getActivity());
				TextView tv2 = new TextView(this.getActivity());
				TextView tv3 = new TextView(this.getActivity());
				TextView tv4 = new TextView(this.getActivity());
				TextView tv5 = new TextView(this.getActivity());
				TextView tv6 = new TextView(this.getActivity());
				TextView tv7 = new TextView(this.getActivity());

				tv1.setTextColor(getResources().getColor(R.color.mylightgray));
				tv1.setTextSize((float) 16.0);
				tv1.setText(getResources().getString(R.string.lap) + " "
						+ String.format("%d", i + 1));

				tv2.setTextColor(getResources().getColor(R.color.mywhite));
				tv2.setTextSize((float) 16.0);
				tv2.setText("  " + String.format("%d", lapsteps[i]));

				tv3.setTextColor(getResources().getColor(R.color.mylightgray));
				tv3.setTextSize((float) 12.0);
				tv3.setText(" " + getString(R.string.widget_st));

				tv4.setTextColor(getResources().getColor(R.color.mywhite));
				tv4.setTextSize((float) 16.0);
				tv4.setText("  " + String.format("%d", (int) lapcalories[i]));

				tv5.setTextColor(getResources().getColor(R.color.mylightgray));
				tv5.setTextSize((float) 12.0);
				tv5.setText(" " + getString(R.string.widget_cal));

				tv6.setTextColor(getResources().getColor(R.color.mywhite));
				tv6.setTextSize((float) 16.0);
				tv6.setText("  "
						+ String.format("%.2f", lapdistance[i]
								* mfDistanceFactor));

				tv7.setTextColor(getResources().getColor(R.color.mylightgray));
				tv7.setTextSize((float) 12.0);
				tv7.setText(" " + msDistanceUnit);

				row.removeAllViews();
				row.addView(tv1);
				row.addView(tv2);
				row.addView(tv3);
				row.addView(tv4);
				row.addView(tv5);
				row.addView(tv6);
				row.addView(tv7);
				table.addView(row, new TableLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}

			Cursor c = Pedometer.mDB.queryDayAll(mCurrent.get(Calendar.YEAR),
					mCurrent.get(Calendar.MONTH) + 1,
					mCurrent.get(Calendar.DATE));

			if (c.moveToFirst()) {
				do {

					hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
					minute = c.getInt(c.getColumnIndex(Constants.KEY_MINUTE));
					steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));

					if ((hour == 23) & (minute == 59)) {
						dSteps[24] = ((Integer) steps).doubleValue();
					} else if (minute == 0) { // discard 30min data
						dSteps[hour] = ((Integer) steps).doubleValue();
					}
				} while (c.moveToNext());
			}

			goalsteps = 0;
			steps = 0;
			distance = 0.00f;
			calories = 0.0f;
			steptime = 0l;

			if (c.moveToLast()) {
				hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
				minute = c.getInt(c.getColumnIndex(Constants.KEY_MINUTE));
				steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
				dSteps[hour] = steps; // this is the last data, i.e. current
										// steps

				distance = c.getFloat(c.getColumnIndex(Constants.KEY_DISTANCE));
				calories = c.getFloat(c.getColumnIndex(Constants.KEY_CALORIES));
				steptime = c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME));
				goalsteps = c.getInt(c
						.getColumnIndex(Constants.KEY_ACHIEVEMENT));
				c.close();
			}

			Pedometer.mDB.close();

			if (goalsteps == 0) {
				percent = 50.0f;
			} else {
				percent = ((float) steps / (float) goalsteps) * 100.0f;
			}

			TextView mStepValueView = (TextView) mRootView
					.findViewById(R.id.step_value);
			TextView mDistanceValueView = (TextView) mRootView
					.findViewById(R.id.distance_value);
			TextView mCaloriesValueView = (TextView) mRootView
					.findViewById(R.id.calories_value);
			TextView mSteptimeValueView = (TextView) mRootView
					.findViewById(R.id.steptime_text_view);
			TextView mDailyPercentValueView = (TextView) mRootView
					.findViewById(R.id.daily_percent_value);
			TextView mDailyGoalValueView = (TextView) mRootView
					.findViewById(R.id.daily_goal_value);
			ProgressBar mProgressBar = (ProgressBar) mRootView
					.findViewById(R.id.trainprogressbar);

			TextView mDistanceUnitView = (TextView) mRootView
					.findViewById(R.id.distance_units);

			mDailyGoalValueView.setText("" + goalsteps);
			mDailyPercentValueView.setText(((Integer) (int) percent).toString()
					+ "%");
			mProgressBar.setProgress((int) percent);
			mStepValueView.setText("" + steps);
			mDistanceValueView.setText(String.format("%.2f", distance
					* mfDistanceFactor));
			mCaloriesValueView.setText(String.format("%d", (int) calories));
			mSteptimeValueView.setText(Utils
					.getHoursMinutesString((int) (steptime / 1000)));
			mDistanceUnitView.setText(msDistanceUnit);

			for (i = 0; i <= 24; i++) {
				if (i == 0) {
					hSteps[i] = 0.0;
				} else {
					if (dSteps[i] < dSteps[i - 1]) { // in case there are
														// missing steps
						dSteps[i] = dSteps[i - 1];
						hSteps[i - 1] = 0.0;
					} else {
						hSteps[i - 1] = (double) (dSteps[i] - dSteps[i - 1]);
					}
				}
			}

			mX.clear();
			mValues.clear();
			mValues.add(hSteps);
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		Tracker t = ((AnalyticsSampleApp) this.getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Statistics");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onResume() {
		super.onResume();
		// WindowManager wm = (WindowManager) this
		// .getSystemService(Context.WINDOW_SERVICE);
		// Display display = wm.getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// int width = size.x; //
		// int width = display.getWidth();
		// if (width <= 320)
		// mScreenLarge = false;

		// mDB = new MyDB(this);
		// mDB.open();

		initializeDateFormat();
		mViewPager.setCurrentItem(mNumberDays - 1);
		mViewPager.getAdapter().notifyDataSetChanged();

		mIsMetric = mPedometerSettings.isMetric();
		if (mIsMetric) {
			mfDistanceFactor = Utils.MILE2KM; // 1.60934f;
			msDistanceUnit = getString(R.string.km);
		} else {
			mfDistanceFactor = 1.0f;
			msDistanceUnit = getString(R.string.widget_mi);
		}
	}

	private void initializeDateFormat() {

		// mDateDisplyay = (TextView) findViewById(R.id.date_display);
		mToday = Calendar.getInstance();
		mCurrent = (Calendar) mToday.clone();
		mFirstDay = (Calendar) mToday.clone();

		// Cursor c = dba.getDayByKey(1);
		Pedometer.mDB.open();
		Cursor c = Pedometer.mDB.queryFirstDay();

		mFirstDay.set(Calendar.YEAR,
				c.getInt(c.getColumnIndex(Constants.KEY_YEAR)));
		mFirstDay.set(Calendar.MONTH,
				c.getInt(c.getColumnIndex(Constants.KEY_MONTH)) - 1);
		mFirstDay.set(Calendar.DATE,
				c.getInt(c.getColumnIndex(Constants.KEY_DAY)));
		mFirstDay.set(Calendar.HOUR_OF_DAY, 0);
		mFirstDay.set(Calendar.MINUTE, 0);
		c.close();
		Pedometer.mDB.close();

		switch (mWeekFormat) {
		case 0: // american
			mToday.setFirstDayOfWeek(Calendar.SUNDAY);
			mCurrent.setFirstDayOfWeek(Calendar.SUNDAY);
			mFirstDay.setFirstDayOfWeek(Calendar.SUNDAY);
			break;
		case 1: // european
			mToday.setFirstDayOfWeek(Calendar.MONDAY);
			mCurrent.setFirstDayOfWeek(Calendar.MONDAY);
			mFirstDay.setFirstDayOfWeek(Calendar.MONDAY);
			break;
		}

		mNumberDays = getNumberDays();

	}

	private int getNumberDays() {
		int numberDays;
		long diff = mToday.getTimeInMillis() - mFirstDay.getTimeInMillis();
		numberDays = (int) (diff / (24 * 60 * 60 * 1000));
		return numberDays + 1;
	}

	@Override
	protected void onPause() {
		super.onPause();
		// mDB.close();
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

}
