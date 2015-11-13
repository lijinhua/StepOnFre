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
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.MathHelper;

import com.corusen.steponfre.base.AnalyticsSampleApp;
import com.corusen.steponfre.R;
import com.corusen.steponfre.base.AccuService;
import com.corusen.steponfre.base.Pedometer;
import com.corusen.steponfre.base.PedometerSettings;
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
import android.content.res.Resources;
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
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class ChartActivity extends FragmentActivity {

	public static final String TYPE = "type";
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;
	private String mDateFormat;

	private SharedPreferences mSettings;
	private static PedometerSettings mPedometerSettings;
	private static int mWeekFormat;

	private static Calendar mCurrent;
	private static Calendar mToday;
	private static Calendar mFirstDay;
	private static Button mButtonDay;
	private static Button mButtonWeek;
	private static Button mButtonMonth;
	private static Button mButtonYear;

	private static final int FLAG_CHART_DAY = 0;
	private static final int FLAG_CHART_WEEK = 1;
	private static final int FLAG_CHART_MONTH = 2;
	private static final int FLAG_CHART_YEAR = 3;
	private static int mFlagChartMode = FLAG_CHART_DAY;

	private static GraphicalView mChartView;
	private static IChart mChart;

	private static boolean mScreenLarge = true;

	private static int mNumberDays;
	private static int mNumberWeeks;
	private static int mNumberMonths;
	private static int mNumberYears;

	private ActionBar actionBar;

	private AdView adView;

	// private static MyDB mDB;

	static List<double[]> mX = new ArrayList<double[]>();
	static List<double[]> mValues = new ArrayList<double[]>();
	DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

	ViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(AccuService.mScreenCollectionChart);

		// java.lang.NullPointerException errors in TE, onResume
		// Repeat just in case
		mButtonDay = (Button) findViewById(R.id.day);
		mButtonWeek = (Button) findViewById(R.id.week);
		mButtonMonth = (Button) findViewById(R.id.month);
		mButtonYear = (Button) findViewById(R.id.year);

		mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(AccuService.mScreenAcitionBarColor)));

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDemoCollectionPagerAdapter);

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
	public void onStart() {
		super.onStart();
		Tracker t = ((AnalyticsSampleApp) this.getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Chart");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chart_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent upIntent = new Intent(this, Pedometer.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.from(this).addNextIntent(upIntent).startActivities();
				finish();
			} else {
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		} else if (id == R.id.menu_to_statistics) {

			sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
			Intent intent = new Intent(getBaseContext(), com.corusen.steponfre.chart.StatisticsActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent;
			int[] array = new int[2];
			array[0] = Pedometer.SPINNER_ITEM_PEDOMETER;
			intent = new Intent(getBaseContext(), Pedometer.class);
			intent.putExtra("navigation_intent", array);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

		public DemoCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new DemoObjectFragment();
			Bundle args = new Bundle();
			args.putInt(DemoObjectFragment.ARG_OBJECT, i);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {

			int count;
			switch (mFlagChartMode) {
			case FLAG_CHART_DAY:
				count = mNumberDays;
				break;
			case FLAG_CHART_WEEK:
				count = mNumberWeeks;
				break;
			case FLAG_CHART_MONTH:
				count = mNumberMonths;
				break;
			case FLAG_CHART_YEAR:
				count = mNumberYears;
				break;
			default:
				count = 0;
				break;
			}
			return count;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Calendar current = (Calendar) mToday.clone();
			String string;
			switch (mFlagChartMode) {
			case FLAG_CHART_DAY:
				// Log.i("Chart_Fragment", ((Integer)position).toString());
				current.add(Calendar.DATE, -(mNumberDays - 1 - position));
				string = DateFormat.format("E, MMM dd", current).toString();
				break;
			case FLAG_CHART_WEEK:
				current.add(Calendar.WEEK_OF_YEAR, -(mNumberWeeks - 1 - position));
				// current.add(Calendar.DATE, -(mNumberWeeks*7-1-position*7));
				string = DateFormat.format("MMM dd, yyyy", current).toString();
				break;
			case FLAG_CHART_MONTH:
				current.add(Calendar.MONTH, -(mNumberMonths - 1 - position));
				string = DateFormat.format("MMM, yyyy", current).toString();
				break;
			case FLAG_CHART_YEAR:
				current.add(Calendar.YEAR, -(mNumberYears - 1 - position));
				string = DateFormat.format("yyyy", current).toString();
				break;

			default:
				string = " ";
				break;
			}

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

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DemoObjectFragment extends Fragment {

		public static final String ARG_OBJECT = "object";
		View mRootView;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.light_fragment_chart, container, false);
			Bundle args = getArguments();
			int position = args.getInt(ARG_OBJECT);
			// ((TextView) mRootView.findViewById(android.R.id.text1))
			// .setText(Integer.toString(position));
			// Log.i("Chart_Fragment", ((Integer)position).toString());

			if (mToday == null) { // NullPoint error check from crash
				mToday = Calendar.getInstance();
			}
			mCurrent = (Calendar) mToday.clone();
			switch (mFlagChartMode) {
			case FLAG_CHART_DAY:
				mCurrent.add(Calendar.DATE, -(mNumberDays - 1 - position));
				break;
			case FLAG_CHART_WEEK:
				mCurrent.add(Calendar.WEEK_OF_YEAR, -(mNumberWeeks - 1 - position));
				// mCurrent.add(Calendar.DATE, -(mNumberWeeks*7-1-position*7));
				break;
			case FLAG_CHART_MONTH:
				mCurrent.add(Calendar.MONTH, -(mNumberMonths - 1 - position));
				break;
			case FLAG_CHART_YEAR:
				mCurrent.add(Calendar.YEAR, -(mNumberYears - 1 - position));
				break;
			default:
				break;
			}

			updateChart();
			return mRootView;
		}

		public void updateChart() {

			Resources res = this.getResources();
			if (res != null) { // java.lang.NullPointerException in TE
				switch (mFlagChartMode) {
				case FLAG_CHART_DAY:
					mButtonDay.setBackgroundDrawable(res.getDrawable(AccuService.mScreenChartSelectBar));
					mButtonWeek.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonMonth.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonYear.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonDay.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextSelected));
					mButtonWeek.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonMonth.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonYear.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					// mButtonDay.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton2));
					// mButtonWeek.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonMonth.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonYear.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					dayChart();
					break;
				case FLAG_CHART_WEEK:
					mButtonDay.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonWeek.setBackgroundDrawable(res.getDrawable(AccuService.mScreenChartSelectBar));
					mButtonMonth.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonYear.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonDay.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonWeek.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextSelected));
					mButtonMonth.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonYear.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					// mButtonDay.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonWeek.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton2));
					// mButtonMonth.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonYear.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					weekChart();
					break;
				case FLAG_CHART_MONTH:
					mButtonDay.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonWeek.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonMonth.setBackgroundDrawable(res.getDrawable(AccuService.mScreenChartSelectBar));
					mButtonYear.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonDay.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonWeek.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonMonth.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextSelected));
					mButtonYear.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					// mButtonDay.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonWeek.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonMonth.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton2));
					// mButtonYear.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					monthChart();
					break;
				case FLAG_CHART_YEAR:
					mButtonDay.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonWeek.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonMonth.setBackgroundDrawable(res.getDrawable(R.drawable.mybutton0));
					mButtonYear.setBackgroundDrawable(res.getDrawable(AccuService.mScreenChartSelectBar));
					mButtonDay.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonWeek.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonMonth.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextNormal));
					mButtonYear.setTextColor(this.getResources().getColor(AccuService.mScreenChartButtonTextSelected));
					// mButtonDay.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonWeek.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonMonth.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton));
					// mButtonYear.setBackground(this.getResources().getDrawable(
					// R.drawable.mybutton2));
					yearChart();
					break;
				default:
					break;
				}
			}
		}

		private void dayChart() {
			LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.chart);
			mChart = new DayLineChart();
			setDayData();
			if (mChartView != null) {
				layout.removeView(mChartView);
			}
			mChartView = mChart.graphicalView(this, mX, mValues, mScreenLarge);
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}

		private void weekChart() {
			LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.chart);
			mChart = new WeekBarChart();
			setWeekData();
			if (mChartView != null) {
				layout.removeView(mChartView);
			}
			mChartView = mChart.graphicalView(this, mX, mValues, mScreenLarge);
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}

		private void monthChart() {
			LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.chart);
			mChart = new MonthBarChart();
			setMonthData();
			if (mChartView != null) {
				layout.removeView(mChartView);
			}
			mChartView = mChart.graphicalView(this, mX, mValues, mScreenLarge);
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}

		private void yearChart() {
			setYearBarData();
			drawYearBarChart();
			// setYearLineData();
			// drawYearLineChart();
		}

		public void setDayData() {
			int hour = 0, minute = 0, steps = 0, goalSteps = 0;
			int currentHour = 0, currentSteps = 0;

			// JS added for test
			double[] dHours = new double[25];
			double[] dSteps = new double[25];
			double[] dGoalHours = new double[25];
			double[] dGoalSteps = new double[25];

			goalSteps = mPedometerSettings.getGoalSteps();
			for (int k = 0; k <= 24; k++) {
				dHours[k] = k + 1;
				dSteps[k] = MathHelper.NULL_VALUE;
				dGoalHours[k] = k + 1;
				dGoalSteps[k] = (double) goalSteps;
			}

			if (Pedometer.mDB != null) {
				Pedometer.mDB.open();

				if (mCurrent == null) {
					mCurrent = Calendar.getInstance();
				}
				if (mToday == null) {
					mToday = Calendar.getInstance();
				}

				Cursor c = Pedometer.mDB.queryDaySteps(mCurrent.get(Calendar.YEAR), mCurrent.get(Calendar.MONTH) + 1, mCurrent.get(Calendar.DATE));

				// JS added to check if c== null :V351
				if (c != null) {
					if (c.getCount() > 0) {
						if (c.moveToFirst()) {
							do {
								hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
								minute = c.getInt(c.getColumnIndex(Constants.KEY_MINUTE));
								steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));

								if ((hour == 23) & (minute == 59)) {
									dHours[24] = ((Integer) 24).doubleValue();
									dSteps[24] = ((Integer) steps).doubleValue();
								} else if (minute == 0) { // discard 30min data
									dHours[hour] = ((Integer) hour).doubleValue();
									dSteps[hour] = ((Integer) steps).doubleValue();
								}
							} while (c.moveToNext());
							c.close();
						}
					}
				}

				Pedometer.mDB.close();

				currentHour = hour;
				currentSteps = steps;

				if (mCurrent.compareTo(mToday) == 0) { // add current step
														// counts
					dHours[currentHour] = ((Integer) currentHour).doubleValue();
					dSteps[currentHour] = ((Integer) currentSteps).doubleValue();
				}

				for (int k = 1; k <= hour; k++) {
					if (dSteps[k] == MathHelper.NULL_VALUE) {
						dSteps[k] = dSteps[k - 1];
					}
				}
			}

			mX.clear();
			mValues.clear();
			mX.add(dHours);
			mX.add(dGoalHours);
			mValues.add(dSteps);
			mValues.add(dGoalSteps);

		}

		public void setWeekData() {
			int i, steps, weekday;
			double[] dSteps = new double[7];
			Calendar cal;

			for (i = 0; i < 7; i++) {
				dSteps[i] = 0.0;
			}

			weekday = mCurrent.get(Calendar.DAY_OF_WEEK); // SUNDAY is 1

			Pedometer.mDB.open();
			switch (mWeekFormat) {
			case 0: // american
				for (i = 1; i <= 7; i++) {
					cal = (Calendar) mCurrent.clone();
					cal.add(Calendar.DATE, -(weekday - i));

					Cursor c = Pedometer.mDB.queryDayMaxSteps(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
					if (c.moveToLast()) {
						steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
						dSteps[i - 1] = ((Integer) steps).doubleValue();
						c.close();
					}
				}
				break;
			case 1: // european
				if (weekday == 1) { // weekday is Sunday
					for (i = 1; i <= 7; i++) {
						cal = (Calendar) mCurrent.clone();
						cal.add(Calendar.DATE, -7 + i);
						Cursor c = Pedometer.mDB.queryDayMaxSteps(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
						if (c.moveToLast()) {
							steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
							dSteps[i - 1] = ((Integer) steps).doubleValue();
							c.close();
						}
					}

				} else {
					for (i = 1; i <= 7; i++) { // weekday is > 1
						cal = (Calendar) mCurrent.clone();
						cal.add(Calendar.DATE, -(weekday - i - 1));
						Cursor c = Pedometer.mDB.queryDayMaxSteps(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
						if (c.moveToLast()) {
							steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
							dSteps[i - 1] = ((Integer) steps).doubleValue();
							c.close();
						}
					}
				}
				break;
			}
			Pedometer.mDB.close();

			mX.clear();
			mValues.clear();
			mValues.add(dSteps);
		}

		public void setMonthData() {
			int i, year, month, steps;
			double[] dSteps = new double[31];

			for (i = 0; i < 31; i++) {
				dSteps[i] = 0.0;
			}

			year = mCurrent.get(Calendar.YEAR);
			month = mCurrent.get(Calendar.MONTH) + 1; // first month is 0

			Pedometer.mDB.open();
			Cursor c = Pedometer.mDB.queryDayMaxStepsForMonth(year, month);
			if (c.moveToFirst()) {
				do {
					i = c.getInt(c.getColumnIndex(Constants.KEY_DAY));
					steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
					dSteps[i - 1] = ((Integer) steps).doubleValue();
				} while (c.moveToNext());
				c.close();
			}
			Pedometer.mDB.close();

			mX.clear();
			mValues.clear();
			mValues.add(dSteps);
		}

		public void setYearBarData() {
			int i, year, sum, num;
			double[] sumSteps = new double[12];
			double[] aveSteps = new double[12];

			for (i = 0; i < 12; i++) {
				sumSteps[i] = 0.0;
				aveSteps[i] = 0.0;
			}

			year = mCurrent.get(Calendar.YEAR);
			int totalcount = 0;
			Pedometer.mDB.open();
			for (i = 1; i <= 12; i++) {
				Cursor c = Pedometer.mDB.queryDayMaxStepsForMonth(year, i);
				sum = 0;
				num = 0;
				if (c.moveToFirst()) {
					do {
						sum = sum + c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
						num++;
						totalcount++;
					} while (c.moveToNext());
					c.close();
				}

				sumSteps[i - 1] = (double) sum; // average
				if (num > 0) {
					aveSteps[i - 1] = (double) (sum / num); // average
				}
			}
			Pedometer.mDB.close();

			int totalSteps = 0;
			int aveTotalAveSteps = 0;

			for (i = 0; i < aveSteps.length; i++) {
				totalSteps = totalSteps + (int) sumSteps[i];
			}

			if (totalcount == 0) {
				aveTotalAveSteps = 0;
			} else {
				aveTotalAveSteps = totalSteps / totalcount;
			}

			sumSteps[0] = (double) totalSteps;
			sumSteps[1] = (double) aveTotalAveSteps;

			mX.clear();
			mValues.clear();
			mX.add(sumSteps); // additional data to count the total steps
			mValues.add(aveSteps);

		}

		public void setYearLineData() {
			double[] dDays = new double[366];
			double[] dSteps = new double[366];
			double[] dGoalSteps = new double[366];
			int year, steps;

			year = mCurrent.get(Calendar.YEAR);

			Pedometer.mDB.open();
			Cursor c = Pedometer.mDB.queryAllDayMaxSteps(year);

			int goalSteps = mPedometerSettings.getGoalSteps();
			for (int k = 0; k <= 365; k++) {
				dDays[k] = k + 1;
				dSteps[k] = 0;
				dGoalSteps[k] = (double) goalSteps;
			}

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			int index = 0;
			if (c.moveToFirst()) {
				do {
					calendar.set(Calendar.MONTH, c.getInt(c.getColumnIndex(Constants.KEY_MONTH)));
					calendar.set(Calendar.DATE, c.getInt(c.getColumnIndex(Constants.KEY_DAY)));
					index = calendar.get(Calendar.DAY_OF_YEAR) - 1; // first day
																	// is 1
					steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
					// goalsteps =
					// c.getInt(c.getColumnIndex(Constants.KEY_ACHIEVEMENT));
					dSteps[index] = ((Integer) steps).doubleValue();
				} while (c.moveToNext());
				c.close();
			}
			Pedometer.mDB.close();

			mX.clear();
			mValues.clear();

			mX.add(dDays);
			mX.add(dDays);
			mValues.add(dSteps);
			mValues.add(dGoalSteps);

		}

		private void drawYearBarChart() {
			LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.chart);
			mChart = new YearBarChart();

			if (mChartView != null) {
				layout.removeView(mChartView);
			}
			mChartView = mChart.graphicalView(this, mX, mValues, mScreenLarge);
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}

		// private void drawYearLineChart() {
		// LinearLayout layout = (LinearLayout) mRootView
		// .findViewById(R.id.chart);
		// mChart = new YearLineChart();
		//
		// if (mChartView != null) {
		// layout.removeView(mChartView);
		// }
		// mChartView = mChart.graphicalView(this, mX, mValues, mScreenLarge);
		// layout.addView(mChartView, new LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// }
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
		mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
		mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
		mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
		mDateFormat = savedState.getString("date_format");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("dataset", mDataset);
		outState.putSerializable("renderer", mRenderer);
		outState.putSerializable("current_series", mCurrentSeries);
		outState.putSerializable("current_renderer", mCurrentRenderer);
		outState.putString("date_format", mDateFormat);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// mDB = new MyDB(this);
		// mDB.open();

		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// int width = size.x; //
		int width = display.getWidth();
		if (width <= 320)
			mScreenLarge = false;

		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);
		mWeekFormat = mPedometerSettings.getWeekFormat();

		initializeDateFormat();

		mButtonDay = (Button) findViewById(R.id.day);
		mButtonWeek = (Button) findViewById(R.id.week);
		mButtonMonth = (Button) findViewById(R.id.month);
		mButtonYear = (Button) findViewById(R.id.year);

		mButtonDay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFlagChartMode = FLAG_CHART_DAY;
				mCurrent = (Calendar) mToday.clone();
				mViewPager.getAdapter().notifyDataSetChanged();
				mViewPager.setCurrentItem(mNumberDays - 1);
				mViewPager.getAdapter().notifyDataSetChanged();
			}
		});
		mButtonWeek.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFlagChartMode = FLAG_CHART_WEEK;
				mCurrent = (Calendar) mToday.clone();
				mViewPager.getAdapter().notifyDataSetChanged();
				mViewPager.setCurrentItem(mNumberWeeks - 1);
				mViewPager.getAdapter().notifyDataSetChanged();
			}
		});
		mButtonMonth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFlagChartMode = FLAG_CHART_MONTH;
				mCurrent = (Calendar) mToday.clone();
				mViewPager.getAdapter().notifyDataSetChanged();
				mViewPager.setCurrentItem(mNumberMonths - 1);
				mViewPager.getAdapter().notifyDataSetChanged();
			}
		});
		mButtonYear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFlagChartMode = FLAG_CHART_YEAR;
				mCurrent = (Calendar) mToday.clone();
				mViewPager.getAdapter().notifyDataSetChanged();
				mViewPager.setCurrentItem(mNumberYears - 1);
				mViewPager.getAdapter().notifyDataSetChanged();
			}
		});

		mViewPager.setCurrentItem(mNumberDays - 1);
		mViewPager.getAdapter().notifyDataSetChanged();
	}

	private void initializeDateFormat() {

		// mDateDisplyay = (TextView) findViewById(R.id.date_display);
		mToday = Calendar.getInstance();
		mCurrent = (Calendar) mToday.clone();
		mFirstDay = (Calendar) mToday.clone();

		// Cursor c = dba.getDayByKey(1);
		Pedometer.mDB.open();
		Cursor c = Pedometer.mDB.queryFirstDay();

		mFirstDay.set(Calendar.YEAR, c.getInt(c.getColumnIndex(Constants.KEY_YEAR)));
		mFirstDay.set(Calendar.MONTH, c.getInt(c.getColumnIndex(Constants.KEY_MONTH)) - 1);
		mFirstDay.set(Calendar.DATE, c.getInt(c.getColumnIndex(Constants.KEY_DAY)));
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
		mNumberWeeks = getNumberWeeks();
		mNumberMonths = getNumberMonths();
		mNumberYears = getNumberYears();
	}

	private int getNumberDays() {
		int numberDays;
		long diff = mToday.getTimeInMillis() - mFirstDay.getTimeInMillis();
		numberDays = (int) (diff / (24 * 60 * 60 * 1000));
		return numberDays + 1;
	}

	private int getNumberWeeks() {
		int quotient, remainder, weekday, numberWeeks;
		quotient = (int) (mNumberDays / 7);
		remainder = mNumberDays % 7;
		weekday = mToday.get(Calendar.DAY_OF_WEEK); // SUNDAY is 1
		numberWeeks = quotient;
		if (remainder >= weekday) { // need to consider first day of week???
			numberWeeks++;
		}
		return numberWeeks + 1;
	}

	private int getNumberMonths() {
		int numberMonths;
		numberMonths = (mToday.get(Calendar.YEAR) - mFirstDay.get(Calendar.YEAR)) * 12;
		numberMonths = numberMonths - mFirstDay.get(Calendar.MONTH) + 1;
		numberMonths += mToday.get(Calendar.MONTH);
		return numberMonths;
	}

	private int getNumberYears() {
		int numberYears = mToday.get(Calendar.YEAR) - mFirstDay.get(Calendar.YEAR) + 1;
		return numberYears;
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
