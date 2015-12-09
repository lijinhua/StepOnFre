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
 *  2/../2015
 *  With the new MacPro (alias: accupedo2015 keystore:Corusen2015.jks), 4gvsysO83eWvGYGMq8u7wJbCfDs=
 *  With android debugkey L36zGKN7VaoCMuxd2RMoat9JT7I=
 *
 *  keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v
 *  Certificate fingerprints:
	 MD5:  B1:82:B9:58:A0:FA:21:5D:A3:25:63:05:C8:3C:C2:69
	 SHA1: B0:B5:D3:9E:A6:B4:43:C5:EB:6A:6D:39:8F:BD:18:8C:4B:F9:85:41
	 Signature algorithm name: SHA1withRSA

	 Using a new Macbook Pro 2/24/2015
     keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v
	 MD5:  0D:79:58:2F:BE:B6:6F:66:6E:1C:68:71:78:A7:BB:15
	 SHA1: 2F:7E:B3:18:A3:7B:55:AA:02:32:EC:5D:D9:13:28:6A:DF:49:4F:B2
	 SHA256: 57:D0:84:53:8C:88:78:D9:A3:49:8E:6F:56:86:15:20:A2:C2:B2:A3:23:52:69:51:B8:B3:D8:A9:38:9D:FC:8F
	 Signature algorithm name: SHA1withRSA

	 keytool -exportcert -alias accupedo2015 -keystore Corusen2015.jks -list -v
	 MD5:  66:98:38:20:6E:77:09:9D:56:E6:1E:C4:FE:3E:67:6B
	 SHA1: E2:0B:EC:CA:C3:BC:DD:E5:AF:19:81:8C:AB:CB:BB:C0:96:C2:7C:3B
	 SHA256: 87:B6:FC:FB:A2:63:34:D5:C5:E5:FF:E5:A7:FF:D7:7B:81:5A:90:EE:B7:D7:4A:A2:60:FF:17:F9:6C:7A:10:03
	 Signature algorithm name: SHA1withRSA

	 2/11/2015
	 android:launchMode="singleTop" is necessary in AndroidManifext.xml. This will make sure always in
	 one single activity. This is added to prevent FC when notification is clicked to open the Pedometer
	 activity. If the app is in the background (no activity), click the notification is fine. But the Pedometer
	 activity is on the screen, then the notification is clicked it will create one more activity. When exit the
	 two activities, it will cause FC. So, this singleTop is added.

	 The side effect of singleTop is that switching from Share activity to Pedometer activity. It used to use
	 Intent putExtra to indicate what fragment to go, i.e. EditSteps in the menu. Since the adding this singleTop
	 no more putExtra works. It carries null.

	 So, the mechanism to move from Fragment to Activity in the draw menu needs to change. It should be all
	 frangement to fragment in the draw menu. Share and Backup activity should be fragments. Challenges are if
	 the fragment works with Tabhost and ViewPagers. ViewPagers only available in V4, V7, V13 support.

	 Settings screen is PreferenceFragement, which is native fragement. This fragment is not compatible with
	 V4 support fragment. So the all the fragments EditSteps, Helps, AccupedoPro, etc. are all native fragments

 */

/**
 * 2/27/2015
 * With a new Macbook Pro and Corusen signing key
 * keytool -exportcert -alias accupedo -keystore Corusen -list -v
 * MD5:  32:A5:E3:6C:AE:B5:B2:A8:0B:75:3E:FD:A0:6A:A4:F7
 SHA1: 5F:DB:7C:9F:0C:2D:F9:E9:BC:FD:C5:28:78:84:01:7E:6C:98:38:17
 SHA256: 76:2C:62:86:01:42:36:FE:2C:6E:6F:38:BB:B7:59:A6:0D:CB:21:3E:5E:EB:56:18:77:27:FA:E1:29:75:B4:99
 Signature algorithm name: SHA1withRSA
 Version: 3

 keytool -exportcert -alias accupedo -keystore Corusen|openssl sha1 -binary|openssl base64
 X9t8nwwt+em8/cUoeIQBfmyYOBc=

 keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
 L36zGKN7VaoCMuxd2RMoat9JT7I=
 */


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;

//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.corusen.steponfre.chart.HourBarChart2;
import com.corusen.steponfre.chart.HourBarChartForMain;
import com.corusen.steponfre.chart.IChart2;
import com.corusen.steponfre.chart.IChartForMain;
import com.corusen.steponfre.database.Constants;
import com.corusen.steponfre.database.MyDB;
import com.corusen.steponfre.database.SdcardManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.corusen.steponfre.R;

import org.achartengine.GraphicalView;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Pedometer extends FragmentActivity { //FragmentActivity { AppCompatActivity

	private InterstitialAd mInterstitialAd;
	public AdRequest mAdRequest;
	private AdView mAdView;
	private static int mAdsClickCount = 0;

	public static final int NUM_HOURLY_BARS = 60;
	public static final int STOP_MODE_NOSAVE = 0; // when stop button pressed
	public static final int STOP_MODE_SAVE = 1; // when stop button pressed
	public static final int START_MODE = 2; // when start button pressed
	public static final int RESUME_MODE = 3; // when resume button pressed
	public static final int PAUSE_MODE = 4; // when pause button pressed

	public static int mOperationMode = STOP_MODE_NOSAVE;

	private static Pedometer mInstance = null;
	public static PedometerSettings mPedometerSettings;
	public static MyDB mDB;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;


	public static final int SPINNER_ITEM_SETTINGSFIRST = 11;
	public static final int SPINNER_ITEM_PEDOMETER = 0;
	public static final int SPINNER_ITEM_CHART = 1;
	public static final int SPINNER_ITEM_HISTORY = 2;
	public static final int SPINNER_ITEM_SHARE = 3;
	public static final int SPINNER_ITEM_EDITSTEPS = 4;
	public static final int SPINNER_ITEM_BACKUP = 5;
	public static final int SPINNER_ITEM_SETTINGS = 6;
	public static final int SPINNER_ITEM_HELP = 7;
	public static final int SPINNER_ITEM_QUIT = 8;
	public static final int SPINNER_ITEM_PURCHASE = 9;

	private static final int DIALOG_FRAGMENT_PAUSE = 1;
	private static final int DIALOG_FRAGMENT_RESUME = 2;
	private static final int DIALOG_FRAGMENT_NEXT_LAP = 3;
	private static final int DIALOG_FRAGMENT_DAILY_RESET = 4;

	private static int mSpinnerPosition = 0;

	private CollectionPagerAdapter mCollectionPagerAdapter;  //V451
	//private ViewPager               mViewPager;
	private MyViewPager mViewPager;


	private static Calendar mCurrent;
	private static Calendar mToday;
	private static Calendar mFirstDay;
	private static GraphicalView mChartView;
	private static int mNumberDays;
	private static int mWeekFormat;
	//private static Boolean  mIsMetric;
	private static float mfDistanceFactor;
	private static float mfCalorieFactor;
	private static String msDistanceUnit;
	private static String msCalorieUnit;
	private static String msSpeedUnit;

	private static final List<double[]> mX = new ArrayList<>();
	private static final List<double[]> mValues = new ArrayList<>();
	private static final List<double[]> mX2 = new ArrayList<>();
	private static final List<double[]> mValues2 = new ArrayList<>();


	private boolean mIsRunning;
	private boolean mIsBound;

	private static boolean mToggleHourlyChart;
	private static View mHourlyChart;
	private static View mHourlyLap;

	private static TextView mLapTextView;
	private static TextView mSpeedValueView;
	private static TextView mTripaStepValueView;
	private static TextView mStepValueView;
	private static TextView mDistanceValueView;
	private static TextView mCaloriesValueView;
	private static TextView mSteptimeValueView;
	private static TextView mDailyPercentValueView;
	private static TextView mDailyGoalValueView;
	private static TextView mLapTitleTextView;
	private static ProgressBar mProgressBar;

	private static int mLap;
	private static int mLapStepValue;
	private static int mStepValue;
	private static int mTripaStepValue;
	private static int mLapSteptimeValue;
	private static int mSteptimeValue;
	private static int mGoal;
	private static float mLapDistanceValue;
	private static float mDistanceValue;
	private static float mSpeedValue;
	private static float mLapCaloriesValue;
	private static float mCaloriesValue;
	private static float mPercent;

//	private static ImageButton mPauseImageButton;
//	private static ImageButton mWalkButton;

	private static LinearLayout mLayoutStart;
	private static LinearLayout mLayoutPause;
	private static LinearLayout.LayoutParams mLayoutParamsVisible = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
	private static LinearLayout.LayoutParams mLayoutParamsHide = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f);

	private static ImageButton mStartButton;
	private static ImageButton mPauseButton;
	private static ImageButton mStopButton;
	private static ImageButton mLockButton;
	private static ImageButton mWalkButton;
	private static ImageButton mNextButton;

	private int mSelectedItem;

	private static LinearLayout mLayout2;
	private static IChart2 mChart2;

	private static ObjectFragment msFragment;

	//private ActionBar       mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

		mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager()); //V451
		mViewPager = (MyViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mCollectionPagerAdapter);

		final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler(); // JS V427
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				if (defaultHandler != null) {
					defaultHandler.uncaughtException(thread, ex);
				} else {
					throw new RuntimeException("No default uncaught exception handler.", ex);
				} // if (thread.getName().startsWith("AdWorker")) {
			}
		});

		Pedometer.mInstance = this;
		SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(Pedometer.getInstance());
		mPedometerSettings = new PedometerSettings(mSettings);
		mDB = new MyDB(this);

		mWeekFormat = mPedometerSettings.getWeekFormat();  //V451

		mTitle = mDrawerTitle = getTitle();
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));// ,true,"22"));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));// ,true,"50+"));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));


		if (Constants.IS_VERSION_TE) {
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
		}

		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.accupedo, R.string.accupedo) {  //Toolbar(MyActivity.this)
			//mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {  //Toolbar(MyActivity.this)
			//mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {  //Toolbar(MyActivity.this)
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


		if (Constants.IS_VERSION_TE) {

			mAdsClickCount = mPedometerSettings.getAdsClickCount();

			if (Constants.IS_ADMOB_INTERSTITIAL) {
				mInterstitialAd = new InterstitialAd(this);
				mInterstitialAd.setAdUnitId(Constants.ACCUPEDO_ADMOB_INTERSTITIAL_ID);
				mInterstitialAd.setAdListener(new AdListener() {
					public void onAdClosed() {
						showFragmentAfterInterstital();
						requestNewInterstitial();
					}
				});
			}

			mAdView = new AdView(Pedometer.getInstance());  //Admob banner
			mAdView.setAdSize(AdSize.SMART_BANNER);         //InMobi does not support SMART_BANNER
			mAdView.setAdUnitId(Constants.ACCUPEDO_ADMOB_ID);
			LinearLayout layout = (LinearLayout) findViewById(R.id.adView);
			layout.addView(mAdView);
			requestNewInterstitial();
		}
	}


	private void requestNewInterstitial() {

		Date birthday = mPedometerSettings.getBirthDate();
		int gender = mPedometerSettings.getGender();

		PackageManager pm = this.getPackageManager();
		int hasPerm = pm.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, this.getPackageName());
		if (hasPerm != PackageManager.PERMISSION_GRANTED) {
			LocationManager coarseLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location coarseLocation = coarseLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			mAdRequest = new AdRequest.Builder().setLocation(coarseLocation).setGender(gender).setBirthday(birthday).build();
		} else {
			mAdRequest = new AdRequest.Builder().setGender(gender).setBirthday(birthday).build();
		}

		mAdView.loadAd(mAdRequest);
		if (Constants.IS_ADMOB_INTERSTITIAL) {
			mInterstitialAd.loadAd(mAdRequest);
		}
	}

	private boolean isTimeForInterstitial() {
		boolean a;
		if (Constants.IS_ADMOB_INTERSTITIAL) {
			a = mInterstitialAd.isLoaded();
		}

		boolean b = mAdsClickCount == 2;
		boolean c = (mAdsClickCount > 2) && ((mAdsClickCount % 8) == 0);
		boolean d = (a && (b || c));

		if (d) mPedometerSettings.setAdsClickCount(mAdsClickCount);
		return d;
	}

	void showFragmentAfterInterstital() {
		Intent intent;
		if (mSpinnerPosition == SPINNER_ITEM_CHART) {
			sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
			intent = new Intent(getBaseContext(), com.corusen.steponfre.chart.ChartActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else if (mSpinnerPosition == SPINNER_ITEM_HISTORY) {
			sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
			intent = new Intent(getBaseContext(), com.corusen.steponfre.database.History.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else if (mSpinnerPosition == SPINNER_ITEM_SHARE) {
			sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
			intent = new Intent(getBaseContext(), ActivityShareTabs.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else if (mSpinnerPosition == SPINNER_ITEM_BACKUP) {
			sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
			intent = new Intent(getBaseContext(), ActivityBackup.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			int[] array = new int[2];
			array[0] = mSpinnerPosition;
			intent = new Intent(getBaseContext(), Pedometer.class);
			intent.putExtra("navigation_intent", array);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			//displayView(mSpinnerPosition);
		}
	}


	//****************************************************************************

	public void enableNavigationDrawMenu() {
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

	}

	private void disableNavigationDrawMenu() {
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setHomeButtonEnabled(false);
		}
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mDrawerLayout.closeDrawers(); //V511

			mSpinnerPosition = position;

			//mAdsClickCount++;

			if ((position == SPINNER_ITEM_CHART) ||
					(position == SPINNER_ITEM_HISTORY) ||
					(position == SPINNER_ITEM_SHARE) ||
					(position == SPINNER_ITEM_BACKUP)) {

				mAdsClickCount++;

				//Log.i("Count1", "Count : " + mAdsClickCount);

				if (Constants.IS_VERSION_TE && isTimeForInterstitial()) {
					finish();
					if (Constants.IS_ADMOB_INTERSTITIAL) {
						mInterstitialAd.show();
					}
				} else {
					displayView(position);
				}
			} else {
				displayView(position);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mPedometerSettings.isNewInstallation()) getMenuInflater().inflate(R.menu.dummy, menu);
		else getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) return true;
		mAdsClickCount++;

		Intent intent;
		int id = item.getItemId();
		if (id == R.id.menu_chart) {
//            AnalyticsApp.tracker().setScreenName("Chart541-M");
//            AnalyticsApp.tracker().send(new HitBuilders.ScreenViewBuilder().build());
			if (Constants.IS_VERSION_TE && isTimeForInterstitial()) {
				mSpinnerPosition = SPINNER_ITEM_CHART;
				if (Constants.IS_ADMOB_INTERSTITIAL) {
					mInterstitialAd.show();
				}
			} else {
				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
				intent = new Intent(getBaseContext(), com.corusen.steponfre.chart.ChartActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			return true;
		} else if (id == R.id.menu_history) {
//            AnalyticsApp.tracker().setScreenName("History541-M");
//            AnalyticsApp.tracker().send(new HitBuilders.ScreenViewBuilder().build());
			if (Constants.IS_VERSION_TE && isTimeForInterstitial()) {
				mSpinnerPosition = SPINNER_ITEM_HISTORY;
				if (Constants.IS_ADMOB_INTERSTITIAL) {
					mInterstitialAd.show();
				}
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

	public void displayView(int position) {
		Fragment fragment = null;
		Intent intent;

		switch (position) {
			case SPINNER_ITEM_SETTINGSFIRST:
				fragment = new FragmentSettingsFirst();
				break;

			case SPINNER_ITEM_PEDOMETER:
				mGoal = mPedometerSettings.getGoalSteps();
				if (mGoal != 0) {
					mPercent = mStepValue * 100 / mGoal;
				} //V551
				else {
					mPercent = 0;
				}

				Boolean isMetric = mPedometerSettings.isMetric();
				if (isMetric) {
					mfDistanceFactor = Utils.MILE2KM; // 1.60934f;
					msDistanceUnit = getString(R.string.km);
					msSpeedUnit = getString(R.string.kilometers_per_hour);
				} else {
					mfDistanceFactor = 1.0f;
					msDistanceUnit = getString(R.string.widget_mi);
					msSpeedUnit = getString(R.string.miles_per_hour);
				}

				msCalorieUnit = getString(R.string.calories_burned);
				mfCalorieFactor = 1.0f;
//				if (mPedometerSettings.isCalories()) {
//					msCalorieUnit = getString(R.string.calories_burned);
//					mfCalorieFactor = 1.0f;
//				} else {
//					msCalorieUnit = getString(R.string.calorie_unit_kilo_joule);
//					mfCalorieFactor = Utils.KCAL2KJ;
//				}
				break;

			case SPINNER_ITEM_CHART:
				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
				intent = new Intent(getBaseContext(), com.corusen.steponfre.chart.ChartActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;

			case SPINNER_ITEM_HISTORY:
				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
				intent = new Intent(getBaseContext(), com.corusen.steponfre.database.History.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;

			case SPINNER_ITEM_SHARE:
				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
				intent = new Intent(getBaseContext(), ActivityShare.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;

			case SPINNER_ITEM_EDITSTEPS:
				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
				fragment = new FragmentEditsteps();
				break;

			case SPINNER_ITEM_BACKUP:
				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
				intent = new Intent(getBaseContext(), ActivityBackup.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;

			case SPINNER_ITEM_SETTINGS:
				fragment = new FragmentSettings();
				break;

			case SPINNER_ITEM_HELP:
				fragment = new FragmentHelp();
				break;

			case SPINNER_ITEM_PURCHASE:
				fragment = new FragmentAccupedoPro();
				break;

			case SPINNER_ITEM_QUIT:
				mService.cancelRestartAlarm();
				mService.updateWidgetQuit();
				unbindStepService();
				stopStepService();
				finish(); // V437
				break;

			default:
				break;
		}

		if (fragment != null) { //V451
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();

			if (position == SPINNER_ITEM_SETTINGSFIRST) {
				transaction.replace(R.id.frame_container, fragment).commit();
			} else if (position == SPINNER_ITEM_PEDOMETER) {
				initializeDateFormat();

				mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());  //V509 //V451
				mViewPager = (MyViewPager) findViewById(R.id.pager); //mViewPager.setAdapter(mCollectionPagerAdapter);

				mViewPager.setAdapter(mCollectionPagerAdapter);
				mViewPager.setCurrentItem(mNumberDays - 1);
				mViewPager.getAdapter().notifyDataSetChanged();

				transaction.hide(fragment);
				Fragment fr = fragmentManager.findFragmentById(R.id.frame_container);
				if ((fr != null) && (fr.getView() != null)) {
					fr.getView().setVisibility(View.GONE);
				}

				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
				mDrawerLayout.closeDrawer(mDrawerList);
				mViewPager.setVisibility(View.VISIBLE);
				if (Constants.IS_VERSION_TE) {
					mAdView.setVisibility(View.VISIBLE);
				}

			} else {
				transaction.replace(R.id.frame_container, fragment).commit();
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
				mDrawerLayout.closeDrawer(mDrawerList);
				mViewPager.setVisibility(View.GONE);
				if (Constants.IS_VERSION_TE) {
					mAdView.setVisibility(View.GONE);
				}
			}

		} else { //V561
			if (position == SPINNER_ITEM_PEDOMETER) {
				FragmentManager fragmentManager = getFragmentManager();
				//FragmentTransaction transaction = fragmentManager.beginTransaction();

				initializeDateFormat();

				mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());  //V509 //V451
				mViewPager = (MyViewPager) findViewById(R.id.pager); //mViewPager.setAdapter(mCollectionPagerAdapter);

				mViewPager.setAdapter(mCollectionPagerAdapter);
				mViewPager.setCurrentItem(mNumberDays - 1);
				mViewPager.getAdapter().notifyDataSetChanged();

				//transaction.hide(fragment);
				Fragment fr = fragmentManager.findFragmentById(R.id.frame_container);
				if ((fr != null) && (fr.getView() != null)) {
					fr.getView().setVisibility(View.GONE);
				}

				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
				mDrawerLayout.closeDrawer(mDrawerList);
				mViewPager.setVisibility(View.VISIBLE);
				if (Constants.IS_VERSION_TE) {
					mAdView.setVisibility(View.VISIBLE);
				}
			}
		}
	}


	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setTitle(mTitle);
		}
	}

	//When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState); // Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState(); // V451
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void openNewInstallationAlertDialog() {
		new AlertDialog.Builder(Pedometer.getInstance()) // .setTitle(R.string.alert_pause_title)
				.setMessage(R.string.alert_new_installation_message).setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					SdcardManager sdmanager = new SdcardManager(Pedometer.getInstance());
					sdmanager.importDatabase();
					mViewPager.setAdapter(mCollectionPagerAdapter); //initializeDateFormat(); V511
					mViewPager.setCurrentItem(mNumberDays - 1);
					mViewPager.getAdapter().notifyDataSetChanged();
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
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
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
		Bundle extras = null; // V437
		Intent intent = getIntent();
		if (intent != null) extras = intent.getExtras();

		initializeDateFormat();
		mViewPager.setAdapter(mCollectionPagerAdapter);
		mViewPager.setCurrentItem(mNumberDays - 1);
		mViewPager.getAdapter().notifyDataSetChanged();

		if (extras == null) {
			if (mPedometerSettings.isNewInstallation()) { // new installation
				PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
				disableNavigationDrawMenu();
				displayView(SPINNER_ITEM_SETTINGSFIRST);


//				if (!mPedometerSettings.isHistoryImported411()) {
//					if (checkDBfileExist()) {
//						openNewInstallationAlertDialog();
//						mPedometerSettings.setHistoryImportedStatusTrue411();
//					}
//				}
				mPedometerSettings.setNewInstallationStatusFalse();
			} else {
				enableNavigationDrawMenu();
				displayView(SPINNER_ITEM_PEDOMETER);
			}

		} else {
			intArray = extras.getIntArray("navigation_intent");
			enableNavigationDrawMenu(); //V515
			if (intArray == null) { //V447
				//mAdsClickCount = 0;
				displayView(SPINNER_ITEM_PEDOMETER);
			} else {
				displayView(intArray[0]);
			}

//
		}

		setPedometer(); //V561 this starts AccuService and Handler, and make sure all TextView loaded and avoid mHanlder null point FC
	}

	@Override
	public void onPause() {
		mPedometerSettings.setAdsClickCount(mAdsClickCount);
		if (mIsRunning)
			unbindStepService(); // this causes problem of sudden stop as it kills service
		super.onPause();
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
////		if (keyCode == KeyEvent.KEYCODE_BACK) {
////			Log.i("destroy", "back button");
////			if (mInterstitialAd.isLoaded() & !mPedometerSettings.isChartInterstitialDisplayed()) {
////				mInterstitialAd.show();
////			} else {
////				Pedometer.getInstance().mPedometerSettings.setChartInterstitialDisplayed(false);
////				finish(); // V437
////			}
////			return true;
////		}
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	protected void onStop() {
		super.onStop();
		mPedometerSettings.setPedometerBackButton(false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPedometerSettings.setPedometerBackButton(true);
	}

	public static class CollectionPagerAdapter extends FragmentStatePagerAdapter {

		public CollectionPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int i) {

			ObjectFragment objectFragment = new ObjectFragment();

			android.support.v4.app.Fragment fragment = objectFragment;
			Bundle args = new Bundle();
			args.putInt(ObjectFragment.ARG_OBJECT, i); // i+1 Our object is just an integer :-P
			fragment.setArguments(args);

			if (Utils.isSameDate(mCurrent, mToday)) {
				msFragment = objectFragment;
			}

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

//        @Override
//        public Object instantiateItem(View container, int position) {
//            return container;
//        }


	}

	private void updateHourlyChart2Dynamic() {
		setDayHourData2Dynamic();
		if (mChartView != null) {
			mLayout2.removeView(mChartView);
		}
		mChartView = mChart2.graphicalView(Pedometer.getInstance(), mX2, mValues2, true);
		mLayout2.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
	}

	private void setDayHourData2Dynamic() {

		int i, j, year, month, day, count = 0, startIndex = 0;
		int[] sumSteps = new int[NUM_HOURLY_BARS + 1];
		double[] delSteps = new double[NUM_HOURLY_BARS + 1];
		double[] x = new double[7];

		for(i=0;i<7;i++) { x[i] = 0.0; }

		for(i=0;i<=NUM_HOURLY_BARS;i++)
		{
			sumSteps[i] = 0;
			delSteps[i] = 0.0;
		}

		Calendar today = Calendar.getInstance();
		year=today.get(Calendar.YEAR);
		month=today.get(Calendar.MONTH)+1;
		day=today.get(Calendar.DATE);

		Pedometer.mDB.open();

		Cursor c0 = Pedometer.mDB.queryLapNumber(year, month, day);
		//Cursor c0 = Pedometer.mDB.queryLapMaxStepsForDay(year, month, day);
		if(c0!=null)
		{
			mLap = c0.getInt(c0.getColumnIndex(Constants.KEY_LAP));
			c0.close();
		}

		Cursor c = Pedometer.mDB.queryLapStepsForDay(year, month, day, mLap);

		if(c==null) { count = 0; }
		else { count = c.getCount(); } // it can be zero

		if(count==0) {
			x[0] = (double) startIndex;
			x[1] = x[0] + 10.0;
			x[2] = x[0] + 20.0;
			x[3] = x[0] + 30.0;
			x[4] = x[0] + 40.0;
			x[5] = x[0] + 50.0;
			x[6] = x[0] + 60.0;
		} else  {
			if (count < NUM_HOURLY_BARS) {
				startIndex = 0;
			} else {
				startIndex = count - NUM_HOURLY_BARS;
				count = NUM_HOURLY_BARS;
			}

			if (c.moveToFirst()) {
				i = 0;
				do {
					j = i - startIndex;
					if (j >= 0) {
						sumSteps[j] = c.getInt(c.getColumnIndex(Constants.KEY_LAPSTEPS));
					}
					i++;
				} while (c.moveToNext());
			}
			c.close();
			Pedometer.mDB.close();

			if (count > 1) {
				for (i = 1; i <= count; i++) {
					j = i - 1;
					delSteps[j] = (double) (sumSteps[i] - sumSteps[i - 1]);
					if (delSteps[j] < 0.0) {
						delSteps[j] = 0.0;
					}
				}
			}

			x[0] = (double) startIndex;
			x[1] = x[0] + 10.0;
			x[2] = x[0] + 20.0;
			x[3] = x[0] + 30.0;
			x[4] = x[0] + 40.0;
			x[5] = x[0] + 50.0;
			x[6] = x[0] + 60.0;
		}

		mX2.clear();
		mX2.add(x);
		mValues2.clear();
		mValues2.add(delSteps);
	}


	public static class ObjectFragment extends android.support.v4.app.Fragment {
		public static final String ARG_OBJECT = "object";
		View mRootView;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.dark_fragment_pedometer, container, false);  //V451
			Bundle args = getArguments();
			int position = args.getInt(ARG_OBJECT);

			mToday = Calendar.getInstance(); //V503 to remove some crashes as it is null
			mCurrent = (Calendar) mToday.clone();
			mCurrent.add(Calendar.DATE, -(mNumberDays - 1 - position));

			if(Utils.isSameDate(mCurrent, mToday)) {
				updateHourlyChart2();
			} else {
				updateHourlyChart();
			}

			return mRootView;
		}


		private void updateHourlyChart() {
			LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.chart_hourly);
			IChartForMain mChart = new HourBarChartForMain();

			setDayHourData();
			if (mChartView != null) { layout.removeView(mChartView); }
			mChartView = mChart.graphicalView(this, mX, mValues, true); //boolean mScreenLarge = true; mScreenLarge is always true
			layout.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}


		private void updateHourlyChart2() {
			mLayout2 = (LinearLayout) mRootView.findViewById(R.id.chart_hourly);
			mChart2 = new HourBarChart2();

			boolean mScreenLarge = true;
			setDayHourData();
			if (mChartView != null) { mLayout2.removeView(mChartView); }
			mChartView = mChart2.graphicalView(Pedometer.getInstance(), mX2, mValues2,  mScreenLarge);
			mLayout2.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		}

		public void setDayHourData() {

			if(Utils.isSameDate(mCurrent, mToday)) {
				int 	i, j, year, month, day, count = 0, startIndex = 0;
				int[] 	sumSteps = new int[NUM_HOURLY_BARS + 1];
				double[] delSteps = new double[NUM_HOURLY_BARS + 1];
				double[] x = new double[7];

				for (i = 0; i < 7; i++) { x[i] = 0.0; }

				for (i = 0; i <= NUM_HOURLY_BARS; i++) {
					sumSteps[i] = 0;
					delSteps[i] = 0.0;
				}

				Calendar today = Calendar.getInstance();
				year 	= today.get(Calendar.YEAR);
				month 	= today.get(Calendar.MONTH) + 1;
				day 	= today.get(Calendar.DATE);


				Pedometer.mDB.open();

				Cursor c0 = Pedometer.mDB.queryLapNumber(year, month, day);
				if (c0.moveToLast()) {
					mLap = c0.getInt(c0.getColumnIndex(Constants.KEY_LAP));
					c0.close();
				}

				Cursor c = Pedometer.mDB.queryLapStepsForDay(year, month, day, mLap);

				if (c == null) { count = 0; }
				else { count = c.getCount(); } // it can be zero

				if (count == 0) {
					x[0] = (double) startIndex;
					x[1] = x[0] + 10.0;
					x[2] = x[0] + 20.0;
					x[3] = x[0] + 30.0;
					x[4] = x[0] + 40.0;
					x[5] = x[0] + 50.0;
					x[6] = x[0] + 60.0;
				} else {

					if (count < NUM_HOURLY_BARS) { startIndex = 0; }
					else {
						startIndex = count - NUM_HOURLY_BARS;
						count = NUM_HOURLY_BARS;
					}

					if (c.moveToFirst()) {
						i = 0;
						do {
							j = i - startIndex;
							if (j >= 0) { sumSteps[j] = c.getInt(c .getColumnIndex(Constants.KEY_LAPSTEPS)); }
							i++;
						} while (c.moveToNext());
					}
					c.close();
					Pedometer.mDB.close();

					if (count > 1) {
						for (i = 1; i <= count; i++) {
							j = i - 1;
							delSteps[j] = (double) (sumSteps[i] - sumSteps[i - 1]);
							if (delSteps[j] < 0.0) { delSteps[j] = 0.0; }
						}
					}

					x[0] = (double) startIndex;
					x[1] = x[0] + 10.0;
					x[2] = x[0] + 20.0;
					x[3] = x[0] + 30.0;
					x[4] = x[0] + 40.0;
					x[5] = x[0] + 50.0;
					x[6] = x[0] + 60.0;
				}

				mX2.clear();
				mX2.add(x);
				mValues2.clear();
				mValues2.add(delSteps);


				mLayoutStart = (LinearLayout) mRootView.findViewById(R.id.linearlayoutstart);
				mLayoutPause = (LinearLayout) mRootView.findViewById(R.id.linearlayoutpause);
				mLayoutStart.setLayoutParams(mLayoutParamsVisible);
				mLayoutPause.setLayoutParams(mLayoutParamsHide);

				mStartButton = (ImageButton) mRootView.findViewById(R.id.startButton);
				mPauseButton = (ImageButton) mRootView.findViewById(R.id.pauseButton);
				mStopButton = (ImageButton) mRootView.findViewById(R.id.stopButton);
				mLockButton = (ImageButton) mRootView.findViewById(R.id.lockButton);
				mWalkButton = (ImageButton) mRootView.findViewById(R.id.walk_button);
				mNextButton = (ImageButton) mRootView.findViewById(R.id.next_ic_button);

				mStartButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						mOperationMode = START_MODE;
						mService.setOperationMode(mOperationMode);

						Pedometer.mPedometerSettings.setLockStatus(true);
						((Pedometer)getActivity()).updateModeDisplay();
						((Pedometer)getActivity()).displayLockState();
						((Pedometer)getActivity()).updateHourlyChart2Dynamic();

					}
				});

				mStopButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						((Pedometer)getActivity()).openStopAlertDialog();
					}
				});


				mPauseButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (Pedometer.mPedometerSettings.isPause()) {
							((Pedometer)getActivity()).showMyDialog(DIALOG_FRAGMENT_RESUME);
						} else {
							((Pedometer)getActivity()).showMyDialog(DIALOG_FRAGMENT_PAUSE);
						}
					}
				});

				mLockButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (Pedometer.mPedometerSettings.isLocked()) {
							Pedometer.mPedometerSettings.setLockStatus(false);
						} else {
							Pedometer.mPedometerSettings.setLockStatus(true);
						}
						((Pedometer)getActivity()).displayLockState();
					}
				});


				mWalkButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						((Pedometer) getActivity()).openWalkRunAlertDialog();
					}
				});

				//((Pedometer)getActivity()).updateExerciseModeDisplay(Pedometer.mPedometerSettings.getExerciseType());

				mHourlyChart 	= mRootView.findViewById(R.id.chart_hourly);
				mHourlyLap 		= mRootView.findViewById(R.id.lap_hourly);

				mToggleHourlyChart = true;
				mHourlyChart.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
				mHourlyLap.getLayoutParams().height = 0;


				mNextButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						if (mToggleHourlyChart) {
							mHourlyChart.getLayoutParams().height = 0;
							mHourlyLap.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
							mToggleHourlyChart = false;
						} else {
							mHourlyLap.getLayoutParams().height = 0;
							mHourlyChart.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
							mToggleHourlyChart = true;
							mCurrent = (Calendar) mToday.clone();
							updateHourlyChart2();
						}
						mTripaStepValueView.setText(((Integer) mStepValue).toString()); // this will refresh the screen
					}
				});

				mStepValueView 		= (TextView) mRootView.findViewById(R.id.step_value);
				mTripaStepValueView = (TextView) mRootView.findViewById(R.id.tripa_step_value);
				mDistanceValueView 	= (TextView) mRootView.findViewById(R.id.distance_value);
				mSpeedValueView 	= (TextView) mRootView.findViewById(R.id.speed_value);
				mCaloriesValueView 	= (TextView) mRootView.findViewById(R.id.calories_value);
				mSteptimeValueView 	= (TextView) mRootView.findViewById(R.id.time_value);
				mDailyPercentValueView = (TextView) mRootView.findViewById(R.id.daily_percent_value);
				mDailyGoalValueView = (TextView) mRootView.findViewById(R.id.daily_goal_value);
				mLapTitleTextView 	= (TextView) mRootView.findViewById(R.id.lap_title);
				mLapTextView 		= (TextView) mRootView.findViewById(R.id.lap_value);
				mProgressBar 		= (ProgressBar) mRootView.findViewById(R.id.trainprogressbar);

				TextView distanceUnitView       = (TextView) mRootView.findViewById(R.id.distance_units);
				TextView calorieUnitView        = (TextView) mRootView.findViewById(R.id.calories_units);
				TextView speedUnitView          = (TextView) mRootView.findViewById(R.id.speed_units);

				((TextView) mRootView.findViewById(R.id.distance_units)).setText(getString(Pedometer.mPedometerSettings.isMetric() ? R.string.km : R.string.miles));
				((TextView) mRootView.findViewById(R.id.speed_units)).setText(getString(Pedometer.mPedometerSettings.isMetric() ? R.string.kilometers_per_hour : R.string.miles_per_hour));

				if (Pedometer.mPedometerSettings.isMetric()) { mfDistanceFactor = Utils.MILE2KM; } //mTempUnit = "C";
				else { mfDistanceFactor = 1.0f; } //mTempUnit = "F";

				mTripaStepValueView.setText("" + mTripaStepValue);
				mStepValueView.setText("" + mLapStepValue);

				if (mLapDistanceValue <= 0) { mDistanceValueView.setText(String.format("%.2f", 0.00f)); }
				else { mDistanceValueView.setText(String.format("%.2f", mLapDistanceValue * mfDistanceFactor)); }

				if (mLapCaloriesValue < 1000f) {
					if (mLapCaloriesValue <= 0) { mCaloriesValueView.setText(String.format("%.1f", 0.00f)); }
					else { mCaloriesValueView.setText(String.format("%.1f",mLapCaloriesValue)); }
				} else {
					mCaloriesValueView.setText(String.format("%d",(int) mLapCaloriesValue));
				}

				mSteptimeValueView.setText(Utils.getHoursMinutesSecondsString(mLapSteptimeValue));
				mDailyGoalValueView.setText(((Integer) mGoal).toString());
				mDailyPercentValueView.setText(((Integer) (int) mPercent).toString() + "%");
				mProgressBar.setProgress((int) mPercent);
				distanceUnitView.setText(msDistanceUnit);
				calorieUnitView.setText(msCalorieUnit);
				speedUnitView.setText(msSpeedUnit);

				if (mSpeedValue <= 0) mSpeedValueView.setText("0");
				else mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f)).substring(0, 4));

				mLapTextView.setText(((Integer) mLap).toString());

//				if (Pedometer.mPedometerSettings.isPause()) ((Pedometer)getActivity()).displayPauseMode();
//				else ((Pedometer)getActivity()).displayResumeMode();

				((Pedometer)getActivity()).updateModeDisplay();
				((Pedometer)getActivity()).displayLockState();
			}

			else {
				int i, steps, hour, minute, goalsteps;
				float calories, distance, percent;
				long steptime;
				double[] dSteps = new double[25];
				double[] hSteps = new double[25];
				boolean[] bMinute = new boolean[25];
				for (i = 0; i <= 24; i++) {
					dSteps[i] = 0.0;
					hSteps[i] = 0.0;
					bMinute[i] = false;
				}

				Pedometer.mDB.open();
				Cursor c = Pedometer.mDB.queryDayAll(mCurrent.get(Calendar.YEAR), mCurrent.get(Calendar.MONTH) + 1, mCurrent.get(Calendar.DATE));

				if (c == null) return; //V511

				if (c.moveToFirst()) {
					do {
						hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
						minute = c.getInt(c.getColumnIndex(Constants.KEY_MINUTE));
						steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
						if ((hour == 23) & (minute > 30)) { //V501, V541
							dSteps[24] = ((Integer) steps).doubleValue();
						} else if ( (minute >=0) || (minute <= 30) ) { // discard 30min data, //V541
							dSteps[hour] = ((Integer) steps).doubleValue();
							bMinute[hour] = true;
						} else { //JS V436
							if (!bMinute[hour]) {
								dSteps[hour] = ((Integer) steps).doubleValue();
								bMinute[hour] = true;
							}
						}
					} while (c.moveToNext());
				}

				goalsteps = 10000;
				steps     = 0;
				distance = 0.00f;
				calories = 0.0f;
				steptime = 0l;

				if (c.moveToLast()) {
					hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
					steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
					dSteps[hour] = steps; // this is the last data, i.e. current steps
					distance    = c.getFloat(c.getColumnIndex(Constants.KEY_DISTANCE));
					calories    = c.getFloat(c.getColumnIndex(Constants.KEY_CALORIES));
					steptime    = c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME));
					goalsteps   = c.getInt(c.getColumnIndex(Constants.KEY_ACHIEVEMENT));
				}

				c.close();
				Pedometer.mDB.close();

				for (i = 0; i <= 24; i++) {
					if (i == 0) {
						hSteps[i] = 0.0;
					} else {
						if (dSteps[i] < dSteps[i - 1]) { // in case there are missing steps
							//dSteps[i] = dSteps[i - 1]; // V541 when previous day's 0:15 step is big, it affects the whole day
							hSteps[i - 1] = 0.0;
						} else {
							hSteps[i - 1] = (dSteps[i] - dSteps[i - 1]);
						}
					}
				}

				mX.clear();
				mValues.clear();
				mValues.add(hSteps);

				if (goalsteps == 0) percent = 0.0f;
				else percent = ((float) steps / (float) goalsteps) * 100.0f;



				LinearLayout mmLayoutStart = (LinearLayout) mRootView.findViewById(R.id.linearlayoutstart);
				LinearLayout mmLayoutPause = (LinearLayout) mRootView.findViewById(R.id.linearlayoutpause);
				mmLayoutStart.setLayoutParams(mLayoutParamsVisible);
				mmLayoutPause.setLayoutParams(mLayoutParamsHide);


				ImageButton mmStartButton = (ImageButton) mRootView.findViewById(R.id.startButton);
				ImageButton mmWalkButton = (ImageButton) mRootView.findViewById(R.id.walk_button);
				ImageButton mmNextButton = (ImageButton) mRootView.findViewById(R.id.next_ic_button);

				mmWalkButton.setVisibility(View.GONE);
				mmNextButton.setVisibility(View.GONE);

				mmStartButton.setEnabled(false);
				mmStartButton.setColorFilter(Color.GRAY);

				View mmHourlyChart  = mRootView.findViewById(R.id.chart_hourly);
				View mmHourlyLap    = mRootView.findViewById(R.id.lap_hourly);
				mmHourlyChart.getLayoutParams().height  = ViewGroup.LayoutParams.MATCH_PARENT;
				mmHourlyLap.getLayoutParams().height    = 0;

				TextView mmStepValueView        = (TextView) mRootView.findViewById(R.id.step_value);
				TextView mmDistanceValueView    = (TextView) mRootView.findViewById(R.id.distance_value);
				TextView mmCaloriesValueView    = (TextView) mRootView.findViewById(R.id.calories_value);
				TextView mmSteptimeValueView    = (TextView) mRootView.findViewById(R.id.time_value);
				TextView mmDailyPercentValueView = (TextView) mRootView.findViewById(R.id.daily_percent_value);
				TextView mmDailyGoalValueView   = (TextView) mRootView.findViewById(R.id.daily_goal_value);
				TextView mmDistanceUnitView     = (TextView) mRootView.findViewById(R.id.distance_units);
				TextView mmCalorieUnitView      = (TextView) mRootView.findViewById(R.id.calories_units);
				TextView mmSpeedUnitView        = (TextView) mRootView.findViewById(R.id.speed_units);
				TextView mmSpeedValueView       = (TextView) mRootView.findViewById(R.id.speed_value);
				TextView mmLapTitleTextView 	= (TextView) mRootView.findViewById(R.id.lap_title);
				ProgressBar mmProgressBar       = (ProgressBar) mRootView.findViewById(R.id.trainprogressbar);

				mmDailyGoalValueView.setText("" + goalsteps);
				mmDailyPercentValueView.setText(((Integer) (int) percent).toString() + "%");
				mmProgressBar.setProgress((int) percent);
				mmStepValueView.setText("" + steps);
				mmDistanceValueView.setText(String.format("%.2f", distance * mfDistanceFactor));
				mmCaloriesValueView.setText(String.format("%d", (int) (calories *mfCalorieFactor) ));
				mmSteptimeValueView.setText(Utils.getHoursMinutesString((int) (steptime / 1000)));
				mmDistanceUnitView.setText(msDistanceUnit);
				mmCalorieUnitView.setText(msCalorieUnit);
				mmSpeedUnitView.setText(msSpeedUnit);
				mmLapTitleTextView.setVisibility(View.GONE);

				double speed;
				String string;
				if (steptime != 0) {
					speed = distance * mfDistanceFactor * 1000.0 * 3600.0 / steptime;
					if (speed <= 0.0) string = "" + "----";
					else string = ("" + (speed + 0.000001f)).substring(0, 4);
				} else {
					string = "" + "0";
				}
				mmSpeedValueView.setText(string);
			}
		}
	}

	public static class MyAlertDialogFragment extends DialogFragment {

		public static MyAlertDialogFragment newInstance(int title, int type) {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			args.putInt("type", type);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");

			return new AlertDialog.Builder(getActivity())   //.setIcon(R.drawable.alert_dialog_icon)
					.setTitle(title)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									int type = getArguments().getInt("type");
									((Pedometer)getActivity()).doPositiveClick(type);
								}
							}
					)
					.setNegativeButton(R.string.cancelled,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									((Pedometer)getActivity()).doNegativeClick();
								}
							}
					)
					.create();
		}
	}

	private void showMyDialog(int type) {
		DialogFragment newFragment;
		switch (type) {
			case DIALOG_FRAGMENT_PAUSE:
				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_pause_message, type);
				break;
			case DIALOG_FRAGMENT_RESUME:
				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_resume_message, type);
				break;
			case DIALOG_FRAGMENT_NEXT_LAP:
				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_next_lap_message, type);
				break;
			case DIALOG_FRAGMENT_DAILY_RESET:
				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_daily_reset_message, type);
				break;
			default:
				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_pause_message, type);
				break;
		}
		newFragment.show(getFragmentManager(), "dialog");
	}

	private void doPositiveClick(int type) {
		switch (type) {
			case DIALOG_FRAGMENT_PAUSE:
				Pedometer.getInstance().sendBroadcast(new Intent(AccuService.ACCUPEDO_PAUSE_REQUEST));
				displayPauseMode();
				break;
			case DIALOG_FRAGMENT_RESUME:
				Pedometer.getInstance().sendBroadcast(new Intent(AccuService.ACCUPEDO_RELEASE_REQUEST));
				displayResumeMode();
				break;
			case DIALOG_FRAGMENT_NEXT_LAP:
//				mService.saveToDB();
//				mService.startNewLap();
				break;
			case DIALOG_FRAGMENT_DAILY_RESET:
				mService.setManualSteps(0);
				break;
		}
	}

	private void doNegativeClick() { }

//	private void openWalkRunAlertDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(Pedometer.getInstance());
//		builder.setTitle(R.string.exercise_type_setting_title);
//		builder.setItems(R.array.exercise_type_preference, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				switch (which) {
//					case 0:
//						Pedometer.mPedometerSettings.setExerciseType(0);
//						mService.reloadSettings();
//						mWalkButton.setImageDrawable(ContextCompat.getDrawable(Pedometer.getInstance(), R.drawable.run_ic));
//						break;
//					case 1:
//						Pedometer.mPedometerSettings.setExerciseType(1);
//						mService.reloadSettings();
//						mWalkButton.setImageDrawable(ContextCompat.getDrawable(Pedometer.getInstance(), R.drawable.walk_ic));
//						break;
//				}
//			}
//		});
//		AlertDialog dialog = builder.create();
//		dialog.show();
//	}

//	private void displayPauseMode() {
//		mStepValueView.setTextColor(Pedometer.getInstance().getResources().getColor(R.color.mygray));
//		mPauseImageButton.setImageDrawable(ContextCompat.getDrawable(Pedometer.getInstance(), R.drawable.start_imagebutton));
//		mTripaStepValueView.setTextColor(Pedometer.getInstance().getResources().getColor(R.color.myblue));
//	}

//	private void displayResumeMode() {
//		mStepValueView.setTextColor(Pedometer.getInstance().getResources().getColor(AccuService.mScreenStepTextColor));
//		mPauseImageButton.setImageDrawable(ContextCompat.getDrawable(Pedometer.getInstance(), R.drawable.stop_imagebutton));
//		mTripaStepValueView.setTextColor(Pedometer.getInstance().getResources().getColor(AccuService.mScreenLapStepTextColor));
//	}

	private void initializeDateFormat() {
		mToday = Calendar.getInstance();
		mCurrent = (Calendar) mToday.clone();
		mFirstDay = (Calendar) mToday.clone();

		Pedometer.mDB.open();
		Cursor c = Pedometer.mDB.queryFirstDay();

		if (c != null) { //V451
			if (c.getCount() != 0 ) {
				mFirstDay.set(Calendar.YEAR, c.getInt(c.getColumnIndex(Constants.KEY_YEAR)));
				mFirstDay.set(Calendar.MONTH, c.getInt(c.getColumnIndex(Constants.KEY_MONTH)) - 1);
				mFirstDay.set(Calendar.DATE, c.getInt(c.getColumnIndex(Constants.KEY_DAY)));
				mFirstDay.set(Calendar.HOUR_OF_DAY, 0);
				mFirstDay.set(Calendar.MINUTE, 0);
				c.close();
			}
		}
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
		numberDays = (int) (diff / 86400000); // V551 (24 * 60 * 60 * 1000));
		return numberDays + 1;
	}

	private void setPedometer() {
		mIsRunning = Pedometer.mPedometerSettings.isServiceRunning();
		if (!mIsRunning && Pedometer.mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsRunning) {
			bindStepService();
		}
		Pedometer.mPedometerSettings.clearServiceRunning();
	}

	public static AccuService mService; //V555

	private final ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((AccuService.StepBinder) service).getService();
			mService.registerCallback(mCallback);
			mService.reloadSettings();
		}
		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			mIsRunning = true;
			Pedometer.getInstance().startService(new Intent(Pedometer.getInstance(), AccuService.class));
		}
	}

	private void bindStepService() {
		mIsRunning = true;
		mIsBound = true; //V437
		Pedometer.getInstance().bindService(new Intent(Pedometer.getInstance(), AccuService.class),
				mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	private void unbindStepService() { //V511 private
		if (mIsRunning) {
			mIsRunning = false;
			if (mConnection != null) {
				if (mIsBound) { //V437
					if (isMyServiceRunning(AccuService.class)) { //V511
						unbindService(mConnection);
					}
					mIsBound = false;
				}
			}
		}
	}

	private void stopStepService() { //V511 private
		mIsRunning = false;
		stopService(new Intent(Pedometer.getInstance(), AccuService.class));
	}

	private AccuService.ICallback mCallback = new AccuService.ICallback() {
		public void stepsChanged(int lapsteps, int steps) {
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, lapsteps,
					steps));
		}

		public void distanceChanged(float lapdistance, float distance) {
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG,
					(int) (lapdistance * 1000), (int) (distance * 1000)));
		}

		public void paceChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
		}

		public void speedChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG,
					(int) (value * 1000), 0));
		}

		public void caloriesChanged(float lapcalories, float calories) {
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,
					(int) (lapcalories * 1000), (int) (calories * 1000)));
		}

		public void steptimeChanged(long lapsteptime, long steptime) {
			mHandler.sendMessage(mHandler.obtainMessage(STEPTIME_MSG,
					(int) (lapsteptime / 1000), (int) (steptime / 1000)));
		}

		public void updateDisplay(int value) {
			//mHandler.sendMessage(mHandler.obtainMessage(UPDATE_MSG, value, 0));
			switch (value) {
				case 1:
					displayPauseMode();
					break;
				case 2:
					displayResumeMode();
					break;
			}
		}

		public void goalChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(GOAL_MSG, value, 0));
		}

		public void percentChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(PERCENT_MSG,
					(int) (value * 1000), 0));
		}

		public void lapnumberChanged(int value) {
			mHandler.sendMessage(mHandler
					.obtainMessage(LAPNUMBER_MSG, value, 0));
		}

		public void weatherChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(WEATHER_MSG,
					(int) (value * 1000), 0));
		}

		public void hourlyChartChanged(int value) {
			//mHandler.sendMessage(mHandler.obtainMessage(HOURLYCHART_MSG, 0, 0));
			updateHourlyChart2Dynamic();
		}

	};

	private static final int STEPS_MSG 		= 1;
	private static final int DISTANCE_MSG 	= 2;
	private static final int CALORIES_MSG 	= 3;
	private static final int STEPTIME_MSG 	= 4;
	private static final int UPDATE_MSG 	= 5;
	private static final int GOAL_MSG 		= 6;
	private static final int PERCENT_MSG 	= 7;
	private static final int SPEED_MSG 		= 8;
	private static final int PACE_MSG 		= 9;
	private static final int LAPNUMBER_MSG 	= 10;
	private static final int WEATHER_MSG 	= 11;
	private static final int HOURLYCHART_MSG = 12;

	final static MessageHandler mHandler = new MessageHandler(); //V551 changed to static
	static class MessageHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case STEPS_MSG:
					mLapStepValue 	= msg.arg1;
					mStepValue 			= msg.arg2;
					mStepValueView.setText("" + mLapStepValue);
					mTripaStepValueView.setText("" + mStepValue);
					break;

				case DISTANCE_MSG:
					mLapDistanceValue = msg.arg1 / 1000f;
					if (mLapDistanceValue <= 0) {
						mDistanceValueView.setText(String.format("%.2f", 0.00f));
					} else {
						mDistanceValueView.setText(String.format("%.2f", mLapDistanceValue * mfDistanceFactor));
					}
					break;

				case CALORIES_MSG:
					mLapCaloriesValue = msg.arg1 / 1000f;
					mCaloriesValue = msg.arg2 / 1000f;
					if (mCaloriesValue < 1000f) {
						if (mLapCaloriesValue <= 0) {
							mCaloriesValueView.setText(String.format("%.1f", 0.00f));
						} else {
							mCaloriesValueView.setText(String.format("%.1f", mLapCaloriesValue));
						}
					} else {
						mCaloriesValueView.setText(String.format("%d", (int) mLapCaloriesValue));
					}
					break;

				case STEPTIME_MSG:
					mLapSteptimeValue = msg.arg1; // msg.arg1 is in second now
					mSteptimeValueView.setText(Utils.getHoursMinutesSecondsString(mLapSteptimeValue));
					break;

				case UPDATE_MSG:
//					switch ((int) msg.arg1) {
//						case 1:
//							((Pedomete)getActivity()).displayPauseMode();
//							break;
//						case 2:
//							displayResumeMode();
//							break;
//					}
					break;

				case GOAL_MSG:
					mGoal = msg.arg1;
					mDailyGoalValueView.setText(((Integer) mGoal).toString());
					break;

				case PERCENT_MSG:
					mPercent = msg.arg1 / 1000f;
					mDailyPercentValueView.setText(((Integer) (int) mPercent).toString() + "%");
					mProgressBar.setProgress((int) mPercent);
					break;

				case PACE_MSG:
					// mPaceValue = msg.arg1;
					// if (mPaceValue <= 0) {
					// mSpeedValueView.setText("0");
					// } else {
					// mSpeedValueView.setText(""
					// + ((Integer) mPaceValue).toString());
					// }
					break;

				case SPEED_MSG:
					mSpeedValue = ((int) msg.arg1) * mfDistanceFactor / 1000f;
					if (mSpeedValue <= 0) {
						mSpeedValueView.setText("0");
					} else {
						mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f)).substring(0, 4));
					}
					break;
				case LAPNUMBER_MSG:
					mLap = msg.arg1;
					mLapTextView.setText(((Integer) mLap).toString());
					if (mLap > 0) {
						mLapTextView.setVisibility(View.VISIBLE);
						//mLapTitleTextView.setVisibility(View.VISIBLE);
					} else {
						mLapTextView.setVisibility(View.GONE);
						//mLapTitleTextView.setVisibility(View.GONE);
					}
					break;

				case HOURLYCHART_MSG:
					//updateHourlyChart2();
					break;

				// case WEATHER_MSG:
				// mCity = mService.mCity;
				// if (mService.mIsMetric) {
				// mTemperature = (int) (msg.arg1 / 1000f);
				// } else {
				// mTemperature = (int) (msg.arg1 / 1000f * 1.8 + 32.0);
				// }
				// updateWeather();
				// break;

				default:
					super.handleMessage(msg);
			}
		}

	}

	private boolean checkDBfileExist() {

		boolean fileExists;
		File sd = new File(Environment.getExternalStorageDirectory() + Constants.ACCUPEDO_FOLDERNAME);
		if (sd.exists()) {
			String backupDBPath = Constants.DATABASE_FILENAME;
			File backupDB = new File(sd, backupDBPath);
			fileExists = backupDB.exists();
		} else {
			fileExists = false;
		}
		return fileExists;
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {   //V511
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

		private void openStopAlertDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				Pedometer.getInstance());

		final CharSequence[] items = { getString(R.string.stop_dialog_nosave),
				getString(R.string.stop_dialog_save)
				// getString(R.string.stop_dialog_save_fb),
		};
		// final CharSequence[] items = { "yes",
		// "yest",
		// "no"};

		mSelectedItem = Pedometer.mPedometerSettings.getSaveOption();
		builder.setSingleChoiceItems(items, mSelectedItem,
				new DialogInterface.OnClickListener() {
					// When you click the radio button
					@Override
					public void onClick(DialogInterface dialog, int item) {

						mSelectedItem = item;
					}
				});

		builder.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {

						switch (mSelectedItem) {
							case STOP_MODE_NOSAVE:
								mOperationMode = STOP_MODE_NOSAVE;
								mService.setOperationMode(mOperationMode);
								updateModeDisplay();
								break;
							case STOP_MODE_SAVE:
								mOperationMode = STOP_MODE_SAVE;
								mService.setOperationMode(mOperationMode);
								updateModeDisplay();
								break;
							default:
								// Log.i(TAG, "Facebook mode3");
								break;

						}
						Pedometer.mPedometerSettings.setSaveOption(mSelectedItem);
					}
				});
		builder.setNegativeButton(getString(R.string.cancelled),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}


	private void updateExerciseModeDisplay(int mode) {
		switch (mode) {
			case AccuService.FLAG_MODE_WALK:
				mWalkButton.setImageDrawable(getResources().getDrawable(
						R.drawable.walk_ic));
				break;
			case AccuService.FLAG_MODE_RUN:
				mWalkButton.setImageDrawable(getResources().getDrawable(
						R.drawable.run_ic));
				break;
			case AccuService.FLAG_MODE_HIKE:
				mWalkButton.setImageDrawable(getResources().getDrawable(
						R.drawable.hiking_ic));
				break;
			case AccuService.FLAG_MODE_STAIRWAY:
				mWalkButton.setImageDrawable(getResources().getDrawable(
						R.drawable.stair_ic));
				break;
		}
	}

	public void updateModeDisplay() {
		if (Pedometer.mPedometerSettings.isPause()) {
			displayPauseMode();
		} else {
			switch (mOperationMode) {
				case STOP_MODE_NOSAVE:
				case STOP_MODE_SAVE:
					displayStopMode();
					break;
				case START_MODE:
				case RESUME_MODE:
					displayResumeMode();
					break;
				case PAUSE_MODE:
					displayPauseMode();
					break;
				default:
			}

		}
	}

	private void displayPauseMode() {
		mStepValueView.setTextColor(Pedometer.getInstance().getResources()
				.getColor(R.color.mygray));

		// mStepValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myblue));
		// mDistanceValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myblue));
		// mSpeedValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myblue));
		// mCaloriesValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myblue));
		// mSteptimeValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myblue));

		// mLayoutPause.setBackgroundColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.mygray));
		// mPauseButton.setImageDrawable(Pedometer.getInstance()
		// .getResources().getDrawable(R.drawable.start_imagebutton));

		mTripaStepValueView.setTextColor(Pedometer.getInstance().getResources()
				.getColor(R.color.mygray));

		// mStartButton.setVisibility(View.GONE);
		// mStopButton.setVisibility(View.VISIBLE);
		// mPauseButton.setVisibility(View.VISIBLE);
		// mPauseButton.setText(R.string.resume);
		// mLockButton.setVisibility(View.VISIBLE);
		mPauseButton.setImageDrawable(Pedometer.getInstance().getResources()
				.getDrawable(R.drawable.resumebutton));
		mLayoutStart.setLayoutParams(mLayoutParamsHide);
		mLayoutPause.setLayoutParams(mLayoutParamsVisible);
	}

	private void displayResumeMode() {
		mStepValueView.setTextColor(Pedometer.getInstance().getResources()
				.getColor(AccuService.mScreenStepTextColor));

		// mStepValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myorange));
		// mDistanceValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myorange));
		// mSpeedValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myorange));
		// mCaloriesValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myorange));
		// mSteptimeValueView.setTextColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myorange));

		// mLayoutPause.setBackgroundColor(Pedometer.getInstance().getResources()
		// .getColor(R.color.myorange));
		// mPauseButton.setImageDrawable(Pedometer.getInstance().getResources()
		// .getDrawable(R.drawable.stop_imagebutton));

		mTripaStepValueView.setTextColor(Pedometer.getInstance().getResources()
				.getColor(R.color.mywhite));
		// mStartButton.setVisibility(View.GONE);
		// mStopButton.setVisibility(View.VISIBLE);
		// mPauseButton.setVisibility(View.VISIBLE);
		// mPauseButton.setText(R.string.pause);
		// mLockButton.setVisibility(View.VISIBLE);
		mPauseButton.setImageDrawable(Pedometer.getInstance().getResources()
				.getDrawable(R.drawable.pausebutton));
		mLayoutStart.setLayoutParams(mLayoutParamsHide);
		mLayoutPause.setLayoutParams(mLayoutParamsVisible);


	}

	private void openWalkRunAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Pedometer.getInstance());
		builder.setTitle(R.string.exercise_type_setting_title);
		builder.setItems(R.array.exercise_type_preference, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				mService.setExerciseMode(which);
				Pedometer.mPedometerSettings.setExerciseType(which);
				updateExerciseModeDisplay(which);
//				switch (which) {
//					case 0:
//						Pedometer.mPedometerSettings.setExerciseType("running");
//						mService.reloadSettings();
//						mWalkButton.setImageDrawable(ContextCompat.getDrawable(Pedometer.getInstance(), R.drawable.run_ic));
//						break;
//					case 1:
//						Pedometer.mPedometerSettings.setExerciseType("walking");
//						mService.reloadSettings();
//						mWalkButton.setImageDrawable(ContextCompat.getDrawable(Pedometer.getInstance(), R.drawable.walk_ic));
//						break;
//				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void displayStopMode() {
		// mStartButton.setVisibility(View.VISIBLE);
		// mStopButton.setVisibility(View.GONE);
		// mPauseButton.setVisibility(View.GONE);
		// mLockButton.setVisibility(View.GONE);
		mLayoutStart.setLayoutParams(mLayoutParamsVisible);
		mLayoutPause.setLayoutParams(mLayoutParamsHide);
	}

	private void displayLockState() {
		if (Pedometer.mPedometerSettings.isLocked()) {
			mLockButton.setImageDrawable(getResources().getDrawable(R.drawable.unlocked_ic));
			AlphaAnimation alpha = new AlphaAnimation(0.25F, 0.25F);
			alpha.setDuration(0); // Make animation instant
			alpha.setFillAfter(true); // Tell it to persist after
			mStopButton.startAnimation(alpha);
			mPauseButton.startAnimation(alpha);

			mStopButton.setEnabled(false);
			mPauseButton.setEnabled(false);

		} else {
			mLockButton.setImageDrawable(getResources().getDrawable(R.drawable.locked_ic));
			AlphaAnimation alpha = new AlphaAnimation(1.0F, 1.0F);
			alpha.setDuration(0); // Make animation instant
			alpha.setFillAfter(true); // Tell it to persist after
			mStopButton.startAnimation(alpha);
			mPauseButton.startAnimation(alpha);
			mStopButton.setEnabled(true);
			mPauseButton.setEnabled(true);
		}
	}


}




































//import java.io.File;
//import java.io.IOException;
//import java.lang.Thread.UncaughtExceptionHandler;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.chart.HourBarChart2;
//import com.corusen.steponfre.chart.HourBarChartForMain;
//import com.corusen.steponfre.chart.IChart2;
//import com.corusen.steponfre.chart.IChartForMain;
//import com.corusen.steponfre.database.Constants;
//import com.corusen.steponfre.database.MyDB;
//import com.corusen.steponfre.database.SdcardManager;
//
//import android.Manifest;
//import android.app.ActivityManager;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.content.res.TypedArray;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.preference.PreferenceManager;
////import android.support.v4.app.ActionBarDrawerToggle;
////import android.support.v4.app.Fragment;
////import android.support.v4.app.FragmentActivity;
////import android.support.v4.app.FragmentManager;
////import android.support.v4.app.FragmentTransaction;
////import android.support.v4.widget.DrawerLayout;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v4.app.ActionBarDrawerToggle;
//import android.text.format.DateFormat;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.AlphaAnimation;
//import android.widget.AdapterView;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//
//import org.achartengine.GraphicalView;
//
//public class Pedometer extends FragmentActivity { //  {  BaseActivity
//
//	public static final int ACCUPEDO_INTENT_REQUEST_MANUAL_STEPS = 1;
//
//	private InterstitialAd      mInterstitialAd;
//	public AdRequest            mAdRequest;
//	private AdView              mAdView;
//
//	private static Pedometer mInstance = null;
//	public static SharedPreferences mSettings;
//	public static PedometerSettings mPedometerSettings;
//	public static MyDB mDB;
//
//	// navigation settings
//	private DrawerLayout 				mDrawerLayout;
//	private ListView 					mDrawerList;
//	private ActionBarDrawerToggle 		mDrawerToggle;
//	private CharSequence 				mDrawerTitle;
//	private CharSequence 				mTitle;
//	private String[] 					navMenuTitles;
//	private TypedArray 					navMenuIcons;
//	private ArrayList<NavDrawerItem> 	navDrawerItems;
//	private NavDrawerListAdapter 		mAdapter;
//    private int mScreenIndex;
//
//	public static final int NUM_HOURLY_BARS 	= 60;
//	public static final int STOP_MODE_NOSAVE 	= 0; // when stop button pressed
//	public static final int STOP_MODE_SAVE 		= 1; // when stop button pressed
//	public static final int START_MODE 			= 2; // when start button pressed
//	public static final int RESUME_MODE 		= 3; // when resume button pressed
//	public static final int PAUSE_MODE 			= 4; // when pause button pressed
//
//	public static int mOperationMode = STOP_MODE_NOSAVE;
//
//	public static final int SPINNER_ITEM_SETTINGSFIRST 	= 11;
//	public static final int SPINNER_ITEM_PEDOMETER 		= 0;
//	public static final int SPINNER_ITEM_CHART 			= 1;
//	public static final int SPINNER_ITEM_HISTORY 		= 2;
//	public static final int SPINNER_ITEM_SHARE 			= 3;
//	public static final int SPINNER_ITEM_EDITSTEPS 		= 4;
//	public static final int SPINNER_ITEM_BACKUP 		= 5;
//	public static final int SPINNER_ITEM_SETTINGS 		= 6;
//	public static final int SPINNER_ITEM_HELP 			= 7;
//	public static final int SPINNER_ITEM_PURCHASE 		= 8;
//	public static final int SPINNER_ITEM_QUIT 			= 9;
//
//	private static final int DIALOG_FRAGMENT_PAUSE       = 1;
//	private static final int DIALOG_FRAGMENT_RESUME      = 2;
//	private static final int DIALOG_FRAGMENT_NEXT_LAP    = 3;
//	private static final int DIALOG_FRAGMENT_DAILY_RESET = 4;
//
//	public static String[] spinnerStrings;
//	public boolean mExternalStorageAvailable = false;
//	public boolean mExternalStorageWriteable = false;
//
//	//public FragmentPedometer mFragmentPedometer;
//
//	private static int mSpinnerPosition = 0;
//
//	private CollectionPagerAdapter  	mCollectionPagerAdapter;  //V451
//	//private ViewPager               	mViewPager;
//	private MyViewPager               	mViewPager;
//
//
//	private static Calendar 		mCurrent;
//	private static Calendar 		mToday;
//	private static Calendar 		mFirstDay;
//	private static GraphicalView 	mChartView;
//	private static int      		mNumberDays;
//	private static int      		mWeekFormat;
//	//private static Boolean  mIsMetric;
//	private static float    		mfDistanceFactor;
//	private static float    		mfCalorieFactor;
//	private static String    		msDistanceUnit;
//	private static String    		msCalorieUnit;
//	private static String    		msSpeedUnit;
//
//	private static final List<double[]> mX = new ArrayList<>();
//	private static final List<double[]> mValues = new ArrayList<>();
//
//	private boolean mIsRunning;
//	private boolean mIsBound;
//
//	private static boolean  mToggleHourlyChart;
//	private static View     mHourlyChart;
//	private static View     mHourlyLap;
//
//	private static TextView mLapTextView;
//	private static TextView mSpeedValueView;
//	private static TextView mTripaStepValueView;
//	private static TextView mStepValueView;
//	private static TextView mDistanceValueView;
//	private static TextView mCaloriesValueView;
//	private static TextView mSteptimeValueView;
//	private static TextView mDailyPercentValueView;
//	private static TextView mDailyGoalValueView;
//	private static TextView mLapTitleTextView;
//
//	private static ProgressBar mProgressBar;
//
//	private static int      mLap;
//	private static int      mStepValue;
//	private static int      mTripaStepValue;
//	private static int      mSteptimeValue;
//	private static int      mGoal;
//	private static float    mDistanceValue;
//	private static float    mSpeedValue;
//	private static float    mCaloriesValue;
//	private static float    mPercent;
//
//	private static LinearLayout mLayoutStart;
//	private static LinearLayout mLayoutPause;
//	private static LinearLayout.LayoutParams mLayoutParamsVisible = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
//	private static LinearLayout.LayoutParams mLayoutParamsHide = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f);
//
//	private static ImageButton mStartButton;
//	private static ImageButton mPauseButton;
//	private static ImageButton mStopButton;
//	private static ImageButton mLockButton;
//	private static ImageButton mWalkButton;
//	private static ImageButton mNextButton;
//
//	private int mSelectedItem;
//
//	private static ObjectFragment msFragment;
//
//
//	// public static boolean mQuitting = false;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//		Pedometer.mInstance = this;
//		mSettings = PreferenceManager.getDefaultSharedPreferences(Pedometer.getInstance());
//		mPedometerSettings = new PedometerSettings(mSettings);
//		mDB = new MyDB(this);
//
//		initializeDateFormat();
//
//		mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager()); //V451
//		mViewPager = (MyViewPager) findViewById(R.id.pager);
//		mViewPager.setAdapter(mCollectionPagerAdapter);
//
//		mTitle = mDrawerTitle = getTitle();
//		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
//		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
//		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
//
//		navDrawerItems = new ArrayList<NavDrawerItem>();
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));// ,true,"22"));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));// ,true,"50+"));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
//
//		if (Constants.IS_VERSION_TE) { navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1))); }
//
//		navMenuIcons.recycle();
//
//		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
//		mAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
//		mDrawerList.setAdapter(mAdapter);
//
//		if (mPedometerSettings.isNewInstallation117()) { disableNavigationDrawMenu(); }
//		else { enableNavigationDrawMenu(); }
//		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(AccuService.mScreenAcitionBarColor)));
//
//		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.accupedo, R.string.accupedo ) {
//			public void onDrawerClosed(View view) {
//				getActionBar().setTitle(mTitle);
//				invalidateOptionsMenu();
//			}
//
//			public void onDrawerOpened(View drawerView) {
//				getActionBar().setTitle(mDrawerTitle);
//				invalidateOptionsMenu();
//			}
//		};
//		mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//		// JS V115
//		final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//
//			@Override
//			public void uncaughtException(Thread thread, Throwable ex) {
//				if (thread.getName().startsWith("AdWorker")) {
//					Log.w("ADMOB", "AdWorker thread thrown an exception.", ex);
//				} else if (defaultHandler != null) {
//					defaultHandler.uncaughtException(thread, ex);
//				} else {
//					throw new RuntimeException("No default uncaught exception handler.", ex);
//				}
//			}
//		});
//
//		if (mPedometerSettings.isNewInstallation()) { // new installation
//			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//			displayView(SPINNER_ITEM_SETTINGSFIRST);
//		} else {
//			if (mPedometerSettings.isNewInstallation117()) { // update to version 411
//				mPedometerSettings.setGender("male");
//				mPedometerSettings.setBirthYear(1990);
//				mPedometerSettings.setBirthMonth(1);
//				mPedometerSettings.setBirthDay(1);
//				if (mPedometerSettings.isMetric()) {
//					mPedometerSettings.setBodyHeight(175f);
//				} else {
//					mPedometerSettings.setBodyHeight(175f * Utils.CM2INCH);
//				}
//				displayView(SPINNER_ITEM_SETTINGSFIRST);
//			} else {
//				if (savedInstanceState == null) {
//					displayView(SPINNER_ITEM_PEDOMETER);
//				}
//			}
//		}
//
//		if (Constants.IS_VERSION_TE) {
//
//			mInterstitialAd = new InterstitialAd(this);
//			mInterstitialAd.setAdUnitId(Constants.ACCUPEDO_ADMOB_INTERSTITIAL_ID);
//			mInterstitialAd.setAdListener(new AdListener() {
//				public void onAdClosed() {
//					Intent intent;
//					if (mScreenIndex == 0) { //Chart
//						sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
//						intent = new Intent(getBaseContext(), com.corusen.steponfre.chart.ChartActivity.class);
//						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(intent);
//					} else if (mScreenIndex == 1) { //History
//						sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
//						intent = new Intent(getBaseContext(), com.corusen.steponfre.database.History.class);
//						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(intent);
//					}
//
//					requestNewInterstitial();
//				}
//			});
//
//			mAdView = new AdView(Pedometer.getInstance());  //Admob banner
//			mAdView.setAdSize(AdSize.SMART_BANNER);         //InMobi does not support SMART_BANNER
//			mAdView.setAdUnitId(Constants.ACCUPEDO_ADMOB_ID);
//			LinearLayout layout = (LinearLayout) findViewById(R.id.adView);
//			layout.addView(mAdView);
//			requestNewInterstitial();
//		}
//	}
//
//	private void requestNewInterstitial() {
//
//		Date birthday = mPedometerSettings.getBirthDate();
//		int gender = mPedometerSettings.getGender();
//
//		PackageManager pm = this.getPackageManager();
//		int hasPerm = pm.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, this.getPackageName());
//		if (hasPerm != PackageManager.PERMISSION_GRANTED) {
//			LocationManager coarseLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			Location coarseLocation = coarseLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			mAdRequest = new AdRequest.Builder().setLocation(coarseLocation).setGender(gender).setBirthday(birthday).build();
//		} else {
//			mAdRequest = new AdRequest.Builder().setGender(gender).setBirthday(birthday).build();
//		}
//
//		mAdView.loadAd(mAdRequest);
//		mInterstitialAd.loadAd(mAdRequest);
//	}
//
//	public void enableNavigationDrawMenu() {
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//		getActionBar().setHomeButtonEnabled(true);
//	}
//
//	public void disableNavigationDrawMenu() {
//		getActionBar().setDisplayHomeAsUpEnabled(false);
//		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//		getActionBar().setHomeButtonEnabled(false);
//	}
//
//	private class SlideMenuClickListener implements ListView.OnItemClickListener {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			// display view for selected nav drawer item
//			displayView(position);
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		if (mPedometerSettings.isNewInstallation117()) {
//			getMenuInflater().inflate(R.menu.dummy, menu);
//		} else {
//			getMenuInflater().inflate(R.menu.main_activity, menu);
//		}
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		if (mDrawerToggle.onOptionsItemSelected(item)) {
//			return true;
//		}
//
//		Intent intent;
//		int id = item.getItemId();
//		if (id == R.id.menu_chart) {
//            Random r = new Random();
//            if (mInterstitialAd.isLoaded() && (r.nextFloat() <= Constants.ADMOB_INTERSTITIAL_DISPLAY_CHANCE)) {
//                mScreenIndex = 0;
//                mInterstitialAd.show();
//            } else {
//                sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
//                intent = new Intent(getBaseContext(), com.corusen.steponfre.chart.ChartActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//			return true;
//		} else if (id == R.id.menu_history) {
//            Random r = new Random();
//            if (mInterstitialAd.isLoaded() && (r.nextFloat() <= Constants.ADMOB_INTERSTITIAL_DISPLAY_CHANCE)) {
//                mScreenIndex = 1;
//                mInterstitialAd.show();
//            } else {
//                sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
//                intent = new Intent(getBaseContext(), com.corusen.steponfre.database.History.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public void displayView(int position) {
//		Fragment fragment = null;
//		Intent intent;
//		switch (position) {
//			case SPINNER_ITEM_SETTINGSFIRST:
//				fragment = new FragmentSettingsFirst();
//				break;
//
//			case SPINNER_ITEM_PEDOMETER:
//				break;
//
//			case SPINNER_ITEM_CHART:
//				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
//				intent = new Intent(getBaseContext(), ActivityShareTabs.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				break;
//
//			case SPINNER_ITEM_HISTORY:
//				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
//				intent = new Intent(getBaseContext(), ActivityShareTabs.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				break;
//
//			case SPINNER_ITEM_SHARE:
//				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
//				intent = new Intent(getBaseContext(), ActivityShareTabs.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				break;
//
//			case SPINNER_ITEM_EDITSTEPS:
//				sendBroadcast(new Intent(AccuService.ACCUPEDO_SAVE_DB_REQUEST));
//				fragment = new FragmentEditsteps();
//				break;
//
//			case SPINNER_ITEM_BACKUP:
//				intent = new Intent(getBaseContext(), ActivityBackup.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				break;
//
//			case SPINNER_ITEM_SETTINGS:
//				fragment = new FragmentSettings();
//				break;
//
//			case SPINNER_ITEM_HELP:
//				fragment = new FragmentHelp();
//				break;
//
//			case SPINNER_ITEM_PURCHASE:
//				fragment = new FragmentAccupedoPro();
//				break;
//
//			case SPINNER_ITEM_QUIT:
//				mService.cancelRestartAlarm();
//				mService.updateWidgetQuit();
//				unbindStepService();
//				stopStepService();
//				finish();
//				break;
//
//			default:
//				break;
//		}
//
//		if (fragment != null) {
//			FragmentManager fragmentManager = getFragmentManager();
//			FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//			if (position != SPINNER_ITEM_SETTINGSFIRST) {
//
//				transaction.replace(R.id.frame_container, fragment).commit();
//
//			} else if (position == SPINNER_ITEM_PEDOMETER) {
//				initializeDateFormat();
//				mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());  //V509 //V451
//				mViewPager = (MyViewPager) findViewById(R.id.pager); //mViewPager.setAdapter(mCollectionPagerAdapter);
//				mViewPager.setAdapter(mCollectionPagerAdapter);
//				mViewPager.setCurrentItem(mNumberDays - 1);
//				mViewPager.getAdapter().notifyDataSetChanged();
//
//				transaction.hide(fragment);
//				Fragment fr = fragmentManager.findFragmentById(R.id.frame_container);
//				if ((fr != null) && (fr.getView() != null)) { fr.getView().setVisibility(View.GONE); }
//
//				mDrawerList.setItemChecked(position, true);
//				mDrawerList.setSelection(position);
//				setTitle(navMenuTitles[position]);
//				mDrawerLayout.closeDrawer(mDrawerList);
//				mViewPager.setVisibility(View.VISIBLE);
//				if (Constants.IS_VERSION_TE) { mAdView.setVisibility(View.VISIBLE); }
//			} else {
//				transaction.replace(R.id.frame_container, fragment).commit();
//				mDrawerList.setItemChecked(position, true);
//				mDrawerList.setSelection(position);
//				setTitle(navMenuTitles[position]);
//				mDrawerLayout.closeDrawer(mDrawerList);
//				mViewPager.setVisibility(View.GONE);
//				if (Constants.IS_VERSION_TE) { mAdView.setVisibility(View.GONE); }
//			}
//
//		} else { //V561
//			if (position == SPINNER_ITEM_PEDOMETER) {
//				FragmentManager fragmentManager = getFragmentManager();
//
//				initializeDateFormat();
//				mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());  //V509 //V451
//				mViewPager = (MyViewPager) findViewById(R.id.pager); //mViewPager.setAdapter(mCollectionPagerAdapter);
//				mViewPager.setAdapter(mCollectionPagerAdapter);
//				mViewPager.setCurrentItem(mNumberDays - 1);
//				mViewPager.getAdapter().notifyDataSetChanged();
//
//				Fragment fr = fragmentManager.findFragmentById(R.id.frame_container);
//				if ((fr != null) && (fr.getView() != null)) { fr.getView().setVisibility(View.GONE); }
//
//				mDrawerList.setItemChecked(position, true);
//				mDrawerList.setSelection(position);
//				setTitle(navMenuTitles[position]);
//				mDrawerLayout.closeDrawer(mDrawerList);
//				mViewPager.setVisibility(View.VISIBLE);
//				//if (Constants.IS_VERSION_TE) { mAdView.setVisibility(View.VISIBLE); }
//			}
//		}
//	}
//
//	@Override
//	public void setTitle(CharSequence title) {
//		mTitle = title;
//		getActionBar().setTitle(mTitle);
//	}
//
//	/**
//	 * When using the ActionBarDrawerToggle, you must call it during
//	 * onPostCreate() and onConfigurationChanged()...
//	 */
//
//	@Override
//	protected void onPostCreate(Bundle savedInstanceState) {
//		super.onPostCreate(savedInstanceState);
//		// Sync the toggle state after onRestoreInstanceState has occurred.
//		mDrawerToggle.syncState();
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		// Pass any configuration change to the drawer toggls
//		mDrawerToggle.onConfigurationChanged(newConfig);
//	}
//
//	public void openNewInstallationAlertDialog() {
//		new AlertDialog.Builder(this)
//		// .setTitle(R.string.alert_pause_title)
//				.setMessage(R.string.alert_new_installation_message).setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						try {
//							SdcardManager sdmanager = new SdcardManager(Pedometer.getInstance());
//							sdmanager.importDatabase();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//					}
//				}).show();
//
//	}
//
//	public static Pedometer getInstance() {
//		return mInstance;
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		super.onSaveInstanceState(savedInstanceState); // this is a know bug. So
//														// remove .super
//	}
//
//	@Override
//	public void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//	}
//
//	@Override
//	protected void onStart() {
//		super.onStart();
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		int[] intArray;
//		intArray = new int[2];
//		Bundle extras = getIntent().getExtras();
//
//		setPedometer(); //V131 this starts AccuService and Handler, and make sure all TextView loaded and avoid mHanlder null point FC
//		initializeDateFormat();
//		mViewPager.setAdapter(mCollectionPagerAdapter);
//		mViewPager.setCurrentItem(mNumberDays - 1);
//		mViewPager.getAdapter().notifyDataSetChanged();
//
//		if (extras == null) {
//			if (mPedometerSettings.isNewInstallation()) { displayView(SPINNER_ITEM_SETTINGSFIRST); }
//			else { displayView(SPINNER_ITEM_PEDOMETER); }
//		} else {
//			intArray = extras.getIntArray("navigation_intent");
//			if (intArray == null) { displayView(SPINNER_ITEM_PEDOMETER); }
//			else { displayView(intArray[0]); }
//		}
//
//
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//	}
//
//	protected void onDestroy() {
//		// if (adView != null) {
//		// adView.destroy();
//		// }
//		super.onDestroy();
//		// mDB.close();
//		// mChecker.onDestroy();
//	}
//
//	protected void onRestart() {
//		super.onRestart();
//		// super.onDestroy();
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
////		if (keyCode == KeyEvent.KEYCODE_BACK) {
////			if (interstitialAd.isLoaded()) {
////				interstitialAd.show();
////			} else {
////				finish(); // V117
////			}
////			return true;
////		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//
//	public static class CollectionPagerAdapter extends FragmentStatePagerAdapter {
//
//		public CollectionPagerAdapter(android.support.v4.app.FragmentManager fm) {
//			super(fm);
//		}
//
//		@Override
//		public android.support.v4.app.Fragment getItem(int i) {
//			ObjectFragment objectFragment = new ObjectFragment();
//			//android.support.v4.app.Fragment fragment = objectFragment;
//			Bundle args = new Bundle();
//			args.putInt(ObjectFragment.ARG_OBJECT, i); // i+1 Our object is just an integer :-P
//			objectFragment.setArguments(args);
//
//			if(Utils.isSameDate(mCurrent, mToday)) {
//				msFragment = objectFragment;
//			}
//
//			return objectFragment;
//		}
//
//		@Override
//		public int getCount() {
//			int count;
//			count = mNumberDays;
//			return count;
//		}
//
//		@Override
//		public CharSequence getPageTitle(int position) {
//			Calendar current = (Calendar) mToday.clone();
//			String string;
//			current.add(Calendar.DATE, -(mNumberDays - 1 - position));
//			string = DateFormat.format("E, MMM dd", current).toString();
//			return string;
//		}
//
//		@Override
//		public int getItemPosition(Object object) {
//			return POSITION_NONE;
//		}
//
////        @Override
////        public Object instantiateItem(View container, int position) {
////            return container;
////        }
//
//	}
//
//
//	public static class ObjectFragment extends android.support.v4.app.Fragment {
//		public static final String ARG_OBJECT = "object";
//		View mRootView;
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//			mRootView = inflater.inflate(R.layout.dark_fragment_pedometer, container, false);  //V451
//			Bundle args = getArguments();
//			int position = args.getInt(ARG_OBJECT);
//
//			mToday = Calendar.getInstance(); //V503 to remove some crashes as it is null
//			mCurrent = (Calendar) mToday.clone();
//			mCurrent.add(Calendar.DATE, -(mNumberDays - 1 - position));
//
//			if(Utils.isSameDate(mCurrent, mToday)) {
//				updateHourlyChart2();
//			} else {
//				updateHourlyChart();
//			}
//
//			return mRootView;
//		}
//
//		public void updateHourlyChart() {
//			LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.chart_hourly);
//			IChartForMain mChart = new HourBarChartForMain();
//
//			setDayHourData();
//			if (mChartView != null) { layout.removeView(mChartView); }
//			mChartView = mChart.graphicalView(this, mX, mValues, true); //boolean mScreenLarge = true; mScreenLarge is always true
//			layout.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//		}
//
//		private void updateHourlyChart2() {
//			LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.chart_hourly);
//			IChart2 mChart = new HourBarChart2();
//
//			boolean mScreenLarge = true;
//			setDayHourData2();
//			if (mChartView != null) { layout.removeView(mChartView); }
//			mChartView = mChart.graphicalView(Pedometer.getInstance(), mX, mValues,  mScreenLarge);
//			layout.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
//		}
//
//		public void setDayHourData() {
//			int i, steps, hour, minute, goalsteps;
//			float calories, distance, percent;
//			long steptime;
//			double[] dSteps = new double[25];
//			double[] hSteps = new double[25];
//			boolean[] bMinute = new boolean[25];
//			for (i = 0; i <= 24; i++) {
//				dSteps[i] = 0.0;
//				hSteps[i] = 0.0;
//				bMinute[i] = false;
//			}
//
//			Pedometer.mDB.open();
//			Cursor c = Pedometer.mDB.queryDayAll(mCurrent.get(Calendar.YEAR), mCurrent.get(Calendar.MONTH) + 1, mCurrent.get(Calendar.DATE));
//
//			if (c == null) return; //V511
//
//			if (c.moveToFirst()) {
//				do {
//					hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
//					minute = c.getInt(c.getColumnIndex(Constants.KEY_MINUTE));
//					steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
//					if ((hour == 23) & (minute > 30)) { //V501, V541
//						dSteps[24] = ((Integer) steps).doubleValue();
//					} else if ( (minute >=0) || (minute <= 30) ) { // discard 30min data, //V541
//						dSteps[hour] = ((Integer) steps).doubleValue();
//						bMinute[hour] = true;
//					} else { //JS V436
//						if (!bMinute[hour]) {
//							dSteps[hour] = ((Integer) steps).doubleValue();
//							bMinute[hour] = true;
//						}
//					}
//				} while (c.moveToNext());
//			}
//
////			if (c.moveToLast()) {
////				hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
////				steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
////				dSteps[hour] = steps; // this is the last data, i.e. current steps
////			}
//
//			goalsteps = 10000;
//			steps     = 0;
//			distance = 0.00f;
//			calories = 0.0f;
//			steptime = 0l;
//
//			if (c.moveToLast()) {
//				hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
//				steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
//				dSteps[hour] = steps; // this is the last data, i.e. current steps
//				distance    = c.getFloat(c.getColumnIndex(Constants.KEY_DISTANCE));
//				calories    = c.getFloat(c.getColumnIndex(Constants.KEY_CALORIES));
//				steptime    = c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME));
//				goalsteps   = c.getInt(c.getColumnIndex(Constants.KEY_ACHIEVEMENT));
//			}
//
//			if (goalsteps == 0) percent = 0.0f;
//			else percent = ((float) steps / (float) goalsteps) * 100.0f;
//
//			c.close();
//			Pedometer.mDB.close();
//
//			for (i = 0; i <= 24; i++) {
//				if (i == 0) {
//					hSteps[i] = 0.0;
//				} else {
//					if (dSteps[i] < dSteps[i - 1]) { // in case there are missing steps
//						//dSteps[i] = dSteps[i - 1]; // V541 when previous day's 0:15 step is big, it affects the whole day
//						hSteps[i - 1] = 0.0;
//					} else {
//						hSteps[i - 1] = (dSteps[i] - dSteps[i - 1]);
//					}
//				}
//			}
//
//			mX.clear();
//			mValues.clear();
//			mValues.add(hSteps);
//
//
//			LinearLayout mmLayoutStart = (LinearLayout) mRootView.findViewById(R.id.linearlayoutstart);
//			LinearLayout mmLayoutPause = (LinearLayout) mRootView.findViewById(R.id.linearlayoutpause);
//			mmLayoutStart.setLayoutParams(mLayoutParamsVisible);
//			mmLayoutPause.setLayoutParams(mLayoutParamsHide);
//
//
//			ImageButton mmStartButton = (ImageButton) mRootView.findViewById(R.id.startButton);
//			ImageButton mmPauseButton = (ImageButton) mRootView.findViewById(R.id.pauseButton);
//			ImageButton mmStopButton = (ImageButton) mRootView.findViewById(R.id.stopButton);
//			ImageButton mmLockButton = (ImageButton) mRootView.findViewById(R.id.lockButton);
//			ImageButton mmWalkButton = (ImageButton) mRootView.findViewById(R.id.walk_button);
//			ImageButton mmNextButton = (ImageButton) mRootView.findViewById(R.id.next_ic_button);
//
//			mmStartButton.setEnabled(false);
//			mmStartButton.setColorFilter(Color.GRAY);
//
//			mmPauseButton.setVisibility(View.GONE);
//			mmStopButton.setVisibility(View.GONE);
//			mmLockButton.setVisibility(View.GONE);
//			mmWalkButton.setVisibility(View.GONE);
//			mmNextButton.setVisibility(View.GONE);
//
//			View mmHourlyChart  = mRootView.findViewById(R.id.chart_hourly);
//			View mmHourlyLap    = mRootView.findViewById(R.id.lap_hourly);
//			mmHourlyChart.getLayoutParams().height  = ViewGroup.LayoutParams.MATCH_PARENT;
//			mmHourlyLap.getLayoutParams().height    = 0;
//
//
//			TextView mmStepValueView 		= (TextView) mRootView.findViewById(R.id.step_value);
//			TextView mmTripaStepValueView = (TextView) mRootView.findViewById(R.id.tripa_step_value);
//			TextView mmDistanceValueView 	= (TextView) mRootView.findViewById(R.id.distance_value);
//			TextView mmSpeedValueView 	= (TextView) mRootView.findViewById(R.id.speed_value);
//			TextView mmCaloriesValueView 	= (TextView) mRootView.findViewById(R.id.calories_value);
//			TextView mmSteptimeValueView 	= (TextView) mRootView.findViewById(R.id.time_value);
//			TextView mmDailyPercentValueView = (TextView) mRootView.findViewById(R.id.daily_percent_value);
//			TextView mmDailyGoalValueView = (TextView) mRootView.findViewById(R.id.daily_goal_value);
//
//			TextView mmLapTitleTextView 	= (TextView) mRootView.findViewById(R.id.lap_title);
//			TextView mmLapTextView 		= (TextView) mRootView.findViewById(R.id.lap_value);
//			ProgressBar mmProgressBar 		= (ProgressBar) mRootView.findViewById(R.id.trainprogressbar);
//
//			TextView distanceUnitView       = (TextView) mRootView.findViewById(R.id.distance_units);
//			TextView calorieUnitView        = (TextView) mRootView.findViewById(R.id.calories_units);
//			TextView speedUnitView          = (TextView) mRootView.findViewById(R.id.speed_units);
//
//
//			mmDailyGoalValueView.setText("" + goalsteps);
//			mmDailyPercentValueView.setText(((Integer) (int) percent).toString() + "%");
//			mmProgressBar.setProgress((int) percent);
//			mmStepValueView.setText("" + steps);
//			mmDistanceValueView.setText(String.format("%.2f", distance * mfDistanceFactor));
//			mmCaloriesValueView.setText(String.format("%d", (int) (calories *mfCalorieFactor) ));
//			mmSteptimeValueView.setText(Utils.getHoursMinutesString((int) (steptime / 1000)));
//			distanceUnitView.setText(msDistanceUnit);
//			calorieUnitView.setText(msCalorieUnit);
//			speedUnitView.setText(msSpeedUnit);
//
//			double speed;
//			String string;
//			if (steptime != 0) {
//				speed = distance * mfDistanceFactor * 1000.0 * 3600.0 / steptime;
//				if (speed <= 0.0) string = "" + "----";
//				else string = ("" + (speed + 0.000001f)).substring(0, 4);
//			} else {
//				string = "" + "0";
//			}
//			mmSpeedValueView.setText(string);
//
//
//
//		}
//
//
//
//		public void setDayHourData2() {
//			int 	i, j, year, month, day, count = 0, startIndex = 0;
//			int[] 	sumSteps = new int[NUM_HOURLY_BARS + 1];
//			double[] delSteps = new double[NUM_HOURLY_BARS + 1];
//			double[] x = new double[7];
//
//			for (i = 0; i < 7; i++) { x[i] = 0.0; }
//
//			for (i = 0; i <= NUM_HOURLY_BARS; i++) {
//				sumSteps[i] = 0;
//				delSteps[i] = 0.0;
//			}
//
//			Calendar today = Calendar.getInstance();
//			year 	= today.get(Calendar.YEAR);
//			month 	= today.get(Calendar.MONTH) + 1;
//			day 	= today.get(Calendar.DATE);
//
//			Pedometer.mDB.open();
//			Cursor c = Pedometer.mDB.queryLapStepsForDay(year, month, day, mLap);
//
//			if (c == null) { count = 0; }
//			else { count = c.getCount(); } // it can be zero
//
//			if (count == 0) {
//				x[0] = (double) startIndex;
//				x[1] = x[0] + 10.0;
//				x[2] = x[0] + 20.0;
//				x[3] = x[0] + 30.0;
//				x[4] = x[0] + 40.0;
//				x[5] = x[0] + 50.0;
//				x[6] = x[0] + 60.0;
//			} else {
//
//				if (count < NUM_HOURLY_BARS) { startIndex = 0; }
//				else {
//					startIndex = count - NUM_HOURLY_BARS;
//					count = NUM_HOURLY_BARS;
//				}
//
//				if (c.moveToFirst()) {
//					i = 0;
//					do {
//						j = i - startIndex;
//						if (j >= 0) { sumSteps[j] = c.getInt(c .getColumnIndex(Constants.KEY_LAPSTEPS)); }
//						i++;
//					} while (c.moveToNext());
//				}
//				c.close();
//				Pedometer.mDB.close();
//
//				if (count > 1) {
//					for (i = 1; i <= count; i++) {
//						j = i - 1;
//						delSteps[j] = (double) (sumSteps[i] - sumSteps[i - 1]);
//						if (delSteps[j] < 0.0) { delSteps[j] = 0.0; }
//					}
//				}
//
//				x[0] = (double) startIndex;
//				x[1] = x[0] + 10.0;
//				x[2] = x[0] + 20.0;
//				x[3] = x[0] + 30.0;
//				x[4] = x[0] + 40.0;
//				x[5] = x[0] + 50.0;
//				x[6] = x[0] + 60.0;
//			}
//
//			mX.clear();
//			mX.add(x);
//			mValues.clear();
//			mValues.add(delSteps);
//
//
//			mLayoutStart = (LinearLayout) mRootView.findViewById(R.id.linearlayoutstart);
//			mLayoutPause = (LinearLayout) mRootView.findViewById(R.id.linearlayoutpause);
//			mLayoutStart.setLayoutParams(mLayoutParamsVisible);
//			mLayoutPause.setLayoutParams(mLayoutParamsHide);
//
//			mStartButton = (ImageButton) mRootView.findViewById(R.id.startButton);
//			mPauseButton = (ImageButton) mRootView.findViewById(R.id.pauseButton);
//			mStopButton = (ImageButton) mRootView.findViewById(R.id.stopButton);
//			mLockButton = (ImageButton) mRootView.findViewById(R.id.lockButton);
//			mWalkButton = (ImageButton) mRootView.findViewById(R.id.walk_button);
//			mNextButton = (ImageButton) mRootView.findViewById(R.id.next_ic_button);
//
//			mStartButton.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					mOperationMode = START_MODE;
//					mService.setOperationMode(mOperationMode);
//
//					Pedometer.mPedometerSettings.setLockStatus(true);
//					((Pedometer)getActivity()).updateModeDisplay();
//					((Pedometer)getActivity()).displayLockState();
//
//				}
//			});
//
//			mStopButton.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					((Pedometer)getActivity()).openStopAlertDialog();
//				}
//			});
//
//
//			mPauseButton.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					if (Pedometer.mPedometerSettings.isPause()) {
//						((Pedometer)getActivity()).showMyDialog(DIALOG_FRAGMENT_RESUME);
//					} else {
//						((Pedometer)getActivity()).showMyDialog(DIALOG_FRAGMENT_PAUSE);
//					}
//				}
//			});
//
//			mLockButton.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					if (Pedometer.mPedometerSettings.isLocked()) {
//						Pedometer.mPedometerSettings.setLockStatus(false);
//					} else {
//						Pedometer.mPedometerSettings.setLockStatus(true);
//					}
//					((Pedometer)getActivity()).displayLockState();
//				}
//			});
//
//
//			mWalkButton.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					((Pedometer) getActivity()).openWalkRunAlertDialog();
//				}
//			});
//
//			((Pedometer)getActivity()).updateExerciseModeDisplay(Pedometer.mPedometerSettings.getExerciseType());
//
//			mHourlyChart 	= mRootView.findViewById(R.id.chart_hourly);
//			mHourlyLap 		= mRootView.findViewById(R.id.lap_hourly);
//
//			mToggleHourlyChart = true;
//			mHourlyChart.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//			mHourlyLap.getLayoutParams().height = 0;
//
//
//			mNextButton.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//
//					if (mToggleHourlyChart) {
//						mHourlyChart.getLayoutParams().height = 0;
//						mHourlyLap.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//						mToggleHourlyChart = false;
//					} else {
//						mHourlyLap.getLayoutParams().height = 0;
//						mHourlyChart.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//						mToggleHourlyChart = true;
//						updateHourlyChart2();
//					}
//					mTripaStepValueView.setText(((Integer) mStepValue).toString()); // this will refresh the screen
//				}
//			});
//
//			mStepValueView 		= (TextView) mRootView.findViewById(R.id.step_value);
//			mTripaStepValueView = (TextView) mRootView.findViewById(R.id.tripa_step_value);
//			mDistanceValueView 	= (TextView) mRootView.findViewById(R.id.distance_value);
//			mSpeedValueView 	= (TextView) mRootView.findViewById(R.id.speed_value);
//			mCaloriesValueView 	= (TextView) mRootView.findViewById(R.id.calories_value);
//			mSteptimeValueView 	= (TextView) mRootView.findViewById(R.id.time_value);
//			mDailyPercentValueView = (TextView) mRootView.findViewById(R.id.daily_percent_value);
//			mDailyGoalValueView = (TextView) mRootView.findViewById(R.id.daily_goal_value);
//			mProgressBar 		= (ProgressBar) mRootView.findViewById(R.id.trainprogressbar);
//			mLapTitleTextView 	= (TextView) mRootView.findViewById(R.id.lap_title);
//			mLapTextView 		= (TextView) mRootView.findViewById(R.id.lap_value);
//
//			TextView distanceUnitView       = (TextView) mRootView.findViewById(R.id.distance_units);
//			TextView calorieUnitView        = (TextView) mRootView.findViewById(R.id.calories_units);
//			TextView speedUnitView          = (TextView) mRootView.findViewById(R.id.speed_units);
//
//			((TextView) mRootView.findViewById(R.id.distance_units)).setText(getString(Pedometer.mPedometerSettings.isMetric() ? R.string.km : R.string.miles));
//			((TextView) mRootView.findViewById(R.id.speed_units)).setText(getString(Pedometer.mPedometerSettings.isMetric() ? R.string.kilometers_per_hour : R.string.miles_per_hour));
//
//			if (Pedometer.mPedometerSettings.isMetric()) { mfDistanceFactor = Utils.MILE2KM; } //mTempUnit = "C";
//			else { mfDistanceFactor = 1.0f; } //mTempUnit = "F";
//
//			mTripaStepValueView.setText("" + mTripaStepValue);
//			mStepValueView.setText("" + mStepValue);
//
//			if (mDistanceValue <= 0) { mDistanceValueView.setText(String.format("%.2f", 0.00f)); }
//			else { mDistanceValueView.setText(String.format("%.2f", mDistanceValue * mfDistanceFactor)); }
//
//			if (mCaloriesValue < 1000f) {
//				if (mCaloriesValue <= 0) { mCaloriesValueView.setText(String.format("%.1f", 0.00f)); }
//				else { mCaloriesValueView.setText(String.format("%.1f",mCaloriesValue *mfCalorieFactor)); }
//			} else {
//				mCaloriesValueView.setText(String.format("%d",(int) mCaloriesValue));
//			}
//
//			mSteptimeValueView.setText(Utils.getHoursMinutesSecondsString(mSteptimeValue));
//			mDailyGoalValueView.setText(((Integer) mGoal).toString());
//			mDailyPercentValueView.setText(((Integer) (int) mPercent).toString() + "%");
//			mProgressBar.setProgress((int) mPercent);
//			distanceUnitView.setText(msDistanceUnit);
//			calorieUnitView.setText(msCalorieUnit);
//			speedUnitView.setText(msSpeedUnit);
//
//			if (mSpeedValue <= 0) mSpeedValueView.setText("0");
//			else mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f)).substring(0, 4));
//
//			mLapTextView.setText(((Integer) mLap).toString());
//
////				if (Pedometer.mPedometerSettings.isPause()) ((Pedometer)getActivity()).displayPauseMode();
////				else ((Pedometer)getActivity()).displayResumeMode();
//
//			((Pedometer)getActivity()).updateModeDisplay();
//			((Pedometer)getActivity()).displayLockState();
//		}
//	}
//
//
//
//
//	public static class MyAlertDialogFragment extends DialogFragment {
//
//		public static MyAlertDialogFragment newInstance(int title, int type) {
//			MyAlertDialogFragment frag = new MyAlertDialogFragment();
//			Bundle args = new Bundle();
//			args.putInt("title", title);
//			args.putInt("type", type);
//			frag.setArguments(args);
//			return frag;
//		}
//
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			int title = getArguments().getInt("title");
//
//			return new AlertDialog.Builder(getActivity())   //.setIcon(R.drawable.alert_dialog_icon)
//					.setTitle(title)
//					.setPositiveButton(R.string.ok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int whichButton) {
//									int type = getArguments().getInt("type");
//									((Pedometer)getActivity()).doPositiveClick(type);
//								}
//							}
//					)
//					.setNegativeButton(R.string.cancelled,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int whichButton) {
//									((Pedometer)getActivity()).doNegativeClick();
//								}
//							}
//					)
//					.create();
//		}
//	}
//
//	private void showMyDialog(int type) {
//		DialogFragment newFragment;
//		switch (type) {
//			case DIALOG_FRAGMENT_PAUSE:
//				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_pause_message, type);
//				break;
//			case DIALOG_FRAGMENT_RESUME:
//				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_resume_message, type);
//				break;
//			case DIALOG_FRAGMENT_NEXT_LAP:
//				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_next_lap_message, type);
//				break;
//			case DIALOG_FRAGMENT_DAILY_RESET:
//				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_daily_reset_message, type);
//				break;
//			default:
//				newFragment = MyAlertDialogFragment.newInstance(R.string.alert_pause_message, type);
//				break;
//		}
//		newFragment.show(getFragmentManager(), "dialog");
//	}
//
//	private void doPositiveClick(int type) {
//		switch (type) {
//			case DIALOG_FRAGMENT_PAUSE:
//				Pedometer.getInstance().sendBroadcast(new Intent(AccuService.ACCUPEDO_PAUSE_REQUEST));
//				displayPauseMode();
//				break;
//			case DIALOG_FRAGMENT_RESUME:
//				Pedometer.getInstance().sendBroadcast(new Intent(AccuService.ACCUPEDO_RELEASE_REQUEST));
//				displayResumeMode();
//				break;
//			case DIALOG_FRAGMENT_NEXT_LAP:
////				mService.saveToDB();
////				mService.startNewLap();
//				break;
//			case DIALOG_FRAGMENT_DAILY_RESET:
//				mService.setManualSteps(0);
//				break;
//		}
//	}
//
//	private void doNegativeClick() { }
//
//	private void openStopAlertDialog() {
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(
//				Pedometer.getInstance());
//
//		final CharSequence[] items = { getString(R.string.stop_dialog_nosave),
//				getString(R.string.stop_dialog_save)
//				// getString(R.string.stop_dialog_save_fb),
//		};
//		// final CharSequence[] items = { "yes",
//		// "yest",
//		// "no"};
//
//		mSelectedItem = Pedometer.mPedometerSettings.getSaveOption();
//		builder.setSingleChoiceItems(items, mSelectedItem,
//				new DialogInterface.OnClickListener() {
//					// When you click the radio button
//					@Override
//					public void onClick(DialogInterface dialog, int item) {
//
//						mSelectedItem = item;
//					}
//				});
//
//		builder.setPositiveButton(getString(R.string.ok),
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int item) {
//
//						switch (mSelectedItem) {
//							case STOP_MODE_NOSAVE:
//								mOperationMode = STOP_MODE_NOSAVE;
//								mService.setOperationMode(mOperationMode);
//								updateModeDisplay();
//								break;
//							case STOP_MODE_SAVE:
//								mOperationMode = STOP_MODE_SAVE;
//								mService.setOperationMode(mOperationMode);
//								updateModeDisplay();
//								break;
//							default:
//								// Log.i(TAG, "Facebook mode3");
//								break;
//
//						}
//						Pedometer.mPedometerSettings.setSaveOption(mSelectedItem);
//					}
//				});
//		builder.setNegativeButton(getString(R.string.cancelled),
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int id) {
//					}
//				});
//		AlertDialog alert = builder.create();
//		alert.show();
//
//	}
//
//
//	private void updateExerciseModeDisplay(int mode) {
//		switch (mode) {
//			case AccuService.FLAG_MODE_WALK:
//				mWalkButton.setImageDrawable(getResources().getDrawable(
//						R.drawable.walk_ic));
//				break;
//			case AccuService.FLAG_MODE_RUN:
//				mWalkButton.setImageDrawable(getResources().getDrawable(
//						R.drawable.run_ic));
//				break;
//			case AccuService.FLAG_MODE_HIKE:
//				mWalkButton.setImageDrawable(getResources().getDrawable(
//						R.drawable.hiking_ic));
//				break;
//			case AccuService.FLAG_MODE_STAIRWAY:
//				mWalkButton.setImageDrawable(getResources().getDrawable(
//						R.drawable.stair_ic));
//				break;
//		}
//	}
//
//	public void updateModeDisplay() {
//		if (Pedometer.mPedometerSettings.isPause()) {
//			displayPauseMode();
//		} else {
//			switch (mOperationMode) {
//				case STOP_MODE_NOSAVE:
//				case STOP_MODE_SAVE:
//					displayStopMode();
//					break;
//				case START_MODE:
//				case RESUME_MODE:
//					displayResumeMode();
//					break;
//				case PAUSE_MODE:
//					displayPauseMode();
//					break;
//				default:
//			}
//
//		}
//	}
//
//	private void displayPauseMode() {
//		mStepValueView.setTextColor(Pedometer.getInstance().getResources()
//				.getColor(R.color.mygray));
//
//		// mStepValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myblue));
//		// mDistanceValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myblue));
//		// mSpeedValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myblue));
//		// mCaloriesValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myblue));
//		// mSteptimeValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myblue));
//
//		// mLayoutPause.setBackgroundColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.mygray));
//		// mPauseButton.setImageDrawable(Pedometer.getInstance()
//		// .getResources().getDrawable(R.drawable.start_imagebutton));
//
//		mTripaStepValueView.setTextColor(Pedometer.getInstance().getResources()
//				.getColor(R.color.mygray));
//
//		// mStartButton.setVisibility(View.GONE);
//		// mStopButton.setVisibility(View.VISIBLE);
//		// mPauseButton.setVisibility(View.VISIBLE);
//		// mPauseButton.setText(R.string.resume);
//		// mLockButton.setVisibility(View.VISIBLE);
//		mPauseButton.setImageDrawable(Pedometer.getInstance().getResources()
//				.getDrawable(R.drawable.resumebutton));
//		mLayoutStart.setLayoutParams(mLayoutParamsHide);
//		mLayoutPause.setLayoutParams(mLayoutParamsVisible);
//	}
//
//	private void displayResumeMode() {
//		mStepValueView.setTextColor(Pedometer.getInstance().getResources()
//				.getColor(AccuService.mScreenStepTextColor));
//
//		// mStepValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myorange));
//		// mDistanceValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myorange));
//		// mSpeedValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myorange));
//		// mCaloriesValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myorange));
//		// mSteptimeValueView.setTextColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myorange));
//
//		// mLayoutPause.setBackgroundColor(Pedometer.getInstance().getResources()
//		// .getColor(R.color.myorange));
//		// mPauseButton.setImageDrawable(Pedometer.getInstance().getResources()
//		// .getDrawable(R.drawable.stop_imagebutton));
//
//		mTripaStepValueView.setTextColor(Pedometer.getInstance().getResources()
//				.getColor(R.color.mywhite));
//		// mStartButton.setVisibility(View.GONE);
//		// mStopButton.setVisibility(View.VISIBLE);
//		// mPauseButton.setVisibility(View.VISIBLE);
//		// mPauseButton.setText(R.string.pause);
//		// mLockButton.setVisibility(View.VISIBLE);
//		mPauseButton.setImageDrawable(Pedometer.getInstance().getResources()
//				.getDrawable(R.drawable.pausebutton));
//		mLayoutStart.setLayoutParams(mLayoutParamsHide);
//		mLayoutPause.setLayoutParams(mLayoutParamsVisible);
//
//
//	}
//
//	private void openWalkRunAlertDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(Pedometer.getInstance());
//		builder.setTitle(R.string.exercise_type_setting_title);
//		builder.setItems(R.array.exercise_type_preference, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//
//				mService.setExerciseMode(which);
//				Pedometer.mPedometerSettings.setExerciseType(which);
//				updateExerciseModeDisplay(which);
////				switch (which) {
////					case 0:
////						Pedometer.mPedometerSettings.setExerciseType("running");
////						mService.reloadSettings();
////						mWalkButton.setImageDrawable(ContextCompat.getDrawable(Pedometer.getInstance(), R.drawable.run_ic));
////						break;
////					case 1:
////						Pedometer.mPedometerSettings.setExerciseType("walking");
////						mService.reloadSettings();
////						mWalkButton.setImageDrawable(ContextCompat.getDrawable(Pedometer.getInstance(), R.drawable.walk_ic));
////						break;
////				}
//			}
//		});
//		AlertDialog dialog = builder.create();
//		dialog.show();
//	}
//
//	private void displayStopMode() {
//		// mStartButton.setVisibility(View.VISIBLE);
//		// mStopButton.setVisibility(View.GONE);
//		// mPauseButton.setVisibility(View.GONE);
//		// mLockButton.setVisibility(View.GONE);
//		mLayoutStart.setLayoutParams(mLayoutParamsVisible);
//		mLayoutPause.setLayoutParams(mLayoutParamsHide);
//	}
//
//	private void displayLockState() {
//		if (Pedometer.mPedometerSettings.isLocked()) {
//			mLockButton.setImageDrawable(getResources().getDrawable(R.drawable.unlocked_ic));
//			AlphaAnimation alpha = new AlphaAnimation(0.25F, 0.25F);
//			alpha.setDuration(0); // Make animation instant
//			alpha.setFillAfter(true); // Tell it to persist after
//			mStopButton.startAnimation(alpha);
//			mPauseButton.startAnimation(alpha);
//
//			mStopButton.setEnabled(false);
//			mPauseButton.setEnabled(false);
//
//		} else {
//			mLockButton.setImageDrawable(getResources().getDrawable(R.drawable.locked_ic));
//			AlphaAnimation alpha = new AlphaAnimation(1.0F, 1.0F);
//			alpha.setDuration(0); // Make animation instant
//			alpha.setFillAfter(true); // Tell it to persist after
//			mStopButton.startAnimation(alpha);
//			mPauseButton.startAnimation(alpha);
//			mStopButton.setEnabled(true);
//			mPauseButton.setEnabled(true);
//		}
//	}
//
//	private void initializeDateFormat() {
//		mToday = Calendar.getInstance();
//		mCurrent = (Calendar) mToday.clone();
//		mFirstDay = (Calendar) mToday.clone();
//
//		Pedometer.mDB.open();
//		Cursor c = Pedometer.mDB.queryFirstDay();
//
//		if (c != null) { //V451
//			if (c.getCount() != 0 ) {
//				mFirstDay.set(Calendar.YEAR, c.getInt(c.getColumnIndex(Constants.KEY_YEAR)));
//				mFirstDay.set(Calendar.MONTH, c.getInt(c.getColumnIndex(Constants.KEY_MONTH)) - 1);
//				mFirstDay.set(Calendar.DATE, c.getInt(c.getColumnIndex(Constants.KEY_DAY)));
//				mFirstDay.set(Calendar.HOUR_OF_DAY, 0);
//				mFirstDay.set(Calendar.MINUTE, 0);
//				c.close();
//			}
//		}
//		Pedometer.mDB.close();
//
//		switch (mWeekFormat) {
//			case 0: // american
//				mToday.setFirstDayOfWeek(Calendar.SUNDAY);
//				mCurrent.setFirstDayOfWeek(Calendar.SUNDAY);
//				mFirstDay.setFirstDayOfWeek(Calendar.SUNDAY);
//				break;
//			case 1: // european
//				mToday.setFirstDayOfWeek(Calendar.MONDAY);
//				mCurrent.setFirstDayOfWeek(Calendar.MONDAY);
//				mFirstDay.setFirstDayOfWeek(Calendar.MONDAY);
//				break;
//		}
//		mNumberDays = getNumberDays();
//	}
//
//	private int getNumberDays() {
//		int numberDays;
//		long diff = mToday.getTimeInMillis() - mFirstDay.getTimeInMillis();
//		numberDays = (int) (diff / 86400000); // V551 (24 * 60 * 60 * 1000));
//		return numberDays + 1;
//	}
//
//
//	private void setPedometer() {
//		mIsRunning = Pedometer.mPedometerSettings.isServiceRunning();
//		if (!mIsRunning && Pedometer.mPedometerSettings.isNewStart()) {
//			startStepService();
//			bindStepService();
//		} else if (mIsRunning) {
//			bindStepService();
//		}
//		Pedometer.mPedometerSettings.clearServiceRunning();
//	}
//
//	public static AccuService mService; //V555
//
//	private final ServiceConnection mConnection = new ServiceConnection() {
//		public void onServiceConnected(ComponentName className, IBinder service) {
//			mService = ((AccuService.StepBinder) service).getService();
//			mService.registerCallback(mCallback);
//			mService.reloadSettings();
//		}
//		public void onServiceDisconnected(ComponentName className) {
//			mService = null;
//		}
//	};
//
//	private void startStepService() {
//		if (!mIsRunning) {
//			mIsRunning = true;
//			Pedometer.getInstance().startService(new Intent(Pedometer.getInstance(), AccuService.class));
//		}
//	}
//
//	private void bindStepService() {
//		mIsRunning = true;
//		mIsBound = true; //V437
//		Pedometer.getInstance().bindService(new Intent(Pedometer.getInstance(), AccuService.class),
//				mConnection,
//				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
//	}
//
//	private void unbindStepService() { //V511 private
//		if (mIsRunning) {
//			mIsRunning = false;
//			if (mConnection != null) {
//				if (mIsBound) { //V437
//					if (isMyServiceRunning(AccuService.class)) { //V511
//						unbindService(mConnection);
//					}
//					mIsBound = false;
//				}
//			}
//		}
//	}
//
//	private void stopStepService() { //V511 private
//		mIsRunning = false;
//		stopService(new Intent(Pedometer.getInstance(), AccuService.class));
//	}
//
//	private AccuService.ICallback mCallback = new AccuService.ICallback() {
//		public void stepsChanged(int lapsteps, int steps) {
//			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, lapsteps,
//					steps));
//		}
//
//		public void distanceChanged(float lapdistance, float distance) {
//			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG,
//					(int) (lapdistance * 1000), (int) (distance * 1000)));
//		}
//
//		public void paceChanged(int value) {
//			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
//		}
//
//		public void speedChanged(float value) {
//			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG,
//					(int) (value * 1000), 0));
//		}
//
//		public void caloriesChanged(float lapcalories, float calories) {
//			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,
//					(int) (lapcalories * 1000), (int) (calories * 1000)));
//		}
//
//		public void steptimeChanged(long lapsteptime, long steptime) {
//			mHandler.sendMessage(mHandler.obtainMessage(STEPTIME_MSG,
//					(int) (lapsteptime / 1000), (int) (steptime / 1000)));
//		}
//
//		public void updateDisplay(int value) {
//			//mHandler.sendMessage(mHandler.obtainMessage(UPDATE_MSG, value, 0));
//			switch (value) {
//				case 1:
//					displayPauseMode();
//					break;
//				case 2:
//					displayResumeMode();
//					break;
//			}
//		}
//
//		public void goalChanged(int value) {
//			mHandler.sendMessage(mHandler.obtainMessage(GOAL_MSG, value, 0));
//		}
//
//		public void percentChanged(float value) {
//			mHandler.sendMessage(mHandler.obtainMessage(PERCENT_MSG,
//					(int) (value * 1000), 0));
//		}
//
//		public void lapnumberChanged(int value) {
//			mHandler.sendMessage(mHandler
//					.obtainMessage(LAPNUMBER_MSG, value, 0));
//		}
//
//		public void weatherChanged(float value) {
//			mHandler.sendMessage(mHandler.obtainMessage(WEATHER_MSG,
//					(int) (value * 1000), 0));
//		}
//
//		public void hourlyChartChanged(int value) {
//			//mHandler.sendMessage(mHandler.obtainMessage(HOURLYCHART_MSG, 0, 0));
//			msFragment.updateHourlyChart2();
//		}
//
//	};
//
//	private static final int STEPS_MSG 		= 1;
//	private static final int DISTANCE_MSG 	= 2;
//	private static final int CALORIES_MSG 	= 3;
//	private static final int STEPTIME_MSG 	= 4;
//	private static final int UPDATE_MSG 	= 5;
//	private static final int GOAL_MSG 		= 6;
//	private static final int PERCENT_MSG 	= 7;
//	private static final int SPEED_MSG 		= 8;
//	private static final int PACE_MSG 		= 9;
//	private static final int LAPNUMBER_MSG 	= 10;
//	private static final int WEATHER_MSG 	= 11;
//	private static final int HOURLYCHART_MSG = 12;
//
//	final static MessageHandler mHandler = new MessageHandler(); //V551 changed to static
//	static class MessageHandler extends Handler {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case STEPS_MSG:
//					int lapStepValue 	= msg.arg1;
//					mStepValue 			= msg.arg2;
//					mStepValueView.setText("" + lapStepValue);
//					mTripaStepValueView.setText("" + mStepValue);
//					break;
//
//				case DISTANCE_MSG:
//					float lapDistanceValue = msg.arg1 / 1000f;
//					if (lapDistanceValue <= 0) {
//						mDistanceValueView.setText(String.format("%.2f", 0.00f));
//					} else {
//						mDistanceValueView.setText(String.format("%.2f", lapDistanceValue * mfDistanceFactor));
//					}
//					break;
//
//				case CALORIES_MSG:
//					float lapCaloriesValue = msg.arg1 / 1000f;
//					mCaloriesValue = msg.arg2 / 1000f;
//					if (mCaloriesValue < 1000f) {
//						if (lapCaloriesValue <= 0) {
//							mCaloriesValueView.setText(String.format("%.1f", 0.00f));
//						} else {
//							mCaloriesValueView.setText(String.format("%.1f", lapCaloriesValue));
//						}
//					} else {
//						mCaloriesValueView.setText(String.format("%d", (int) lapCaloriesValue));
//					}
//					break;
//
//				case STEPTIME_MSG:
//					int lapSteptimeValue = msg.arg1; // msg.arg1 is in second now
//					mSteptimeValueView.setText(Utils.getHoursMinutesSecondsString(lapSteptimeValue));
//					break;
//
//				case UPDATE_MSG:
////					switch ((int) msg.arg1) {
////						case 1:
////							((Pedomete)getActivity()).displayPauseMode();
////							break;
////						case 2:
////							displayResumeMode();
////							break;
////					}
//					break;
//
//				case GOAL_MSG:
//					mGoal = msg.arg1;
//					mDailyGoalValueView.setText(((Integer) mGoal).toString());
//					break;
//
//				case PERCENT_MSG:
//					mPercent = msg.arg1 / 1000f;
//					mDailyPercentValueView.setText(((Integer) (int) mPercent).toString() + "%");
//					mProgressBar.setProgress((int) mPercent);
//					break;
//
//				case PACE_MSG:
//					// mPaceValue = msg.arg1;
//					// if (mPaceValue <= 0) {
//					// mSpeedValueView.setText("0");
//					// } else {
//					// mSpeedValueView.setText(""
//					// + ((Integer) mPaceValue).toString());
//					// }
//					break;
//
//				case SPEED_MSG:
//					mSpeedValue = ((int) msg.arg1) * mfDistanceFactor / 1000f;
//					if (mSpeedValue <= 0) {
//						mSpeedValueView.setText("0");
//					} else {
//						mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f)).substring(0, 4));
//					}
//					break;
//				case LAPNUMBER_MSG:
//					mLap = msg.arg1;
//					mLapTextView.setText(((Integer) mLap).toString());
//					if (mLap > 0) {
//						mLapTextView.setVisibility(View.VISIBLE);
//						mLapTitleTextView.setVisibility(View.VISIBLE);
//					} else {
//						mLapTextView.setVisibility(View.GONE);
//						mLapTitleTextView.setVisibility(View.GONE);
//					}
//					break;
//
//				case HOURLYCHART_MSG:
//					//updateHourlyChart2();
//					break;
//
//				// case WEATHER_MSG:
//				// mCity = mService.mCity;
//				// if (mService.mIsMetric) {
//				// mTemperature = (int) (msg.arg1 / 1000f);
//				// } else {
//				// mTemperature = (int) (msg.arg1 / 1000f * 1.8 + 32.0);
//				// }
//				// updateWeather();
//				// break;
//
//				default:
//					super.handleMessage(msg);
//			}
//		}
//
//	};
//
//
//	private boolean isMyServiceRunning(Class<?> serviceClass) {   //V511
//		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//			if (serviceClass.getName().equals(service.service.getClassName())) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//
//	private boolean checkDBfileExist() {
//
//		boolean fileExists;
//		File sd = new File(Environment.getExternalStorageDirectory() + Constants.ACCUPEDO_FOLDERNAME);
//		File data = Environment.getDataDirectory();
//
//		if (sd.exists()) {
//			String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH + Constants.DATABASE_NAME;
//			String backupDBPath = Constants.DATABASE_FILENAME; // Constants.DATABASE_NAME;
//			File currentDB = new File(data, currentDBPath);
//			File backupDB = new File(sd, backupDBPath);
//			if (backupDB.exists()) {
//				fileExists = true;
//			} else {
//				fileExists = false;
//			}
//		} else {
//			fileExists = false;
//		}
//		return fileExists;
//	}
//
//}