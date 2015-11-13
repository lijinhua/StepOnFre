/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */

package com.corusen.steponfre.base;

import android.text.format.Time;

public class Utils {

    private static Utils instance = null;
	public static final float INCH2CM = 2.54f;
	public static final float MILE2KM = 1.609344f;
	public static final float MILE2M = 1609.344f;
	public static final float MILE2YARD = 1760f;
	public static final float MILE2FEET = 5280f;
	public static final float MILE2NCH = 63360f;
	public static final float KG2LB = 2.2f;
	public static final float LB2KG = 0.45359237f;
	public static final float INCH2MILE = 1.57828e-5f;
	public static final float CM2INCH = 0.393701f;


    private Utils() {
    }
     
    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }
    
	// public void setService(Service service) {
	// // mService = service;
	// }
    
    public static long currentTimeInMillis() {
        Time time = new Time();
        time.setToNow();
        return time.toMillis(false);
    }
    
    public static int currentDate() {
    	Time time = new Time();
    	time.setToNow();
    	return time.monthDay;
    }
    
	public static String getSecondString(int second) {
		String format = String.format("%%0%dd", 2);
		String string = String.format(format, second / 60);
		return string;
	}
	
    
	public static String getHoursMinutesString(int second) {
		String format = String.format("%%0%dd", 2);
		String minutes = String.format(format, (second % 3600) / 60);
		String hours = String.format(format, second / 3600);
		String string = hours + ":" + minutes; // + ":" + seconds;
		return string;
	}
	
	public static String getHoursMinutesSecondsString(int second) {
		int hr, min, sec;
		String format = String.format("%%0%dd", 2);
		min = (second % 3600) / 60;
		hr = second / 3600;
		sec = second - hr*3600 - min*60;
		String minutes = String.format(format, min );
		String hours = String.format(format, hr);
		String seconds = String.format(format, sec);
		String string = hours + ":" + minutes + ":" + seconds;
		return string;
	}
	
	public static String getHoursMinutesStringWithUnit(int second) {
		String format = String.format("%%0%dd", 2);
		String minutes = String.format(format, (second % 3600) / 60);
		String hours = String.format(format, second / 3600);
		String string = hours + "h" + minutes+"m"; // + ":" + seconds;
		return string;
	}
 
}
