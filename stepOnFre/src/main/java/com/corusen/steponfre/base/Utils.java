/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */

package com.corusen.steponfre.base;

import android.text.format.Time;

import java.util.Calendar;

public class Utils {

	private static Utils instance       = null;
	public static final float INCH2CM   = 2.54f;
	public static final float MILE2KM   = 1.609344f;
	//public static final float MILE2M    = 1609.344f;
	//public static final float MILE2YARD = 1760f;
	//public static final float MILE2FEET = 5280f;
	public static final float MILE2NCH  = 63360f;
	public static final float KG2LB     = 2.20462f;
	public static final float LB2KG     = 0.45359237f;
	public static final float INCH2MILE = 1.57828e-5f;
	public static final float CM2INCH   = 0.393701f;
	public static final float KCAL2KJ   = 4.184f;
	public static final float IMPERIAL_RUNNING_FACTOR = 0.75f;
	public static final float IMPERIAL_WALKING_FACTOR = 0.53f;


    private Utils() {
    }
     
    public static Utils getInstance() {
        if (instance == null) { instance = new Utils(); }
        return instance;
    }
    
	// public void setService(Service service) {
	// // mService = service;
	// }

	public static long currentTimeInMillis() {
		return Calendar.getInstance().getTimeInMillis();
	}

	public static int currentDate() {
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.DATE);
	}

	public static String getHoursMinutesString(int second) {
		int m = (second / 60) % 60;
		int h = (second / (60 * 60)) % 24;
		return String.format("%d:%02d", h,m);
	}

	public static String getHoursMinutesSecondsString(int second) {
		int hr, min, sec;
		String format = String.format("%%0%dd", 2);
		min = (second % 3600) / 60;
		hr  = second / 3600;
		sec = second - hr*3600 - min*60;
		String minutes  = String.format(format, min );
		String hours    = String.format(format, hr);
		String seconds  = String.format(format, sec);
		return (hours + ":" + minutes + ":" + seconds);
	}

	public static boolean isSameDate(Calendar c1, Calendar c2) {
		boolean val = false;
		if (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
			if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
				if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
					val = true;
				}
			}
		}
		return val;
	}
 
}
