/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */

package com.corusen.steponfre.base;

public class CaloriesNotifier implements StepListener {

	public interface Listener {
		public void valueChanged(float lapcalories, float calories);

		public void passValue();
	}

	private Listener mListener;

	private static float IMPERIAL_RUNNING_FACTOR = 0.75f;
	private static float IMPERIAL_WALKING_FACTOR = 0.53f;

	private PedometerSettings mSettings;
	private float mLapcalories = 0;
	private float mCalories = 0;
	private float mIncrementalCalories = 0;
	private int mConsecutiveSteps;

	public CaloriesNotifier(Listener listener, PedometerSettings settings) {
		mListener = listener;
		mSettings = settings;
		reloadSettings();
	}

	public void setCalories(float lapcalories, float calories) {
		mLapcalories = lapcalories;
		mCalories = calories;
		notifyListener();
	}

	public void setConsecutiveSteps(int consecutiveSteps) {
		mConsecutiveSteps = consecutiveSteps;
	}

	public void reloadSettings() {
		boolean isMetric = mSettings.isMetric();
		int exercise = mSettings.getExerciseType();
		float stepLength, runLength, bodyWeight;
		if (isMetric) {
			stepLength = mSettings.getStepLength() * Utils.CM2INCH;
			runLength = mSettings.getRunLength() * Utils.CM2INCH;
			bodyWeight = mSettings.getBodyWeight() * Utils.KG2LB;
		} else {
			stepLength = mSettings.getStepLength();
			runLength = mSettings.getRunLength();
			bodyWeight = mSettings.getBodyWeight();
		}

		switch (exercise) {
		case AccuService.FLAG_MODE_WALK:
			mIncrementalCalories = bodyWeight * IMPERIAL_WALKING_FACTOR
					* stepLength * Utils.INCH2MILE;
			break;
		case AccuService.FLAG_MODE_RUN:
			mIncrementalCalories = bodyWeight * IMPERIAL_RUNNING_FACTOR
					* runLength * Utils.INCH2MILE;
			break;
		case AccuService.FLAG_MODE_HIKE:
			mIncrementalCalories = bodyWeight * IMPERIAL_WALKING_FACTOR
					* stepLength * Utils.INCH2MILE;
			break;
		case AccuService.FLAG_MODE_STAIRWAY:
			mIncrementalCalories = bodyWeight * IMPERIAL_WALKING_FACTOR
					* stepLength * Utils.INCH2MILE;
			break;
		}
		notifyListener();
	}

	public void onStep() {
		mLapcalories += mIncrementalCalories;
		mCalories += mIncrementalCalories;
		notifyListener();
	}

	public void onStep13() {
		mLapcalories += mIncrementalCalories * (float) mConsecutiveSteps;
		mCalories += mIncrementalCalories * (float) mConsecutiveSteps;
		notifyListener();
	}

	public void onStepEstimated(int steps) {
		mLapcalories += mIncrementalCalories * (float) steps;
		mCalories += mIncrementalCalories * (float) steps;
		notifyListener();
	}

	public void onStep100(int steps) {
		mCalories = mIncrementalCalories * (float) steps;
		notifyListener();
	}

	public float onStep100ForHistory(int steps) {
		return mIncrementalCalories * (float) steps;
	}

	private void notifyListener() {
		mListener.valueChanged(mLapcalories, mCalories);
	}

	public void passValue() {
	}

}
