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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.corusen.steponfre.R;

public class FragmentDrive extends Fragment implements ActivityBackup.ConnectionListener {

	private ImageButton mButtonExport;
	private ImageButton mButtonImport;
	private Button mButtonConnect;
	private TextView mSdcardText;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dark_fragment_backup_gdrive, container, false);
		mButtonExport = (ImageButton) view.findViewById(R.id.dbaseexportButton);
		mButtonImport = (ImageButton) view.findViewById(R.id.dbaseimportButton);
		mButtonConnect = (Button) view.findViewById(R.id.connectButton);

		mSdcardText = (TextView) view.findViewById(R.id.export_to_gdrive);

		mButtonConnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				if ( mButtonConnect.getText().toString().equals(getString(R.string.connect)) ) {
//					mProgressDialog = new ProgressDialog(ActivityBackup.getInstance());
//					mProgressDialog.setMessage(ActivityBackup.getInstance().getString(R.string.wait_for_exporting_db));
//					mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//					mProgressDialog.show();
//
//					mHandler = new Handler() {
//						public void handleMessage(Message msg) {
//							mProgressDialog.dismiss();
//						}
//					};
//				} else {
//					mHandler = null;
//				}

				ActivityBackup.getInstance().gDriveConnect();
			}
		});

		mButtonExport.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ActivityBackup.getInstance().gDriveExport();
			}
		});

		mButtonImport.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openImportGdriveAlertDialog();
			}
		});

		mButtonExport.setContentDescription(getString(R.string.export_success_message));
		mButtonImport.setContentDescription(getString(R.string.import_success_message));

		ActivityBackup.getInstance().setListener(this);

		updateDisplay();

		return view;
	}

	private void openImportGdriveAlertDialog() {
		new AlertDialog.Builder(this.getActivity())
				.setMessage(R.string.alert_import_gdrive_message)
				.setPositiveButton(R.string.dialog_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ActivityBackup.getInstance().gDriveImport();
							}
						})
				.setNegativeButton(R.string.dialog_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();

	}

	@Override
	public void onResume() {
		super.onResume();
		updateDisplay();
	}


	private void updateDisplay() {
		mSdcardText.setText(R.string.network_connection);
		if (ActivityBackup.getInstance().isConnected()) {
			mButtonExport.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.blue_dbase_export_ic));
			mButtonImport.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.blue_dbase_import_ic));
			mButtonExport.setEnabled(true);
			mButtonImport.setEnabled(true);
			mButtonConnect.setText(R.string.switch_account);
		} else {
			mButtonExport.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dbase_export_off_ic));
			mButtonImport.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dbase_import_off_ic));
			mButtonExport.setEnabled(false);
			mButtonImport.setEnabled(false);
			mButtonConnect.setText(R.string.connect);
		}
	}

//	private boolean isNetworkAvailable() {
//		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//	}


	@Override
	public void driveConnected() {
		updateDisplay();
		//if (mHandler != null) { mHandler.sendEmptyMessage(0); }
	}
}











//
//import java.util.Arrays;
//
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.base.AnalyticsSampleApp.TrackerName;
//import com.corusen.steponfre.database.Constants;
//import com.corusen.steponfre.database.GdriveExportManager;
//
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//
//import android.accounts.AccountManager;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
////Include google.play jar for Gdrive function for Pro
////goolge.play jar conflicts with AdMob jar, so it cannot be used in TE
//
//public class FragmentBackupGdrive extends Fragment {
//
//	public boolean mExternalStorageAvailable = false;
//	public boolean mExternalStorageWriteable = false;
//	// private ProgressDialog mProgressDialog;
//	private View mView;
//
//	// private Button mButtonSdcard;
//	// private Button mButtonGdrive;
//	// private Button mButtonDropbox;
//
//	private ImageButton mButtonExport;
//	private ImageButton mButtonImport;
//	private TextView mSdcardText;
//
//	// Google Drive
//	private static GoogleAccountCredential mCredential;
//	private static Drive mGdriveService;
//	private static GdriveExportManager mGdrive;
//	public static Uri mFileUri;
//	private boolean mIsExportMode = true;
//
//	// private static final int FLAG_DRIVE_SDCARD = 0;
//	// private static final int FLAG_DRIVE_GOOGLE_DRIVE = 1;
//	// private static final int FLAG_DRIVE_DROPBOX = 2;
//	// private static int mFlagDriveMode = FLAG_DRIVE_SDCARD;
//
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		super.onSaveInstanceState(savedInstanceState);
//	}
//
//	@Override
//	public void onStart() {
//		super.onStart();
//		Tracker t = ((AnalyticsSampleApp) Pedometer.getInstance().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("BackupGdrive");
//		t.send(new HitBuilders.AppViewBuilder().build());
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
//		mView = inflater.inflate(AccuService.mScreenFragmentBackupGdrive, container,
//				false);
//		mButtonExport = (ImageButton) mView
//				.findViewById(R.id.dbaseexportButton);
//		mButtonImport = (ImageButton) mView
//				.findViewById(R.id.dbaseimportButton);
//
//		mSdcardText = (TextView) mView.findViewById(R.id.export_to_gdrive);
//
//		updateDisplay();
//
//		mButtonExport.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				mIsExportMode = true;
//				mGdrive = new GdriveExportManager(getActivity());
//				mCredential = GoogleAccountCredential.usingOAuth2(
//						getActivity(), Arrays.asList(DriveScopes.DRIVE_FILE));
//				startActivityForResult(mCredential.newChooseAccountIntent(),
//						Constants.GDRIVE_REQUEST_ACCOUNT_PICKER);
//			}
//		});
//
//		mButtonImport.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				openImportGdriveAlertDialog();
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
//	@Override
//	public void onActivityResult(final int requestCode, final int resultCode,
//			final Intent data) {
//		// super.onActivityResult(requestCode, resultCode, data);
//		// Log.i("FBackup", "Called 2");
//		switch (requestCode) {
//		case Constants.GDRIVE_REQUEST_ACCOUNT_PICKER:
//			// Log.i("FBackup", "REQUEST_ACCOUNT_PICKER");
//			if (resultCode == Activity.RESULT_OK && data != null
//					&& data.getExtras() != null) {
//				String accountName = data
//						.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//				if (isNetworkAvailable()) {
//					if (accountName != null) {
//						mCredential.setSelectedAccountName(accountName);
//						mGdriveService = getDriveService(mCredential);
//						if (mIsExportMode) {
//							mGdrive.saveFileToDrive(mGdriveService);
//						} else {
//							// Log.i("FBackup", "call download");
//							mGdrive.dowloadFilefromDrive(mGdriveService);
//						}
//						// startCameraIntent();
//					} else {
//						showToast(getString(R.string.try_again));
//					}
//				} else {
//					showToast(getString(R.string.network_connection));
//				}
//			} else {
//				showToast(getString(R.string.try_again));
//			}
//			break;
//		// case Constants.GDRIVE_REQUEST_AUTHORIZATION:
//		// // Log.i("FBackup", "REQUEST_AUTHORIZATION");
//		// if (resultCode == Activity.RESULT_OK) {
//		// mGdrive.saveFileToDrive(mGdriveService);
//		// } else {
//		// startActivityForResult(mCredential.newChooseAccountIntent(),
//		// Constants.GDRIVE_REQUEST_ACCOUNT_PICKER);
//		// }
//		// break;
//		}
//	}
//
//	private void openImportGdriveAlertDialog() {
//		new AlertDialog.Builder(this.getActivity())
//				.setMessage(R.string.alert_import_gdrive_message)
//				.setPositiveButton(R.string.dialog_yes,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								mIsExportMode = false;
//								mGdrive = new GdriveExportManager(getActivity());
//								mCredential = GoogleAccountCredential
//										.usingOAuth2(getActivity(),
//												Arrays.asList(DriveScopes.DRIVE_FILE));
//								startActivityForResult(
//										mCredential.newChooseAccountIntent(),
//										Constants.GDRIVE_REQUEST_ACCOUNT_PICKER);
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
//	private Drive getDriveService(GoogleAccountCredential credential) {
//		return new Drive.Builder(AndroidHttp.newCompatibleTransport(),
//				new GsonFactory(), credential).build();
//	}
//
//	private void showToast(final String toast) {
//		getActivity().runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
//			}
//		});
//	}
//
//	public void updateDisplay() {
//
//		Resources res = this.getResources();
//		if (res != null) { // java.lang.NullPointerException in TE
//			mSdcardText.setText(R.string.network_connection);
//			// mButtonSdcard.setBackgroundDrawable(res
//			// .getDrawable(R.drawable.mybutton0));
//			// mButtonGdrive.setBackgroundDrawable(res
//			// .getDrawable(R.drawable.mybutton5));
//			// mButtonDropbox.setBackgroundDrawable(res
//			// .getDrawable(R.drawable.mybutton0));
//
//			//JS turn on for Pro
////			mButtonExport.setImageDrawable(getActivity()
////					.getResources()
////					.getDrawable(AccuService.mScreenBackupDBExport));
////			mButtonImport.setImageDrawable(getActivity()
////					.getResources()
////					.getDrawable(AccuService.mScreenBackupDBImport));
////			mButtonExport.setEnabled(true);
////			mButtonImport.setEnabled(true);
//
//			// JS Block for TE
//			 mButtonExport.setImageDrawable(getActivity()
//			 .getResources()
//			 .getDrawable(R.drawable.dbase_export_off_ic));
//			 mButtonImport.setImageDrawable(getActivity()
//			 .getResources()
//			 .getDrawable(R.drawable.dbase_import_off_ic));
//			 mButtonExport.setEnabled(false);
//			 mButtonImport.setEnabled(false);
//		}
//	}
//
//	private boolean isNetworkAvailable() {
//		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetworkInfo = connectivityManager
//				.getActiveNetworkInfo();
//		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//	}
//
//}