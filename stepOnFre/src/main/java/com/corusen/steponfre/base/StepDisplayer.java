/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */

package com.corusen.steponfre.base;

import java.util.ArrayList;

public class StepDisplayer implements StepListener {

	private int mLapsteps = 0;
	private int mSteps = 0;
	private int mConsecutiveSteps;

	public StepDisplayer() {
		notifyListener();
	}

	public void setSteps(int lapsteps, int steps) {
		mLapsteps = lapsteps;
		mSteps = steps;
		notifyListener();
	}
	
	public void setConsecutiveSteps(int consecutiveSteps) {
		mConsecutiveSteps = consecutiveSteps;
	}

	public void onStep() {
		mLapsteps++;
		mSteps++;
		notifyListener();
	}

	public void onStep13() {
		mLapsteps = mLapsteps + mConsecutiveSteps;
		mSteps = mSteps + mConsecutiveSteps;
		notifyListener();
	}
	
	public void onStepEstimated(int steps) {
		mLapsteps = mLapsteps + steps;
		mSteps = mSteps + steps;
		notifyListener();
	}
	
	public void onStep100(int steps) {
		mSteps = steps;
		notifyListener();
	}
	
	public void reloadSettings() {
		notifyListener();
	}

	public void passValue() {
	}

	// -----------------------------------------------------
	// Listener

	public interface Listener {
		public void stepsChanged(int lapsteps, int steps);
		public void passValue();
	}

	private ArrayList<Listener> mListeners = new ArrayList<Listener>();

	public void addListener(Listener l) {
		mListeners.add(l);
	}

	public void notifyListener() {
		for (Listener listener : mListeners) {
			listener.stepsChanged(mLapsteps, mSteps);
		}
	}


}
