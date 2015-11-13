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
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Date;
//import java.util.List;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.location.Location;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.actionbarsherlock.app.SherlockFragment;
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.database.Constants;
//import com.facebook.FacebookAuthorizationException;
//
//import com.facebook.FacebookOperationCanceledException;
//import com.facebook.FacebookRequestError;
//import com.facebook.HttpMethod;
//import com.facebook.Request;
//import com.facebook.RequestAsyncTask;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphObject;
//
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
//import com.facebook.widget.ProfilePictureView;
//
//public class FragmentShare extends SherlockFragment {
//
//	private static final String ACCUPEDO_FOLDERNAME = "/Accupedo";
//	private static final String ACCUPEDO_CSV_FILENAME = "Accupedo.csv";
//
//	private static final List<String> PERMISSIONS = Arrays
//			.asList("publish_actions");
//	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
//	private boolean pendingPublishReauthorization = false;
//
//	private final String PENDING_ACTION_BUNDLE_KEY = "com.corusen.steponfre:PendingAction";
//
//	private String mMessageStep1;
//	private String mMessageStep2;
//	private String mMessageStep3;
//	private String mMessageStep4;
//	private String mMessageTyped = " ";
//	private float mfDistanceFactor;
//	private String mDistanceUnit;
//
//	public boolean mExternalStorageAvailable = false;
//	public boolean mExternalStorageWriteable = false;
//
//	private ProgressDialog mProgressDialog;
//	private FileWriter mWriter;
//	private Cursor mCursorThread;
//	private int mColumnIndexYear;
//	private int mColumnIndexMonth;
//	private int mColumnIndexDay;
//	private int mColumnIndexSteps;
//	private int mColumnIndexDistance;
//	private int mColumnIndexCalories;
//	private int mColumnIndexSteptime;
//
//	private LoginButton loginButton;
//	private ImageButton postStatusUpdateButton;
//	private ImageButton sendcvsButton;
//	private PendingAction pendingAction = PendingAction.NONE;
//	private GraphUser user;
//	private EditText editText;
//	private TextView textStep1;
//	private TextView textStep2;
//	//private ViewGroup controlsContainer;
//
//	private View mView;
//
//	private enum PendingAction {
//		NONE, POST_PHOTO, POST_STATUS_UPDATE
//	}
//
//	private UiLifecycleHelper uiHelper;
//
//	private Session.StatusCallback callback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state,
//				Exception exception) {
//			onSessionStateChange(session, state, exception);
//		}
//	};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		uiHelper = new UiLifecycleHelper(
//				FragmentShare.this.getSherlockActivity(), callback);
//		uiHelper.onCreate(savedInstanceState);
//
//		if (savedInstanceState != null) {
//			String name = savedInstanceState
//					.getString(PENDING_ACTION_BUNDLE_KEY);
//			pendingAction = PendingAction.valueOf(name);
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//
//		mView = inflater.inflate(R.layout.light_fragment_share_facebook, container, false);
//		loginButton = (LoginButton) mView.findViewById(R.id.login_button);
//		loginButton.setPublishPermissions(Arrays.asList("publish_actions"));
//		loginButton.setFragment(this);
//		loginButton
//				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
//					@Override
//					public void onUserInfoFetched(GraphUser user) {
//
//						FragmentShare.this.user = user;
//						handlePendingAction();
//					}
//				});
//
//		postStatusUpdateButton = (ImageButton) mView
//				.findViewById(R.id.postStatusUpdateButton);
//		postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				onClickPostStatusUpdate();
//			}
//		});
//
////		controlsContainer = (ViewGroup) mView
////				.findViewById(R.id.main_ui_container);
//
//		sendcvsButton = (ImageButton) mView.findViewById(R.id.sendcvsButton);
//		sendcvsButton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				sendCSVfile();
//			}
//		});
//
//		editText = (EditText) mView.findViewById(R.id.editMessage);
//		editText.addTextChangedListener(new TextWatcher() {
//			public void afterTextChanged(Editable s) {
//				Log.v("TAG", "afterTextChanged");
//			}
//
//			public void beforeTextChanged(CharSequence arg0, int arg1,
//					int arg2, int arg3) {
//				Log.v("TAG", "beforeTextChanged");
//			}
//
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				Log.v("TAG", "onTextChanged");
//				mMessageTyped = editText.getText().toString();
//
//			}
//		});
//
//		if (Pedometer.mPedometerSettings.isMetric()) {
//			mfDistanceFactor = 1.60934f;
//			mDistanceUnit = getString(R.string.widget_km);
//		} else {
//			mfDistanceFactor = 1.0f;
//			mDistanceUnit = getString(R.string.widget_mi);
//		}
//
//		Calendar today = Calendar.getInstance();
//		int year = today.get(Calendar.YEAR);
//		int month = today.get(Calendar.MONTH) + 1;
//		int day = today.get(Calendar.DATE);
//
//		Pedometer.mDB.open();
//		Cursor c = Pedometer.mDB.queryDayMaxSteps(year, month, day);
//		mColumnIndexYear = c.getColumnIndex(Constants.KEY_YEAR);
//		mColumnIndexMonth = c.getColumnIndex(Constants.KEY_MONTH);
//		mColumnIndexDay = c.getColumnIndex(Constants.KEY_DAY);
//		mColumnIndexSteps = c.getColumnIndex(Constants.KEY_STEPS);
//		mColumnIndexDistance = c.getColumnIndex(Constants.KEY_DISTANCE);
//		mColumnIndexCalories = c.getColumnIndex(Constants.KEY_CALORIES);
//		mColumnIndexSteptime = c.getColumnIndex(Constants.KEY_STEPTIME);
//
//		// getString(R.string.fb_walk_report) + "\r\n" + " \u2022 "
//		mMessageStep1 = " \u2022 "
//				+ String.format("%d", c.getInt(mColumnIndexSteps))
//				+ " "
//				+ getString(R.string.steps);
//		mMessageStep2 = " \u2022 "
//				+ String.format("%.2f", c.getFloat(mColumnIndexDistance)
//						* mfDistanceFactor)
//				+ mDistanceUnit;
//		mMessageStep3 = " \u2022 "
//				+ String.format("%.1f", c.getFloat(mColumnIndexCalories))
//				+ " "
//				+ getString(R.string.cal);
//		mMessageStep4 =" \u2022 "
//				+ Utils.getHoursMinutesString((int) c
//						.getLong(mColumnIndexSteptime) / 1000) + " "
//				+ getString(R.string.hhmm);
//		c.close();
//		Pedometer.mDB.close();
//
//		textStep1 = (TextView) mView.findViewById(R.id.textStep1);
//		textStep2 = (TextView) mView.findViewById(R.id.textStep2);
//		textStep1.setText(mMessageStep1 +" " + mMessageStep2);
//		textStep2.setText(mMessageStep3 +" " + mMessageStep4);
//
//		return mView;
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//
//		Session session = Session.getActiveSession();
//		if (session != null && (session.isOpened() || session.isClosed())) {
//			onSessionStateChange(session, session.getState(), null);
//		}
//
//		uiHelper.onResume();
//		Log.i("Login", "Login here2");
//		updateUI();
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		uiHelper.onSaveInstanceState(outState);
//
//		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		uiHelper.onActivityResult(requestCode, resultCode, data);
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		uiHelper.onPause();
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		uiHelper.onDestroy();
//	}
//
//	private void onSessionStateChange(Session session, SessionState state,
//			Exception exception) {
//		if (pendingAction != PendingAction.NONE
//				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
//			new AlertDialog.Builder(FragmentShare.this.getSherlockActivity())
//					.setTitle(R.string.cancelled)
//					.setMessage(R.string.permission_not_granted)
//					.setPositiveButton(R.string.ok, null).show();
//			pendingAction = PendingAction.NONE;
//		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
//			handlePendingAction();
//		}
//		updateUI();
//	}
//
//	private void updateUI() {
//		Session session = Session.getActiveSession();
//		boolean enableButtons = (session != null && session.isOpened());
//		if (enableButtons) {
//			postStatusUpdateButton.setImageDrawable(Pedometer.getInstance()
//					.getResources().getDrawable(R.drawable.blue_fbsend_ic));
//		} else {
//			postStatusUpdateButton.setImageDrawable(Pedometer.getInstance()
//					.getResources().getDrawable(R.drawable.fbsend_off_ic));
//		}
//		postStatusUpdateButton.setEnabled(enableButtons);
//	}
//
//	@SuppressWarnings("incomplete-switch")
//	private void handlePendingAction() {
//		PendingAction previouslyPendingAction = pendingAction;
//		// These actions may re-set pendingAction if they are still pending, but
//		// we assume they
//		// will succeed.
//		pendingAction = PendingAction.NONE;
//
//		switch (previouslyPendingAction) {
//		case POST_PHOTO:
//			// postPhoto();
//			break;
//		case POST_STATUS_UPDATE:
//			postStatusUpdate();
//			break;
//		}
//	}
//
//	private interface GraphObjectWithId extends GraphObject {
//		String getId();
//	}
//
//	private void showPublishResult(String message, GraphObject result,
//			FacebookRequestError error) {
//		String title = null;
//		String alertMessage = null;
//		if (error == null) {
//			title = getString(R.string.success);
//			String id = result.cast(GraphObjectWithId.class).getId();
//			alertMessage = getString(R.string.successfully_posted_post,
//					message, id);
//		} else {
//			title = getString(R.string.error);
//			alertMessage = error.getErrorMessage();
//		}
//
//		new AlertDialog.Builder(this.getSherlockActivity()).setTitle(title)
//				.setMessage(alertMessage).setPositiveButton(R.string.ok, null)
//				.show();
//	}
//
//	private void onClickPostStatusUpdate() {
//		// performPublish(PendingAction.POST_STATUS_UPDATE);
//		publishStory();
//	}
//
//	private void publishStory() {
//		Session session = Session.getActiveSession();
//
//		if (session != null) {
//
//			// Check for publish permissions
//			List<String> permissions = session.getPermissions();
//			if (!isSubsetOf(PERMISSIONS, permissions)) {
//				pendingPublishReauthorization = true;
//				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
//						this, PERMISSIONS);
//				session.requestNewPublishPermissions(newPermissionsRequest);
//				return;
//			}
//
//			Bundle postParams = new Bundle();
//			postParams.putString("name", mMessageStep1 + "\r\n" + mMessageStep2+ "\r\n" + mMessageStep3+ "\r\n" + mMessageStep4);
//			postParams.putString("caption", mMessageTyped);
//			postParams.putString("description",
//					getString(R.string.fb_lets_walk));
//			postParams.putString("link", "http://www.accupedo.com");
//			postParams.putString("picture",
//					"http://www.accupedo.com/images/accupedo_icon.png");
//
//			Request.Callback callback = new Request.Callback() {
//				public void onCompleted(Response response) {
//					JSONObject graphResponse = response.getGraphObject()
//							.getInnerJSONObject();
//					String postId = null;
//					try {
//						postId = graphResponse.getString("id");
//					} catch (JSONException e) {
//						// Log.i(TAG, "JSON error " + e.getMessage());
//					}
//					FacebookRequestError error = response.getError();
//					if (error != null) {
//						Toast.makeText(Pedometer.getInstance(),
//								error.getErrorMessage(), Toast.LENGTH_SHORT)
//								.show();
//					} else {
//						Toast.makeText(Pedometer.getInstance(),
//								getString(R.string.fb_updated),
//								Toast.LENGTH_LONG).show();
//					}
//				}
//			};
//
//			Request request = new Request(session, "me/feed", postParams,
//					HttpMethod.POST, callback);
//
//			RequestAsyncTask task = new RequestAsyncTask(request);
//			task.execute();
//		}
//
//	}
//
//	private boolean isSubsetOf(Collection<String> subset,
//			Collection<String> superset) {
//		for (String string : subset) {
//			if (!superset.contains(string)) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	private void postStatusUpdate() {
//		if (user != null && hasPublishPermission()) {
//			final String message = getString(R.string.status_update,
//					user.getFirstName(), (new Date().toString()));
//			Request request = Request.newStatusUpdateRequest(
//					Session.getActiveSession(), message,
//					new Request.Callback() {
//						@Override
//						public void onCompleted(Response response) {
//							showPublishResult(message,
//									response.getGraphObject(),
//									response.getError());
//						}
//					});
//			request.executeAsync();
//		} else {
//			pendingAction = PendingAction.POST_STATUS_UPDATE;
//		}
//	}
//
//	// private void showAlert(String title, String message) {
//	// new AlertDialog.Builder(this.getSherlockActivity()).setTitle(title)
//	// .setMessage(message).setPositiveButton(R.string.ok, null)
//	// .show();
//	// }
//
//	private boolean hasPublishPermission() {
//		Session session = Session.getActiveSession();
//		return session != null
//				&& session.getPermissions().contains("publish_actions");
//	}
//
//	// private void performPublish(PendingAction action) {
//	// Session session = Session.getActiveSession();
//	// if (session != null) {
//	// pendingAction = action;
//	// if (hasPublishPermission()) {
//	// // We can do the action right away.
//	// handlePendingAction();
//	// } else {
//	// // We need to get new permissions, then complete the action when
//	// // we get called back.
//	// session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
//	// this, PERMISSIONS));
//	// }
//	// }
//	// }
//
//	public void generateCsvFile() {
//		boolean success = true;
//		File folder;
//		String folderName = ACCUPEDO_FOLDERNAME;
//		String fileName = ACCUPEDO_CSV_FILENAME; // "AccupedoSteps" + ".csv";//
//													// +String.valueOf(++mFileIndex)+".csv";
//		checkExternalStorage();
//
//		try {
//			// File root = Environment.getExternalStorageDirectory();
//			if (mExternalStorageAvailable && mExternalStorageWriteable) {
//				folder = new File(Environment.getExternalStorageDirectory()
//						+ folderName);
//				if (!folder.exists()) {
//					success = folder.mkdirs();
//				}
//			} else {
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_need_sdcard),
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			if (success) {
//				File gpxfile = new File(folder, fileName);
//				FileWriter writer = new FileWriter(gpxfile);
//
//				Pedometer.mDB.open();
//				Cursor c = Pedometer.mDB.queryAllDayMaxSteps();
//				if (c.moveToFirst()) {
//					do {
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_YEAR))));
//						writer.append(',');
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_MONTH))));
//						writer.append(',');
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_DAY))));
//						writer.append(',');
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_STEPS))));
//						writer.append(',');
//						writer.append(String.format(
//								"%5.2f",
//								c.getFloat(c
//										.getColumnIndex(Constants.KEY_DISTANCE))
//										* mfDistanceFactor));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c
//								.getColumnIndex(Constants.KEY_CALORIES))));
//						writer.append(',');
//						writer.append(Utils.getHoursMinutesString((int) c.getLong(c
//								.getColumnIndex(Constants.KEY_STEPTIME)) / 1000));
//						writer.append("\r\n");
//					} while (c.moveToNext());
//					c.close();
//					Pedometer.mDB.close();
//				}
//				writer.flush();
//				writer.close();
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_save), Toast.LENGTH_SHORT)
//						.show();
//			} else {
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_folder), Toast.LENGTH_SHORT)
//						.show();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void generateAllCsvFile() {
//		boolean success = true;
//		File folder;
//		String folderName = ACCUPEDO_FOLDERNAME;
//		String fileName = ACCUPEDO_CSV_FILENAME;
//		checkExternalStorage();
//
//		try {
//			// File root = Environment.getExternalStorageDirectory();
//			if (mExternalStorageAvailable && mExternalStorageWriteable) {
//				folder = new File(Environment.getExternalStorageDirectory()
//						+ folderName);
//				if (!folder.exists()) {
//					success = folder.mkdirs();
//				}
//			} else {
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_need_sdcard),
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			if (success) {
//				File gpxfile = new File(folder, fileName);
//				FileWriter writer = new FileWriter(gpxfile);
//
//				Pedometer.mDB.open();
//				Cursor c = Pedometer.mDB.queryDataBase();
//				writer.append("ID, Lap, Year, Month, Day, Hour, Minute, LapSteps, LapDistance, LapCalories, LapStepTime, Steps, Distance, Caloires, Speed, Pace, StepTime, Acheivement");
//				writer.append("\r\n");
//
//				if (c.moveToFirst()) {
//					do {
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_ID))));
//						writer.append(',');
//
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_LAP))));
//						writer.append(',');
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_YEAR))));
//						writer.append(',');
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_MONTH))));
//						writer.append(',');
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_DAY))));
//						writer.append(',');
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_HOUR))));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c
//								.getColumnIndex(Constants.KEY_MINUTE))));
//						writer.append(',');
//
//						writer.append(String.format("%5d", c.getInt(c
//								.getColumnIndex(Constants.KEY_LAPSTEPS))));
//						writer.append(',');
//						writer.append(String.format(
//								"%5.2f",
//								c.getFloat(c
//										.getColumnIndex(Constants.KEY_LAPDISTANCE))
//										* mfDistanceFactor));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c
//								.getColumnIndex(Constants.KEY_LAPCALORIES))));
//						writer.append(',');
//						writer.append(Utils.getHoursMinutesString((int) c.getLong(c
//								.getColumnIndex(Constants.KEY_LAPSTEPTIME)) / 1000));
//						writer.append(',');
//
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_STEPS))));
//						writer.append(',');
//						writer.append(String.format(
//								"%5.2f",
//								c.getFloat(c
//										.getColumnIndex(Constants.KEY_DISTANCE))
//										* mfDistanceFactor));
//						writer.append(',');
//						writer.append(String.format("%5d", c.getInt(c
//								.getColumnIndex(Constants.KEY_CALORIES))));
//						writer.append(',');
//						writer.append(String.format(
//								"%5.2f",
//								c.getFloat(c
//										.getColumnIndex(Constants.KEY_SPEED))
//										* mfDistanceFactor));
//						writer.append(',');
//						writer.append(String.format("%5d",
//								c.getInt(c.getColumnIndex(Constants.KEY_PACE))));
//						writer.append(',');
//
//						writer.append(Utils.getHoursMinutesString((int) c.getLong(c
//								.getColumnIndex(Constants.KEY_STEPTIME)) / 1000));
//						writer.append(',');
//
//						writer.append(String.format("%5d", c.getInt(c
//								.getColumnIndex(Constants.KEY_ACHIEVEMENT))));
//
//						writer.append("\r\n");
//
//					} while (c.moveToNext());
//					c.close();
//					Pedometer.mDB.close();
//				}
//				writer.flush();
//				writer.close();
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_save), Toast.LENGTH_SHORT)
//						.show();
//			} else {
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_folder), Toast.LENGTH_SHORT)
//						.show();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void checkExternalStorage() {
//		String string = Environment.getExternalStorageState();
//
//		if (Environment.MEDIA_MOUNTED.equals(string)) {
//			mExternalStorageAvailable = mExternalStorageWriteable = true;
//		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(string)) {
//			mExternalStorageAvailable = true;
//			mExternalStorageWriteable = false;
//
//		} else {
//			mExternalStorageAvailable = mExternalStorageWriteable = false;
//
//		}
//	}
//
//	void sendCSVfile() {
//		mProgressDialog = new ProgressDialog(Pedometer.getInstance());
//		mProgressDialog.setMessage(getString(R.string.wait_for_generating_csv));
//		mProgressDialog.setIndeterminate(false);
//		mProgressDialog.setMax(100);
//		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		mProgressDialog.show();
//
//		final Handler handler = new Handler() {
//			public void handleMessage(Message msg) {
//				mCursorThread.close();
//				Pedometer.mDB.close();
//				mProgressDialog.dismiss();
//				emailCSVfile();
//			}
//		};
//
//		Thread sendCSVfile = new Thread() {
//			public void run() {
//
//				int i = 0;
//				int j = mCursorThread.getCount();
//				try {
//					if (mCursorThread.moveToFirst()) {
//						do {
//
//							mWriter.append(String.format("%5d",
//									mCursorThread.getInt(mColumnIndexYear)));
//							mWriter.append(',');
//							mWriter.append(String.format("%5d",
//									mCursorThread.getInt(mColumnIndexMonth)));
//							mWriter.append(',');
//							mWriter.append(String.format("%5d",
//									mCursorThread.getInt(mColumnIndexDay)));
//							mWriter.append(',');
//							mWriter.append(String.format("%5d",
//									mCursorThread.getInt(mColumnIndexSteps)));
//							mWriter.append(',');
//							mWriter.append(String.format(
//									"%5.2f",
//									mCursorThread
//											.getFloat(mColumnIndexDistance)
//											* mfDistanceFactor));
//							mWriter.append(',');
//							mWriter.append(String.format("%5d",
//									mCursorThread.getInt(mColumnIndexCalories)));
//							mWriter.append(',');
//							mWriter.append(Utils.getHoursMinutesString((int) mCursorThread
//									.getLong(mColumnIndexSteptime) / 1000));
//							mWriter.append("\r\n");
//
//							mProgressDialog.setProgress((int) (i++ * 100 / j));
//
//						} while (mCursorThread.moveToNext());
//						mWriter.flush();
//						mWriter.close();
//					}
//
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				handler.sendEmptyMessage(0);
//			}
//		};
//
//		boolean success = true;
//		File folder;
//		String folderName = ACCUPEDO_FOLDERNAME; // "/Accupedo";
//		String fileName = ACCUPEDO_CSV_FILENAME; // "AccupedoSteps" + ".csv";//
//													// +String.valueOf(++mFileIndex)+".csv";
//		checkExternalStorage();
//
//		try {
//			// File root = Environment.getExternalStorageDirectory();
//			if (mExternalStorageAvailable && mExternalStorageWriteable) {
//				folder = new File(Environment.getExternalStorageDirectory()
//						+ folderName);
//				if (!folder.exists()) {
//					success = folder.mkdirs();
//				}
//			} else {
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_need_sdcard),
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			if (success) {
//				File gpxfile = new File(folder, fileName);
//				mWriter = new FileWriter(gpxfile);
//
//				Pedometer.mDB.open();
//				mCursorThread = Pedometer.mDB.queryAllDayMaxSteps();
//				mColumnIndexYear = mCursorThread
//						.getColumnIndex(Constants.KEY_YEAR);
//				mColumnIndexMonth = mCursorThread
//						.getColumnIndex(Constants.KEY_MONTH);
//				mColumnIndexDay = mCursorThread
//						.getColumnIndex(Constants.KEY_DAY);
//				mColumnIndexSteps = mCursorThread
//						.getColumnIndex(Constants.KEY_STEPS);
//				mColumnIndexDistance = mCursorThread
//						.getColumnIndex(Constants.KEY_DISTANCE);
//				mColumnIndexCalories = mCursorThread
//						.getColumnIndex(Constants.KEY_CALORIES);
//				mColumnIndexSteptime = mCursorThread
//						.getColumnIndex(Constants.KEY_STEPTIME);
//
//				sendCSVfile.start();
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_save), Toast.LENGTH_SHORT)
//						.show();
//			} else {
//				Toast.makeText(Pedometer.getInstance(),
//						getString(R.string.toast_folder), Toast.LENGTH_SHORT)
//						.show();
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		// generateCsvFile();
//		// generateAllCsvFile();
//	}
//
//	void emailCSVfile() {
//		String path;
//		if (mExternalStorageAvailable && mExternalStorageWriteable) {
//			path = Environment.getExternalStorageDirectory().getPath()
//					+ ACCUPEDO_FOLDERNAME + "/" + ACCUPEDO_CSV_FILENAME; // +
//																			// "/Accupedo/AccupedoSteps.csv";
//		} else {
//			return;
//		}
//
//		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
//		intent.setType("text/html");
//		intent.putExtra(Intent.EXTRA_SUBJECT,
//				getString(R.string.email_file_subject));
//		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
//		startActivity(intent);
//	}
//
//}
