/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *  Set "singleInstance" property for Activity in Manifest xml. Allows "back" and "home" buttons
 *  http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
 *  Ver 3.3.1
 *  1. Fixed adView.close in onDestroy of FragmentPedometer, ChartActivity, StatisticActivity
 *  2. Added AlarmManager.cancel() for mpIntentSave, mpIntentDaily, mpIntentPause, mpIntentRelease, mpIntentWake in onDestroy()
 *  3. When it wakes up, it uses registerDetectorNormal for motion detection. If motion, it unregister and re-registerDetectorGame
 *  4. DropBox service: Export to / Import from DropBox (this works without signed apk)
 *  5. Google Drive service (this works only with signed apk)
 *  6. Fixes all Warnings
 *  7. Fixes by ViewPager error by adding mViewPager.getAdapter().notifyDataSetChanged(); in ChartActivity, StatisticsActivity, and History. See following ADT22 issue.
 *  http://stackoverflow.com/questions/16756131/fragmentstatepageradapter-stopped-working-after-updating-to-adt-22
 *  8. millenia and inmobi mediations are added
 *  9. Initial settings screen is added
 *  10. Goal achievement notification
 *  11. Blocked millenia and inmobi mediation becacuse of low impression tested with Accutrainer
 *  12. WakeLock is acquired using member function, so it does not create duplicated objects. Then it uses isHeld() method before acquiring.
 *  13. Release WakeLock if there is no motion on Screen on mode (StepDetector)
 *  14. Add sensor registration in 30min data log cycle because Samsung S4 does not detect step sometimes while even serice is alive
 *  15. Steptime for consecutive steps is increased. Now DT*4 from DT*2
 *  16. Changed wake up cycle is 5 sec from always awake for the most accurate mode
 *  17. Added calling timer function at screen off event for the less, and lease accurate modes. It is too long to wait turn off the sensor without it.
 *  18. Steptime is fixed by adding SteptimeListener
 *  19. AdView is displayed in the bottom of chart and history view
 *  20. Coarse location, city and temperature are added. Location info is used for AdMob
 *  21. Average speed is added.
 *  22. Updating hourly bar chart is moved in onResume() in the FragmentPedometer.java
 *  23. Add a new least power mode that estimates step counts and steptime with 60sec alarm interval
 */
package com.corusen.steponfre.base;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.corusen.steponfre.R;
import com.corusen.steponfre.database.SdcardManager;
import com.corusen.steponfre.database.Constants;
import com.corusen.steponfre.database.MyDB;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
//import android.util.Log;
import android.widget.RemoteViews;

public class AccuService extends Service {

	public static final int NUM_DAYS_KEEP_DATA = 2;
	public static final String ACCUPEDO_WIDGET_DETAIL 					= "com.corusen.steponfre.ACCUPEDO_WIDGET_DETAIL";
	public static final String ACCUPEDO_MODE_CHANGE 					= "com.corusen.steponfre.ACCUPEDO_MODE_CHANGE";
	public static final String ACCUPEDO_TRIPA_RESET						= "com.corusen.steponfre.ACCUPEDO_TRIPA_RESET";
	public static final String ACCUPEDO_PAUSE_REQUEST 					= "com.corusen.steponfre.ACCUPEDO_PAUSE_REQUEST";
	public static final String ACCUPEDO_RELEASE_REQUEST 				= "com.corusen.steponfre.ACCUPEDO_RELEASE_REQUEST";
	public static final String ACCUPEDO_DAILYLOG_ALARM 					= "com.corusen.steponfre.ACCUPEDO_DAILYLOG_ALARM";
	public static final String ACCUPEDO_SAVESTATES_ALARM 				= "com.corusen.steponfre.ACCUPEDO_SAVESTATES_ALARM";
	public static final String ACCUPEDO_SAVESTATES_ONEMINUTE 			= "com.corusen.steponfre.ACCUPEDO_SAVESTATES_ONEMINUTE";
	public static final String ACCUPEDO_UPDATE_REQUEST 					= "com.corusen.steponfre.ACCUPEDO_UPDATE_REQUEST";
	public static final String ACCUPEDO_SETTINGS_RELOAD 				= "com.corusen.steponfre.ACCUPEDO_SETTINGS_RELOAD";
	public static final String ACCUPEDO_WAKE_ALARM 						= "com.corusen.steponfre.ACCUPEDO_WAKE_ALARM";
	public static final String ACCUPEDO_WIDGET_PAUSE 					= "com.corusen.steponfre.ACCUPEDO_WIDGET_PAUSE";
	public static final String ACCUPEDO_SAVE_DB_REQUEST 				= "com.corusen.steponfre.ACCUPEDO_SAVE_DB_REQUEST";
	public static final String ACCUPEDO_EDITSTEPS_REQUEST 				= "com.corusen.steponfre.ACCUPEDO_EDITSTEPS_REQUEST";
	public static final String ACCUPEDO_EDITSTEPS_TODAY_REQUEST 		= "com.corusen.steponfre.ACCUPEDO_EDITSTEPS_TODAY_REQUEST";
	public static final String ACCUPEDO_EDITSTEPS_HISTORY_REQUEST 		= "com.corusen.steponfre.ACCUPEDO_EDITSTEPS_HISTORY_REQUEST";
	public static final String ACCUPEDO_EDITSTEPS_HISTORY_INSERT_REQUEST = "com.corusen.accupedo.te.ACCUPEDO_EDITSTEPS_HISTORY_INSERT_REQUEST";
	public static final String ACCUPEDO_SERVICE_RESTART_ALARM 			= "com.corusen.steponfre.ACCUPEDO_SERVICE_RESTART_ALARM";

	private static final String TAG = "AccuService";
	private PedometerSettings 	mPedometerSettings;
	private PedometerStates 	mPedometerStates;
	private SensorManager 		mSensorManager;
	private Sensor 				mSensor;
	private StepDetector 		mStepDetector;
	private PowerManager.WakeLock mWakeLock;

	public StepDisplayer 		mStepDisplayer;
	public DistanceNotifier 	mDistanceNotifier;
	public SpeedNotifier 		mSpeedNotifier;
	public PaceNotifier 		mPaceNotifier;
	public CaloriesNotifier 	mCaloriesNotifier;
	public SteptimeNotifier 	mSteptimeNotifier;

	//private int 		mOperationModePast = Constants.STOP_MODE_SAVE;
	private int 		mOperationMode;
	public int 		mSteps;
	public long 	mSteptime;
	public boolean 	mIsMetric = true;
	public boolean 	mIsSpeedAverage = true;

	private int 	mOldSteps;
	private int 	mSession;
	private int 	mLap = 0;
	private int 	mLapsteps;
	private int 	mPace;
	private int 	mGoalSteps = 10000;

	private float 	mLapdistance;
	private float 	mLapcalories;
	private float 	mDistance;
	private float 	mCalories;
	private float 	mSpeed;
	private float 	mPercent;

	private long 	mLapsteptime;

	private int 		mWidgetSkinColor = R.color.mywidgetblack;
	private String 		msDistanceUnit;
	private float 		mfDistanceFactor;
	private RemoteViews 	 mRemoteViews;
	private int[] 			 mAppWidgetIds;
	private AppWidgetManager mAppWidgetManager;

	private PendingIntent mpIntentWake 		= null;
	private PendingIntent mpIntentPause 	= null;
	private PendingIntent mpIntentRelease 	= null;
	private PendingIntent mpIntentDaily 	= null;
	private PendingIntent mpIntentSave 		= null;
	private PendingIntent mpIntentOneminute = null;
	private PendingIntent mpIntentRestart 	= null;

	private static final int NUM_WIDGET_MODES 		= 4;
	private static final int NUM_SAVESTATES_CYCLE 	= 20;

	private static final int ALARM_DAILY_CYCLE_HOURS 			= 24;
	private static final int ALARM_DATABASE_CYCLE_MINUTES 		= 30;
	private static final int ALARM_DATABASE_CYCLE_ONEMINUTES 	= 1;

	private static final int ALARM_WAKE_CYCLE_SECONDS_MOST 		= 5;
	private static final int ALARM_WAKE_CYCLE_SECONDS_BALANCED 	= 10;
	private static final int ALARM_WAKE_CYCLE_SECONDS_LEAST 	= 30;

	public static final int FLAG_MODE_POWER_MOST 				= 10;
	public static final int FLAG_MODE_POWER_BALANCED 			= 11;
	public static final int FLAG_MODE_POWER_LEAST 				= 12;

	public static boolean mFlagScreenOn 	= true;
	private static int mFlagSaveStatesMode	= 0;
	private static int mFlagExerciseMode 	= 0;

	public static final int FLAG_MODE_WALK 	= 0;
	public static final int FLAG_MODE_RUN 	= 1;
	public static final int FLAG_MODE_HIKE 	= 2;
	public static final int FLAG_MODE_STAIRWAY = 3;

	private static final int TIMER_TIME_DIFF_THESH 		= 3000; // milliseconds
	private static final int TIMER_CHECK_STEPS_DELAY 	= 1000;

	private static final int TIMER_CHECK_STEPS_DELAY_ESTIMATION = 3000;
	private static final int TIMER_CHECK_STEPS_THRESH_ESTIMATION = 1;

	public int mFlagPowerMode = FLAG_MODE_POWER_BALANCED;
	private boolean mIsEstimation;

	public boolean mIsGoalAchievementNotification = false;
	public boolean mIsAchievementNotificationFiredToday = false;
	
	public static boolean mIsGoalRewardNotification = false;
	public static boolean mIsGoalAchievement = false;
	private int mNMID;
	private Calendar mNow;
	private MyDB mDB;

	private AlarmManager mAlarmManager;
	private SdcardManager mSdmanager;
	private Timer myTimer;

	public class StepBinder extends Binder {
		AccuService getService() {
			return AccuService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences states = getSharedPreferences("state", MODE_PRIVATE);
		mPedometerStates = new PedometerStates(states);
		mPedometerSettings = new PedometerSettings(settings);
		mSdmanager = new SdcardManager(this.getBaseContext());
		mPedometerSettings.setResetToday(Calendar.getInstance()); //V135

		switch (mPedometerSettings.getPowerUsageMode()) {
			case 0: mPedometerSettings.setPowerUsageMode(FLAG_MODE_POWER_MOST); break; // old most accurate
			case 1: mPedometerSettings.setPowerUsageMode(FLAG_MODE_POWER_BALANCED); break; // old more accurate
			case 2: mPedometerSettings.setPowerUsageMode(FLAG_MODE_POWER_BALANCED); break; // old balanced
			case 3: mPedometerSettings.setPowerUsageMode(FLAG_MODE_POWER_BALANCED); break; // old less power
			case 4: mPedometerSettings.setPowerUsageMode(FLAG_MODE_POWER_LEAST); break; // old least power
			case FLAG_MODE_POWER_MOST:
			case FLAG_MODE_POWER_BALANCED:
			case FLAG_MODE_POWER_LEAST:
				break;
			default: mPedometerSettings.setPowerUsageMode(FLAG_MODE_POWER_BALANCED); break;
		}

		mStepDetector = new StepDetector(this);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SHUTDOWN);
		filter.addAction(ACCUPEDO_UPDATE_REQUEST);
		filter.addAction(ACCUPEDO_MODE_CHANGE);
		filter.addAction(ACCUPEDO_TRIPA_RESET);
		filter.addAction(ACCUPEDO_DAILYLOG_ALARM);
		filter.addAction(ACCUPEDO_SAVESTATES_ALARM);
		filter.addAction(ACCUPEDO_SAVESTATES_ONEMINUTE);
		filter.addAction(ACCUPEDO_SETTINGS_RELOAD);
		filter.addAction(ACCUPEDO_WAKE_ALARM);
		filter.addAction(ACCUPEDO_PAUSE_REQUEST);
		filter.addAction(ACCUPEDO_RELEASE_REQUEST);
		filter.addAction(ACCUPEDO_WIDGET_PAUSE);
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		filter.addAction(ACCUPEDO_SAVE_DB_REQUEST);
		filter.addAction(ACCUPEDO_EDITSTEPS_REQUEST);
		filter.addAction(ACCUPEDO_EDITSTEPS_TODAY_REQUEST);
		filter.addAction(ACCUPEDO_EDITSTEPS_HISTORY_REQUEST);
		filter.addAction(ACCUPEDO_EDITSTEPS_HISTORY_INSERT_REQUEST);
		registerReceiver(mReceiver, filter);

		mStepDisplayer 	= new StepDisplayer();
		mPaceNotifier 	= new PaceNotifier();
		mSpeedNotifier 	= new SpeedNotifier(mSpeedListener, mPedometerSettings);
		mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings);
		mCaloriesNotifier = new CaloriesNotifier(mCaloriesListener, mPedometerSettings);
		mSteptimeNotifier = new SteptimeNotifier(mSteptimeListener);

		mStepDetector.addStepListener(mStepDisplayer);
		mStepDetector.addStepListener(mPaceNotifier);
		mStepDetector.addStepListener(mDistanceNotifier);
		mStepDetector.addStepListener(mCaloriesNotifier);
		mStepDetector.addSteptimeListener(mSteptimeNotifier);
		mStepDisplayer.addListener(mStepListener);
		mPaceNotifier.addListener(mPaceListener);
		mPaceNotifier.addListener(mSpeedNotifier);

		reloadSettings();

		Intent intent;
		mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		intent = new Intent(ACCUPEDO_DAILYLOG_ALARM);
		mpIntentDaily = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar midnight = Calendar.getInstance();
		midnight.set(Calendar.HOUR_OF_DAY, 23);
		midnight.set(Calendar.MINUTE, 59);
		midnight.set(Calendar.SECOND, 59);
		midnight.set(Calendar.MILLISECOND, 0);
		mAlarmManager.setRepeating(AlarmManager.RTC, midnight.getTimeInMillis(), 3600 * ALARM_DAILY_CYCLE_HOURS * 1000, mpIntentDaily);

		// hourly database alarm setting
		intent = new Intent(ACCUPEDO_SAVESTATES_ALARM);
		mpIntentSave = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar everyhour = Calendar.getInstance();
		int start = everyhour.get(Calendar.HOUR_OF_DAY);
		if (start >= 23) { start = 0; }
		else { start++; }

		everyhour.set(Calendar.HOUR_OF_DAY, start);
		everyhour.set(Calendar.MINUTE, 0);
		everyhour.set(Calendar.SECOND, 0);
		everyhour.set(Calendar.MILLISECOND, 0);
		mAlarmManager.setRepeating(AlarmManager.RTC, everyhour.getTimeInMillis(), 60 * ALARM_DATABASE_CYCLE_MINUTES * 1000, mpIntentSave);

		// service restart alarm setting every 30min
		intent = new Intent(ACCUPEDO_SERVICE_RESTART_ALARM);
		mpIntentRestart = PendingIntent.getBroadcast(this, 0, intent, 0);
		mAlarmManager.setRepeating(AlarmManager.RTC, everyhour.getTimeInMillis(), 60 * ALARM_DATABASE_CYCLE_MINUTES * 1000, mpIntentRestart);

		mDB = new MyDB(this);
		mDB.open();

		loadStatesFromDB();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			loadStatesFromDB();
			reloadSettings();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		releaseWakeLock();
		unregisterDetector();
		unregisterReceiver(mReceiver);
		saveStates();
		saveToDBForDaily();
		mAlarmManager.cancel(mpIntentDaily);
		mAlarmManager.cancel(mpIntentSave);
		mAlarmManager.cancel(mpIntentPause);
		mAlarmManager.cancel(mpIntentRelease);
		mAlarmManager.cancel(mpIntentWake); // mpIntentRestart is canceled when Exit menu selected
		super.onDestroy();
	}

//	private boolean isWiFiEnabled() {
//		boolean connection = false;
//		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//		if (!cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()) {
//			connection = true;
//		}
//		return connection;
//	}

	public void cancelRestartAlarm() {
		mAlarmManager.cancel(mpIntentRestart);
	}

	public void setOperationMode(int mode) {
		mOperationMode = mode;

		switch (mOperationMode) {
		case Constants.START_MODE:
			setPowerMode();
			registerDetectorGame();
			acquireWakeLock();
			startNewLap();
			setLapDataSaveAlarms(); // this will save initial data with indicator 1
			saveToDBForLapStartStop(); // this will save data with indicator 2
			mPedometerSettings.setOperationMode(Constants.START_MODE);
			break;

		case Constants.STOP_MODE_NOSAVE:
			//mOperationModePast = Constants.STOP_MODE_NOSAVE;
			removeWakeAlarm();
			unregisterDetector();
			releaseWakeLock();
			removeLapDataSaveAlarms();
			cancelNewLap();
			mPedometerSettings.setOperationMode(Constants.STOP_MODE_SAVE);
			break;

		case Constants.STOP_MODE_SAVE:
			//mOperationModePast = Constants.STOP_MODE_NOSAVE;
			saveToDBForLapStartStop(); // this will save data with indicator 2
			deleteOldHistory();
			removeWakeAlarm();
			unregisterDetector();
			releaseWakeLock();
			removeLapDataSaveAlarms();
			mPedometerSettings.setOperationMode(Constants.STOP_MODE_SAVE);
			break;

		case Constants.PAUSE_MODE:
			break;

		case Constants.RESUME_MODE:
			break;
		}
	}

	private void saveStates() {
		mPedometerStates.saveStates(mLap, mLapsteps, mLapdistance, mLapcalories, mLapsteptime, mSteps, mDistance, mCalories, mSteptime);
		mPedometerSettings.saveEndingTimeStamp();
	}

	// indicator 0: lap = 0
	// indicator 1: lap step every minute
	// indicator 2: lap start and stop
	// indicator 3: step every 30 min and chart / history
	public void saveToDBForDaily() {
		int indicator;
		if (mLap == 0) {
			indicator = 0;
			mDB.open();
			mDB.saveStepsToDatabase(mSession, mLap, mLapsteps, mLapdistance, mLapcalories, mLapsteptime, mSteps, mDistance, mCalories, mSpeed, mPace, mSteptime, mGoalSteps, indicator, mFlagExerciseMode);
			mDB.close();
		} else {
			indicator = 3;
			mDB.open();
			mDB.saveStepsToDatabase(mSession, mLap, mLapsteps, mLapdistance, mLapcalories, mLapsteptime, mSteps, mDistance, mCalories, mSpeed, mPace, mSteptime, mGoalSteps, indicator, mFlagExerciseMode);
			mDB.close();
		}
	}

	private void saveToDBForLap() {
//		Time time = new Time();
//		time.setToNow();
//		int hour = time.hour;
//		int minute = time.minute;
//		int indicator = 1;
//		if ((hour == 23) && (minute == 59)) { indicator = 2; }
//		else if ((hour == 0) && (minute == 0)) { indicator = 2; }
//		else { indicator = 1; }
		mDB.open();
		mDB.saveStepsToDatabase(mSession, mLap, mLapsteps, mLapdistance, mLapcalories, mLapsteptime, mSteps, mDistance, mCalories, mSpeed, mPace, mSteptime, mGoalSteps, 1, mFlagExerciseMode);
		mDB.close();
		if (mCallback != null) { mCallback.hourlyChartChanged(0); }
	}

	private void saveToDBForLapStartStop() {
		mDB.open();
		mDB.saveStepsToDatabase(mSession, mLap, mLapsteps, mLapdistance, mLapcalories, mLapsteptime, mSteps, mDistance, mCalories, mSpeed, mPace, mSteptime, mGoalSteps, 2, mFlagExerciseMode);
		mDB.close();
		if (mCallback != null) { mCallback.hourlyChartChanged(0); }
	}

	private void deleteOldHistory() {
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_MONTH) - NUM_DAYS_KEEP_DATA;
		if (day <= 0) day = 31 - day;

		mDB.open();
		mDB.deleteSessionsByDayBefore(day);
		mDB.close();
	}

//	private void loadStates() {
//		mLap = mPedometerStates.getLapNumberState();
//		mLapsteps = mPedometerStates.getLapStepsState();
//		mLapdistance = mPedometerStates.getLapDistanceState();
//		mLapcalories = mPedometerStates.getLapCaloriesState();
//		mLapsteptime = mPedometerStates.getLapStepTimeState();
//		// mCity = mPedometerStates.getCityState();
//		// mTemp = mPedometerStates.getTemperatureState();
//
//		mSteps = mPedometerStates.getStepsState();
//		mDistance = mPedometerStates.getDistanceState();
//		mCalories = mPedometerStates.getCaloriesState();
//		mSteptime = mPedometerStates.getStepTimeState();
//
//		mStepDisplayer.setSteps(mLapsteps, mSteps);
//		mDistanceNotifier.setDistance(mLapdistance, mDistance);
//		mCaloriesNotifier.setCalories(mLapcalories, mCalories);
//		mSteptimeNotifier.setSteptime(mLapsteptime, mSteptime);
//
//		if (mCallback != null) {
//			mCallback.lapnumberChanged(mLap);
//			// mCallback.weatherChanged(mTemp);
//		}
//
//		if (mPedometerSettings.isNewDate()) {
//			resetValues();
//		}
//	}

	private void loadStatesFromDB() {
		int year, month, day;
		Calendar today = Calendar.getInstance();
		year 	= today.get(Calendar.YEAR);
		month 	= today.get(Calendar.MONTH) + 1;
		day 	= today.get(Calendar.DATE);

		mDB.open();
		Cursor c0 = mDB.queryLapNumber(year, month, day); //Cursor c0 = mDB.queryLapMaxStepsForDay(year, month, day);

		if (c0.moveToFirst()) {
			mLap = c0.getInt(c0.getColumnIndex(Constants.KEY_LAP));
			c0.close();
		}

		if (mLap != 0) {
			Cursor c = mDB.queryLapStepsForDay(year, month, day, mLap); //mLap = mPedometerStates.getLapNumberState();
			c.isAfterLast();
			mLapsteps = c.getInt(c.getColumnIndex(Constants.KEY_LAPSTEPS));
			mLapdistance = c.getFloat(c.getColumnIndex(Constants.KEY_LAPDISTANCE));
			mLapcalories = c.getFloat(c.getColumnIndex(Constants.KEY_LAPCALORIES));
			mLapsteptime = c.getLong(c.getColumnIndex(Constants.KEY_LAPSTEPTIME));
			c.close();

			Cursor c1 = mDB.queryDayAll(year, month, day);
			c1.isAfterLast();
			mSteps = c1.getInt(c1.getColumnIndex(Constants.KEY_STEPS));
			mDistance = c1.getFloat(c1.getColumnIndex(Constants.KEY_DISTANCE));
			mCalories = c1.getFloat(c1.getColumnIndex(Constants.KEY_CALORIES));
			mSteptime = c1.getLong(c1.getColumnIndex(Constants.KEY_STEPTIME));

			mStepDisplayer.setSteps(mLapsteps, mSteps);
			mDistanceNotifier.setDistance(mLapdistance, mDistance);
			mCaloriesNotifier.setCalories(mLapcalories, mCalories);
			mSteptimeNotifier.setSteptime(mLapsteptime, mSteptime);

			if (mCallback != null) { mCallback.lapnumberChanged(mLap); }
			if (mPedometerSettings.isNewDate()) { resetValues(); }
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * Receives messages from activity.
	 */
	private final IBinder mBinder = new StepBinder();

	public interface ICallback {
		void stepsChanged	(int lapsteps, int steps);
		void distanceChanged(float lapdistance, float distance);
		void paceChanged	(int value);
		void speedChanged	(float value);
		void caloriesChanged(float lapcalories, float calories);
		void steptimeChanged(long lapsteptime, long steptime);
		void updateDisplay	(int value);
		void goalChanged	(int value);
		void percentChanged	(float value);
		void lapnumberChanged(int value);
		//void weatherChanged	(float value);
		void hourlyChartChanged(int value);
	}

	private ICallback mCallback;

	public void registerCallback(ICallback cb) {
		mCallback = cb;
	}

	public void reloadSettings() {
		releaseWakeLock();
		removeWakeAlarm();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		int wakeFlags;
		switch (mPedometerSettings.getScreenOperationLevel()) {
		case 0: // run background
			wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
			break;
		case 1:
			wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
			break;
		case 2:
			wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
			break;
		default:
			wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
			break;
		}
		mWakeLock = pm.newWakeLock(wakeFlags, TAG);
		mWakeLock.setReferenceCounted(false);

		mFlagPowerMode = mPedometerSettings.getPowerUsageMode();
		
		mIsGoalAchievementNotification = mPedometerSettings.isGoalAchievementNotification();
		mIsAchievementNotificationFiredToday = mPedometerSettings.isAchievementNotificationFiredToday();

		mIsGoalRewardNotification = mPedometerSettings.isGoalRewardNotification();
		mIsGoalAchievement = mPedometerSettings.isGoalAchievement();

		mIsSpeedAverage = mPedometerSettings.isSpeedAverage();
		mIsMetric = mPedometerSettings.isMetric();
		if (mIsMetric) {
			msDistanceUnit = getString(R.string.km);  //msWidgetDistanceUnit = getString(R.string.km);
			mfDistanceFactor = Utils.MILE2KM;
		} else {
			msDistanceUnit = getString(R.string.miles); //msWidgetDistanceUnit = getString(R.string.widget_mi);
			mfDistanceFactor = 1.0f;
		}

		setExerciseMode(mPedometerSettings.getExerciseType());

		if (mStepDetector != null) {
			mStepDetector.setSensitivity(mPedometerSettings.getSensitivity());
			int conSteps = mPedometerSettings.getConsecutiveSteps();
			mStepDetector.setConsecutiveSteps(conSteps);
			mStepDisplayer.setConsecutiveSteps(conSteps);
			mDistanceNotifier.setConsecutiveSteps(conSteps);
			mSpeedNotifier.setConsecutiveSteps(conSteps);
			mCaloriesNotifier.setConsecutiveSteps(conSteps);
		}


		if (mStepDisplayer != null)  	mStepDisplayer.reloadSettings();
		if (mDistanceNotifier != null) 	mDistanceNotifier.reloadSettings();
		if (mSpeedNotifier != null)  	mSpeedNotifier.reloadSettings();
		if (mCaloriesNotifier != null)  mCaloriesNotifier.reloadSettings();
		if (mSteptimeNotifier != null)	mSteptimeNotifier.reloadSettings();

		mGoalSteps = mPedometerSettings.getGoalSteps();
		if (mCallback != null) {
			mCallback.goalChanged(mGoalSteps);
			mCallback.lapnumberChanged(mLap);
			mCallback.hourlyChartChanged(0);
		}

		setWidgetSkinColor();

		Locale locale;
		switch (mPedometerSettings.getLocaleType()) {
		case 0:
			locale = Locale.getDefault();
			break;
		case 1:
			locale = new Locale("en");
			break;
		default:
			locale = Locale.getDefault();
			break;
		}

		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

		if (mCallback != null) mCallback.updateDisplay(0); // dummy call to update the main screen with new color
		updateWidget();
	}


	public void setWidgetSkinColor() {
		switch (mPedometerSettings.getWidgetSkinType()) {
			case 0: mWidgetSkinColor = R.color.mywidgetltblack; break;
			case 1: mWidgetSkinColor = R.color.mywidgetblack; break;
			case 2: mWidgetSkinColor = R.color.mywidgetblue; break;
			case 3: mWidgetSkinColor = R.color.mywidgetgreen; break;
			case 4: mWidgetSkinColor = R.color.mywidgetorange; break;
			case 5: mWidgetSkinColor = R.color.mywidgetpink; break;
			case 6: mWidgetSkinColor = R.color.mywidgettransparent; break;
			default: mWidgetSkinColor = R.color.mywidgetblack; break;
		}
	}

	private void setPowerMode() {

		switch (mFlagPowerMode) {
		case FLAG_MODE_POWER_MOST:
			setWakeAlarm(ALARM_WAKE_CYCLE_SECONDS_MOST);
			break;
		case FLAG_MODE_POWER_BALANCED:
			setWakeAlarm(ALARM_WAKE_CYCLE_SECONDS_BALANCED);
			break;
		case FLAG_MODE_POWER_LEAST:
			setWakeAlarm(ALARM_WAKE_CYCLE_SECONDS_LEAST);
			break;
		default:
			break;
		}
	}

	private void removeWakeAlarm() {
		if (mpIntentWake != null) mAlarmManager.cancel(mpIntentWake);
	}

	private void setWakeAlarm(int sec) {
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		Intent nIntent = new Intent(ACCUPEDO_WAKE_ALARM);
		mpIntentWake = PendingIntent.getBroadcast(this, 0, nIntent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), sec * 1000, mpIntentWake);
	}

	public void setExerciseMode(int mode) {
		mFlagExerciseMode = mode;
		if (mStepDetector != null) {
			mDistanceNotifier.reloadSettings();
			mSpeedNotifier.reloadSettings();
			mCaloriesNotifier.reloadSettings();
		}
	}

	private void setAccupedoPause() {
		mPedometerSettings.savePauseStatus(true);
		removeWakeAlarm();
		unregisterDetector();
		releaseWakeLock();
		if (mCallback != null)
			mCallback.updateDisplay(1);
		updateWidget();
	}

	private void setAccupedoResume() {
		mPedometerSettings.savePauseStatus(false);
		removeWakeAlarm();
		setPowerMode();
		if (mCallback != null) mCallback.updateDisplay(2);
		updateWidget();
	}

	private void setLapDataSaveAlarms() {
		Calendar currenttime = Calendar.getInstance();
		Intent intent = new Intent(ACCUPEDO_SAVESTATES_ONEMINUTE);
		mpIntentOneminute = PendingIntent.getBroadcast(this, 0, intent, 0);
		mAlarmManager.setRepeating(AlarmManager.RTC, currenttime.getTimeInMillis(), 60 * ALARM_DATABASE_CYCLE_ONEMINUTES * 1000, mpIntentOneminute);
	}

	private void removeLapDataSaveAlarms() {
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(mpIntentOneminute);
	}

//	private void removePauseReleaseAlarms() {
//		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//		alarmManager.cancel(mpIntentPause);
//		alarmManager.cancel(mpIntentRelease);
//	}
//
//	private void setPauseReleaseAlarms() {
//		Intent nIntent;
//		int[] itime = new int[2];
//
//		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//
//		itime = mPedometerSettings.getAccupedoReleaseTime();
//		nIntent = new Intent(ACCUPEDO_RELEASE_REQUEST);
//		mpIntentRelease = PendingIntent.getBroadcast(this, 0, nIntent, 0);
//		Calendar cNow = Calendar.getInstance();
//		Calendar cRelease = (Calendar) cNow.clone();
//		cRelease.set(Calendar.HOUR_OF_DAY, itime[0]);
//		cRelease.set(Calendar.MINUTE, itime[1]);
//		cRelease.set(Calendar.SECOND, 0);
//		cRelease.set(Calendar.MILLISECOND, 0);
//
//		if (cNow.compareTo(cRelease) == 1) cRelease.add(Calendar.DATE, 1);
//
//		alarmManager.setRepeating(AlarmManager.RTC, cRelease.getTimeInMillis(), 3600 * ALARM_DAILY_CYCLE_HOURS * 1000, mpIntentRelease);
//
//		itime = mPedometerSettings.getAccupedoPauseTime();
//		nIntent = new Intent(ACCUPEDO_PAUSE_REQUEST);
//		mpIntentPause = PendingIntent.getBroadcast(this, 0, nIntent, 0);
//		Calendar cPause = Calendar.getInstance();
//		cPause.set(Calendar.HOUR_OF_DAY, itime[0]);
//		cPause.set(Calendar.MINUTE, itime[1]);
//		cPause.set(Calendar.SECOND, 0);
//		cPause.set(Calendar.MILLISECOND, 0);
//		if (cNow.compareTo(cPause) == 1) cPause.add(Calendar.DATE, 1);
//
//		alarmManager.setRepeating(AlarmManager.RTC, cPause.getTimeInMillis(), 3600 * ALARM_DAILY_CYCLE_HOURS * 1000, mpIntentPause);
//	}
//
//	public void setInitialData() {
//		int lapsteps = 0, steps = 0;
//		float lapdistance = 0, distance = 0;
//		float lapcalories = 0, calories = 0;
//		long lapsteptime = 0, steptime = 0;
//		Time time = new Time();
//		time.setToNow();
//
//		mDB.open();
//		Cursor c2 = mDB.queryDayMaxSteps(time.year, time.month + 1, time.monthDay);
//		if (c2 != null) {
//
//			c2.moveToLast();
//			lapsteps = c2.getInt(c2.getColumnIndex(Constants.KEY_LAPSTEPS));
//			lapdistance = c2.getFloat(c2.getColumnIndex(Constants.KEY_LAPDISTANCE));
//			lapcalories = c2.getFloat(c2.getColumnIndex(Constants.KEY_LAPCALORIES));
//			lapsteptime = c2.getLong(c2.getColumnIndex(Constants.KEY_LAPSTEPTIME));
//
//			steps = c2.getInt(c2.getColumnIndex(Constants.KEY_STEPS));
//			distance = c2.getFloat(c2.getColumnIndex(Constants.KEY_DISTANCE));
//			calories = c2.getFloat(c2.getColumnIndex(Constants.KEY_CALORIES));
//			steptime = c2.getLong(c2.getColumnIndex(Constants.KEY_STEPTIME));
//
//			mStepDisplayer.setSteps(lapsteps, steps);
//			mDistanceNotifier.setDistance(lapdistance, distance);
//			mCaloriesNotifier.setCalories(lapcalories, calories);
//			mSteptimeNotifier.setSteptime(lapsteptime, steptime);
//			c2.close();
//		}
//		mDB.close();
//	}

	private void startNewLap() {   // check today's new lap
		int lap;
		Calendar now = Calendar.getInstance();
		mDB.open();
		Cursor c2 = mDB.queryLapNumber(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
		if (c2 != null) lap = c2.getInt(c2.getColumnIndex(Constants.KEY_LAP));
		else lap = 0;

		if (lap == 0) mLap = 1;
		else mLap = lap + 1;

		mDB.close();

		mLapsteps 	 = 0;
		mLapdistance = 0.0f;
		mLapcalories = 0.0f;
		mLapsteptime = 0;
		mSpeed 		 = 0f;

		mStepDisplayer.setSteps(mLapsteps, mSteps);
		mDistanceNotifier.setDistance(mLapdistance, mDistance);
		mCaloriesNotifier.setCalories(mLapcalories, mCalories);
		mSteptimeNotifier.setSteptime(mLapsteptime, mSteptime);
		mSpeedNotifier.setSpeed(mSpeed);

		updateWidget();
		if (mCallback != null) mCallback.lapnumberChanged(mLap);

	}

	private void cancelNewLap() {
		Calendar now = Calendar.getInstance();
		mDB.open();
		mDB.deleteLap(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), mLap);
		mDB.close();

		mLapsteps 	 = 0;
		mLapdistance = 0.0f;
		mLapcalories = 0.0f;
		mLapsteptime = 0;
		mSpeed 	 	 = 0.0f;

		mStepDisplayer.setSteps(mLapsteps, mSteps);
		mDistanceNotifier.setDistance(mLapdistance, mDistance);
		mCaloriesNotifier.setCalories(mLapcalories, mCalories);
		mSteptimeNotifier.setSteptime(mLapsteptime, mSteptime);
		mSpeedNotifier.setSpeed(mSpeed);

		updateWidget();
		if (mCallback != null) mCallback.lapnumberChanged(mLap);

	}

	public void resetValues() {
		mLap 		 = 0;
		mLapsteps 	 = 0;
		mLapdistance = 0.0f;
		mLapcalories = 0.0f;
		mLapsteptime = 0;

		mSteps = 0;
		mStepDisplayer.setSteps(0, 0);
		mDistanceNotifier.setDistance(0f, 0f);
		mSpeedNotifier.setSpeed(0.0f);
		mCaloriesNotifier.setCalories(0f, 0f);
		mSteptimeNotifier.setSteptime(0L, 0L);
		updateWidget();
		if (mCallback != null) { mCallback.lapnumberChanged(mLap); }
	}

	public void setManualSteps(int steps) {
		mStepDisplayer.onStep100(steps);
		mDistanceNotifier.onStep100(steps);
		mCaloriesNotifier.onStep100(steps);
		mSteptimeNotifier.onStep100(steps);
		mSpeedNotifier.setSpeed(0.0f);
		saveStates();
		saveToDBForDaily();
		updateWidget();
	}

	public void setManualStepsForHistory(int year, int month, int day, int steps) {
		float distance = mDistanceNotifier.onStep100ForHistory(steps);
		float calories = mCaloriesNotifier.onStep100ForHistory(steps);
		long steptime = mSteptimeNotifier.onStep100ForHistory(steps);
		mDB.open();
		mDB.updateDayMaxSteps(year, month, day, steps, distance, calories, steptime);
		mDB.close();
	}


	private void setManualStepsForHistoryInsert(int year, int month, int day, int steps) {
		float distance = mDistanceNotifier.onStep100ForHistory(steps);
		float calories = mCaloriesNotifier.onStep100ForHistory(steps);
		long steptime = mSteptimeNotifier.onStep100ForHistory(steps);
		mDB.open();
		mDB.insertDayMaxSteps(year, month, day, steps, distance, calories, steptime);
		mDB.close();
	}

	private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
		public void stepsChanged(int lapsteps, int steps) {
			mLapsteps = lapsteps;
			mSteps = steps;
			mPercent = ((float) mSteps / (float) mGoalSteps) * 100.0f;
			mFlagSaveStatesMode = (mFlagSaveStatesMode + 1) % NUM_SAVESTATES_CYCLE;
			if (mFlagSaveStatesMode == 0)  saveStates();

			passValue();
			updateWidget();

			if (mIsGoalAchievementNotification && !mIsAchievementNotificationFiredToday && (mSteps >= mGoalSteps)) {
				showNotification();
				mIsAchievementNotificationFiredToday = true;
				mPedometerSettings.setAchievementNotificationFiredToday(true);
			}
			
			if (mIsGoalRewardNotification && !mIsGoalAchievement && (mSteps >= mGoalSteps)) {
				mIsGoalAchievement = true;
				mPedometerSettings.setGoalAchievement(true);
			}
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.stepsChanged(mLapsteps, mSteps);
				mCallback.percentChanged(mPercent);
			}
		}
	};

	private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
		public void valueChanged(float lapdistance, float distance) {
			mLapdistance = lapdistance;
			mDistance = distance;
			passValue();
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.distanceChanged(mLapdistance, mDistance);
			}
		}
	};

	private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
		public void paceChanged(int value) {
			mPace = value;
			passValue();
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.paceChanged(mPace);
			}
		}
	};

	private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
		public void valueChanged(float value) {
			if (mIsSpeedAverage) {
				if (mLapsteptime != 0)  mSpeed = mLapdistance / ((float) mLapsteptime / 1000.0f / 3600.0f);
				else  mSpeed = 0.0f;
			} else {
				mSpeed = value;
			}
			passValue();
		}

		public void passValue() {
			if (mCallback != null) mCallback.speedChanged(mSpeed);
		}
	};

	private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
		public void valueChanged(float lapcalories, float calories) {
			mLapcalories = lapcalories;
			mCalories = calories;
			passValue();
		}

		public void passValue() {
			if (mCallback != null)  mCallback.caloriesChanged(mLapcalories, mCalories);
		}
	};

	private SteptimeNotifier.Listener mSteptimeListener = new SteptimeNotifier.Listener() {
		public void valueChanged(long lapsteptime, long steptime) {
			mLapsteptime = lapsteptime;
			mSteptime = steptime;
			passValue();
		}

		public void passValue() {
			if (mCallback != null) mCallback.steptimeChanged(mLapsteptime, mSteptime);
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String string = intent.getAction();
			if (string.equals(Intent.ACTION_SCREEN_OFF)) {
				mFlagScreenOn = false;
				if (!mPedometerSettings.isPause() && ((mOperationMode == Constants.START_MODE) || (mOperationMode == Constants.RESUME_MODE))) {
					unregisterDetector();
					releaseWakeLock();
					acquireWakeLock();
					registerDetectorGame();

					if (mFlagPowerMode >= FLAG_MODE_POWER_LEAST) setTimerToCheckStepEstimation(3);
				}

			} else if (string.equals(Intent.ACTION_SCREEN_ON)) {
				mFlagScreenOn = true;
				if (!mPedometerSettings.isPause() && ((mOperationMode == Constants.START_MODE) || (mOperationMode == Constants.RESUME_MODE))) {
					registerDetectorGame();
					mStepDetector.setWakeMode();
				}
				updateWidget();

			} else if (string.equals(Intent.ACTION_SHUTDOWN)) {
				saveStates();

			} else if (string.equals(ACCUPEDO_UPDATE_REQUEST)) {
				updateWidget();

			} else if (string.equals(ACCUPEDO_DAILYLOG_ALARM)) {
				deleteOldHistory();

				mNow = Calendar.getInstance();
				if (!mPedometerSettings.isResetToday(mNow)) { //V563
					resetValues();
					if ((mOperationMode == Constants.START_MODE) || (mOperationMode == Constants.RESUME_MODE)) { mLap = 1; };
					mPedometerSettings.setResetToday(mNow);
				}


				mIsAchievementNotificationFiredToday = false;
				mPedometerSettings.setAchievementNotificationFiredToday(false);
				
				mIsGoalAchievement = false;
				mPedometerSettings.setGoalAchievement(false);
				
				if (mPedometerSettings.isAutomaticBackupToSDcard()) {
					try {
						mSdmanager.exportDatabaseService();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} else if (string.equals(ACCUPEDO_SAVESTATES_ALARM)) {

				if (!mPedometerSettings.isPause() && ((mOperationMode == Constants.START_MODE) || (mOperationMode == Constants.RESUME_MODE))) {
					unregisterDetector();
					registerDetectorGame();

					mNow = Calendar.getInstance();
					if ( !((mNow.get(Calendar.HOUR_OF_DAY) == 0) && (mNow.get(Calendar.MINUTE) < 5)) &&
							!((mNow.get(Calendar.HOUR_OF_DAY) == 23) && (mNow.get(Calendar.MINUTE) > 58)) ) {
						if (!mPedometerSettings.isResetToday(mNow)) { //V563
							resetValues();
							if ((mOperationMode == Constants.START_MODE) || (mOperationMode == Constants.RESUME_MODE)) { mLap = 1; };
							mPedometerSettings.setResetToday(mNow);
						}
						saveStates();
						saveToDBForDaily();
					}
				}
			} else if (string.equals(ACCUPEDO_WAKE_ALARM)) {
				if (!mPedometerSettings.isPause() && ((mOperationMode == Constants.START_MODE) || (mOperationMode == Constants.RESUME_MODE))) {
					acquireWakeLock();
					registerDetectorGame();
					if (isWalking()) { mStepDetector.setStepMode(); }
					else { mStepDetector.setWakeMode(); }
				}

			} else if (string.equals(ACCUPEDO_SAVESTATES_ONEMINUTE)) {
				mNow = Calendar.getInstance();
				if (!mPedometerSettings.isResetToday(mNow)) { //V563
					resetValues();
					if ((mOperationMode == Constants.START_MODE) || (mOperationMode == Constants.RESUME_MODE)) { mLap = 1; };
					mPedometerSettings.setResetToday(mNow);
				}
				saveToDBForLap();

			} else if (string.equals(ACCUPEDO_PAUSE_REQUEST)) {
				setAccupedoPause();

			} else if (string.equals(ACCUPEDO_RELEASE_REQUEST)) {
				setAccupedoResume();

			} else if (string.equals(ACCUPEDO_WIDGET_PAUSE)) {
				if (mPedometerSettings.isPause()) { setAccupedoResume(); }
				else { setAccupedoPause(); }

			} else if (string.equals(ACCUPEDO_SETTINGS_RELOAD)) {
				reloadSettings();

			} else if (string.equals(ACCUPEDO_MODE_CHANGE)) { // called by widget mode button
				int newMode = (mFlagExerciseMode + 1) % NUM_WIDGET_MODES;
				setExerciseMode(newMode);
				updateWidget();

			} else if (string.equals(ACCUPEDO_TRIPA_RESET)) {
				saveToDBForLap();
				startNewLap();
				updateWidget();

			} else if (string.equals(Intent.ACTION_POWER_CONNECTED)) {
				if (mPedometerSettings.isAutoPauseDuringCharging()) { setAccupedoPause(); }

			} else if (string.equals(Intent.ACTION_POWER_DISCONNECTED)) {
				if (mPedometerSettings.isAutoPauseDuringCharging()) {
					if (mPedometerSettings.isActiveHour()) {
						if (mPedometerSettings.isInActiveHour()) {setAccupedoResume(); }
					} else {setAccupedoResume(); }
				}
			} else if (string.equals(ACCUPEDO_SAVE_DB_REQUEST)) {
				saveToDBForDaily();
			} else if (string.equals(ACCUPEDO_EDITSTEPS_REQUEST)) {
				setManualSteps(FragmentEditsteps.getNewCount());
			} else if (string.equals(ACCUPEDO_EDITSTEPS_TODAY_REQUEST)) {
				setManualSteps(ActivityHistoryEditsteps.getNewCount());
			} else if (string.equals(ACCUPEDO_EDITSTEPS_HISTORY_REQUEST)) {
				setManualStepsForHistory(ActivityHistoryEditsteps.getYear(), ActivityHistoryEditsteps.getMonth(), ActivityHistoryEditsteps.getDay(),
						ActivityHistoryEditsteps.getNewCount());
			}
			else if (string.equals(ACCUPEDO_EDITSTEPS_HISTORY_INSERT_REQUEST)) {
				setManualStepsForHistoryInsert(ActivityHistoryEditsteps.getYear(), ActivityHistoryEditsteps.getMonth(), ActivityHistoryEditsteps.getDay(), ActivityHistoryEditsteps.getNewCount());
			}
		}
	};

	public boolean isWalking() {
		if (mOldSteps < mSteps) {
			mOldSteps = mSteps;
			return true;
		} else {
			return false;
		}
	}

	private void registerDetectorGame() {
		mSensorManager.registerListener(mStepDetector, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public void unregisterDetector() {
		mSensorManager.unregisterListener(mStepDetector);
	}

	private void acquireWakeLock() {
		if (mWakeLock != null) {
			if (!mWakeLock.isHeld()) {
				mWakeLock.acquire();
			}
		}
	}

	public void releaseWakeLock() {
		if (mWakeLock != null) {
			if (mWakeLock.isHeld()) {
				mWakeLock.release();
			}
		}
	}

	public void setTimerToCheckStep() {
		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				long dtime = SystemClock.elapsedRealtime() - mStepDetector.mCurStepCountTime;
				if (dtime > TIMER_TIME_DIFF_THESH) {
					mStepDetector.setSleepMode();
					unregisterDetector();
					releaseWakeLock();
				}
				cancel();
				myTimer.cancel();
				myTimer.purge();
			}

		}, TIMER_CHECK_STEPS_DELAY);
	}



	public void setTimerToCheckStepEstimation(int factor) {
		mStepDetector.mWalkCount_Estimation = 0;
		mStepDetector.mSumDT_Estimation = 0;

		if (factor == 1) mIsEstimation = true;
		else mIsEstimation = false;

		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				int walkcount, estimatedsteps;
				float walksecond;
				long estimatedsteptime;
				if (mIsEstimation) { // in screen off mode
					walkcount = mStepDetector.mWalkCount_Estimation;
					if (walkcount >= TIMER_CHECK_STEPS_THRESH_ESTIMATION) {
						walksecond = (float) mStepDetector.mSumDT_Estimation / 1000.0f; // *2?
						estimatedsteps = (int) ((float) walkcount / walksecond * (float) ALARM_WAKE_CYCLE_SECONDS_LEAST);
						estimatedsteptime = (long) (walksecond / (float) walkcount * (float) ALARM_WAKE_CYCLE_SECONDS_LEAST * 1000);
						mStepDetector.callStepEstimated(estimatedsteps, estimatedsteptime);
					}
				}
				unregisterDetector();
				releaseWakeLock();
				cancel();
				myTimer.cancel();
				myTimer.purge();
			}

		}, TIMER_CHECK_STEPS_DELAY_ESTIMATION * factor);
	}

	public int getSteps() {
		return mSteps;
	}

	public void updateWidgetQuit() {
		updateWidget4Quit();
	}

	public void updateWidget4Quit() {
		if (mFlagScreenOn) {

			mRemoteViews = new RemoteViews(this.getPackageName(), R.layout.main40);
			mAppWidgetManager = AppWidgetManager.getInstance(this);
			mAppWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(this, PedoWidget4.class));
			if (mAppWidgetIds.length == 0) return;

			mRemoteViews.setInt(R.id.widget_main40, "setBackgroundColor", ContextCompat.getColor(getBaseContext(), mWidgetSkinColor));
			mRemoteViews.setTextViewText(R.id.widget_textview_daily, getString(R.string.widget_quit));
			mRemoteViews.setTextViewText(R.id.widget_textview, ((Integer) mSteps).toString());
			mRemoteViews.setTextViewText(R.id.widget_distance_value, String.format("%5.2f", mDistance * mfDistanceFactor));
			mRemoteViews.setTextViewText(R.id.widget_calories_value, String.format("%5.1f", mCalories));
			mRemoteViews.setTextViewText(R.id.widget_time_value, Utils.getHoursMinutesString((int) (mSteptime / 1000)));
			mRemoteViews.setProgressBar(R.id.widget_trainprogressbar, 100, (int) mPercent, false);
			mRemoteViews.setTextViewText(R.id.widget_textview_steps, getString(R.string.widget_steps));
			mRemoteViews.setTextViewText(R.id.widget_distance_units, msDistanceUnit);
			mRemoteViews.setTextViewText(R.id.widget_calories_units, getString(R.string.widget_calories));
			mRemoteViews.setTextViewText(R.id.widget_time_units, getString(R.string.hhmm));

			Intent nIntent = new Intent(this, Pedometer.class);
			nIntent.setAction(ACCUPEDO_WIDGET_DETAIL);
			nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, nIntent, 0);
			mRemoteViews.setOnClickPendingIntent(R.id.linearLayout0, pIntent);

			for (int id: mAppWidgetIds) mAppWidgetManager.updateAppWidget(id, mRemoteViews);
		}
	}

	public void updateWidget() {
		updateWidget4();
	}

	public void updateWidget4() {
		if (mFlagScreenOn) {

			mRemoteViews = new RemoteViews(this.getPackageName(), R.layout.main40);
			mAppWidgetManager = AppWidgetManager.getInstance(this);
			mAppWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(this, PedoWidget4.class));
			if (mAppWidgetIds.length == 0) return;

			mRemoteViews.setInt(R.id.widget_main40, "setBackgroundColor", ContextCompat.getColor(getBaseContext(), mWidgetSkinColor));
			if (mPedometerSettings.isPause()) {
				mRemoteViews.setTextColor(R.id.widget_textview, 		ContextCompat.getColor(getBaseContext(), R.color.mygray));
				mRemoteViews.setTextColor(R.id.widget_textview_daily, 	ContextCompat.getColor(getBaseContext(),R.color.mygray));
				mRemoteViews.setTextViewText(R.id.widget_textview_daily, getString(R.string.widget_paused));
			} else {
				mRemoteViews.setTextColor(R.id.widget_textview, 		ContextCompat.getColor(getBaseContext(), R.color.mywhite));
				mRemoteViews.setTextColor(R.id.widget_textview_daily, 	ContextCompat.getColor(getBaseContext(), R.color.mywhite));
				mRemoteViews.setTextViewText(R.id.widget_textview_daily, getString(R.string.widget_daily));
			}

			mRemoteViews.setTextViewText(R.id.widget_textview, ((Integer) mSteps).toString());
			mRemoteViews.setTextViewText(R.id.widget_distance_value, String.format("%5.2f", mDistance * mfDistanceFactor));
			mRemoteViews.setTextViewText(R.id.widget_calories_value, String.format("%5.1f", mCalories));
			mRemoteViews.setTextViewText(R.id.widget_time_value, Utils.getHoursMinutesString((int) (mSteptime / 1000)));
			mRemoteViews.setProgressBar(R.id.widget_trainprogressbar, 100, (int) mPercent, false);
			mRemoteViews.setTextViewText(R.id.widget_textview_steps, getString(R.string.widget_steps));
			mRemoteViews.setTextViewText(R.id.widget_distance_units, msDistanceUnit);
			mRemoteViews.setTextViewText(R.id.widget_calories_units, getString(R.string.widget_calories));
			mRemoteViews.setTextViewText(R.id.widget_time_units, getString(R.string.hhmm));
			mRemoteViews.setTextViewText(R.id.widget_textview_steps, getString(R.string.widget_steps));

			Intent nIntent = new Intent(this, Pedometer.class);
			nIntent.setAction(ACCUPEDO_WIDGET_DETAIL);
			nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, nIntent, 0);
			mRemoteViews.setOnClickPendingIntent(R.id.linearLayout1, pIntent);

			for (int id: mAppWidgetIds) mAppWidgetManager.updateAppWidget(id, mRemoteViews);

		} // mFlagScreenMode
	}

	private void showNotification() {
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Intent notificationIntent = new Intent(this, Pedometer.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.goal_achievement_notification)).setSound(alarmSound)
				.setAutoCancel(true);
		mBuilder.setContentIntent(contentIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification noti = mBuilder.build();
		mNotificationManager.notify(mNMID, noti);
	}

}
