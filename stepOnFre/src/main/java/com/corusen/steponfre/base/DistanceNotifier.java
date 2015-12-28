/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */

package com.corusen.steponfre.base;

public class DistanceNotifier implements StepListener {

	public interface Listener {
		void valueChanged(float lapdistance, float distance);
		void passValue();
	}

	private PedometerSettings mSettings;
	private Listener mListener;
	private float mLapdistance = 0;
	private float mDistance = 0;
	private int mConsecutiveSteps;
	private float mIncrementalLength;

	public DistanceNotifier(Listener listener, PedometerSettings settings) {
		mListener = listener;
		mSettings = settings;
		reloadSettings();
	}

	public void setDistance(float lapdistance, float distance) {
		mLapdistance = lapdistance;
		mDistance = distance;
		notifyListener();
	}

	public void setConsecutiveSteps(int consecutiveSteps) {
		mConsecutiveSteps = consecutiveSteps;
	}

	public void reloadSettings() {

		boolean isMetric= mSettings.isMetric();
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
			mIncrementalLength = stepLength * Utils.INCH2MILE;
			break;
		case AccuService.FLAG_MODE_RUN:
			mIncrementalLength = runLength * Utils.INCH2MILE;
			break;
		case AccuService.FLAG_MODE_HIKE:
			mIncrementalLength = stepLength * Utils.INCH2MILE;
			break;
		case AccuService.FLAG_MODE_STAIRWAY:
			mIncrementalLength = stepLength * Utils.INCH2MILE;
			break;
		}
		
		notifyListener();
	}

	public void onStep() {
		mLapdistance += mIncrementalLength;
		mDistance += mIncrementalLength;
		notifyListener();
	}

	public void onStep13() {
		mLapdistance += mIncrementalLength * (float) mConsecutiveSteps;
		mDistance += mIncrementalLength * (float) mConsecutiveSteps;
		notifyListener();
	}
	
	public void onStepEstimated(int steps) {
		mLapdistance += mIncrementalLength * (float) steps;
		mDistance += mIncrementalLength * (float) steps;
		notifyListener();
	}

	public void onStep100(int steps) {
		mDistance = mIncrementalLength * (float) steps; 
		notifyListener();
	}
	
	public float onStep100ForHistory(int steps) {
		return mIncrementalLength * (float) steps; 
	}

	private void notifyListener() {
		mListener.valueChanged(mLapdistance, mDistance);
	}

	public void passValue() {
	}

}
