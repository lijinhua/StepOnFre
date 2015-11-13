/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */

package com.corusen.steponfre.base;

public class SpeedNotifier implements PaceNotifier.Listener {

	public interface Listener {
		public void valueChanged(float value);
		public void passValue();
	}

	private PedometerSettings mSettings;
	private Listener mListener;
	private float mSpeed = 0;
	private float mIncrementalFactor = 0;

	public SpeedNotifier(Listener listener, PedometerSettings settings) {
		mListener = listener;
		mSettings = settings;
		reloadSettings();
	}

	public void setSpeed(float speed) {
		mSpeed = speed;
		notifyListener();
	}

	public void setConsecutiveSteps(int steps) {
		// mConsecutiveSteps = steps;
	}

	public void reloadSettings() {
		boolean isMetric = mSettings.isMetric();
		int exercise = mSettings.getExerciseType();
		float stepLength, runLength;
		if (isMetric) {
			stepLength = mSettings.getStepLength() * Utils.CM2INCH;
			runLength = mSettings.getRunLength() * Utils.CM2INCH;
		} else {
			stepLength = mSettings.getStepLength();
			runLength = mSettings.getRunLength();
		}
		
		switch (exercise) {
		case AccuService.FLAG_MODE_WALK:
			mIncrementalFactor = stepLength * Utils.INCH2MILE * 60f;
			break;
		case AccuService.FLAG_MODE_RUN:
			mIncrementalFactor = runLength * Utils.INCH2MILE * 60f;
			break;
		case AccuService.FLAG_MODE_HIKE:
			mIncrementalFactor = stepLength * Utils.INCH2MILE * 60f;
			break;
		case AccuService.FLAG_MODE_STAIRWAY:
			mIncrementalFactor = stepLength * Utils.INCH2MILE * 60f;
			break;
		}
		
		notifyListener();
	}

	private void notifyListener() {
		mListener.valueChanged(mSpeed);
	}

	public void paceChanged(int value) {
		mSpeed = value * mIncrementalFactor;
		notifyListener();
	}

	public void passValue() {
	}

}
