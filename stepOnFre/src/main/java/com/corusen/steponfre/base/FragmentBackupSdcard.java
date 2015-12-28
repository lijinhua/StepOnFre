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
//
//package com.corusen.steponfre.base;
//
//import java.io.IOException;
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.database.SdcardManager;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.res.Resources;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class FragmentBackupSdcard extends Fragment {
//
//	public boolean mExternalStorageAvailable = false;
//	public boolean mExternalStorageWriteable = false;
//
//	private View mView;
//
//	private ImageButton mButtonExport;
//	private ImageButton mButtonImport;
//	private TextView mSdcardText;
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
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		mView = inflater.inflate(R.layout.dark_fragment_backup_sdcard, container,
//				false);
//		mButtonExport = (ImageButton) mView
//				.findViewById(R.id.dbaseexportButton);
//		mButtonImport = (ImageButton) mView
//				.findViewById(R.id.dbaseimportButton);
//
//		mSdcardText = (TextView) mView.findViewById(R.id.export_to_sdcard);
//
//		updateDisplay();
//
//		mButtonExport.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				try {
//					SdcardManager sdmanager = new SdcardManager(getActivity());
//					sdmanager.exportDatabase();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//		});
//
//		mButtonImport.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				openImportAlertDialog();
//			}
//		});
//
//		return mView;
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//	}
//
//	private void openImportAlertDialog() {
//		new AlertDialog.Builder(this.getActivity())
//				.setMessage(R.string.alert_import_message)
//				.setPositiveButton(R.string.dialog_yes,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								try {
//									SdcardManager sdmanager = new SdcardManager(
//											getActivity());
//									sdmanager.importDatabase();
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
//							}
//						})
//				.setNegativeButton(R.string.dialog_no,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//							}
//						}).show();
//
//	}
//
//	private void showToast(final String toast) {
//		getActivity().runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
//			}
//		});
//	}
//
//	public void updateDisplay() {
//
//		Resources res = this.getResources();
//		if (res != null) { // java.lang.NullPointerException in TE
//			mSdcardText.setText(R.string.export_to_sdcard);
//			mButtonExport.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.blue_dbase_export_ic));
//			mButtonImport.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.blue_dbase_import_ic));
//			mButtonExport.setEnabled(true);
//			mButtonImport.setEnabled(true);
//		}
//	}
//
//	// private boolean isNetworkAvailable() {
//	// ConnectivityManager connectivityManager = (ConnectivityManager)
//	// getActivity()
//	// .getSystemService(Context.CONNECTIVITY_SERVICE);
//	// NetworkInfo activeNetworkInfo = connectivityManager
//	// .getActiveNetworkInfo();
//	// return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//	// }
//
//}