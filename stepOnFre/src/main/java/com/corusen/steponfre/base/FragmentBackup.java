package com.corusen.steponfre.base;


import java.io.IOException;

import com.corusen.steponfre.R;
import com.corusen.steponfre.database.SdcardManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class FragmentBackup extends Fragment {

    private ImageButton mButtonExport;
    private ImageButton mButtonImport;
    private TextView mSdcardText;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
//		AnalyticsApp.tracker().setScreenName("BackupMemory541");
//		AnalyticsApp.tracker().send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(AccuService.mScreenFragmentBackupSdcard, container, false);
        mButtonExport = (ImageButton) view.findViewById(R.id.dbaseexportButton);
        mButtonImport = (ImageButton) view.findViewById(R.id.dbaseimportButton);
        mSdcardText = (TextView) view.findViewById(R.id.export_to_sdcard);
        updateDisplay();

        mButtonExport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    SdcardManager sdmanager = new SdcardManager(getActivity());
                    sdmanager.exportDatabase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonImport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openImportAlertDialog();
            }
        });
        mButtonExport.setContentDescription(getString(R.string.export_success_message));
        mButtonImport.setContentDescription(getString(R.string.import_success_message));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void openImportAlertDialog() {
        new AlertDialog.Builder(this.getActivity()).setMessage(R.string.alert_import_message)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            SdcardManager sdmanager = new SdcardManager(getActivity());
                            sdmanager.importDatabase();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    private void updateDisplay() {
        Resources res = this.getResources();
        if (res != null) { // java.lang.NullPointerException in TE
            mSdcardText.setText(R.string.export_to_sdcard);
            mButtonExport.setImageDrawable(ContextCompat.getDrawable(getActivity(), AccuService.mScreenBackupDBExport));
            mButtonImport.setImageDrawable(ContextCompat.getDrawable(getActivity(), AccuService.mScreenBackupDBImport));
            mButtonExport.setEnabled(true);
            mButtonImport.setEnabled(true);
        }
    }
}


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
//import java.io.File;
//import java.io.IOException;
//
//import com.actionbarsherlock.app.SherlockFragment;
//import com.corusen.steponfre.Pedometer;
//import com.corusen.steponfre.database.Constants;
////import com.corusen.steponfre.database.DropBoxDownload;
////import com.corusen.steponfre.database.DropBoxUpload;
//import com.corusen.steponfre.database.GdriveExportManager;
//import com.corusen.steponfre.database.SdcardManager;
//import com.corusen.steponfre.R;
//
////import com.dropbox.client2.DropboxAPI;
////import com.dropbox.client2.android.AndroidAuthSession;
////import com.dropbox.client2.session.Session.AccessType;
//
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.DriveScopes;
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
//import android.os.Environment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class FragmentBackup extends SherlockFragment {
//
//	public boolean mExternalStorageAvailable = false;
//	public boolean mExternalStorageWriteable = false;
//	// private ProgressDialog mProgressDialog;
//	private View mView;
//
//	/**
//	 * DropBox variables not used
//	 */
//	// final static private String APP_KEY = "txhd1ck37ufeuw1";
//	// final static private String APP_SECRET = "xw8ueyozu3h7ov7";
//	// final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
//	// final static private String DROPBOX_ACCOUNT_PREFS_NAME = "dropbox_prefs";
//	// final static private String DROPBOX_ACCESS_KEY_NAME =
//	// "dropbox_access_key";
//	// final static private String DROPBOX_ACCESS_SECRET_NAME =
//	// "dropbox_access_secret";
//	// private boolean mDboxLoggedIn;
//
//	//private DropboxAPI<AndroidAuthSession> mDBApi;
//
//	private Button mButtonSdcard;
//	private Button mButtonGdrive;
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
//	private static final int FLAG_DRIVE_SDCARD = 0;
//	private static final int FLAG_DRIVE_GOOGLE_DRIVE = 1;
//	// private static final int FLAG_DRIVE_DROPBOX = 2;
//	private static int mFlagDriveMode = FLAG_DRIVE_SDCARD;
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
//		mView = inflater.inflate(R.layout.export, container, false);
//		mButtonExport = (ImageButton) mView
//				.findViewById(R.id.dbaseexportButton);
//		mButtonImport = (ImageButton) mView
//				.findViewById(R.id.dbaseimportButton);
//
//		mButtonSdcard = (Button) mView.findViewById(R.id.button_sdcard);
//		mButtonGdrive = (Button) mView.findViewById(R.id.button_google_drive);
//		// mButtonDropbox = (Button) mView.findViewById(R.id.button_dropbox);
//		mSdcardText = (TextView) mView.findViewById(R.id.export_to_sdcard);
//
//		updateDisplay();
//
//		mButtonExport.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				switch (mFlagDriveMode) {
//				case FLAG_DRIVE_SDCARD:
//					try {
//						SdcardManager sdmanager = new SdcardManager(Pedometer
//								.getInstance());
//						sdmanager.exportDatabase();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					break;
//
//				case FLAG_DRIVE_GOOGLE_DRIVE:
//					mIsExportMode = true;
//					mGdrive = new GdriveExportManager(Pedometer.getInstance());
//					mCredential = GoogleAccountCredential.usingOAuth2(
//							Pedometer.getInstance(), DriveScopes.DRIVE);
//					startActivityForResult(
//							mCredential.newChooseAccountIntent(),
//							Constants.GDRIVE_REQUEST_ACCOUNT_PICKER);
//					break;
//
//				// case FLAG_DRIVE_DROPBOX:
//				// exportDatabaseDbox();
//				// break;
//
//				default:
//					break;
//
//				}
//
//			}
//		});
//
//		mButtonImport.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				switch (mFlagDriveMode) {
//				case FLAG_DRIVE_SDCARD:
//					openImportAlertDialog();
//					break;
//
//				case FLAG_DRIVE_GOOGLE_DRIVE:
//					openImportGdriveAlertDialog();
//					break;
//
//				// case FLAG_DRIVE_DROPBOX:
//				// openImportDropboxAlertDialog();
//				// break;
//
//				default:
//					break;
//
//				}
//			}
//		});
//
//		mButtonSdcard.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				mFlagDriveMode = FLAG_DRIVE_SDCARD;
//				updateDisplay();
//			}
//		});
//
//		mButtonGdrive.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				mFlagDriveMode = FLAG_DRIVE_GOOGLE_DRIVE;
//				updateDisplay();
//			}
//		});
//
//		// mButtonDropbox.setOnClickListener(new OnClickListener() {
//		// public void onClick(View v) {
//		// mFlagDriveMode = FLAG_DRIVE_DROPBOX;
//		// updateDisplay();
//		//
//		// /**
//		// * Block for TE
//		// */
//		// // AndroidAuthSession session = buildDboxSession();
//		// // mDBApi = new DropboxAPI<AndroidAuthSession>(session);
//		// // mDboxLoggedIn = mDBApi.getSession().isLinked();
//		// //
//		// // // AndroidAuthSession session = mDBApi.getSession();
//		// // if (session.authenticationSuccessful()) {
//		// // try {
//		// // // Mandatory call to complete the auth
//		// // session.finishAuthentication();
//		// //
//		// // // Store it locally in our app for later use
//		// // TokenPair tokens = session.getAccessTokenPair();
//		// // storeDboxKeys(tokens.key, tokens.secret);
//		// // // setDboxLoggedIn(true);
//		// // } catch (IllegalStateException e) {
//		// // showToast("Couldn't authenticate with Dropbox:"
//		// // + e.getLocalizedMessage());
//		// // // Log.i(TAG, "Error authenticating", e);
//		// // }
//		// // }
//		// //
//		// // if (mDboxLoggedIn) {
//		// //
//		// // } else {
//		// // // Start the remote authentication
//		// // mDBApi.getSession().startAuthentication(
//		// // Pedometer.getInstance());
//		// // }
//		// }
//		// });
//
//		return mView;
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//
//		/**
//		 * Blocking button, old way
//		 */
//		// layout.setAlpha(0.5f); //above API11
//		// AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
//		// alpha.setDuration(0); // Make animation instant
//		// alpha.setFillAfter(true); // Tell it to persist after the animation
//		// ends
//		// mButtonGdrive.startAnimation(alpha);
//		// mButtonDropbox.startAnimation(alpha);
//		// mButtonGdrive.setEnabled(false);
//		// mButtonDropbox.setEnabled(false);
//
//		// AndroidAuthSession session = mDBApi.getSession();
//		// if (session.authenticationSuccessful()) {
//		// try {
//		// // Mandatory call to complete the auth
//		// session.finishAuthentication();
//		//
//		// // Store it locally in our app for later use
//		// TokenPair tokens = session.getAccessTokenPair();
//		// storeDboxKeys(tokens.key, tokens.secret);
//		// //setDboxLoggedIn(true);
//		// } catch (IllegalStateException e) {
//		// showToast("Couldn't authenticate with Dropbox:"
//		// + e.getLocalizedMessage());
//		// // Log.i(TAG, "Error authenticating", e);
//		// }
//		// }
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
//	private void openImportAlertDialog() {
//		new AlertDialog.Builder(Pedometer.getInstance())
//				.setMessage(R.string.alert_import_message)
//				.setPositiveButton(R.string.dialog_yes,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								try {
//									SdcardManager sdmanager = new SdcardManager(
//											Pedometer.getInstance());
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
//	private void openImportGdriveAlertDialog() {
//		new AlertDialog.Builder(Pedometer.getInstance())
//				.setMessage(R.string.alert_import_gdrive_message)
//				.setPositiveButton(R.string.dialog_yes,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								mIsExportMode = false;
//								mGdrive = new GdriveExportManager(Pedometer
//										.getInstance());
//								mCredential = GoogleAccountCredential
//										.usingOAuth2(Pedometer.getInstance(),
//												DriveScopes.DRIVE);
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
//	private void openImportDropboxAlertDialog() {
//		new AlertDialog.Builder(Pedometer.getInstance())
//				.setMessage(R.string.alert_import_dropbox_message)
//				.setPositiveButton(R.string.dialog_yes,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
////								DropBoxDownload download = new DropBoxDownload(
////										Pedometer.getInstance(), mDBApi, " ");
////								download.execute();
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
//	private void exportDatabaseDbox() {
////		File data = Environment.getDataDirectory();
////		String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH
////				+ Constants.DATABASE_NAME;
////		File currentDB = new File(data, currentDBPath);
////		DropBoxUpload upload = new DropBoxUpload(this.getActivity(), mDBApi,
////				"", currentDB, Constants.DATABASE_FILENAME);
////		upload.execute();
//	}
//
//	/**
//	 * DropBox functions not used
//	 */
//	// private void logDboxOut() {
//	// // Remove credentials from the session
//	// mDBApi.getSession().unlink();
//	//
//	// // Clear our stored keys
//	// clearDboxKeys();
//	// // Change UI state to display logged out version
//	// setDboxLoggedIn(false);
//	// }
//
//	// private void setDboxLoggedIn(boolean loggedIn) {
//	// mDboxLoggedIn = loggedIn;
//	// if (loggedIn) {
//	// mButtonDboxLogin.setText("Unlink Dropbox");
//	// mButtonDboxExport.setVisibility(View.VISIBLE);
//	// mButtonDboxImport.setVisibility(View.VISIBLE);
//	// } else {
//	// mButtonDboxLogin.setText("Link Dropbox");
//	// mButtonDboxExport.setVisibility(View.GONE);
//	// mButtonDboxImport.setVisibility(View.GONE);
//	// }
//	// }
//	//
//	// private String[] getDboxKeys() {
//	// SharedPreferences prefs = Pedometer.getInstance().getSharedPreferences(
//	// DROPBOX_ACCOUNT_PREFS_NAME, 0);
//	// String key = prefs.getString(DROPBOX_ACCESS_KEY_NAME, null);
//	// String secret = prefs.getString(DROPBOX_ACCESS_SECRET_NAME, null);
//	// if (key != null && secret != null) {
//	// String[] ret = new String[2];
//	// ret[0] = key;
//	// ret[1] = secret;
//	// return ret;
//	// } else {
//	// return null;
//	// }
//	// }
//	//
//	// private void storeDboxKeys(String key, String secret) {
//	// SharedPreferences prefs = Pedometer.getInstance().getSharedPreferences(
//	// DROPBOX_ACCOUNT_PREFS_NAME, 0);
//	// Editor edit = prefs.edit();
//	// edit.putString(DROPBOX_ACCESS_KEY_NAME, key);
//	// edit.putString(DROPBOX_ACCESS_SECRET_NAME, secret);
//	// edit.commit();
//	// }
//	//
//	// private void clearDboxKeys() {
//	// SharedPreferences prefs = Pedometer.getInstance().getSharedPreferences(
//	// DROPBOX_ACCOUNT_PREFS_NAME, 0);
//	// Editor edit = prefs.edit();
//	// edit.clear();
//	// edit.commit();
//	// }
//	//
//	// private AndroidAuthSession buildDboxSession() {
//	// AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
//	// AndroidAuthSession session;
//	//
//	// String[] stored = getDboxKeys();
//	// if (stored != null) {
//	// AccessTokenPair accessToken = new AccessTokenPair(stored[0],
//	// stored[1]);
//	// session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
//	// accessToken);
//	// } else {
//	// session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
//	// }
//	//
//	// return session;
//	// }
//
//	private Drive getDriveService(GoogleAccountCredential credential) {
//		return new Drive.Builder(AndroidHttp.newCompatibleTransport(),
//				new GsonFactory(), credential).build();
//	}
//
//	private void showToast(final String toast) {
//		Pedometer.getInstance().runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(Pedometer.getInstance(), toast,
//						Toast.LENGTH_SHORT).show();
//			}
//		});
//	}
//
//	public void updateDisplay() {
//
//		Resources res = this.getResources();
//		if (res != null) { // java.lang.NullPointerException in TE
//			switch (mFlagDriveMode) {
//			case FLAG_DRIVE_SDCARD:
//				mSdcardText.setText(R.string.export_to_sdcard);
//				mButtonSdcard.setBackgroundDrawable(res
//						.getDrawable(R.drawable.mybutton5));
//				mButtonGdrive.setBackgroundDrawable(res
//						.getDrawable(R.drawable.mybutton0));
//				// mButtonDropbox.setBackgroundDrawable(res
//				// .getDrawable(R.drawable.mybutton0));
//				/**
//				 * Disable the buttons for TE
//				 */
//				mButtonExport
//						.setImageDrawable(Pedometer.getInstance()
//								.getResources()
//								.getDrawable(R.drawable.dbase_export_ic));
//				mButtonImport
//						.setImageDrawable(Pedometer.getInstance()
//								.getResources()
//								.getDrawable(R.drawable.dbase_import_ic));
//				mButtonExport.setEnabled(true);
//				mButtonImport.setEnabled(true);
//				break;
//
//			case FLAG_DRIVE_GOOGLE_DRIVE:
//				mSdcardText.setText(R.string.network_connection);
//				mButtonSdcard.setBackgroundDrawable(res
//						.getDrawable(R.drawable.mybutton0));
//				mButtonGdrive.setBackgroundDrawable(res
//						.getDrawable(R.drawable.mybutton5));
//				// mButtonDropbox.setBackgroundDrawable(res
//				// .getDrawable(R.drawable.mybutton0));
//				/**
//				 * Block for TE
//				 */
//				mButtonExport.setImageDrawable(Pedometer.getInstance()
//						.getResources()
//						.getDrawable(R.drawable.dbase_export_off_ic));
//				mButtonImport.setImageDrawable(Pedometer.getInstance()
//						.getResources()
//						.getDrawable(R.drawable.dbase_import_off_ic));
//				mButtonExport.setEnabled(false);
//				mButtonImport.setEnabled(false);
//				showToast(getString(R.string.trial_expired));
//				break;
//			//
//			// case FLAG_DRIVE_DROPBOX:
//			// mButtonSdcard.setBackgroundDrawable(res
//			// .getDrawable(R.drawable.mybutton0));
//			// mButtonGdrive.setBackgroundDrawable(res
//			// .getDrawable(R.drawable.mybutton0));
//			// // mButtonDropbox.setBackgroundDrawable(res
//			// // .getDrawable(R.drawable.mybutton5));
//			// /**
//			// * Block for TE
//			// */
//			// mButtonExport.setImageDrawable(Pedometer.getInstance()
//			// .getResources()
//			// .getDrawable(R.drawable.dbase_export_off_ic));
//			// mButtonImport.setImageDrawable(Pedometer.getInstance()
//			// .getResources()
//			// .getDrawable(R.drawable.dbase_import_off_ic));
//			// mButtonExport.setEnabled(false);
//			// mButtonImport.setEnabled(false);
//			// showToast(getString(R.string.trial_expired));
//			// break;
//
//			default:
//				break;
//			}
//		}
//	}
//	
//	private boolean isNetworkAvailable() {
//		ConnectivityManager connectivityManager = (ConnectivityManager) Pedometer
//				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetworkInfo = connectivityManager
//				.getActiveNetworkInfo();
//		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//	}
//
//}