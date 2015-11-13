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



//import java.util.Arrays;
import java.util.Calendar;
//import java.util.Collections;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.corusen.steponfre.R;
import com.corusen.steponfre.database.Constants;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class FragmentFacebook extends Fragment {

	private String mMessageStep1;
	private String mMessageStep2;
	private String mMessageStep3;
	private String mMessageStep4;

	private ImageButton postStatusUpdateButton;
	private ProfilePictureView profilePictureView;

	//private static final String PERMISSION = ""; //V541 "publish_actions";
	private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";
	private PendingAction pendingAction = PendingAction.NONE;
	private boolean canPresentShareDialog;
	private CallbackManager callbackManager;
	private ProfileTracker profileTracker;
	private ShareDialog shareDialog;

	private final FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
		@Override
		public void onCancel() {
			Log.d("HelloFacebook", "Canceled");
		}

		@Override
		public void onError(FacebookException error) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
			String title = getString(R.string.error);
			String alertMessage = error.getMessage();
			showResult(title, alertMessage);
		}

		@Override
		public void onSuccess(Sharer.Result result) {
			Log.d("HelloFacebook", "Success!");
			if (result.getPostId() != null) {
				String title = getString(R.string.success);
				String id = result.getPostId();
				String alertMessage = getString(R.string.successfully_posted_post, id);
				showResult(title, alertMessage);
			}
		}

		private void showResult(String title, String alertMessage) {
			new AlertDialog.Builder(getActivity())
					.setTitle(title)
					.setMessage(alertMessage)
					.setPositiveButton(R.string.ok, null)
					.show();
		}
	};

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(this.getActivity().getApplicationContext());

		callbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						handlePendingAction();
						updateUI();
					}

					@Override
					public void onCancel() {
						if (pendingAction != PendingAction.NONE) {
							showAlert();
							pendingAction = PendingAction.NONE;
						}
						updateUI();
					}

					@Override
					public void onError(FacebookException exception) {
						if (pendingAction != PendingAction.NONE
								&& exception instanceof FacebookAuthorizationException) {
							showAlert();
							pendingAction = PendingAction.NONE;
						}
						updateUI();
					}

					private void showAlert() {
						new AlertDialog.Builder(getActivity())
								.setTitle(R.string.cancelled)
								.setMessage(R.string.permission_not_granted)
								.setPositiveButton(R.string.ok, null)
								.show();
					}
				});

		shareDialog = new ShareDialog(this);
		shareDialog.registerCallback(
				callbackManager,
				shareCallback);

		if (savedInstanceState != null) {
			String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
				updateUI();
				// It's possible that we were waiting for Profile to be populated in order to post a status update.
				handlePendingAction();
			}
		};

		// Can we present the share dialog for regular links?
		canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View mView = inflater.inflate(AccuService.mScreenFragmentShareFacebook, container, false);

		postStatusUpdateButton = (ImageButton) mView.findViewById(R.id.postStatusUpdateButton);
		postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onClickPostStatusUpdate();
			}
		});

		profilePictureView = (ProfilePictureView) mView.findViewById(R.id.profilePicture);

		LoginButton loginButton = (LoginButton) mView.findViewById(R.id.login_button);
		//loginButton.setReadPermissions("user_friends");
		loginButton.setFragment(this); // If using in a fragment

		// Callback registration
		loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {

			}

			@Override
			public void onCancel() {
				// App code
			}

			@Override
			public void onError(FacebookException exception) {
				// App code
			}
		});

		float mfDistanceFactor;
		String mDistanceUnit;
		if (Pedometer.mPedometerSettings.isMetric()) {
			mfDistanceFactor = 1.60934f;
			mDistanceUnit = getString(R.string.widget_km);
		} else {
			mfDistanceFactor = 1.0f;
			mDistanceUnit = getString(R.string.widget_mi);
		}

		Calendar today = Calendar.getInstance();
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH) + 1;
		int day = today.get(Calendar.DATE);

		Pedometer.mDB.open();
		Cursor c = Pedometer.mDB.queryDayMaxSteps(year, month, day);

		int mColumnIndexSteps, mColumnIndexDistance, mColumnIndexCalories, mColumnIndexSteptime;
		mColumnIndexSteps = c.getColumnIndex(Constants.KEY_STEPS);
		mColumnIndexDistance = c.getColumnIndex(Constants.KEY_DISTANCE);
		mColumnIndexCalories = c.getColumnIndex(Constants.KEY_CALORIES);
		mColumnIndexSteptime = c.getColumnIndex(Constants.KEY_STEPTIME);

		mMessageStep1 = " \u2022 " + String.format("%d", c.getInt(mColumnIndexSteps)) + " " + getString(R.string.steps);
		mMessageStep2 = " \u2022 " + String.format("%.2f", c.getFloat(mColumnIndexDistance) * mfDistanceFactor) + mDistanceUnit;
		mMessageStep3 = " \u2022 " + String.format("%.1f", c.getFloat(mColumnIndexCalories)) + " " + getString(R.string.cal);
		mMessageStep4 = " \u2022 " + Utils.getHoursMinutesString((int) c.getLong(mColumnIndexSteptime) / 1000) + " " + getString(R.string.hhmm);
		c.close();
		Pedometer.mDB.close();

		TextView textStep1, textStep2;
		textStep1 = (TextView) mView.findViewById(R.id.textStep1);
		textStep2 = (TextView) mView.findViewById(R.id.textStep2);
		textStep1.setText(mMessageStep1 + " " + mMessageStep2);
		textStep2.setText(mMessageStep3 + " " + mMessageStep4);

		postStatusUpdateButton.setContentDescription(getString(R.string.send));

		return mView;
	}


	@Override
	public void onResume() {
		super.onResume();

		// Call the 'activateApp' method to log an app event for use in analytics and advertising
		// reporting.  Do so in the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(getActivity());

		updateUI();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	}

	@Override
	public void onStart() {
		super.onStart();
//        AnalyticsApp.tracker().setScreenName("Facebook541");
//        AnalyticsApp.tracker().send(new HitBuilders.ScreenViewBuilder().build());
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();

		// Call the 'deactivateApp' method to log an app event for use in analytics and advertising
		// reporting.  Do so in the onPause methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.deactivateApp(getActivity());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		profileTracker.stopTracking();
	}

	private void updateUI() {
		boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
		Profile profile = Profile.getCurrentProfile();

		if (enableButtons) {
			postStatusUpdateButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), AccuService.mScreenShareFBSend));
		} else {
			postStatusUpdateButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.fbsend_off_ic));
		}

		if (enableButtons && profile != null) {
			profilePictureView.setProfileId(profile.getId());
			//greeting.setText(getString(R.string.hello_user, profile.getFirstName()));
		} else {
			profilePictureView.setProfileId(null);
			//greeting.setText(null);
		}
		postStatusUpdateButton.setEnabled(enableButtons && canPresentShareDialog);
	}

	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction; // These actions may re-set pendingAction if they are still pending, but we assume they will succeed.
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
			case NONE:
				break;
			case POST_STATUS_UPDATE:
				postStatusUpdate();
				break;
		}
	}

	private void onClickPostStatusUpdate() {
		//performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog); //V541
		postStatusUpdate(); //V541
	}

	private void postStatusUpdate() {
		Profile profile = Profile.getCurrentProfile();
		ShareLinkContent linkContent = new ShareLinkContent.Builder()
				.setContentTitle(getString(R.string.fb_walk_report))
				.setContentDescription(mMessageStep1 + " " + mMessageStep2 + mMessageStep3 + " " + mMessageStep4)
				.setContentUrl(Uri.parse("http://accupedo.com/accupedo_fb11"))
				.build();
		if (canPresentShareDialog) {
			shareDialog.show(linkContent);
		} else if (profile != null && hasPublishPermission()) {
			ShareApi.share(linkContent, shareCallback);
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private boolean hasPublishPermission() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return accessToken != null && accessToken.getPermissions().contains("publish_actions");
	}

//    private void performPublish(PendingAction action, boolean allowNoToken) {
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if (accessToken != null) {
//            pendingAction = action;
//            if (hasPublishPermission()) {
//                // We can do the action right away.
//                handlePendingAction();
//                return;
//            } else {
//                // We need to get new permissions, then complete the action when we get called back.
//                LoginManager.getInstance().logInWithPublishPermissions(
//                        this,
//                    Collections.singletonList(PERMISSION));
//                return;
//            }
//        }
//
//        if (allowNoToken) {
//            pendingAction = action;
//            handlePendingAction();
//        }
//    }
}


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
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.text.Editable;
//import android.text.TextWatcher;
////import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.base.AnalyticsSampleApp.TrackerName;
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
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//
//public class FragmentFacebook extends Fragment {
//
//	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
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
//	private int mColumnIndexSteps;
//	private int mColumnIndexDistance;
//	private int mColumnIndexCalories;
//	private int mColumnIndexSteptime;
//
//	private LoginButton loginButton;
//	private ImageButton postStatusUpdateButton;
//
//	private PendingAction pendingAction = PendingAction.NONE;
//	private GraphUser user;
//	private EditText editText;
//	private TextView textStep1;
//	private TextView textStep2;
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
//		public void call(Session session, SessionState state, Exception exception) {
//			onSessionStateChange(session, state, exception);
//		}
//	};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		uiHelper = new UiLifecycleHelper(FragmentFacebook.this.getActivity(), callback);
//		uiHelper.onCreate(savedInstanceState);
//
//		if (savedInstanceState != null) {
//			String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
//			pendingAction = PendingAction.valueOf(name);
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//		mView = inflater.inflate(AccuService.mScreenFragmentShareFacebook, container, false);
//		loginButton = (LoginButton) mView.findViewById(R.id.login_button);
//		loginButton.setPublishPermissions(Arrays.asList("publish_actions"));
//		loginButton.setFragment(this);
//		loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
//			@Override
//			public void onUserInfoFetched(GraphUser user) {
//
//				FragmentFacebook.this.user = user;
//				handlePendingAction();
//			}
//		});
//
//		postStatusUpdateButton = (ImageButton) mView.findViewById(R.id.postStatusUpdateButton);
//		postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				onClickPostStatusUpdate();
//			}
//		});
//
//		// controlsContainer = (ViewGroup) mView
//		// .findViewById(R.id.main_ui_container);
//
//		editText = (EditText) mView.findViewById(R.id.editMessage);
//		editText.setTextColor(getResources().getColor(R.color.myblack));
//		editText.addTextChangedListener(new TextWatcher() {
//			public void afterTextChanged(Editable s) {
//				//Log.v("TAG", "afterTextChanged");
//			}
//
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				//Log.v("TAG", "beforeTextChanged");
//			}
//
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				//Log.v("TAG", "onTextChanged");
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
//		c.getColumnIndex(Constants.KEY_YEAR);
//		c.getColumnIndex(Constants.KEY_MONTH);
//		c.getColumnIndex(Constants.KEY_DAY);
//		mColumnIndexSteps = c.getColumnIndex(Constants.KEY_STEPS);
//		mColumnIndexDistance = c.getColumnIndex(Constants.KEY_DISTANCE);
//		mColumnIndexCalories = c.getColumnIndex(Constants.KEY_CALORIES);
//		mColumnIndexSteptime = c.getColumnIndex(Constants.KEY_STEPTIME);
//
//		// getString(R.string.fb_walk_report) + "\r\n" + " \u2022 "
//		mMessageStep1 = " \u2022 " + String.format("%d", c.getInt(mColumnIndexSteps)) + " " + getString(R.string.steps);
//		mMessageStep2 = " \u2022 " + String.format("%.2f", c.getFloat(mColumnIndexDistance) * mfDistanceFactor) + mDistanceUnit;
//		mMessageStep3 = " \u2022 " + String.format("%.1f", c.getFloat(mColumnIndexCalories)) + " " + getString(R.string.cal);
//		mMessageStep4 = " \u2022 " + Utils.getHoursMinutesString((int) c.getLong(mColumnIndexSteptime) / 1000) + " " + getString(R.string.hhmm);
//		c.close();
//		Pedometer.mDB.close();
//
//		textStep1 = (TextView) mView.findViewById(R.id.textStep1);
//		textStep2 = (TextView) mView.findViewById(R.id.textStep2);
//		textStep1.setText(mMessageStep1 + " " + mMessageStep2);
//		textStep2.setText(mMessageStep3 + " " + mMessageStep4);
//
//		return mView;
//	}
//
//	@Override
//	public void onStart() {
//		super.onStart();
//		Tracker t = ((AnalyticsSampleApp) Pedometer.getInstance().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Facebook");
//		t.send(new HitBuilders.AppViewBuilder().build());
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
//		//Log.i("Login", "Login here2");
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
//	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//		if (pendingAction != PendingAction.NONE
//				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
//			new AlertDialog.Builder(FragmentFacebook.this.getActivity()).setTitle(R.string.cancelled).setMessage(R.string.permission_not_granted)
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
//			postStatusUpdateButton.setImageDrawable(this.getResources().getDrawable(AccuService.mScreenShareFBSend));
//		} else {
//			postStatusUpdateButton.setImageDrawable(this.getResources().getDrawable(R.drawable.fbsend_off_ic));
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
//	private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
//		String title = null;
//		String alertMessage = null;
//		if (error == null) {
//			title = getString(R.string.success);
//			String id = result.cast(GraphObjectWithId.class).getId();
//			alertMessage = getString(R.string.successfully_posted_post, message, id);
//		} else {
//			title = getString(R.string.error);
//			alertMessage = error.getErrorMessage();
//		}
//
//		new AlertDialog.Builder(this.getActivity()).setTitle(title).setMessage(alertMessage).setPositiveButton(R.string.ok, null).show();
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
//				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
//				session.requestNewPublishPermissions(newPermissionsRequest);
//				return;
//			}
//
//			Bundle postParams = new Bundle();
//			postParams.putString("name", mMessageStep1 + "\r\n" + mMessageStep2 + "\r\n" + mMessageStep3 + "\r\n" + mMessageStep4);
//			postParams.putString("caption", mMessageTyped);
//			postParams.putString("description", getString(R.string.fb_lets_walk));
//			postParams.putString("link", "http://www.accupedo.com");
//			postParams.putString("picture", "http://www.accupedo.com/images/istepon_icon.png");
//
//			Request.Callback callback = new Request.Callback() {
//				public void onCompleted(Response response) {
//					JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
//					String postId = null;
//					try {
//						postId = graphResponse.getString("id");
//					} catch (JSONException e) {
//						// Log.i(TAG, "JSON error " + e.getMessage());
//					}
//					FacebookRequestError error = response.getError();
//					if (error != null) {
//						Toast.makeText(getActivity(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
//					} else {
//						Toast.makeText(getActivity(), getString(R.string.fb_updated), Toast.LENGTH_LONG).show();
//					}
//				}
//			};
//
//			Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);
//
//			RequestAsyncTask task = new RequestAsyncTask(request);
//			task.execute();
//		}
//
//	}
//
//	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
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
//			final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
//			Request request = Request.newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {
//				@Override
//				public void onCompleted(Response response) {
//					showPublishResult(message, response.getGraphObject(), response.getError());
//				}
//			});
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
//		return session != null && session.getPermissions().contains("publish_actions");
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
//}
