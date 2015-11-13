/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */
package com.corusen.steponfre.base;

import android.content.SharedPreferences;

public class PedometerStates {

	SharedPreferences mStates;

	public PedometerStates(SharedPreferences states) {
		mStates = states;
	}

	// public void saveEndingTimeStamp() {
	// SharedPreferences.Editor editor = mStates.edit();
	// editor.putInt("last_date", Utils.currentDate());
	// editor.commit();
	// }
	//
	// public boolean isNewDate() {
	// return mStates.getInt("last_date", 0) != Utils.currentDate();
	// }

	public void saveStates(int lapnumber, int lapsteps, float lapdistance,
			float lapcalories, long lapsteptime, int steps, float distance,
			float calories, long steptime) {
		SharedPreferences.Editor editor = mStates.edit();
		editor.putInt("lapnumber", lapnumber);
		editor.putInt("lapsteps", lapsteps);
		editor.putFloat("lapdistance", lapdistance);
		editor.putFloat("lapcalories", lapcalories);
		editor.putLong("lapsteptime", lapsteptime);
		editor.putInt("steps", steps);
		editor.putFloat("distance", distance);
		editor.putFloat("calories", calories);
		editor.putLong("steptime", steptime);
		// editor.putString("city", city);
		// editor.putFloat("temperature", temperature);
		editor.commit();
	}

	// public void saveStatesWithTemp(int lapnumber, int lapsteps, float lapdistance,
	// float lapcalories, long lapsteptime,
	// int steps, float distance, float calories, long steptime, String city,
	// float temperature) {
	// SharedPreferences.Editor editor = mStates.edit();
	// editor.putInt("lapnumber", lapnumber);
	// editor.putInt("lapsteps", lapsteps);
	// editor.putFloat("lapdistance", lapdistance);
	// editor.putFloat("lapcalories", lapcalories);
	// editor.putLong("lapsteptime", lapsteptime);
	// editor.putInt("steps", steps);
	// editor.putFloat("distance", distance);
	// editor.putFloat("calories", calories);
	// editor.putLong("steptime", steptime);
	// editor.putString("city", city);
	// editor.putFloat("temperature", temperature);
	// editor.commit();
	// }

	public int getLapNumberState() {
		return mStates.getInt("lapnumber", 1);
	}

	public int getLapStepsState() {
		return mStates.getInt("lapsteps", 0);
	}

	public float getLapDistanceState() {
		return mStates.getFloat("lapdistance", 0);
	}

	public float getLapCaloriesState() {
		return mStates.getFloat("lapcalories", 0);
	}

	public long getLapStepTimeState() {
		return mStates.getLong("lapsteptime", 0);
	}

	public int getStepsState() {
		return mStates.getInt("steps", 0);
	}

	public float getDistanceState() {
		return mStates.getFloat("distance", 0);
	}

	public float getCaloriesState() {
		return mStates.getFloat("calories", 0);
	}

	public long getStepTimeState() {
		return mStates.getLong("steptime", 0);
	}

	public String getCityState() {
		return mStates.getString("city", "city");
	}

	public float getTemperatureState() {
		return mStates.getFloat("temperature", 0f);
	}

}
