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
 *  X9t8nwwt+em8/cUoeIQBfmyYOBc=
 *  138032342998022
 */

package com.corusen.steponfre.base;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.GraphicalView;

import com.corusen.steponfre.base.AnalyticsSampleApp.TrackerName;
import com.corusen.steponfre.R;
import com.corusen.steponfre.chart.HourBarChart2;
import com.corusen.steponfre.chart.IChart2;
import com.corusen.steponfre.database.Constants;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;

public class FragmentPedometer extends Fragment {

	public static final int ACCUPEDO_INTENT_REQUEST_MANUAL_STEPS = 1;
	public static final int NUM_HOURLY_BARS = 60;

	public static final int STOP_MODE_NOSAVE = 0; // when stop button pressed
	public static final int STOP_MODE_SAVE = 1; // when stop button pressed
	public static final int START_MODE = 2; // when start button pressed
	public static final int RESUME_MODE = 3; // when resume button pressed
	public static final int PAUSE_MODE = 4; // when pause button pressed

	public static int mOperationMode = STOP_MODE_NOSAVE;

	private View mView;

	private TextView mStepValueView;
	private TextView mTripaStepValueView;
	private TextView mDistanceValueView;
	private TextView mSpeedValueView;
	private TextView mCaloriesValueView;
	private TextView mSteptimeValueView;
	private TextView mDailyPercentValueView;
	private TextView mDailyGoalValueView;
	private TextView mLapTitleTextView;
	private TextView mLapTextView;
	private TextView mWeatherTextView;
	private View mHourlyChart;
	private View mHourlyLap;

	private LinearLayout mLayoutStart;
	private LinearLayout mLayoutPause;
	private LinearLayout.LayoutParams mLayoutParamsVisible = new LinearLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, 0, 1.0f);
	private LinearLayout.LayoutParams mLayoutParamsHide = new LinearLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, 0, 0.0f);

	private ImageButton mStartButton;
	private ImageButton mPauseButton;
	private ImageButton mStopButton;
	private ImageButton mLockButton;

	private ImageButton mWalkButton;
	private ImageButton mNextButton;
	private ProgressBar mProgressBar;

	private int mLap;
	private int mStepValue;

	private float mSpeedValue;
	private float mCaloriesValue;
	private int mLapStepValue;
	private float mLapDistanceValue;
	private float mLapCaloriesValue;
	private int mLapSteptimeValue;

	private int mGoal;
	private float mPercent;
	private int mTemperature = 100;
	private String mCity = "city";
	private String mTempUnit = "C";

	private boolean mIsRunning;
	private float mfDistanceFactor;
	private GraphicalView mChartView;

	private boolean mToggleHourlyChart;

	final int DEVICE_VERSION = Build.VERSION.SDK_INT;
	final int DEVICE_HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;

	private List<double[]> mX = new ArrayList<double[]>();
	private List<double[]> mValues = new ArrayList<double[]>();

	private AdView adView;

	private int mSectedItem;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("MyStepValue", mStepValue);
	}

	@Override
	public void onStart() {
		super.onStart();
		Tracker t = ((AnalyticsSampleApp) Pedometer.getInstance()
				.getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Pedometer");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// mPosition = Integer.parseInt(getTag());
	}

	@Override
	public void onPause() {
		// Log.i("WidgetQuit", "onPause1");
		if (mIsRunning) {
			// Log.i("WidgetQuit", "onPause2");
			unbindStepService();
		}
		//
		// if (Pedometer.mQuitting) {
		// Pedometer.mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
		// }
		// else {
		// Pedometer.mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
		// }
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(AccuService.mScreenFragmentPedometer,
				container, false);

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

		LocationManager coarseLocationManager = (LocationManager) Pedometer.getInstance().getSystemService(Context.LOCATION_SERVICE);
		Location coarseLocation = coarseLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Date birthday = Pedometer.mPedometerSettings.getBirthDate();
		int gender = Pedometer.mPedometerSettings.getGender();

		adView = new AdView(Pedometer.getInstance());
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(Constants.ACCUPEDO_ADMOB_ID);
		LinearLayout layout = (LinearLayout) mView.findViewById(R.id.adView);
		layout.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().setLocation(coarseLocation).setGender(gender).setBirthday(birthday).build();
		// .addTestDevice("bd0d08d5").addTestDevice("038d2d940b35a5ce").addTestDevice("TA648014AN").
		adView.loadAd(adRequest);
		setPedometer();
		
//		if (Pedometer.mPedometerSettings.isNewInstallation()) {
//			if (!Pedometer.mPedometerSettings.isHistoryImported117()) {
//				if (checkDBfileExist()) {
//					openNewInstallationAlertDialog();
//					Pedometer.mPedometerSettings
//							.setHistoryImportedStatusTrue117();
//				}
//			}
//			Pedometer.mPedometerSettings.setNewInstallationStatusFalse();
//		}

		return mView;
	}
	
//	public void openNewInstallationAlertDialog() {
//		new AlertDialog.Builder(Pedometer.getInstance())
//				// .setTitle(R.string.alert_pause_title)
//				.setMessage(R.string.alert_new_installation_message)
//				.setPositiveButton(R.string.dialog_yes,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								try {
//									SdcardManager sdmanager = new SdcardManager(
//											Pedometer.getInstance());
//									sdmanager.importDatabase();
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
//							}
//						})
//				.setNegativeButton(R.string.dialog_no,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//							}
//						}).show();
//
//	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateHourlyChart();
	}

	void setPedometer() {
		mIsRunning = Pedometer.mPedometerSettings.isServiceRunning();
		if (!mIsRunning && Pedometer.mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsRunning) {
			bindStepService();
		}
		Pedometer.mPedometerSettings.clearServiceRunning();

		// for the mid size phone, hide the daily progressbar
		WindowManager wm = (WindowManager) Pedometer.getInstance()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// int width = size.x;
		// int height = size.y;
		int width = display.getWidth();
		int height = display.getHeight();
		if ((240 < width) && (width <= 320)) {
			LinearLayout layout = (LinearLayout) mView
					.findViewById(R.id.layout_percent_value);
			layout.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 0, 0.0f));
		}

		mLayoutStart = (LinearLayout) mView
				.findViewById(R.id.linearlayoutstart);
		mLayoutPause = (LinearLayout) mView
				.findViewById(R.id.linearlayoutpause);
		mLayoutStart.setLayoutParams(mLayoutParamsVisible);
		mLayoutPause.setLayoutParams(mLayoutParamsHide);

		mStartButton = (ImageButton) mView.findViewById(R.id.startButton);
		mPauseButton = (ImageButton) mView.findViewById(R.id.pauseButton);
		mStopButton = (ImageButton) mView.findViewById(R.id.stopButton);
		mLockButton = (ImageButton) mView.findViewById(R.id.lockButton);

		mStartButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mOperationMode = START_MODE;
				mService.setOperationMode(mOperationMode);

				Pedometer.mPedometerSettings.setLockStatus(true);
				updateModeDisplay();
				displayLockState();

			}
		});

		mStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openStopAlertDialog();
			}
		});

		mPauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Pedometer.mPedometerSettings.isPause()) {
					openResumeAlertDialog();
				} else {
					openPauseAlertDialog();
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
				displayLockState();
			}
		});

		// mLapImageButton = (ImageButton) mView.findViewById(R.id.TripAButton);
		// mLapImageButton.setImageDrawable(this.getResources().getDrawable(
		// R.drawable.reset_imagebutton));
		// mLapImageButton.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// openNextLapAlertDialog();
		// }
		// });

		// mDailyResetButton = (ImageButton) mView
		// .findViewById(R.id.daily_reset_button);
		// mDailyResetButton.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// openDailyResetAlertDialog();
		// }
		// });

		mWalkButton = (ImageButton) mView.findViewById(R.id.walk_button);
		mWalkButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openWalkRunAlertDialog();
			}
		});
		updateExerciseModeDisplay(Pedometer.mPedometerSettings
				.getExerciseType());

		mHourlyChart = mView.findViewById(R.id.chart_hourly);
		mHourlyLap = mView.findViewById(R.id.lap_hourly);

		mToggleHourlyChart = true;
		mHourlyChart.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
		mHourlyLap.getLayoutParams().height = 0;

		mNextButton = (ImageButton) mView.findViewById(R.id.next_ic_button);
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
					updateHourlyChart();
				}
				mTripaStepValueView.setText(((Integer) mStepValue).toString()); // this
				// will
				// refresh
				// the
				// screen
			}
		});

		mStepValueView = (TextView) mView.findViewById(R.id.step_value);
		mTripaStepValueView = (TextView) mView
				.findViewById(R.id.tripa_step_value);
		mDistanceValueView = (TextView) mView.findViewById(R.id.distance_value);
		mSpeedValueView = (TextView) mView.findViewById(R.id.speed_value);
		mCaloriesValueView = (TextView) mView.findViewById(R.id.calories_value);
		mSteptimeValueView = (TextView) mView.findViewById(R.id.time_value);
		mDailyPercentValueView = (TextView) mView
				.findViewById(R.id.daily_percent_value);
		mDailyGoalValueView = (TextView) mView
				.findViewById(R.id.daily_goal_value);
		mProgressBar = (ProgressBar) mView.findViewById(R.id.trainprogressbar);
		mLapTitleTextView = (TextView) mView.findViewById(R.id.lap_title);
		mLapTextView = (TextView) mView.findViewById(R.id.lap_value);
		// mWeatherTextView = (TextView) mView.findViewById(R.id.city_temp);

		((TextView) mView.findViewById(R.id.distance_units))
				.setText(getString(Pedometer.mPedometerSettings.isMetric() ? R.string.km
						: R.string.miles));
		((TextView) mView.findViewById(R.id.speed_units))
				.setText(getString(Pedometer.mPedometerSettings.isMetric() ? R.string.kilometers_per_hour
						: R.string.miles_per_hour));

		if (Pedometer.mPedometerSettings.isMetric()) {
			mfDistanceFactor = Utils.MILE2KM; // 1.60934f;
			mTempUnit = "C";
		} else {
			mfDistanceFactor = 1.0f;
			mTempUnit = "F";
		}

		// changing the background color blocks cling effect
		// mStartButton.setBackgroundColor(getResources().getColor(AccuService.mScreenAcitionBarColor));

		updateModeDisplay();
		displayLockState();
		// updateHourlyChart();

		// updateWeather();

		// actionBar.setSelectedNavigationItem(SPINNER_ITEM_PEDOMETER);

	}

	// private void openStopAlertDialog() {
	// new AlertDialog.Builder(Pedometer.getInstance())
	// // .setTitle(R.string.alert_pause_title)
	// .setMessage(R.string.alert_pause_message)
	// .setPositiveButton(R.string.dialog_yes,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// mOperationMode = STOP_MODE;
	// mService.setOperationMode(mOperationMode);
	// updateModeDisplay();
	// }
	// })
	// .setNegativeButton(R.string.dialog_no,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// }
	// }).show();
	//
	// }

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

		mSectedItem = Pedometer.mPedometerSettings.getSaveOption();
		builder.setSingleChoiceItems(items, mSectedItem,
				new DialogInterface.OnClickListener() {
					// When you click the radio button
					@Override
					public void onClick(DialogInterface dialog, int item) {

						mSectedItem = item;
					}
				});

		builder.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {

						switch (mSectedItem) {
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
						Pedometer.mPedometerSettings.setSaveOption(mSectedItem);
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

	private void openPauseAlertDialog() {
		new AlertDialog.Builder(Pedometer.getInstance())
				// .setTitle(R.string.alert_pause_title)
				.setMessage(R.string.alert_pause_message)
				.setPositiveButton(R.string.dialog_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Pedometer
										.getInstance()
										.sendBroadcast(
												new Intent(
														AccuService.ACCUPEDO_PAUSE_REQUEST));
								mOperationMode = PAUSE_MODE;
								mService.setOperationMode(mOperationMode);
								updateModeDisplay();
							}
						})
				.setNegativeButton(R.string.dialog_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();

	}

	private void openResumeAlertDialog() {
		new AlertDialog.Builder(Pedometer.getInstance())
				// .setTitle(R.string.alert_resume_title)
				.setMessage(R.string.alert_resume_message)
				.setPositiveButton(R.string.dialog_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Pedometer
										.getInstance()
										.sendBroadcast(
												new Intent(
														AccuService.ACCUPEDO_RELEASE_REQUEST));
								mOperationMode = RESUME_MODE;
								mService.setOperationMode(mOperationMode);
								updateModeDisplay();

							}
						})
				.setNegativeButton(R.string.dialog_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();

	}

	// private void openNextLapAlertDialog() {
	// new AlertDialog.Builder(Pedometer.getInstance())
	// // .setTitle(R.string.alert_reset_title)
	// .setMessage(R.string.alert_next_lap_message)
	// .setPositiveButton(R.string.dialog_yes,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// mService.startNewLap();
	// mService.saveToDBForLap();
	// }
	// })
	// .setNegativeButton(R.string.dialog_no,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// }
	// }).show();
	// }

	// private void openDailyResetAlertDialog() {
	// new AlertDialog.Builder(Pedometer.getInstance())
	// // .setTitle(R.string.alert_reset_title)
	// .setMessage(R.string.alert_daily_reset_message)
	// .setPositiveButton(R.string.dialog_yes,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// mService.setManualSteps(0);
	// }
	// })
	// .setNegativeButton(R.string.dialog_no,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// }
	// }).show();
	// }

	private void openWalkRunAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				Pedometer.getInstance());
		builder.setTitle(R.string.exercise_type_setting_title);
		builder.setItems(R.array.exercise_type_preference,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mService.setExerciseMode(which);
						Pedometer.mPedometerSettings.setExerciseType(which);
						updateExerciseModeDisplay(which);
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
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

	private void updateModeDisplay() {
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
			mLockButton.setImageDrawable(getResources().getDrawable(
					R.drawable.unlocked_ic));
			AlphaAnimation alpha = new AlphaAnimation(0.25F, 0.25F);
			alpha.setDuration(0); // Make animation instant
			alpha.setFillAfter(true); // Tell it to persist after
			mStopButton.startAnimation(alpha);
			mPauseButton.startAnimation(alpha);

			mStopButton.setEnabled(false);
			mPauseButton.setEnabled(false);

		} else {
			mLockButton.setImageDrawable(getResources().getDrawable(
					R.drawable.locked_ic));
			AlphaAnimation alpha = new AlphaAnimation(1.0F, 1.0F);
			alpha.setDuration(0); // Make animation instant
			alpha.setFillAfter(true); // Tell it to persist after
			mStopButton.startAnimation(alpha);
			mPauseButton.startAnimation(alpha);
			mStopButton.setEnabled(true);
			mPauseButton.setEnabled(true);
		}
	}

	public AccuService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
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
			Pedometer.getInstance().startService(
					new Intent(Pedometer.getInstance(), AccuService.class));
		}
	}

	private void bindStepService() {
		mIsRunning = true;
		Pedometer.getInstance().bindService(
				new Intent(Pedometer.getInstance(), AccuService.class),
				mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	public void unbindStepService() {
		// Log.i("WidgetQuit", "unbindStepService");
		if (mIsRunning) {
			mIsRunning = false;
			Pedometer.getInstance().unbindService(mConnection);
		}
	}

	public void stopStepService() {
		// Log.i("WidgetQuit", "stopStepService");
		mIsRunning = false;
		Pedometer.getInstance().stopService(
				new Intent(Pedometer.getInstance(), AccuService.class));
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
			mHandler.sendMessage(mHandler.obtainMessage(UPDATE_MSG, value, 0));
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
			mHandler.sendMessage(mHandler.obtainMessage(HOURLYCHART_MSG, 0, 0));
		}

	};

	private static final int STEPS_MSG = 1;
	private static final int DISTANCE_MSG = 2;
	private static final int CALORIES_MSG = 3;
	private static final int STEPTIME_MSG = 4;
	private static final int UPDATE_MSG = 5;
	private static final int GOAL_MSG = 6;
	private static final int PERCENT_MSG = 7;
	private static final int SPEED_MSG = 8;
	private static final int PACE_MSG = 9;
	private static final int LAPNUMBER_MSG = 10;
	private static final int WEATHER_MSG = 11;
	private static final int HOURLYCHART_MSG = 12;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STEPS_MSG:
				mLapStepValue = (int) msg.arg1;
				mStepValueView.setText("" + mLapStepValue);
				mStepValue = (int) msg.arg2;
				mTripaStepValueView.setText("" + mStepValue);
				break;

			case DISTANCE_MSG:
				mLapDistanceValue = ((int) msg.arg1) / 1000f;
				if (mLapDistanceValue <= 0) {
					mDistanceValueView.setText(String.format("%.2f", 0.00f));
				} else {
					mDistanceValueView.setText(String.format("%.2f",
							mLapDistanceValue * mfDistanceFactor));
				}
				break;

			case CALORIES_MSG:
				mLapCaloriesValue = ((int) msg.arg1) / 1000f;
				mCaloriesValue = ((int) msg.arg2) / 1000f;
				if (mCaloriesValue < 1000f) {
					if (mLapCaloriesValue <= 0) {
						mCaloriesValueView
								.setText(String.format("%.1f", 0.00f));
					} else {
						mCaloriesValueView.setText(String.format("%.1f",
								mLapCaloriesValue));
					}
				} else {
					mCaloriesValueView.setText(String.format("%d",
							(int) mLapCaloriesValue));
				}
				break;

			case STEPTIME_MSG:
				// msg.arg1 is in second now
				mLapSteptimeValue = msg.arg1;
				mSteptimeValueView.setText(Utils
						.getHoursMinutesSecondsString(mLapSteptimeValue));
				break;

			case UPDATE_MSG:
				switch ((int) msg.arg1) {
				case 1:
					displayPauseMode();
					break;
				case 2:
					displayResumeMode();
					break;
				}
				break;

			case GOAL_MSG:
				mGoal = msg.arg1;
				mDailyGoalValueView.setText(((Integer) mGoal).toString());
				break;

			case PERCENT_MSG:
				mPercent = ((int) msg.arg1) / 1000f;

				mDailyPercentValueView.setText(((Integer) (int) mPercent)
						.toString() + "%");
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
					mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f))
							.substring(0, 4));
				}
				break;
			case LAPNUMBER_MSG:
				mLap = msg.arg1;
				mLapTextView.setText(((Integer) mLap).toString());
				if (mLap > 0) {
					mLapTextView.setVisibility(View.VISIBLE);
					mLapTitleTextView.setVisibility(View.VISIBLE);
				} else {
					mLapTextView.setVisibility(View.GONE);
					mLapTitleTextView.setVisibility(View.GONE);
				}
				break;

			case HOURLYCHART_MSG:
				updateHourlyChart();
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

	};

	private void updateWeather() {
		if ((mCity == "city")) {
			mWeatherTextView.setText(" ");
		} else {
			mWeatherTextView.setText(mCity + " : "
					+ Integer.toString(mTemperature) + (char) 0x00B0
					+ mTempUnit);
		}
	}

	private void updateHourlyChart() {
		LinearLayout layout = (LinearLayout) mView
				.findViewById(R.id.chart_hourly);
		IChart2 mChart = new HourBarChart2();

		boolean mScreenLarge = true;
		setDayHourData();
		if (mChartView != null) {
			layout.removeView(mChartView);
		}
		mChartView = mChart.graphicalView(Pedometer.getInstance(), mX, mValues,
				mScreenLarge);
		layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	public void setDayHourData() {
		int i, j, year, month, day;

		int count = 0;
		int startIndex = 0;
		int[] sumSteps = new int[NUM_HOURLY_BARS + 1];
		double[] delSteps = new double[NUM_HOURLY_BARS + 1];
		double[] x = new double[7];
		// int stepForOver60 = 0; // if sartIndex is over 60;

		for (i = 0; i < 7; i++) {
			x[i] = 0.0;
		}

		for (i = 0; i <= NUM_HOURLY_BARS; i++) {
			sumSteps[i] = 0;
			delSteps[i] = 0.0;
		}

		Calendar today = Calendar.getInstance();
		year = today.get(Calendar.YEAR);
		month = today.get(Calendar.MONTH) + 1;
		day = today.get(Calendar.DATE);

		Pedometer.mDB.open();
		Cursor c = Pedometer.mDB.queryLapStepsForDay(year, month, day, mLap);

		if (c == null) {
			count = 0;
		} else {
			count = c.getCount(); // it can be zero
		}

		// Log.i("SetHourlyData", "Count is : " + ((Integer) count).toString());

		if (count == 0) {
			x[0] = (double) startIndex;
			x[1] = x[0] + 10.0;
			x[2] = x[0] + 20.0;
			x[3] = x[0] + 30.0;
			x[4] = x[0] + 40.0;
			x[5] = x[0] + 50.0;
			x[6] = x[0] + 60.0;
			mX.clear();
			mX.add(x);
			mValues.clear();
			mValues.add(delSteps);
			return;
		}

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
					sumSteps[j] = c.getInt(c
							.getColumnIndex(Constants.KEY_LAPSTEPS));
				}
				// if (j >= 0) { // skip some bars if the total is greater than
				// // NUM_HOURLY_BARS
				// sumSteps[j] = c.getInt(c
				// .getColumnIndex(Constants.KEY_LAPSTEPS));
				// } else if (j == -1) {
				// stepForOver60 = c.getInt(c
				// .getColumnIndex(Constants.KEY_LAPSTEPS));
				// }
				i++;
			} while (c.moveToNext());
		}
		c.close();
		Pedometer.mDB.close();

		if (count > 1) {
			for (i = 1; i <= count; i++) {
				// if (i == 0) {
				// delSteps[i] = (double) (sumSteps[i] - stepForOver60);
				// if (delSteps[i] < 0.0) {
				// delSteps[i] = 0.0;
				// }
				// } else {
				j = i - 1;
				delSteps[j] = (double) (sumSteps[i] - sumSteps[i - 1]);
				if (delSteps[j] < 0.0) {
					delSteps[j] = 0.0;
					// }
				}

				// Log.i("SetHourlyData", ((Integer) count).toString() + ":"
				// + ((Integer) i).toString() + ":"
				// + ((Integer) (int) delSteps[i]).toString());
			}
		}

		x[0] = (double) startIndex;
		x[1] = x[0] + 10.0;
		x[2] = x[0] + 20.0;
		x[3] = x[0] + 30.0;
		x[4] = x[0] + 40.0;
		x[5] = x[0] + 50.0;
		x[6] = x[0] + 60.0;

		mX.clear();
		mValues.clear();
		mX.add(x);
		mValues.add(delSteps);
	}
	
	public boolean checkDBfileExist() {

		boolean fileExists;
		File sd = new File(Environment.getExternalStorageDirectory()
				+ Constants.ACCUPEDO_FOLDERNAME);
		File data = Environment.getDataDirectory();

		if (sd.exists()) {
			String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH
					+ Constants.DATABASE_NAME;
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
