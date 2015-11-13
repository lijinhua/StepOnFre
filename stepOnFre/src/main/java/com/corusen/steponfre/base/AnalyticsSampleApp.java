/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.corusen.steponfre.base;

import android.app.Application;
import android.util.Log;

import java.util.HashMap;

//import me.kiip.sdk.Kiip;
//import me.kiip.sdk.Kiip.OnContentListener;
//import me.kiip.sdk.KiipFragmentCompat;
//import me.kiip.sdk.Poptart;

import com.corusen.steponfre.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsSampleApp extends Application { //implements OnContentListener {

	// The following line should be changed to include the correct property id.
	private static final String PROPERTY_ID = "UA-51682816-3";
	public static int GENERAL_TRACKER = 0;
	
	// Kiip implementation
    public static final String TAG = "kiip";
    private static final String APP_KEY = "19ffbe448902788a42ac13c46388dca8";
    private static final String APP_SECRET = "9da067699337518c2eb1d7be9b89c464";

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	public AnalyticsSampleApp() {
		super();
	}

	public synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
					.newTracker(R.xml.global_tracker) : analytics.newTracker(R.xml.ecommerce_tracker);
			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}
	
	@Override
	public void onCreate() {
	    super.onCreate();
//	    Log.v("aa", "kiip oncrete");
//	    Kiip kiip = Kiip.init(this, APP_KEY, APP_SECRET);
//	    Kiip.setInstance(kiip);
	}

    // Content listener

//    @Override
//    public void onContent(Kiip kiip, String content, int quantity, String transactionId, String signature) {
//        Log.v(TAG, "onContent content=" + content + " quantity=" + quantity + " transactionId=" + transactionId + " signature=" + signature);
//
//        // Add quantity amount of content to player's profile
//        // e.g +20 coins to user's wallet
//        // http://docs.kiip.com/en/guide/android.html#getting_virtual_rewards
//    }
}
