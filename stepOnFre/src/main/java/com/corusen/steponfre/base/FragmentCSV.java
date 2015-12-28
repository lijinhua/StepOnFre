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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.corusen.steponfre.R;
import com.corusen.steponfre.database.Constants;
import com.corusen.steponfre.database.MyDB;


public class FragmentCSV extends Fragment {

	private float mfDistanceFactor;
	public boolean mExternalStorageAvailable = false;
	public boolean mExternalStorageWriteable = false;

	private ProgressDialog mProgressDialog;
	private FileWriter mWriter;
	private Cursor mCursorThread;
	private int mColumnIndexYear;
	private int mColumnIndexMonth;
	private int mColumnIndexDay;
	private int mColumnIndexSteps;
	private int mColumnIndexDistance;
	private int mColumnIndexCalories;
	private int mColumnIndexSteptime;

	private static MyDB mDB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.dark_fragment_share_csvemail, container, false);

		mDB = new MyDB(getActivity());

		SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getContext());
		PedometerSettings pedometerSettings = new PedometerSettings(mSettings);

		ImageButton sendcvsButton = (ImageButton) view.findViewById(R.id.sendcvsButton);
		sendcvsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendCSVfile();
			}
		});

		if (pedometerSettings.isMetric()) {
			mfDistanceFactor = 1.60934f;
			getString(R.string.widget_km);
		} else {
			mfDistanceFactor = 1.0f;
			getString(R.string.widget_mi);
		}

		Calendar today = Calendar.getInstance();
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH) + 1;
		int day = today.get(Calendar.DATE);

		mDB.open();
		Cursor c = mDB.queryDayMaxSteps(year, month, day);

		mColumnIndexYear = c.getColumnIndex(Constants.KEY_YEAR);
		mColumnIndexMonth = c.getColumnIndex(Constants.KEY_MONTH);
		mColumnIndexDay = c.getColumnIndex(Constants.KEY_DAY);
		mColumnIndexSteps = c.getColumnIndex(Constants.KEY_STEPS);
		mColumnIndexDistance = c.getColumnIndex(Constants.KEY_DISTANCE);
		mColumnIndexCalories = c.getColumnIndex(Constants.KEY_CALORIES);
		mColumnIndexSteptime = c.getColumnIndex(Constants.KEY_STEPTIME);

		c.close();
		mDB.close();

		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

//	public void generateCsvFile() {
//		boolean success = true;
//		File folder;
//		String folderName = Constants.ACCUPEDO_FOLDERNAME;
//		String fileName = Constants.ACCUPEDO_CSV_FILENAME;
//		checkExternalStorage();
//
//		try {
//			// File root = Environment.getExternalStorageDirectory();
//			if (mExternalStorageAvailable && mExternalStorageWriteable) {
//				folder = new File(Environment.getExternalStorageDirectory() + folderName);
//				if (!folder.exists()) {
//					success = folder.mkdirs();
//				}
//			} else {
//				Toast.makeText(getActivity(), getString(R.string.toast_need_sdcard), Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			if (success) {
//				File gpxfile = new File(folder, fileName);
//				FileWriter writer = new FileWriter(gpxfile);
//
//				mDB.open();
//				Cursor c = mDB.queryAllDayMaxSteps();
//				if (c.moveToFirst()) {
//					do {
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_YEAR))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_MONTH))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_DAY))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_STEPS))));
//						writer.append(',');
//						writer.append(String.format("%5.2f", c.getFloat(c.getColumnIndex(Constants.KEY_DISTANCE)) * mfDistanceFactor));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_CALORIES))));
//						writer.append(',');
//						writer.append(Utils.getHoursMinutesString((int) c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME)) / 1000));
//						writer.append("\r\n");
//					} while (c.moveToNext());
//					c.close();
//					mDB.close();
//				}
//				writer.flush();
//				writer.close();
//				Toast.makeText(getActivity(), getString(R.string.toast_save), Toast.LENGTH_SHORT).show();
//			} else {
//				Toast.makeText(getActivity(), getString(R.string.toast_folder), Toast.LENGTH_SHORT).show();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void generateAllCsvFile() {
//		boolean success = true;
//		File folder;
//		String folderName = Constants.ACCUPEDO_FOLDERNAME;
//		String fileName = Constants.ACCUPEDO_CSV_FILENAME;
//		checkExternalStorage();
//
//		try {
//			// File root = Environment.getExternalStorageDirectory();
//			if (mExternalStorageAvailable && mExternalStorageWriteable) {
//				folder = new File(Environment.getExternalStorageDirectory() + folderName);
//				if (!folder.exists()) {
//					success = folder.mkdirs();
//				}
//			} else {
//				Toast.makeText(getActivity(), getString(R.string.toast_need_sdcard), Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			if (success) {
//				File gpxfile = new File(folder, fileName);
//				FileWriter writer = new FileWriter(gpxfile);
//
//				mDB.open();
//				Cursor c = mDB.queryDataBase();
//				writer.append("ID, Lap, Year, Month, Day, Hour, Minute, LapSteps, LapDistance, LapCalories, LapStepTime, Steps, Distance, Caloires, Speed, Pace, StepTime, Acheivement");
//				writer.append("\r\n");
//
//				if (c.moveToFirst()) {
//					do {
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_ID))));
//						writer.append(',');
//
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_LAP))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_YEAR))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_MONTH))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_DAY))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_HOUR))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_MINUTE))));
//						writer.append(',');
//
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_LAPSTEPS))));
//						writer.append(',');
//						writer.append(String.format("%5.2f", c.getFloat(c.getColumnIndex(Constants.KEY_LAPDISTANCE)) * mfDistanceFactor));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_LAPCALORIES))));
//						writer.append(',');
//						writer.append(Utils.getHoursMinutesString((int) c.getLong(c.getColumnIndex(Constants.KEY_LAPSTEPTIME)) / 1000));
//						writer.append(',');
//
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_STEPS))));
//						writer.append(',');
//						writer.append(String.format("%5.2f", c.getFloat(c.getColumnIndex(Constants.KEY_DISTANCE)) * mfDistanceFactor));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_CALORIES))));
//						writer.append(',');
//						writer.append(String.format("%5.2f", c.getFloat(c.getColumnIndex(Constants.KEY_SPEED)) * mfDistanceFactor));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_PACE))));
//						writer.append(',');
//
//						writer.append(Utils.getHoursMinutesString((int) c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME)) / 1000));
//						writer.append(',');
//
//						writer.append(String.format("%5d", c.getInt(c.getColumnIndex(Constants.KEY_ACHIEVEMENT))));
//
//						writer.append("\r\n");
//
//					} while (c.moveToNext());
//					c.close();
//					mDB.close();
//				}
//				writer.flush();
//				writer.close();
//				Toast.makeText(getActivity(), getString(R.string.toast_save), Toast.LENGTH_SHORT).show();
//			} else {
//				Toast.makeText(getActivity(), getString(R.string.toast_folder), Toast.LENGTH_SHORT).show();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public void checkExternalStorage() {
		String string = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(string)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(string)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;

		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;

		}
	}

	void sendCSVfile() {
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage(getString(R.string.wait_for_generating_csv));
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.show();

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				mCursorThread.close();
				mDB.close();
				mProgressDialog.dismiss();
				emailCSVfile();
			}
		};

		Thread sendCSVfile = new Thread() {
			public void run() {

				int i = 0;
				int j = mCursorThread.getCount();
				try {
					if (mCursorThread.moveToFirst()) {
						do {

							mWriter.append(String.format("%5d", mCursorThread.getInt(mColumnIndexYear)));
							mWriter.append(',');
							mWriter.append(String.format("%5d", mCursorThread.getInt(mColumnIndexMonth)));
							mWriter.append(',');
							mWriter.append(String.format("%5d", mCursorThread.getInt(mColumnIndexDay)));
							mWriter.append(',');
							mWriter.append(String.format("%5d", mCursorThread.getInt(mColumnIndexSteps)));
							mWriter.append(',');
							mWriter.append(String.format("%5.2f", mCursorThread.getFloat(mColumnIndexDistance) * mfDistanceFactor));
							mWriter.append(',');
							mWriter.append(String.format("%5d", mCursorThread.getInt(mColumnIndexCalories)));
							mWriter.append(',');
							mWriter.append(Utils.getHoursMinutesString((int) mCursorThread.getLong(mColumnIndexSteptime) / 1000));
							mWriter.append("\r\n");

							mProgressDialog.setProgress((int) (i++ * 100 / j));

						} while (mCursorThread.moveToNext());
						mWriter.flush();
						mWriter.close();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		};

		boolean success = true;
		File folder;
		String folderName = Constants.ACCUPEDO_FOLDERNAME;
		String fileName = Constants.ACCUPEDO_CSV_FILENAME; 
		
		checkExternalStorage();

		try {
			// File root = Environment.getExternalStorageDirectory();
			if (mExternalStorageAvailable && mExternalStorageWriteable) {
				folder = new File(Environment.getExternalStorageDirectory() + folderName);
				if (!folder.exists()) {
					success = folder.mkdirs();
				}
			} else {
				Toast.makeText(getActivity(), getString(R.string.toast_need_sdcard), Toast.LENGTH_SHORT).show();
				return;
			}

			if (success) {
				File gpxfile = new File(folder, fileName);
				mWriter = new FileWriter(gpxfile);

				mDB.open();
				mCursorThread = mDB.queryAllDayMaxSteps();
				mColumnIndexYear = mCursorThread.getColumnIndex(Constants.KEY_YEAR);
				mColumnIndexMonth = mCursorThread.getColumnIndex(Constants.KEY_MONTH);
				mColumnIndexDay = mCursorThread.getColumnIndex(Constants.KEY_DAY);
				mColumnIndexSteps = mCursorThread.getColumnIndex(Constants.KEY_STEPS);
				mColumnIndexDistance = mCursorThread.getColumnIndex(Constants.KEY_DISTANCE);
				mColumnIndexCalories = mCursorThread.getColumnIndex(Constants.KEY_CALORIES);
				mColumnIndexSteptime = mCursorThread.getColumnIndex(Constants.KEY_STEPTIME);

				sendCSVfile.start();
				Toast.makeText(getActivity(), getString(R.string.toast_save), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), getString(R.string.toast_folder), Toast.LENGTH_SHORT).show();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// generateCsvFile();
		// generateAllCsvFile();
	}

	void emailCSVfile() {
		String path;
		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			path = Environment.getExternalStorageDirectory().getPath() + Constants.ACCUPEDO_FOLDERNAME + "/" + Constants.ACCUPEDO_CSV_FILENAME;
		} else {
			return;
		}

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_file_subject));
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
		startActivity(intent);
	}

}
