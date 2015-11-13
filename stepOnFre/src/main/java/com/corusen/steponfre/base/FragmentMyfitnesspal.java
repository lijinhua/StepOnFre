package com.corusen.steponfre.base;
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
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.nio.channels.FileChannel;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.ParseException;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.actionbarsherlock.app.SherlockFragment;
//import com.corusen.steponfre.Pedometer;
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.database.Constants;
//import com.google.gson.GsonBuilder;
//
//import com.myfitnesspal.android.sdk.MfpAuthError;
//import com.myfitnesspal.android.sdk.MfpAuthListener;
//import com.myfitnesspal.android.sdk.MfpWebError;
//import com.myfitnesspal.android.sdk.MyFitnessPal;
//import com.myfitnesspal.android.sdk.ResponseType;
//import com.myfitnesspal.android.sdk.Scope;
//import com.myfitnesspal.shared.utils.Ln;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.preference.PreferenceManager;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.text.format.DateFormat;
//import android.util.JsonWriter;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class FragmentMyfitnesspal extends SherlockFragment {
//
//	public static final String MFP_CLIENT_ID = "accupedo";
//	public static final String MFP_APP_ID = "79656b6e6f6d";
//	public static final String MFP_CLIENT_SECRET = "1759a9aa5fd3d1ba3fce";
//
//	public static SharedPreferences mSettings;
//	public static PedometerSettings mPedometerSettings;
//
//	private Button mBtnCode;
//	private Button mBtnTokens;
//	private ImageButton mBtnPost;
//
//	// private static TextView mTextCode;
//	// private static TextView mTextAccessToken;
//	// private static TextView mTextRefreshToken;
//	// private static TextView mTextError;
//
//	private static String mAuthoCode = null;
//	private static String mAccessToken = null;
//	private static String mRefreshToken = null;
//
//	private View.OnClickListener mOnClickListener;
//	private View mView;
//
//	private ActivityShareTabs mActivity;
//
//	private float mfDistanceFactor;
//	private String mUnit;
//
//	private int mColumnIndexYear;
//	private int mColumnIndexMonth;
//	private int mColumnIndexDay;
//	private int mColumnIndexSteps;
//	private int mColumnIndexDistance;
//	private int mColumnIndexCalories;
//	private int mColumnIndexSteptime;
//
//	private String mMessageStep1;
//	private String mMessageStep2;
//	private TextView textStep1;
//
//	private ProgressDialog mProgressDialog;
//	private String mUpdatedDate;
//	private String mUpdatedTime;
//	private String mUrl;
//	private String mJson;
//	private int mIntCalories;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mSettings = PreferenceManager
//				.getDefaultSharedPreferences(getActivity());
//		mPedometerSettings = new PedometerSettings(mSettings);
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
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		mActivity = (ActivityShareTabs) activity;
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		mView = inflater.inflate(AccuService.mScreenFragmentShareMyfitnesspal,
//				container, false);
//		// mTextCode = (TextView) mView.findViewById(R.id.txtCode);
//		// mTextAccessToken = (TextView)
//		// mView.findViewById(R.id.txtAccessToken);
//		// mTextRefreshToken = (TextView)
//		// mView.findViewById(R.id.txtRefreshToken);
//		// mTextError = (TextView) mView.findViewById(R.id.txtError);
//
//		// mBtnCode = (Button) mView.findViewById(R.id.btn_code);
//		mBtnTokens = (Button) mView.findViewById(R.id.btn_tokens);
//		mBtnPost = (ImageButton) mView.findViewById(R.id.btn_post);
//
//		mOnClickListener = new View.OnClickListener() {
//			@Override
//			public void onClick(final View v) {
//				// mActivity.authorizeMFP(ResponseType.Code); //this seems not
//				// necessary
//				if (v == mBtnTokens) {
//					mActivity.authorizeMFP(ResponseType.Token);
//				} else if (v == mBtnPost) {
//					try {
//						postToMyfitnesspal();
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//			}
//		};
//
//		// mBtnCode.setOnClickListener(mOnClickListener);
//		mBtnTokens.setOnClickListener(mOnClickListener);
//		mBtnPost.setOnClickListener(mOnClickListener);
//
//		if (Pedometer.mPedometerSettings.isMetric()) {
//			mfDistanceFactor = 1.60934f;
//			mUnit = "METRIC";
//		} else {
//			mfDistanceFactor = 1.0f;
//			mUnit = "US";
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
//		String stringCalories = String.format("%d",
//				(int) c.getFloat(mColumnIndexCalories));
//		mIntCalories = (int) c.getFloat(mColumnIndexCalories);
//		mUpdatedDate = DateFormat.format("yyyy-MM-dd", today).toString();
//		mUpdatedTime = DateFormat.format("hh:mm:ss", today).toString();
//
//		mMessageStep1 = " \u2022 "
//				+ String.format("%d", (int) c.getFloat(mColumnIndexCalories))
//				+ " " + getString(R.string.cal);
//		c.close();
//		Pedometer.mDB.close();
//
//		mMessageStep2 = " \u2022 " + mUpdatedTime;
//		textStep1 = (TextView) mView.findViewById(R.id.textStep1);
//		textStep1.setText(mMessageStep1 + " " + mMessageStep2);
//
//		return mView;
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//
//		mAccessToken = mPedometerSettings.getMyfitnesspalAccessToken();
//		// if (mAccessToken != null) {
//		// mBtnTokens.setVisibility(View.GONE);
//		// } else {
//		// mBtnTokens.setVisibility(View.VISIBLE);
//		// }
//	}
//
//	// public void setActivity (FragmentShareTabs activity) {
//	// mActivity = activity;
//	// }
//	//
//	public void updateDisplay(Bundle params) {
//		String autho, atoken, rtoken;
//		autho = params
//				.getString(com.myfitnesspal.shared.constants.Constants.Params.CODE);
//		atoken = params
//				.getString(com.myfitnesspal.shared.constants.Constants.Params.ACCESS_TOKEN);
//		rtoken = params
//				.getString(com.myfitnesspal.shared.constants.Constants.Params.REFRESH_TOKEN);
//
//		if (autho != null) {
//			mAuthoCode = autho;
//			mPedometerSettings.setMyfitnesspalAuthoCode(mAuthoCode);
//		}
//		if (atoken != null) {
//			mAccessToken = atoken;
//			mPedometerSettings.setMyfitnesspalAccessToken(mAccessToken);
//		}
//		if (rtoken != null) {
//			mRefreshToken = rtoken;
//			mPedometerSettings.setMyfitnesspalRefreshToken(mRefreshToken);
//		}
//		// mTextCode.setText("code = " + mAuthoCode);
//		// mTextAccessToken.setText("accessToken = " + mAccessToken);
//		// mTextRefreshToken.setText("refreshToken = " + mRefreshToken);
//		// Log.i("FBackup", "updateDisply at myfitnesspal");
//	}
//
//	/**
//	 * Json creation and HTTP Post
//	 * 
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//
//	public void postToMyfitnesspal() throws ParseException, IOException { // static
//																			// ?
//		Map<String, String> comment = new HashMap<String, String>();
//		Map<String, Integer> comment2 = new HashMap<String, Integer>();
//		// comment.put("subject", "Using the GSON library");
//		// comment.put("message", "Using libraries is convenient.");
//		// String json = new GsonBuilder().create().toJson(comment, Map.class);
//		// makeRequest("http://192.168.0.1:3000/post/77/comments", json);
//
//		// Echo API example
//		// comment.put("action", "echo");
//		// comment.put("test_content", "hello, world");
//
//		// log_cardio_exercise
//		// comment.put("action", "log_cardio_exercise");
//		// comment.put("access_token", mAccessToken);
//		// comment.put("app_id", MFP_APP_ID);
//		// comment.put("exercise_id", "log_cardio_exercise");
//		// comment.put("duration", stringSteptime);
//		// comment.put("energy_expended", stringCalories);
//		// comment.put("distance", stringDistance);
//		// comment.put("start_time", "log_cardio_exercise");
//		// comment.put("status_update_message", "log_cardio_exercise");
//
//		// log_expended_energy
//		// comment.put("action", "log_expended_energy");
//		// comment.put("access_token", mAccessToken);
//		// comment.put("app_id", MFP_APP_ID);
//		// comment.put("date", updatedDate);
//		// comment.put("updated at", updatedTime);
//		// comment.put("energy_expended", stringCalories);
//		// mUrl =
//		// "https://api.myfitnesspal.com/client_api/json/1.0.0?client_id=accupedo";
//		// mJson = new GsonBuilder().create().toJson(comment, Map.class);
//
//		// log_expended_energy using JSONObject
//		JSONObject object = new JSONObject();
//		try {
//			object.put("action", "log_expended_energy");
//			object.put("access_token", mAccessToken);
//			object.put("app_id", MFP_APP_ID);
//			object.put("date", mUpdatedDate);
//			object.put("updated at", mUpdatedTime);
//			object.put("energy_expended", new Integer(mIntCalories));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		mUrl = "https://api.myfitnesspal.com/client_api/json/1.0.0?client_id=accupedo";
//		mJson = object.toString();
//
//		makeRequest();
//
//		// HttpResponse response = makeRequest(url, json);
//		// String result = EntityUtils.toString(response.getEntity());
//		// Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
//	}
//
//	public void makeRequest() throws IOException {
//		mProgressDialog = new ProgressDialog(Pedometer.getInstance());
//		mProgressDialog.setMessage(getActivity().getString(
//				R.string.wait_for_importing_db));
//		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		mProgressDialog.show();
//
//		final Handler handler = new Handler() {
//			public void handleMessage(Message msg) {
//				mProgressDialog.dismiss();
//			}
//		};
//
//		Thread exportData = new Thread() {
//			public void run() {
//				try {
//					HttpPost httpPost = new HttpPost(mUrl);
//					httpPost.setEntity(new StringEntity(mJson));
//					// httpPost.setHeader("Accept", "application/json");
//					// httpPost.setHeader("Content-type", "application/json");
//					httpPost.setHeader("Content-type",
//							"application/json; charset=utf-8");
//
//					HttpResponse response = new DefaultHttpClient()
//							.execute(httpPost);
//					String result = EntityUtils.toString(response.getEntity());
//					showToast(result);
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				} catch (ClientProtocolException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				handler.sendEmptyMessage(0);
//			}
//		};
//
//		exportData.start();
//	}
//
//	// class MakeRequestTask extends AsyncTask<String, Void, ARequest> {
//	//
//	// private Exception exception;
//	//
//	// protected ARequest doInBackground(String... urls) {
//	// try {
//	// URL url= new URL(urls[0]);
//	// SAXParserFactory factory =SAXParserFactory.newInstance();
//	// SAXParser parser=factory.newSAXParser();
//	// XMLReader xmlreader=parser.getXMLReader();
//	// RssHandler theRSSHandler=new RssHandler();
//	// xmlreader.setContentHandler(theRSSHandler);
//	// InputSource is=new InputSource(url.openStream());
//	// xmlreader.parse(is);
//	// return theRSSHandler.getFeed();
//	// } catch (Exception e) {
//	// this.exception = e;
//	// return null;
//	// }
//	// }
//	//
//	// protected void onPostExecute(ARequest feed) {
//	// // TODO: check this.exception
//	// // TODO: do something with the feed
//	// }
//	// }
//
//	// public void makeRequest(String uri, String json) {
//	//
//	// Thread thread = new Thread(new Runnable() {
//	// @Override
//	// public void run() {
//	// try {
//	// HttpPost httpPost = new HttpPost(mUrl);
//	// httpPost.setEntity(new StringEntity(mJson));
//	// // httpPost.setHeader("Accept", "application/json");
//	// // httpPost.setHeader("Content-type", "application/json");
//	// httpPost.setHeader("Content-type",
//	// "application/json; charset=utf-8");
//	//
//	// HttpResponse response = DefaultHttpClient().execute(
//	// httpPost);
//	// } catch (UnsupportedEncodingException e) {
//	// e.printStackTrace();
//	// } catch (ClientProtocolException e) {
//	// e.printStackTrace();
//	// } catch (IOException e) {
//	// e.printStackTrace();
//	// }
//	// return null;
//	// }
//	// });
//	//
//	// thread.start();
//	//
//	// }
//
//	public HttpResponse makeRequest2(String uri, String json) {
//		try {
//			HttpPost httpPost = new HttpPost(uri);
//			httpPost.setEntity(new StringEntity(json));
//			// httpPost.setHeader("Accept", "application/json");
//			// httpPost.setHeader("Content-type", "application/json");
//			httpPost.setHeader("Content-type",
//					"application/json; charset=utf-8");
//
//			return new DefaultHttpClient().execute(httpPost);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//
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
//	private void showToast(final String toast) {
//		getActivity().runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (toast.contains("energy_expended")) {
//					Toast.makeText(
//							Pedometer.getInstance(),
//							Pedometer.getInstance().getString(
//									R.string.successfully_posted_post),
//							Toast.LENGTH_SHORT).show();
//				} else {
//					String errormsg = Pedometer.getInstance().getString(
//							R.string.error)
//							+ ":"
//							+ Pedometer.getInstance().getString(
//									R.string.connect_to_mfp);
//					Toast.makeText(Pedometer.getInstance(), errormsg,
//							Toast.LENGTH_SHORT).show();
//				}
//
//			}
//		});
//	}
//}