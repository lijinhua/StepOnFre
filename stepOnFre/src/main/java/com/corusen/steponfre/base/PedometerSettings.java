/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */
package com.corusen.steponfre.base;

import java.util.Calendar;
import java.util.Date;

import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.text.format.Time;

public class PedometerSettings {

	SharedPreferences mSettings;

//	public static int M_NONE = 1;
//	public static int M_PACE = 2;
//	public static int M_SPEED = 3;

	public PedometerSettings(SharedPreferences settings) {
		mSettings = settings;
	}

	public boolean isMetric() {
		return mSettings.getString("units", "imperial").equals("metric");
	}

	public void setUnits(String type) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("units", type);
		editor.commit();
	}

	public int getPowerUsageMode() {
		try {
			return Integer.valueOf(mSettings.getString("power_usage_type", "11"));
			// 1: power efficient mode 2: balanced mode
		} catch (NumberFormatException e) {
			return 11;
		}
	}

	public void setPowerUsageMode(int mode) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("power_usage_type", ((Integer) mode).toString());
		editor.commit();
	}

	public float getSensitivity() {
		try {
			return Float.valueOf(mSettings.getString("sensitivity", "0.1"));
		} catch (NumberFormatException e) {
			return 0.1f;
		}
	}

	public float getStepLength() {
		try {
			return Float.valueOf(mSettings.getString("step_length", "70").trim());
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	void setStepLength(float value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("step_length", String.valueOf(value));
		editor.commit();
	}

	public float getRunLength() {
		try {
			return Float.valueOf(mSettings.getString("run_length", "105").trim());
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	void setRunLength(float value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("run_length", String.valueOf(value));
		editor.commit();
	}

	public float getBodyWeight() {
		try {
			return Float.valueOf(mSettings.getString("body_weight", "70").trim());
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	void setBodyWeight(float value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("body_weight", String.valueOf(value));
		editor.commit();
	}

public float getBodyHeight() {
		try {
			return Float.valueOf(mSettings.getString("body_height", "175").trim());
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	void setBodyHeight(float value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("body_height", String.valueOf(value));
		editor.commit();
	}

	public int getBirthYear() {
		try {
			return Integer.valueOf(mSettings.getString("birth_year", "1980"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	void setBirthYear(int value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("birth_year", String.valueOf(value));
		editor.commit();
	}

	public int getBirthMonth() {
		try {
			return Integer.valueOf(mSettings.getString("birth_month", "1"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	void setBirthMonth(int value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("birth_month", String.valueOf(value));
		editor.commit();
	}

	public int getBirthDay() {
		try {
			return Integer.valueOf(mSettings.getString("birth_day", "1"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	void setBirthDay(int value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("birth_day", String.valueOf(value));
		editor.commit();
	}

	public Date getBirthDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(getBirthYear(), getBirthMonth(), getBirthDay());
		Date date = cal.getTime();
		return date;
	}

	public int getGoalSteps() {
		try {
			return Integer.valueOf(mSettings.getString("goal_steps", "10000"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public int getConsecutiveSteps() {
		try {
			return Integer.valueOf(mSettings.getString("consecutive", "10"));
		} catch (NumberFormatException e) {
			return 10;
		}
	}

	public int getWidgetSkinType() {
		try {
			return Integer.valueOf(mSettings.getString("widget_skin_type", "0"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

//	public int getScreenSkinType() {
//		try {
//			return Integer.valueOf(mSettings.getString("screen_skin_type", "0"));
//		} catch (NumberFormatException e) {
//			return 0;
//		}
//	}

	public int getLocaleType() {
		try {
			return Integer.valueOf(mSettings.getString("locale_type", "0"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

//	public boolean isRunning() {
//		String string = mSettings.getString("exercise_type", "walking");
//		return (string != null) && string.equals("running");
//	}

	public void setExerciseType(int exercise) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("exercise_type", String.valueOf(exercise));
		editor.commit();
	}

	public int getExerciseType() {
		try {
			return Integer.valueOf(mSettings.getString("exercise_type", "0"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public boolean isGenderMale() {
		return mSettings.getString("gender", "male").equals("male");
	}

	public int getGender() {
		int gender = 0; // unknown
		if (isGenderMale()) {
			gender = 1; // male
		} else {
			gender = 2;
		}
		return gender;
	}

	// public boolean isRunning() {
	// return mSettings.getString("exercise_type", "walking")
	// .equals("running");
	// }
	//
	// public void setExerciseType(String type) {
	// SharedPreferences.Editor editor = mSettings.edit();
	// editor.putString("exercise_type", type);
	// editor.commit();
	// }

	public void setGender(String type) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("gender", type);
		editor.commit();
	}

	public void saveServiceRunningWithTimestamp(boolean running) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", running);
		editor.putLong("last_seen", Utils.currentTimeInMillis());
		editor.commit();
	}

	public void saveServiceRunningWithNullTimestamp(boolean running) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", running);
		editor.putLong("last_seen", 0);
		editor.commit();
	}

	public void saveEndingTimeStamp() {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putInt("last_date", Utils.currentDate());
		editor.commit();
	}

	public void clearServiceRunning() {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", false);
		editor.putLong("last_seen", 0);
		editor.commit();
	}

	public boolean isServiceRunning() {
		return mSettings.getBoolean("service_running", false);
	}

	public boolean isPause() {
		return mSettings.getBoolean("pause_status", false);
	}

	public void savePauseStatus(boolean pause) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("pause_status", pause);
		editor.commit();
	}

	public boolean isNewStart() {
		// activity last paused more than 10 minutes ago
		return mSettings.getLong("last_seen", 0) < Utils.currentTimeInMillis() - 1000 * 60 * 10;
	}

	public boolean isNewDate() {
		return mSettings.getInt("last_date", 0) != Utils.currentDate();
	}

	// public int getDateFormat() {
	// return Integer.valueOf(mSettings.getString("date_type", "0"));
	// }

	public int getWeekFormat() {
		return Integer.valueOf(mSettings.getString("week_type", "0"));
	}

	public boolean isSpeedAverage() {
		return Integer.valueOf(mSettings.getString("speed_type", "0")) == 0;
	}

	public boolean isActiveHour() {
		return mSettings.getBoolean("activehour", false);
	}

	public boolean isInActiveHour() {
		int[] itime = new int[2];
		int releasetime, pausetime, currenttime;
		itime = getAccupedoReleaseTime();
		releasetime = itime[0] * 60 + itime[1];
		itime = getAccupedoPauseTime();
		pausetime = itime[0] * 60 + itime[1];
		Time time = new Time();
		time.setToNow();
		currenttime = time.hour * 60 + time.minute;
		if ((releasetime < currenttime) && (currenttime < pausetime)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAutoPauseDuringCharging() {
		return mSettings.getBoolean("autopause_charging", false);
	}

	public int[] getAccupedoReleaseTime() {
		int[] itime = new int[2];
		String stime = null;

		stime = mSettings.getString("daily_start", "07:00");

		String[] timeParts = stime.split(":");
		itime[0] = Integer.parseInt(timeParts[0]);
		itime[1] = Integer.parseInt(timeParts[1]);
		return itime;
	}

	public int[] getAccupedoPauseTime() {
		int[] itime = new int[2];
		String stime = null;

		stime = mSettings.getString("daily_end", "21:00");

		String[] timeParts = stime.split(":");
		itime[0] = Integer.parseInt(timeParts[0]);
		itime[1] = Integer.parseInt(timeParts[1]);
		return itime;
	}

	public boolean isNewInstallation() {
		return mSettings.getBoolean("new_installation_status", true);
	}

	public void setNewInstallationStatusFalse() {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("new_installation_status", false);
		editor.commit();
	}

//	public boolean isNewInstallation117() {
//		return mSettings.getBoolean("new_installation_version_117", true);
//	}
//
//	public void setNewInstallationStatusFalse117() {
//		SharedPreferences.Editor editor = mSettings.edit();
//		editor.putBoolean("new_installation_version_117", false);
//		editor.commit();
//	}
//
//	public boolean isHistoryImported117() {
//		return mSettings.getBoolean("history_imported_117", false);
//	}
//
//	public void setHistoryImportedStatusTrue117() {
//		SharedPreferences.Editor editor = mSettings.edit();
//		editor.putBoolean("history_imported_117", true);
//		editor.commit();
//	}

	public int getScreenOperationLevel() {
		return (Integer.valueOf(mSettings.getString("operation_level", "0")));
	}

	// public boolean isLowBatteryCheck() {
	// return mSettings.getBoolean("lowbattery_check", false);
	// }
	//
	// public int getGoalLowBatteryLevel() {
	// return Integer.valueOf(mSettings.getString("lowbattery_level", "20"));
	// }

	public boolean isGoalRewardNotification() {
		return mSettings.getBoolean("goal_reward_notification", false);
	}

	public boolean isGoalAchievementNotification() {
		return mSettings.getBoolean("goal_achievement_notification", false);
	}

//	public boolean isGoalAchievementSound() {
//		return mSettings.getBoolean("goal_achievement_sound", false);
//	}

	public int getHistoryEditOption() {
		return mSettings.getInt("history_edit_option", 0);
	}

	public void setHistoryEditOption(int option) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putInt("history_edit_option", option);
		editor.commit();
	}

	public boolean isAchievementNotificationFiredToday() {
		return mSettings.getBoolean("achievement_notification_fired", false);
	}

	public void setAchievementNotificationFiredToday(boolean noti) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("achievement_notification_fired", noti);
		editor.commit();
	}

	public boolean isGoalAchievement() {
		return mSettings.getBoolean("goal_achievement", false);
	}

	public void setGoalAchievement(boolean noti) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("goal_achievement", noti);
		editor.commit();
	}

//	public boolean isGoalRewardFired() {
//		return mSettings.getBoolean("goal_reward_fired", false);
//	}
//
//	public void setGoalRewardFired(boolean noti) {
//		SharedPreferences.Editor editor = mSettings.edit();
//		editor.putBoolean("goal_reward_fired", noti);
//		editor.commit();
//	}

	public boolean isOpenHistoryUpdateVersion401() {
		return mSettings.getBoolean("history_update_version_401", false);
	}

	public void setOpenHistoryUpdateVersion401(boolean answer) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("history_update_version_401", answer);
		editor.commit();
	}

//	void setMyfitnesspalAuthoCode(String value) {
//		SharedPreferences.Editor editor = mSettings.edit();
//		editor.putString("myfitnesspal_autho_code", value);
//		editor.commit();
//	}
//
//	void setMyfitnesspalAccessToken(String value) {
//		SharedPreferences.Editor editor = mSettings.edit();
//		editor.putString("myfitnesspal_access_token", value);
//		editor.commit();
//	}
//
//	void setMyfitnesspalRefreshToken(String value) {
//		SharedPreferences.Editor editor = mSettings.edit();
//		editor.putString("myfitnesspal_refresh_token", value);
//		editor.commit();
//	}
//
//	public String getMyfitnesspalAuthoCode() {
//		return mSettings.getString("myfitnesspal_autho_code", null);
//	}
//
//	public String getMyfitnesspalAccessToken() {
//		return mSettings.getString("myfitnesspal_access_token", null);
//	}
//
//	public String getMyfitnesspalRefreshToken() {
//		return mSettings.getString("myfitnesspal_refresh_token", null);
//	}

	public boolean isLocked() {
		return mSettings.getBoolean("lock_status", true);
	}

	public void setLockStatus(boolean lock) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("lock_status", lock);
		editor.commit();
	}

	public int getSaveOption() {
		return mSettings.getInt("save_option", 1);
	}

	public void setSaveOption(int option) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putInt("save_option", option);
		editor.commit();
	}

//	public int getOperationMode() {
//		return mSettings.getInt("operation_mode", 0); // stop no save mode
//	}

	public void setOperationMode(int option) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putInt("operation_mode", option);
		editor.commit();
	}

	public boolean isAutomaticBackupToSDcard() {
		return mSettings.getBoolean("automtaic_backup", true);
	}

	public int getAdsClickCount() {
		try {
			return Integer.valueOf(mSettings.getString("ads_click_count", "0"));
		} catch (NumberFormatException e) {
			return 101;
		}
	}

	public void setAdsClickCount(int value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("ads_click_count", String.valueOf(value));
		editor.apply(); //commit();
	}

	public void setPedometerBackButton(boolean value) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("pedometer_back_button", value);
		editor.apply(); //commit();
	}

	public void setResetToday(Calendar now) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString("reset_today", DateFormat.format("yyyy-MM-dd", now).toString());
		editor.apply(); //commit();
	}

	public boolean isResetToday(Calendar now) {
		String string = mSettings.getString("reset_today", "2000-01-01");
		return string.equals(DateFormat.format("yyyy-MM-dd", now).toString());
	}


}
