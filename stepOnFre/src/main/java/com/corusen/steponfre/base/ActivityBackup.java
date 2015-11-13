/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.corusen.steponfre.base;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.corusen.steponfre.R;
import com.corusen.steponfre.database.Constants;
import com.corusen.steponfre.database.MyDB;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

public class ActivityBackup extends ActivityDriveBase { //FragmentActivity {

	private static final String TAG = "ActivityBackupTabs";
	private static ActivityBackup mInstance = null;

	private TabHost mTabHost;
	private ConnectionListener mListener;

	//private ProgressDialog mProgressDialog;
	private Handler mHandler;
	private DriveId mDriveId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tabhost);

		ActivityBackup.mInstance = this;

		ActionBar actionBar;
		actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(AccuService.mScreenAcitionBarColor)));
		}

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		MyViewPager mViewPager = (MyViewPager) findViewById(R.id.pager);
		TabsAdapter mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
		mTabsAdapter.addTab(mTabHost.newTabSpec(getString(R.string.sdcard)).setIndicator(getString(R.string.sdcard)), FragmentBackup.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec(getString(R.string.google_drive)).setIndicator(getString(R.string.google_drive)), FragmentDrive.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

//		// only for API <== 10
//		if (android.os.Build.VERSION.SDK_INT <= 10) {
//			for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
//				mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.mywhite));
//			}
//			mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.mylightgray));
//		}

		TextView tv; // for all API
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			tv.setTextColor(getResources().getColor(R.color.mydarkgray));
		}
		tv = (TextView) mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).findViewById(android.R.id.title);
		tv.setTextColor(getResources().getColor(AccuService.mScreenStepTextColor));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.dummy, menu);
		menu.getItem(0).setEnabled(false);
		menu.getItem(1).setEnabled(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent intent = new Intent(this, Pedometer.class); //V541
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
//            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//                TaskStackBuilder.from(this).addNextIntent(upIntent).startActivities();
//                finish();
//            } else {
//                //NavUtils.navigateUpTo(this, upIntent); //V531 somehow this does not work after interstitial
//                TaskStackBuilder.from(this).addNextIntent(upIntent).startActivities();
//                finish();
//            }
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static ActivityBackup getInstance() {
		return mInstance;
	}

	private static void setTabColor(TabHost tabhost) { // static does not work?

		TextView tv;
//		// only for API <== 10
//		if (android.os.Build.VERSION.SDK_INT <= 10) {
//			for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
//				tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Pedometer.getInstance().getResources().getColor(R.color.mywhite)); // unselected
//			}
//			tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundResource(R.color.mylightgray); // selected
//		}

		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) { // for all API
			tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			tv.setTextColor(Pedometer.getInstance().getResources().getColor(R.color.mydarkgray));
		}

		tv = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(android.R.id.title);
		tv.setTextColor(Pedometer.getInstance().getResources().getColor(AccuService.mScreenStepTextColor));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final MyViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<>();

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost, MyViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			//mViewPager.setOnPageChangeListener(this); //V541 Use addOnPageChangeListener
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);

			// JS set color of tab
			setTabColor(mTabHost);

			// JS Google Drive is available in Pro version
			//if (position == 1) {
			//	Toast.makeText(Pedometer.getInstance(), Pedometer.getInstance().getResources().getString(R.string.trial_expired), Toast.LENGTH_LONG).show();
			//}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		//Log.i(TAG, "GoogleApiClient connected");
		if (mListener != null) { mListener.driveConnected(); }
	}


	public interface ConnectionListener {
		void driveConnected();
	}

	public void setListener (ConnectionListener cl) {
		mListener = cl;
	}

	public boolean isConnected() {
		return isDriveConnected();
	}

	public void gDriveConnect() { connectDrive(); }

	public void gDriveExport() {
		Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);
	}

	static class ProgresstHandler extends Handler {
		private final ProgressDialog progressDialog;

		ProgresstHandler(ProgressDialog dialog) {
			progressDialog = dialog;
		}

		@Override
		public void handleMessage(Message msg)
		{
			progressDialog.dismiss();
		}
	}

	public void gDriveImport() {
		ProgressDialog progressDialog = new ProgressDialog(ActivityBackup.getInstance());
		progressDialog.setMessage(ActivityBackup.getInstance().getString(R.string.wait_for_importing_db));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();

		mHandler = new ProgresstHandler(progressDialog);

//        mHandler = new Handler() {
//            public void  handleMessage(Message msg) {
//                mProgressDialog.dismiss();
//            }
//        };

		Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, "Accupedo.db")).build();
		Drive.DriveApi.query(getGoogleApiClient(), query) .setResultCallback(metadataCallback);
	}

	final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new
			ResultCallback<DriveApi.MetadataBufferResult>() {
				@Override
				public void onResult(DriveApi.MetadataBufferResult result) {
					if (!result.getStatus().isSuccess()) {
						showMessage("Problem while retrieving results");
						return;
					}

					//int a = result.getMetadataBuffer().getCount();
					Metadata metadata = result.getMetadataBuffer().get(0);
					mDriveId = metadata.getDriveId();

					GoogleApiClient mGoogleApiClient = getGoogleApiClient();
					DriveFile mfile = Drive.DriveApi.getFile(mGoogleApiClient, mDriveId);
					Drive.DriveApi.fetchDriveId(getGoogleApiClient(), mfile.toString()).setResultCallback(idCallback);
				}
			};


	final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
			ResultCallback<DriveApi.DriveContentsResult>() {
				@Override
				public void onResult(DriveApi.DriveContentsResult result) {
					if (!result.getStatus().isSuccess()) {
						showMessage("Error while trying to create new file contents");
						return;
					}
					final DriveContents driveContents = result.getDriveContents();

					ProgressDialog progressDialog = new ProgressDialog(ActivityBackup.getInstance());
					progressDialog.setMessage(ActivityBackup.getInstance().getString(R.string.wait_for_exporting_db));
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();

					mHandler = new ProgresstHandler(progressDialog);

//                    mHandler = new Handler() {
//                        public void handleMessage(Message msg) {
//                            mProgressDialog.dismiss();
//                        }
//                    };

					new Thread() {
						@Override
						public void run() {

							File dataFolder = Environment.getDataDirectory();
							String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH + Constants.DATABASE_NAME;
							File currentDB = new java.io.File(dataFolder, currentDBPath);
							OutputStream outputStream = driveContents.getOutputStream();
							try {
								int size = (int) currentDB.length();
								byte[] bytes = new byte[size];
								BufferedInputStream buf = new BufferedInputStream(new FileInputStream(currentDB));
								buf.read(bytes, 0, bytes.length);
								buf.close();
								outputStream.write(bytes);
							} catch (IOException e) {
								Log.e(TAG, e.getMessage());
							}

							String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType("db");
							MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
									.setMimeType(mimeType).setTitle(Constants.DATABASE_FILENAME)
									.build();

							mHandler.sendEmptyMessage(0);

							Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFile(getGoogleApiClient(), metadataChangeSet, driveContents)
									.setResultCallback(fileCallback);
						}
					}.start();
				}
			};

	final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
			ResultCallback<DriveFolder.DriveFileResult>() {
				@Override
				public void onResult(DriveFolder.DriveFileResult result) {
					if (!result.getStatus().isSuccess()) {
						showMessage("Error while trying to create the file");
						return;
					} //showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
					showMessage(ActivityBackup.getInstance().getString(R.string.google_drive_uploaded));
				}
			};

	final private ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
		@Override
		public void onResult(DriveApi.DriveIdResult result) {
			new RetrieveDriveFileContentsAsyncTask(ActivityBackup.this).execute(result.getDriveId());
		}
	};

	final private class RetrieveDriveFileContentsAsyncTask extends ApiClientAsyncTask<DriveId, Boolean, String> {

		public RetrieveDriveFileContentsAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected String doInBackgroundConnected(DriveId... params) {
			String contents = null;
			//DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), params[0]);
			DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), mDriveId);
			DriveApi.DriveContentsResult driveContentsResult =
					file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
			if (!driveContentsResult.getStatus().isSuccess()) {
				return null;
			}
			DriveContents driveContents = driveContentsResult.getDriveContents();
			InputStream inputStream = driveContents.getInputStream();

			try {

				java.io.File data = Environment.getDataDirectory();
				String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH + Constants.DATABASE_NAME;
				java.io.File currentDB = new java.io.File(data, currentDBPath);
				FileOutputStream outputStream = new FileOutputStream(currentDB);
				int read;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) { outputStream.write(bytes, 0, read); }
				inputStream.close();
				outputStream.close();

				MyDB db = new MyDB(getBaseContext()); //V535
				db.open();
				int version = db.getDBVersion();
				if (version < Constants.DATABASE_VERSION) { db.upgradeDBVersion(); }
				db.close();

				contents = String.valueOf(version);

			} catch (IOException e) {
				Log.e(TAG, "IOException while reading from the stream", e);
			}
			driveContents.discard(getGoogleApiClient());

			mHandler.sendEmptyMessage(0);

			return contents;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				showMessage("Error while reading from the file");
				return;
			}
			showMessage(ActivityBackup.getInstance().getString(R.string.google_drive_downloaded));
		}
	}
}










//
//import com.corusen.steponfre.R;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.content.res.TypedArray;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.v4.app.ActionBarDrawerToggle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.DrawerLayout;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.TabHost;
//import android.widget.TabWidget;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class ActivityBackup extends FragmentActivity {
//	TabHost mTabHost;
//	ViewPager mViewPager;
//	public TabsAdapter mTabsAdapter;
//
//	// navigation settings
//	private DrawerLayout mDrawerLayout;
//	private ListView mDrawerList;
//	private ActionBarDrawerToggle mDrawerToggle;
//	private CharSequence mDrawerTitle;
//	private CharSequence mTitle;
//	private String[] navMenuTitles;
//	private TypedArray navMenuIcons;
//	private ArrayList<NavDrawerItem> navDrawerItems;
//	private NavDrawerListAdapter mAdapter;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.activity_tabhost);
//		mTitle = mDrawerTitle = getTitle();
//		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
//		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
//		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
//
//		navDrawerItems = new ArrayList<NavDrawerItem>();
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));// ,true,"22"));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));// ,true,"50+"));
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
//		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1))); //quit
//		navMenuIcons.recycle();
//
//		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
//		mAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
//		mDrawerList.setAdapter(mAdapter);
//
//		getActionBar().setDisplayHomeAsUpEnabled(true); // enabling action bar app icon and behaving it as toggle button
//		getActionBar().setHomeButtonEnabled(true);
//		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(AccuService.mScreenAcitionBarColor)));
//
//		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, // nav menu toggle icon
//				R.string.app_name, // nav drawer open - description for accessibility
//				R.string.app_name // nav drawer close - description for accessibility
//		) {
//			public void onDrawerClosed(View view) {
//				getActionBar().setTitle(mTitle);
//				invalidateOptionsMenu(); // calling onPrepareOptionsMenu() to show action bar icons
//			}
//
//			public void onDrawerOpened(View drawerView) {
//				getActionBar().setTitle(mDrawerTitle);
//				invalidateOptionsMenu(); // calling onPrepareOptionsMenu() to hide action bar icons
//			}
//		};
//		mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//		if (savedInstanceState == null) {
//			displayView(Pedometer.SPINNER_ITEM_BACKUP);
//		}
//
//		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
//		mTabHost.setup();
//
//		mViewPager = (ViewPager) findViewById(R.id.pager);
//
//		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
//
//		mTabsAdapter.addTab(mTabHost.newTabSpec(getString(R.string.sdcard)).setIndicator(getString(R.string.sdcard)), FragmentBackupSdcard.class, null);
//		mTabsAdapter.addTab(mTabHost.newTabSpec(getString(R.string.google_drive)).setIndicator(getString(R.string.google_drive)), FragmentBackupGdrive.class,
//				null);
//
//		if (savedInstanceState != null) {
//			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
//		}
//
//		// only for API <== 10
//		if (android.os.Build.VERSION.SDK_INT <= 10) {
//			for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
//				mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.mywhite));
//			}
//			mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.mylightgray));
//		}
//
//		// for all API
//		TextView tv;
//		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
//
//			tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
//			tv.setTextColor(getResources().getColor(R.color.mydarkgray));
//		}
//		tv = (TextView) mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).findViewById(android.R.id.title);
//		tv.setTextColor(getResources().getColor(AccuService.mScreenStepTextColor));
//	}
//
//	/**
//	 * Slide menu item click listener
//	 **/
//	private class SlideMenuClickListener implements ListView.OnItemClickListener {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			displayView(position);
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.dummy, menu);
//		menu.getItem(0).setEnabled(false);
//		menu.getItem(1).setEnabled(false);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (mDrawerToggle.onOptionsItemSelected(item)) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//
////	@Override
////	public boolean onCreateOptionsMenu(Menu menu) {
////		getMenuInflater().inflate(R.menu.main, menu);
////		return true;
////	}
////
////	@Override
////	public boolean onOptionsItemSelected(MenuItem item) {
////		// toggle nav drawer on selecting action bar app icon/title
////		if (mDrawerToggle.onOptionsItemSelected(item)) {
////			return true;
////		}
////		// Handle action bar actions click
////		switch (item.getItemId()) {
////		case R.id.action_settings:
////			return true;
////		default:
////			return super.onOptionsItemSelected(item);
////		}
////	}
////
////	/* *
////	 * Called when invalidateOptionsMenu() is triggered
////	 */
////	@Override
////	public boolean onPrepareOptionsMenu(Menu menu) {
////		// if nav drawer is opened, hide the action items
////		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
////		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
////		return super.onPrepareOptionsMenu(menu);
////	}
//
//	/**
//	 * Diplaying fragment view for selected nav drawer list item
//	 * */
//	private void displayView(int position) {
//		// update the main content by replacing fragments
//		Fragment fragment = null;
//		Intent intent;
//		int[] array = new int[2];
//		switch (position) {
//		case Pedometer.SPINNER_ITEM_PEDOMETER:
//			array[0] = Pedometer.SPINNER_ITEM_PEDOMETER;
//			intent = new Intent(getBaseContext(), Pedometer.class);
//			intent.putExtra("navigation_intent", array);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			break;
//
//		case Pedometer.SPINNER_ITEM_SHARE:
//			intent = new Intent(getBaseContext(), ActivityShareTabs.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			break;
//
//		case Pedometer.SPINNER_ITEM_EDITSTEPS:
//			array[0] = Pedometer.SPINNER_ITEM_EDITSTEPS;
//			intent = new Intent(getBaseContext(), Pedometer.class);
//			intent.putExtra("navigation_intent", array);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			break;
//
//		case Pedometer.SPINNER_ITEM_BACKUP:
//			// update selected item and title, then close the drawer
//			mDrawerList.setItemChecked(position, true);
//			mDrawerList.setSelection(position);
//			setTitle(navMenuTitles[position]);
//			mDrawerLayout.closeDrawer(mDrawerList);
//			break;
//
//		case Pedometer.SPINNER_ITEM_SETTINGS:
//			array[0] = Pedometer.SPINNER_ITEM_SETTINGS;
//			intent = new Intent(getBaseContext(), Pedometer.class);
//			intent.putExtra("navigation_intent", array);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			break;
//
//		case Pedometer.SPINNER_ITEM_HELP:
//			array[0] = Pedometer.SPINNER_ITEM_HELP;
//			intent = new Intent(getBaseContext(), Pedometer.class);
//			intent.putExtra("navigation_intent", array);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			break;
//
//		case Pedometer.SPINNER_ITEM_PURCHASE:
//			array[0] = Pedometer.SPINNER_ITEM_PURCHASE;
//			intent = new Intent(getBaseContext(), Pedometer.class);
//			intent.putExtra("navigation_intent", array);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			break;
//
//		case Pedometer.SPINNER_ITEM_QUIT:
//			array[0] = Pedometer.SPINNER_ITEM_PEDOMETER;
//			array[1] = 1;
//			intent = new Intent(getBaseContext(), Pedometer.class);
//			intent.putExtra("navigation_intent", array);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	@Override
//	public void setTitle(CharSequence title) {
//		mTitle = title;
//		getActionBar().setTitle(mTitle);
//	}
//
//	/**
//	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
//	 */
//
//	@Override
//	protected void onPostCreate(Bundle savedInstanceState) {
//		super.onPostCreate(savedInstanceState);
//		// Sync the toggle state after onRestoreInstanceState has occurred.
//		mDrawerToggle.syncState();
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		// Pass any configuration change to the drawer toggls
//		mDrawerToggle.onConfigurationChanged(newConfig);
//	}
//
//	public static void setTabColor(TabHost tabhost) { // static does not work?
//
//		TextView tv;
//		// only for API <== 10
//		if (android.os.Build.VERSION.SDK_INT <= 10) {
//			for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
//				tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Pedometer.getInstance().getResources().getColor(R.color.mywhite)); // unselected
//			}
//			tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundResource(R.color.mylightgray); // selected
//		}
//
//		// for all API
//		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
//			tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
//			tv.setTextColor(Pedometer.getInstance().getResources().getColor(R.color.mydarkgray));
//		}
//
//		tv = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(android.R.id.title);
//		tv.setTextColor(Pedometer.getInstance().getResources().getColor(AccuService.mScreenStepTextColor));
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		outState.putString("tab", mTabHost.getCurrentTabTag());
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	public static class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
//		private final Context mContext;
//		private final TabHost mTabHost;
//		private final ViewPager mViewPager;
//		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
//
//		static final class TabInfo {
//			private final String tag;
//			private final Class<?> clss;
//			private final Bundle args;
//
//			TabInfo(String _tag, Class<?> _class, Bundle _args) {
//				tag = _tag;
//				clss = _class;
//				args = _args;
//			}
//		}
//
//		static class DummyTabFactory implements TabHost.TabContentFactory {
//			private final Context mContext;
//
//			public DummyTabFactory(Context context) {
//				mContext = context;
//			}
//
//			@Override
//			public View createTabContent(String tag) {
//				View v = new View(mContext);
//				v.setMinimumWidth(0);
//				v.setMinimumHeight(0);
//				return v;
//			}
//		}
//
//		public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
//			super(activity.getSupportFragmentManager());
//			mContext = activity;
//			mTabHost = tabHost;
//			mViewPager = pager;
//			mTabHost.setOnTabChangedListener(this);
//			mViewPager.setAdapter(this);
//			mViewPager.setOnPageChangeListener(this);
//		}
//
//		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
//			tabSpec.setContent(new DummyTabFactory(mContext));
//			String tag = tabSpec.getTag();
//
//			TabInfo info = new TabInfo(tag, clss, args);
//			mTabs.add(info);
//			mTabHost.addTab(tabSpec);
//			notifyDataSetChanged();
//		}
//
//		@Override
//		public int getCount() {
//			return mTabs.size();
//		}
//
//		@Override
//		public Fragment getItem(int position) {
//			TabInfo info = mTabs.get(position);
//			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
//		}
//
//		@Override
//		public void onTabChanged(String tabId) {
//			int position = mTabHost.getCurrentTab();
//			mViewPager.setCurrentItem(position);
//
//			// JS set color of tab
//			setTabColor(mTabHost);
//
//			// JS Google Drive is available in Pro version
//			if (position == 1) {
//				Toast.makeText(Pedometer.getInstance(), Pedometer.getInstance().getResources().getString(R.string.trial_expired), Toast.LENGTH_LONG).show();
//			}
//		}
//
//		@Override
//		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//		}
//
//		@Override
//		public void onPageSelected(int position) {
//			TabWidget widget = mTabHost.getTabWidget();
//			int oldFocusability = widget.getDescendantFocusability();
//			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//			mTabHost.setCurrentTab(position);
//			widget.setDescendantFocusability(oldFocusability);
//		}
//
//		@Override
//		public void onPageScrollStateChanged(int state) {
//		}
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent intent;
//			int[] array = new int[2];
//			array[0] = Pedometer.SPINNER_ITEM_PEDOMETER;
//			intent = new Intent(getBaseContext(), Pedometer.class);
//			intent.putExtra("navigation_intent", array);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//			startActivity(intent);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//}
