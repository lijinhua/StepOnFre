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

//package com.corusen.steponfre;
//
//import java.util.Calendar;
//
//import kankan.wheel.widget.OnWheelChangedListener;
//import kankan.wheel.widget.OnWheelScrollListener;
//import kankan.wheel.widget.WheelView;
//import kankan.wheel.widget.adapters.NumericWheelAdapter;
//
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.Pedometer;
//import com.corusen.steponfre.database.Constants;
//import com.corusen.steponfre.database.SdcardManager;
//
////import com.google.ads.AdRequest;
////import com.google.ads.AdSize;
////import com.google.ads.AdView;
//
//import android.app.ActionBar;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.view.animation.AnticipateOvershootInterpolator;
//import android.widget.Button;
//
//public class ActivityAccupedoPro extends Activity {
//
//	// private AdView adView;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.light_fragment_accupedopro);
//		
//		getActionBar().setDisplayHomeAsUpEnabled(true); // enabling action bar app icon and behaving it as toggle button
//		getActionBar().setHomeButtonEnabled(true);
//		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
//				.getColor(AccuService.mScreenAcitionBarColor)));
//
//		Button btnBuyPro = (Button) findViewById(R.id.btn_buypro);
//		btnBuyPro.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				try {
//					SdcardManager sdmanager = new SdcardManager(
//							Pedometer.getInstance());
//					sdmanager.exportDatabase();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.setData(Uri
//						.parse("market://details?id=com.corusen.steponpro"));
//				startActivity(intent);
//			}
//		});
//
//
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		super.onSaveInstanceState(savedInstanceState);
//	}
//
//	@Override
//	public void onStart() {
//		super.onStart();
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//}
