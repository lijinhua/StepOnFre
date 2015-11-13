/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *  Updated for version 1.04
 *  Added one more LPF to filter out driving peaks
 */

package com.corusen.steponfre.base;

import java.util.ArrayList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
//import android.util.Log;

public class StepDetectorWithArray implements SensorEventListener {
	private static final int NUM_AXES = 3;
	// private static final int WALK_PATTERN = 5; // consecutive walking pattern
	// number 10
	private static final int SINGLE_STEP_TIME_MIN = 200;
	private static final int SINGLE_STEP_TIME_MAX = 800;
	private static final int STEP_TIME_MIN = 150; // step minimum time
	private static final int STEP_TIME_MAX = 800; // step maximum time

	// private static final long WALK_TIME_WIN_MIN = STEP_TIME_MIN *
	// WALK_PATTERN; // millisecond
	// private static final long WALK_TIME_WIN_MAX = STEP_TIME_MAX *
	// WALK_PATTERN; // millisecond
	private static final float LOW_PASS_GAIN = 0.05f;

	private static final float MOTION_THRESHOLD = 1.0f; // acceleration level to
														// determin motion

	// private static final float LOW_PASS_GAIN2 = 0.65f; // added on May 12

	private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();
	private ArrayList<SteptimeListener> mSteptimeListeners = new ArrayList<SteptimeListener>();

	private AccuService mService;
	private ArrayQueue mArrayQueue;

	private float mSensitivity;
	private float mUpThresh;
	private float mDownThresh;

	private int mWalkCount;
	private long mCurTime;
	private long mPreTime;;
	private long mDelTime;
	private long mSumDT;
	private long[] mDT;

	// private float mFilterd;
	private float[] mOldAcc;
	private float[] mFilteredAcc;
	private float[] mPastAcc;

	// private float mFil_vAcc; // added on May 12
	// private float mPastvAcc; // added on May 12

	private boolean mIsUpDown;
	private boolean mIsUp;
	private boolean mIsWalk; // check for walking check
	private boolean mIsContWalk; // check for continuous walking

	private boolean mIsTime; // check for step time [added july 7 13]

	private int mConsecutiveSteps;
	private long mWalkTimeWinMin;
	private long mWalkTimeWinMax;

	public static int mFlagSavePowerMode;
	public static final int FLAG_MODE_STEP = 0;
	public static final int FLAG_MODE_WAKE = 1;
	public static final int FLAG_MODE_SLEEP = 2;

	public long mCurStepCountTime = 0;
	public long mSumDT_Estimation;
	public int mWalkCount_Estimation;

	public StepDetectorWithArray(AccuService service, ArrayQueue arrayQueue) {
		mArrayQueue = arrayQueue;
		mService = service;
		mSensitivity = 0.1f;
		mPreTime = SystemClock.elapsedRealtime();
		mWalkCount = 0;
		mIsWalk = false;

		mOldAcc = new float[NUM_AXES];
		mFilteredAcc = new float[NUM_AXES];
		mPastAcc = new float[NUM_AXES];
		mDT = new long[12]; // set it as max 12

		mFlagSavePowerMode = FLAG_MODE_STEP;
	}

	public void setSensitivity(float sensitivity) {
		mSensitivity = sensitivity;
	}

	public void setConsecutiveSteps(int steps) {
		mConsecutiveSteps = (int) (steps / 2);
		mWalkTimeWinMin = STEP_TIME_MIN * mConsecutiveSteps;
		mWalkTimeWinMax = STEP_TIME_MAX * mConsecutiveSteps;
	}

	public void addStepListener(StepListener sl) {
		mStepListeners.add(sl);
	}

	public void addSteptimeListener(SteptimeListener sl) {
		mSteptimeListeners.add(sl);
	}

	public void setStepMode() {
		mFlagSavePowerMode = FLAG_MODE_STEP;
		// Log.i("OnSensorChangedA", "Set Step Mode");
	}

	public void setWakeMode() {
		mFlagSavePowerMode = FLAG_MODE_WAKE;
		// Log.i("OnSensorChangedA", "Set Wake Mode");
	}

	public void setSleepMode() {
		mFlagSavePowerMode = FLAG_MODE_SLEEP;
		// Log.i("OnSensorChangedA", "Set Sleep Mode");
	}

	// public void onSensorChanged(int sensor, float[] values) {
	public void onSensorChanged(SensorEvent event) {
		// Log.i("OnSensorChangedA", "FlagPowerMode: "
		// + ((Integer) mFlagSavePowerMode).toString());
		Sensor sensor = event.sensor;
		synchronized (this) {
			if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				switch (mFlagSavePowerMode) {
				case FLAG_MODE_STEP:

					if (isStep(event.values)) {
						if (mIsContWalk == true) {
							for (StepListener stepListener : mStepListeners) {
								stepListener.onStep13(); // mSumDT*2
							}
							mSteptimeListeners.get(0).onSteptime13(mSumDT * 2); 
						} else {
							for (StepListener stepListener : mStepListeners) {
								stepListener.onStep();
							}
							mSteptimeListeners.get(0).onSteptime(mDelTime); 
						}
					} else { // added on july 7 13
						if (mIsTime == true) {
							mSteptimeListeners.get(0).onSteptime(mDelTime); 
							mIsTime = false;
						}
					}

					break;

				case FLAG_MODE_WAKE:
					if (AccuService.mFlagScreenOn) {
						mFlagSavePowerMode = FLAG_MODE_STEP;
					} else {
						if (isMotion(event.values)) {
							//Log.i("OnSensorChangedA", "become STEP_MODE");
							mFlagSavePowerMode = FLAG_MODE_STEP;
							mService.setTimerToCheckStep();
							
							
							
							// if (mService.mFlagPowerMode <=
							// AccuService.FLAG_MODE_POWER_BALANCED) {
							// mService.setTimerToCheckStep();
							// } else {
							// mService.setTimerToCheckStepEstimation(1);
							// }

						} else {
							//Log.i("OnSensorChangedA", "become SLEEP_MODE");
							mFlagSavePowerMode = FLAG_MODE_SLEEP;
							mService.unregisterDetector();
							mService.releaseWakeLock();
						}
					}
					break;

				case FLAG_MODE_SLEEP:
//					mService.unregisterDetector();
//					mService.releaseWakeLock();
					break;

				default:
//					mFlagSavePowerMode = FLAG_MODE_SLEEP;
//					mService.unregisterDetector();
//					mService.releaseWakeLock();
					break;
				}
			}
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	public boolean isStep(float[] acc) {
		float vAcc;
		int i, j, k;

		// step detection threshold
		mUpThresh = mSensitivity * SensorManager.STANDARD_GRAVITY;
		mDownThresh = -mUpThresh;

		// low pass filter
		for (k = 0; k < NUM_AXES; k++) {
			mFilteredAcc[k] = LOW_PASS_GAIN * acc[k] + (1.0f - LOW_PASS_GAIN)
					* mPastAcc[k];
			mPastAcc[k] = mFilteredAcc[k];
		}

		// selection of vertical axis
		i = (Math.abs(mFilteredAcc[1]) > Math.abs(mFilteredAcc[0])) ? 1 : 0;
		if (i == 1) {
			j = (Math.abs(mFilteredAcc[2]) > Math.abs(mFilteredAcc[1])) ? 2 : 1;
		} else {
			j = (Math.abs(mFilteredAcc[2]) > Math.abs(mFilteredAcc[0])) ? 2 : 0;
		}

		vAcc = acc[j] - mFilteredAcc[j];

		mArrayQueue.insert(SystemClock.elapsedRealtime() / 1000f, acc,
				(float) mService.mSteps, (float) mService.mSteptime, (float) j); // cause
																					// service
																					// killed
																					// when
																					// activity
																					// loose
																					// focus

		// detection of up and down
		mIsUpDown = false;

		if (vAcc > mUpThresh) {
			mIsUp = true;
		}

		if ((vAcc < mDownThresh) && mIsUp) {
			mIsUp = false;
			mIsUpDown = true;
			mCurTime = SystemClock.elapsedRealtime();
			mDelTime = mCurTime - mPreTime;
			mPreTime = mCurTime;
			mCurStepCountTime = mCurTime;
		}

		// May 12
		// mFil_vAcc = LOW_PASS_GAIN2*vAcc + (1.0f - LOW_PASS_GAIN2)*mPastvAcc;
		// mPastvAcc = mFil_vAcc;
		//
		// // detection of up and down
		// mIsUpDown = false;
		//
		// if (mFil_vAcc > mUpThresh) {
		// mIsUp = true;
		// }
		//
		// if ((mFil_vAcc < mDownThresh) && mIsUp) {
		// mIsUp = false;
		// mIsUpDown = true;
		// mCurTime = SystemClock.elapsedRealtime();
		// mDelTime = mCurTime - mPreTime;
		// mPreTime = mCurTime;
		// }
		// May 12

		if (mIsUpDown) {
			for (k = 0; k < mConsecutiveSteps - 1; k++) {
				mDT[k] = mDT[k + 1];
			}
			mDT[mConsecutiveSteps - 1] = mDelTime;

			mSumDT = 0;
			for (k = 0; k < mConsecutiveSteps; k++) {
				mSumDT = mSumDT + mDT[k];
			}

			mIsContWalk = false;
			if ((mSumDT > mWalkTimeWinMin) && (mSumDT < mWalkTimeWinMax)) {
				mWalkCount++;
				mWalkCount_Estimation++; // Estimation purpose. Reset to zero at
											// the estimation timer
				mSumDT_Estimation = mSumDT_Estimation + mDelTime;
				// Log.i("Step count", "step count        " + mWalkCount);
				if (mWalkCount == mConsecutiveSteps) {
					mIsContWalk = true;
					mIsWalk = true;
					// Log.i("Step count", "consecutive    " + mWalkCount);
				} else if (mWalkCount > mConsecutiveSteps) {
					mIsWalk = true;
					// Log.i("Step count", "single step    " + mWalkCount);
				} else {
					mIsWalk = false;
					// Log.i("Step count", "no increme     " + mWalkCount);
				}
				if (mDelTime < SINGLE_STEP_TIME_MIN
						|| mDelTime > SINGLE_STEP_TIME_MAX) {
					mIsWalk = false;
					mWalkCount--; // add on May 9
					mWalkCount_Estimation--;
					mSumDT_Estimation = mSumDT_Estimation - mDelTime;
					mIsTime = true; // add on July 7 13
				}
			} else {
				mIsWalk = false;
				// Restart time window
				if (mWalkCount > mConsecutiveSteps) {
					// mDT[mConsecutiveSteps - 1] = mWalkTimeWinMax; //modified
					// 11/4/2012
					mDT[mConsecutiveSteps - 1] = mDelTime;
				}
				mWalkCount = 0;
				mWalkCount_Estimation = 0;
				mSumDT_Estimation = 0;
			}
		}

		if (mIsUpDown && mIsWalk) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isMotion(float[] acc) {
		boolean b;

		if ((Math.abs(mOldAcc[0] - acc[0]) > MOTION_THRESHOLD)
				|| (Math.abs(mOldAcc[1] - acc[1]) > MOTION_THRESHOLD)
				|| (Math.abs(mOldAcc[2] - acc[2]) > MOTION_THRESHOLD)) {
			b = true;
		} else {
			b = false;
		}

		mOldAcc[0] = acc[0];
		mOldAcc[1] = acc[1];
		mOldAcc[2] = acc[2];

		// Log.i("OnSensorChangedA", "Is Motion: "
		// + ((Integer) mFlagSavePowerMode).toString());

		return b;
	}

	public void callStepEstimated(int steps, long millisecond) {
		for (StepListener stepListener : mStepListeners) {
			stepListener.onStepEstimated(steps);
		}
		mSteptimeListeners.get(0).onSteptimeEstimated(millisecond);
	}

}