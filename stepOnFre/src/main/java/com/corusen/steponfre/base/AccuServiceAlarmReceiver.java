/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *  Set "singleInstance" property for Activity in Manifest xml. Allows "back" and "home" buttons
 *  http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
 */
package com.corusen.steponfre.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AccuServiceAlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.i("AccuServiceAlarmReceiver", "AccuServiceAlarmReceiver");
		Intent startIntent = new Intent(context, AccuService.class);
		context.startService(startIntent);
	}
}
