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
 *  
 *  keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v
 *  Certificate fingerprints:
	 MD5:  B1:82:B9:58:A0:FA:21:5D:A3:25:63:05:C8:3C:C2:69
	 SHA1: B0:B5:D3:9E:A6:B4:43:C5:EB:6A:6D:39:8F:BD:18:8C:4B:F9:85:41
	 Signature algorithm name: SHA1withRSA
 */

package com.corusen.steponfre.base;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import com.corusen.steponfre.R;
import com.corusen.steponfre.database.Constants;
import com.corusen.steponfre.database.MyDB;
import com.corusen.steponfre.database.SdcardManager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
//import android.support.v4.app.ActionBarDrawerToggle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Pedometer extends BaseActivity { // FragmentActivity {

	public static final int ACCUPEDO_INTENT_REQUEST_MANUAL_STEPS = 1;

	private InterstitialAd mInterstitialAd;

	private static Pedometer mInstance = null;
	public static SharedPreferences mSettings;
	public static PedometerSettings mPedometerSettings;
	public static MyDB mDB;

	// navigation settings
	private DrawerLayout 				mDrawerLayout;
	private ListView 					mDrawerList;
	private ActionBarDrawerToggle 		mDrawerToggle;
	private CharSequence 				mDrawerTitle;
	private CharSequence 				mTitle;
	private String[] 					navMenuTitles;
	private TypedArray 					navMenuIcons;
	private ArrayList<NavDrawerItem> 	navDrawerItems;
	private NavDrawerListAdapter 		mAdapter;
    private int mScreenIndex;

	public static final int SPINNER_ITEM_SETTINGSFIRST 	= 11;
	public static final int SPINNER_ITEM_PEDOMETER 		= 0;
	public static final int SPINNER_ITEM_CHART 			= 1;
	public static final int SPINNER_ITEM_HISTORY 		= 2;
	public static final int SPINNER_ITEM_SHARE 			= 3;
	public static final int SPINNER_ITEM_EDITSTEPS 		= 4;
	public static final int SPINNER_ITEM_BACKUP 		= 5;
	public static final int SPINNER_ITEM_SETTINGS 		= 6;
	public static final int SPINNER_ITEM_HELP 			= 7;
	public static final int SPINNER_ITEM_PURCHASE 		= 8;
	public static final int SPINNER_ITEM_QUIT 			= 9;
	public static String[] spinnerStrings;
	public boolean mExternalStorageAvailable = false;
	public boolean mExternalStorageWriteable = false;
	public FragmentPedometer mFragmentPedometer;

	// public static boolean mQuitting = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Pedometer.mInstance = this;
		mSettings = PreferenceManager.getDefaultSharedPreferences(Pedometer.getInstance());
		mPedometerSettings = new PedometerSettings(mSettings);
		mDB = new MyDB(this);

		mTitle = mDrawerTitle = getTitle();
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));// ,true,"22"));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));// ,true,"50+"));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(8, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(9, -1)));
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		mAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(mAdapter);

		if (mPedometerSettings.isNewInstallation117()) {
			disableNavigationDrawMenu();
		} else {
			enableNavigationDrawMenu();
		}
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(AccuService.mScreenAcitionBarColor)));

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.accupedo, R.string.accupedo ) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// JS V115
		final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				if (thread.getName().startsWith("AdWorker")) {
					Log.w("ADMOB", "AdWorker thread thrown an exception.", ex);
				} else if (defaultHandler != null) {
					defaultHandler.uncaughtException(thread, ex);
				} else {
					throw new RuntimeException("No default uncaught exception handler.", ex);
				}
			}
		});

		// AdMob interstitial
		LocationManager coarseLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location coarseLocation = coarseLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Date birthday = Pedometer.mPedometerSettings.getBirthDate();
		int gender = Pedometer.mPedometerSettings.getGender();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.ACCUPEDO_ADMOB_INTERSTITIAL_ID);
//		interstitialAd.setAdListener(new AdListener() {
//			public void onAdClosed() {
//				finish();
//			}
//		});
        mInterstitialAd.setAdListener(new AdListener() {
			public void onAdClosed() {
				Intent intent;
				if (mScreenIndex == 0) { //Chart
					sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
					intent = new Intent(getBaseContext(), com.corusen.steponfre.chart.ChartActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else if (mScreenIndex == 1) { //History
					sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
					intent = new Intent(getBaseContext(), com.corusen.steponfre.database.History.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			}
		});

		AdRequest adRequest = new AdRequest.Builder().setLocation(coarseLocation).setGender(gender).setBirthday(birthday).build();
        mInterstitialAd.loadAd(adRequest);

		if (mPedometerSettings.isNewInstallation()) { // new installation
			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
			displayView(SPINNER_ITEM_SETTINGSFIRST);
		} else {
			if (mPedometerSettings.isNewInstallation117()) { // update to version 411
				mPedometerSettings.setGender("male");
				mPedometerSettings.setBirthYear(1990);
				mPedometerSettings.setBirthMonth(1);
				mPedometerSettings.setBirthDay(1);
				if (mPedometerSettings.isMetric()) {
					mPedometerSettings.setBodyHeight(175f);
				} else {
					mPedometerSettings.setBodyHeight(175f * Utils.CM2INCH);
				}
				displayView(SPINNER_ITEM_SETTINGSFIRST);
			} else {
				if (savedInstanceState == null) {
					displayView(SPINNER_ITEM_PEDOMETER);
				}
			}
		}

		// if (mPedometerSettings.isNewInstallation()) {
		// PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		// displayView(SPINNER_ITEM_SETTINGSFIRST);
		// // Intent intent = new Intent(this, SettingsFirst.class);
		// // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// // startActivityForResult(intent, 1);
		//
		// // if (checkDBfileExist()) {
		// // openNewInstallationAlertDialog();
		// // }
		// } else {
		// if (savedInstanceState == null) {
		// displayView(SPINNER_ITEM_PEDOMETER);
		// }
		// }
	}

	public void enableNavigationDrawMenu() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		getActionBar().setHomeButtonEnabled(true);
	}

	public void disableNavigationDrawMenu() {
		getActionBar().setDisplayHomeAsUpEnabled(false);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		getActionBar().setHomeButtonEnabled(false);
	}

	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mPedometerSettings.isNewInstallation117()) {
			getMenuInflater().inflate(R.menu.dummy, menu);
		} else {
			getMenuInflater().inflate(R.menu.main_activity, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		Intent intent;
		int id = item.getItemId();
		if (id == R.id.menu_chart) {
            Random r = new Random();
            if (mInterstitialAd.isLoaded() && (r.nextFloat() <= Constants.ADMOB_INTERSTITIAL_DISPLAY_CHANCE)) {
                mScreenIndex = 0;
                mInterstitialAd.show();
            } else {
                sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
                intent = new Intent(getBaseContext(), com.corusen.steponfre.chart.ChartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
			return true;
		} else if (id == R.id.menu_history) {
            Random r = new Random();
            if (mInterstitialAd.isLoaded() && (r.nextFloat() <= Constants.ADMOB_INTERSTITIAL_DISPLAY_CHANCE)) {
                mScreenIndex = 1;
                mInterstitialAd.show();
            } else {
                sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
                intent = new Intent(getBaseContext(), com.corusen.steponfre.database.History.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	public void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		Intent intent;
		switch (position) {
		case SPINNER_ITEM_SETTINGSFIRST:
			fragment = new FragmentSettingsFirst();
			break;

		case SPINNER_ITEM_PEDOMETER:
			if (mFragmentPedometer == null) {
				mFragmentPedometer = new FragmentPedometer();
			}
			fragment = mFragmentPedometer;
			Log.i("kiip", "Test");
			break;

		case SPINNER_ITEM_CHART:
				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
				intent = new Intent(getBaseContext(), ActivityShareTabs.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;

		case SPINNER_ITEM_HISTORY:
				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
				intent = new Intent(getBaseContext(), ActivityShareTabs.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;

		case SPINNER_ITEM_SHARE:
			sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
			intent = new Intent(getBaseContext(), ActivityShareTabs.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		case SPINNER_ITEM_EDITSTEPS:
			sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
			fragment = new FragmentEditsteps();
			break;

		case SPINNER_ITEM_BACKUP:
			intent = new Intent(getBaseContext(), ActivityBackup.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		case SPINNER_ITEM_SETTINGS:
			// intent = new Intent(getBaseContext(), Settings.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// startActivity(intent);

			fragment = new FragmentSettings();

			break;

		case SPINNER_ITEM_HELP:
			fragment = new FragmentHelp();
			break;

		case SPINNER_ITEM_PURCHASE:
			fragment = new FragmentAccupedoPro();
			break;

		case SPINNER_ITEM_QUIT:
			mFragmentPedometer.mService.cancelRestartAlarm();
			mFragmentPedometer.mService.updateWidgetQuit();
			mFragmentPedometer.unbindStepService();
			mFragmentPedometer.stopStepService();
            finish();

//			if (interstitialAd.isLoaded()) {
//				interstitialAd.show();
//			} else {
//				finish();
//			}

			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.frame_container, fragment).commit();
			// commitAllowingStateLoss();
			// fragmentManager.executePendingTransactions();

			if (position != SPINNER_ITEM_SETTINGSFIRST) {
				// update selected item and title, then close the drawer
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		} else {
			// error in creating fragment
			// Log.e("MainActivity", "Error in creating fragment");
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void openNewInstallationAlertDialog() {
		new AlertDialog.Builder(this)
		// .setTitle(R.string.alert_pause_title)
				.setMessage(R.string.alert_new_installation_message).setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							SdcardManager sdmanager = new SdcardManager(Pedometer.getInstance());
							sdmanager.importDatabase();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();

	}

	public static Pedometer getInstance() {
		return mInstance;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState); // this is a know bug. So
														// remove .super
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();

		int[] intArray;
		intArray = new int[2];
		Bundle extras = getIntent().getExtras();

		if (extras == null) {
			if (mPedometerSettings.isNewInstallation()) {
				displayView(SPINNER_ITEM_SETTINGSFIRST);
			} else {
				displayView(SPINNER_ITEM_PEDOMETER);
			}
		} else {
			intArray = extras.getIntArray("navigation_intent");
			//V123
			if (intArray == null) {
				displayView(SPINNER_ITEM_PEDOMETER);
			} else {
				displayView(intArray[0]);
			}
		}

		// Log.i("kiip", "OnResume");

		// //JS (0 <== item) added to avoid null pointer :V351
		// if ((0 <= item) && (item < spinnerStrings.length)) { // navigation
		// from Settings
		// actionBar.setSelectedNavigationItem(item);
		// } else {
		// }

//		// Kiip reward
//		if (AccuService.mIsGoalRewardNotification && AccuService.mIsGoalAchievement && !AccuService.mIsGoalRewardFired) {
//			Kiip.getInstance().saveMoment("Contradulation", new Kiip.Callback() {
//				@Override
//				public void onFinished(Kiip kiip, Poptart reward) {
//					onPoptart(reward);
//					AccuService.mIsGoalRewardFired = true;
//					mPedometerSettings.setGoalRewardFired(true);
//				}
//
//				@Override
//				public void onFailed(Kiip kiip, Exception exception) {
//					// handle failure
//				}
//			});
//		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		// if (adView != null) {
		// adView.destroy();
		// }
		super.onDestroy();
		// mDB.close();
		// mChecker.onDestroy();
	}

	protected void onRestart() {
		super.onRestart();
		// super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (interstitialAd.isLoaded()) {
//				interstitialAd.show();
//			} else {
//				finish(); // V117
//			}
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean checkDBfileExist() {

		boolean fileExists;
		File sd = new File(Environment.getExternalStorageDirectory() + Constants.ACCUPEDO_FOLDERNAME);
		File data = Environment.getDataDirectory();

		if (sd.exists()) {
			String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH + Constants.DATABASE_NAME;
			String backupDBPath = Constants.DATABASE_FILENAME; // Constants.DATABASE_NAME;
			File currentDB = new File(data, currentDBPath);
			File backupDB = new File(sd, backupDBPath);
			if (backupDB.exists()) {
				fileExists = true;
			} else {
				fileExists = false;
			}
		} else {
			fileExists = false;
		}
		return fileExists;
	}

}