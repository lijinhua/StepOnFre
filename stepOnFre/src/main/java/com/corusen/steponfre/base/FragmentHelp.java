/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *  http://stackoverflow.com/questions/3748568/how-can-i-launch-androids-email-activity-with-an-attachment-attached-in-the-emai
 *  
 *   *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *  Cleaned step detection algorithm
 *  Sensitivity 0.1, 0.11, 0.12, 0.14, 0.18
 *  Added Pause/resume buttons
 *  Check mIsSensorRegisterd for pause/resume buttons 
 *  Commented LE version functions: limited count,days, link to the market, expiration message
 *  
 *  updateWidget at TripA mode 5/18/2011
 *  category.LAUNCHER property at .Settings in Manifest xml
 *  Added Help information (link to video instruction) in the setting screen
 *  Set "singleInstance" property for Activity in Manifest xml. Allows "back" and "home" buttons
 *  http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
 *  
 *  Key hash for android-Fabcebook app
 *  use this command to generate key hash: 
 *  go to the AndroidMarketUpload folder, then
 *  keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
 *  or
 *  keytool -exportcert -alias accupedo -keystore Corusen|openssl sha1 -binary|openssl base64
 *  sLXTnqa0Q8Xram05j70YjEv5hUE
 *  138032342998022
 */

package com.corusen.steponfre.base;


//import com.actionbarsherlock.app.SherlockFragment;
import com.corusen.steponfre.R;
import com.corusen.steponfre.base.AnalyticsSampleApp.TrackerName;
import com.corusen.steponfre.database.Constants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

//import com.google.ads.AdRequest;
//import com.google.ads.AdSize;
//import com.google.ads.AdView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentHelp extends Fragment {

	private View mView;
	// private AdView adView;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		Tracker t = ((AnalyticsSampleApp) Pedometer.getInstance().getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Help");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.light_help, container, false);
		 final Button emailbutton = (Button) mView.findViewById(R.id.emailButton);
	        emailbutton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {         	
	            	Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:stepontrack@gmail.com"));

					String  emailSubject;
					if (Constants.IS_VERSION_TE) {
						emailSubject = getString(R.string.app_name_te);
					} else {
						emailSubject = getString(R.string.app_name_pro);
					}
					intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject + ":" + getString(R.string.version_number));
					startActivity(intent);
	            }
	        });
	        
	        final Button button = (Button) mView.findViewById(R.id.reviewButton);
	        button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {   
	            	
	            	Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=com.corusen.steponfre"));
					startActivity(intent);
	            }
	        });
		return mView;
	}

}