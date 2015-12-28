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






import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;

import com.corusen.steponfre.R;
import com.corusen.steponfre.database.Constants;
import com.corusen.steponfre.database.MyDB;

import java.util.Calendar;

public class ActivityHistoryEditsteps extends Activity {

	private int mCount;
	private boolean mIsToday;
	private boolean mIsDBInsert;

	private static int mNewCount;
	private static int mYear;
	private static int mMonth;
	private static int mDay;

	private NumberPicker mNP1;
	private NumberPicker mNP2;
	private NumberPicker mNP3;
	private NumberPicker mNP4;
	private NumberPicker mNP5;

	private MyDB mDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dark_fragment_editsteps);

		mDB = new MyDB(this);

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.mydarkblue)));
		}

		Calendar today = Calendar.getInstance();

		mYear = today.get(Calendar.YEAR);
		mMonth = today.get(Calendar.MONTH) + 1;
		mDay = today.get(Calendar.DATE);
		mIsToday = true;
		mIsDBInsert = false;

		int[] intArray;
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			intArray = extras.getIntArray("edit_history");
			if ((mYear == intArray[0]) && (mMonth == intArray[1]) && (mDay == intArray[2])) {
				mIsToday = true;
			} else {
				mYear   = intArray[0];
				mMonth  = intArray[1];
				mDay    = intArray[2];
				mCount  = intArray[3];
				mIsToday = false;
				if(mCount == -1) { mIsDBInsert = true; }
			}
		}

		mDB.open();
		Cursor c = mDB.queryDayMaxSteps(mYear, mMonth, mDay);
		int columnIndexSteps = c.getColumnIndex(Constants.KEY_STEPS);
		mCount = c.getInt(columnIndexSteps);
		c.close();
		mDB.close();

		if (mCount >= 100000) {
			mCount = 99999;
		}

		mNP1=(NumberPicker)findViewById(R.id.np1);
		mNP2=(NumberPicker)findViewById(R.id.np2);
		mNP3=(NumberPicker)findViewById(R.id.np3);
		mNP4=(NumberPicker)findViewById(R.id.np4);
		mNP5=(NumberPicker)findViewById(R.id.np5);

		mNP1.setMinValue(0);
		mNP2.setMinValue(0);
		mNP3.setMinValue(0);
		mNP4.setMinValue(0);
		mNP5.setMinValue(0);

		mNP1.setMaxValue(9);
		mNP2.setMaxValue(9);
		mNP3.setMaxValue(9);
		mNP4.setMaxValue(9);
		mNP5.setMaxValue(9);

		mNP1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		mNP2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		mNP3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		mNP4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		mNP5.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		int a1, a2, a3, a4, a5;

		a1 = (mCount / 10000);
		a2 = ((mCount - a1 * 10000) / 1000);
		a3 = ((mCount - a1 * 10000 - a2 * 1000) / 100);
		a4 = ((mCount - a1 * 10000 - a2 * 1000 - a3 * 100) / 10);
		a5 = (mCount - a1 * 10000 - a2 * 1000 - a3 * 100 - a4 * 10);

		mNP1.setValue(a1);
		mNP2.setValue(a2);
		mNP3.setValue(a3);
		mNP4.setValue(a4);
		mNP5.setValue(a5);

		Button mix = (Button) findViewById(R.id.btn_set_steps);
		mix.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateStatus();
				mNewCount = mCount;
				if (mIsToday) {
					ActivityHistoryEditsteps.this.sendBroadcast(new Intent( AccuService.ACCUPEDO_EDITSTEPS_TODAY_REQUEST));
				} else {
					if (mIsDBInsert) {
						ActivityHistoryEditsteps.this.sendBroadcast(new Intent(AccuService.ACCUPEDO_EDITSTEPS_HISTORY_INSERT_REQUEST));
					} else {
						ActivityHistoryEditsteps.this.sendBroadcast(new Intent( AccuService.ACCUPEDO_EDITSTEPS_HISTORY_REQUEST));
					}
				}
				finish();
			}
		});

		Button btnReset = (Button) findViewById(R.id.btn_reset);
		btnReset.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mNP1.setValue(0);
				mNP2.setValue(0);
				mNP3.setValue(0);
				mNP4.setValue(0);
				mNP5.setValue(0);
			}
		});

		updateStatus();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//int id = item.getItemId();
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public static int getNewCount() {
		return mNewCount;
	}

	public static int getYear() {
		return mYear;
	}

	public static int getMonth() {
		return mMonth;
	}

	public static int getDay() { return mDay; }

	private void updateStatus() {
		int v1, v2, v3, v4, v5;
		v1 = mNP1.getValue();
		v2 = mNP2.getValue();
		v3 = mNP3.getValue();
		v4 = mNP4.getValue();
		v5 = mNP5.getValue();
		mCount = v1 * 10000 + v2 * 1000 + v3 * 100 + v4 * 10 + v5;
	}

}






//import java.util.Calendar;
//import kankan.wheel.widget.OnWheelChangedListener;
//import kankan.wheel.widget.OnWheelScrollListener;
//import kankan.wheel.widget.WheelView;
//import kankan.wheel.widget.adapters.NumericWheelAdapter;
//
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.base.AnalyticsSampleApp.TrackerName;
//import com.corusen.steponfre.database.Constants;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.animation.AnticipateOvershootInterpolator;
//import android.widget.Button;
//
//public class ActivityHistoryEditsteps extends Activity {
//
//	// private View mView;
//	private int mCount;
//	private int mKeyID;
//	private boolean mIsToday;
//	private int mColumnIndexSteps;
//	private int mColumnIndexKeyID;
//
//	private static int mNewCount;
//	private static int mYear;
//	private static int mMonth;
//	private static int mDay;
//
//	// private AdView adView;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(AccuService.mScreenFragmentEditStepsDetail);
//
//		getActionBar().setDisplayHomeAsUpEnabled(true); // enabling action bar app icon_te and behaving it as toggle button
//		getActionBar().setHomeButtonEnabled(true);
//		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
//				.getColor(AccuService.mScreenAcitionBarColor)));
//
//		Calendar today = Calendar.getInstance();
//
//		mYear = today.get(Calendar.YEAR);
//		mMonth = today.get(Calendar.MONTH) + 1;
//		mDay = today.get(Calendar.DATE);
//		mIsToday = true;
//
//		int[] intArray;
//		Bundle extras = getIntent().getExtras();
//		if (extras == null) {
//
//		} else {
//			intArray = extras.getIntArray("edit_history");
//			if ((mYear == intArray[0]) && (mMonth == intArray[1])
//					&& (mDay == intArray[2])) {
//				mIsToday = true;
//			} else {
//				mYear = intArray[0];
//				mMonth = intArray[1];
//				mDay = intArray[2];
//				mCount = intArray[3];
//				mIsToday = false;
//			}
//		}
//
//		Pedometer.mDB.open();
//		Cursor c = Pedometer.mDB.queryDayMaxSteps(mYear, mMonth, mDay);
//		mColumnIndexSteps = c.getColumnIndex(Constants.KEY_STEPS);
//		mColumnIndexKeyID = c.getColumnIndex(Constants.KEY_ID);
//		mCount = c.getInt(mColumnIndexSteps);
//		mKeyID = c.getInt(mColumnIndexKeyID);
//		c.close();
//		Pedometer.mDB.close();
//
//		if (mCount >= 100000) {
//			mCount = 99999;
//		}
//
//		int a1, a2, a3, a4, a5;
//
//		a1 = (int) (mCount / 10000);
//		a2 = (int) ((mCount - a1 * 10000) / 1000);
//		a3 = (int) ((mCount - a1 * 10000 - a2 * 1000) / 100);
//		a4 = (int) ((mCount - a1 * 10000 - a2 * 1000 - a3 * 100) / 10);
//		a5 = (int) (mCount - a1 * 10000 - a2 * 1000 - a3 * 100 - a4 * 10);
//
//		initWheel(R.id.passw_1, a1);
//		initWheel(R.id.passw_2, a2);
//		initWheel(R.id.passw_3, a3);
//		initWheel(R.id.passw_4, a4);
//		initWheel(R.id.passw_5, a5);
//
//		Button mix = (Button) findViewById(R.id.btn_set_steps);
//		mix.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				//openSetStepsAlertDialog();
//				mNewCount = mCount;
//				if (mIsToday) {
//					ActivityHistoryEditsteps.this
//							.sendBroadcast(new Intent(
//									AccuService.ACCUPEDO_EDITSTEPS_TODAY_REQUEST));
//				} else {
//					ActivityHistoryEditsteps.this
//					.sendBroadcast(new Intent(
//							AccuService.ACCUPEDO_EDITSTEPS_HISTORY_REQUEST));
////					Pedometer.mDB.open();
////					Pedometer.mDB.updateDayMaxSteps(mYear, mMonth, mDay, mNewCount);
////					Pedometer.mDB.close();
//				}
//				finish();
//			}
//		});
//
//		Button btnReset = (Button) findViewById(R.id.btn_reset);
//		btnReset.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				initWheel(R.id.passw_1, 0);
//				initWheel(R.id.passw_2, 0);
//				initWheel(R.id.passw_3, 0);
//				initWheel(R.id.passw_4, 0);
//				initWheel(R.id.passw_5, 0);
//			}
//		});
//
//		updateStatus();
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		super.onSaveInstanceState(savedInstanceState);
//	}
//
//	@Override
//	public void onStart() {
//		super.onStart();
//		Tracker t = ((AnalyticsSampleApp) this.getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("EditStepsFromHistory");
//		t.send(new HitBuilders.AppViewBuilder().build());
//	}
//
////	private void openSetStepsAlertDialog() {
////		new AlertDialog.Builder(this)
////				// .setTitle(R.string.alert_pause_title)
////				.setMessage(R.string.alert_set_steps_message)
////				.setPositiveButton(R.string.dialog_yes,
////						new DialogInterface.OnClickListener() {
////							@Override
////							public void onClick(DialogInterface dialog,
////									int which) {
////								mNewCount = mCount;
////								if (mIsToday) {
////									ActivityHistoryEditsteps.this
////											.sendBroadcast(new Intent(
////													AccuService.ACCUPEDO_EDITSTEPS_REQUEST));
////									finish();
////								} else {
////									mNewCount = mCount;
////									Pedometer.mDB.open();
////									Pedometer.mDB.updateDayMaxSteps(mYear, mMonth, mDay, mNewCount);
////									Pedometer.mDB.close();
////								}
////							}
////						})
////				.setNegativeButton(R.string.dialog_no,
////						new DialogInterface.OnClickListener() {
////							@Override
////							public void onClick(DialogInterface dialog,
////									int which) {
////							}
////						}).show();
////	}
//
//	public static int getNewCount() {
//		return mNewCount;
//	}
//
//	public static int getYear() {
//		return mYear;
//	}
//
//	public static int getMonth() {
//		return mMonth;
//	}
//
//	public static int getDay() {
//		return mDay;
//	}
//
//	private boolean wheelScrolled = false;
//
//	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
//		public void onScrollingStarted(WheelView wheel) {
//			wheelScrolled = true;
//		}
//
//		public void onScrollingFinished(WheelView wheel) {
//			wheelScrolled = false;
//			updateStatus();
//		}
//	};
//
//	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
//		public void onChanged(WheelView wheel, int oldValue, int newValue) {
//			if (!wheelScrolled) {
//				updateStatus();
//			}
//		}
//	};
//
//	private void updateStatus() {
//		int v1, v2, v3, v4, v5;
//		v1 = getWheel(R.id.passw_1).getCurrentItem();
//		v2 = getWheel(R.id.passw_2).getCurrentItem();
//		v3 = getWheel(R.id.passw_3).getCurrentItem();
//		v4 = getWheel(R.id.passw_4).getCurrentItem();
//		v5 = getWheel(R.id.passw_5).getCurrentItem();
//		mCount = v1 * 10000 + v2 * 1000 + v3 * 100 + v4 * 10 + v5;
//	}
//
//	private void initWheel(int id, int a) {
//		WheelView wheel = getWheel(id);
//		wheel.setViewAdapter(new NumericWheelAdapter(Pedometer.getInstance(),
//				0, 9));
//		// wheel.setCurrentItem((int) (Math.random() * 10));
//		wheel.setCurrentItem(a);
//
//		wheel.addChangingListener(changedListener);
//		wheel.addScrollingListener(scrolledListener);
//		wheel.setCyclic(true);
//		wheel.setInterpolator(new AnticipateOvershootInterpolator());
//	}
//
//	private WheelView getWheel(int id) {
//		return (WheelView) findViewById(id);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//}
