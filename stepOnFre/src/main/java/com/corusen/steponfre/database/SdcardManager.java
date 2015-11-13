/*
 * Copyright (c) 2011 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.corusen.steponfre.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.corusen.steponfre.R;
import com.corusen.steponfre.base.Pedometer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class SdcardManager {
	private ProgressDialog mProgressDialog;
	public boolean mExternalStorageAvailable = false;
	public boolean mExternalStorageWriteable = false;
	private Context mContext;

	public SdcardManager(Context context) {
		mContext = context;
	}
	
	public void exportDatabaseService() throws IOException {

		Thread exportDBfile = new Thread() {
			public void run() {
				boolean success = true;
				File folder;
				checkExternalStorage();

				FileInputStream fileInputStream = null;
				FileOutputStream fileOutputStream = null;
				try {

					if (mExternalStorageAvailable && mExternalStorageWriteable) {
						folder = new File(
								Environment.getExternalStorageDirectory()
										+ Constants.ACCUPEDO_FOLDERNAME);
						if (!folder.exists()) {
							success = folder.mkdirs();
						}
					} else {
						return;
					}

					if (success) {
						File sd = new File(
								Environment.getExternalStorageDirectory()
										+ Constants.ACCUPEDO_FOLDERNAME);
						File data = Environment.getDataDirectory();

						if (sd.canWrite()) {
							String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH
									+ Constants.DATABASE_NAME;
							String backupDBPath = Constants.DATABASE_FILENAME; // Constants.DATABASE_NAME;
							File currentDB = new File(data, currentDBPath);
							File backupDB = new File(sd, backupDBPath);

							if (currentDB.exists()) {
								fileInputStream = new FileInputStream(currentDB);
								fileOutputStream = new FileOutputStream(
										backupDB);
								FileChannel src = fileInputStream.getChannel();
								FileChannel dst = fileOutputStream.getChannel();
								dst.transferFrom(src, 0, src.size());
								src.close();
								dst.close();
							}
						} else {
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {

				}
			}
		};
		exportDBfile.start();
	}

	public void exportDatabase() throws IOException {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext
				.getString(R.string.wait_for_exporting_db));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();

		final Handler handlerExport = new Handler() {
			public void handleMessage(Message msg) {
				mProgressDialog.dismiss();
			}
		};

		Thread exportDBfile = new Thread() {
			public void run() {
				boolean success = true;
				File folder;
				checkExternalStorage();

				FileInputStream fileInputStream = null;
				FileOutputStream fileOutputStream = null;
				try {

					if (mExternalStorageAvailable && mExternalStorageWriteable) {
						folder = new File(
								Environment.getExternalStorageDirectory()
										+ Constants.ACCUPEDO_FOLDERNAME);
						if (!folder.exists()) {
							success = folder.mkdirs();
						}
					} else {
						showToast(mContext
								.getString(R.string.toast_need_sdcard));
						return;
					}

					if (success) {
						File sd = new File(
								Environment.getExternalStorageDirectory()
										+ Constants.ACCUPEDO_FOLDERNAME);
						File data = Environment.getDataDirectory();

						if (sd.canWrite()) {
							String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH
									+ Constants.DATABASE_NAME;
							String backupDBPath = Constants.DATABASE_FILENAME; // Constants.DATABASE_NAME;
							File currentDB = new File(data, currentDBPath);
							File backupDB = new File(sd, backupDBPath);

							if (currentDB.exists()) {
								fileInputStream = new FileInputStream(currentDB);
								fileOutputStream = new FileOutputStream(
										backupDB);
								FileChannel src = fileInputStream.getChannel();
								FileChannel dst = fileOutputStream.getChannel();
								dst.transferFrom(src, 0, src.size());
								src.close();
								dst.close();
								showToast(mContext
										.getString(R.string.export_success_message));
							}
						} else {
							showToast(mContext
									.getString(R.string.toast_external_storage_not_writeable));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {

				}
				handlerExport.sendEmptyMessage(0);
			}
		};
		exportDBfile.start();
	}

	public void importDatabase() throws IOException {
		mProgressDialog = new ProgressDialog(Pedometer.getInstance());
		mProgressDialog.setMessage(mContext
				.getString(R.string.wait_for_importing_db));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				mProgressDialog.dismiss();
			}
		};

		Thread importDBfile = new Thread() {
			public void run() {
				FileInputStream fileInputStream = null;
				FileOutputStream fileOutputStream = null;
				try {
					File sd = new File(
							Environment.getExternalStorageDirectory()
									+ Constants.ACCUPEDO_FOLDERNAME);
					File data = Environment.getDataDirectory();

					if (sd.exists()) {
						String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH
								+ Constants.DATABASE_NAME;
						String backupDBPath = Constants.DATABASE_FILENAME; // Constants.DATABASE_NAME;
						File currentDB = new File(data, currentDBPath);
						File backupDB = new File(sd, backupDBPath);

						if (backupDB.exists()) {
							fileInputStream = new FileInputStream(backupDB);
							fileOutputStream = new FileOutputStream(currentDB);
							FileChannel src = fileInputStream
									.getChannel();
							FileChannel dst = fileOutputStream
									.getChannel();
							dst.transferFrom(src, 0, src.size());
							src.close();
							dst.close();
							showToast(mContext
									.getString(R.string.import_success_message));
						} else {

						}
					} else {
						showToast(mContext
								.getString(R.string.toast_folder_doesnot_exist));
					}
				} catch (Exception e) {
					// throw new Error("Unable to create database");
				} finally {

				}
				handler.sendEmptyMessage(0);
			}
		};

		importDBfile.start();
	}

	private void checkExternalStorage() {
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

	private void showToast(final String toast) {
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(Pedometer.getInstance(), toast,
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
