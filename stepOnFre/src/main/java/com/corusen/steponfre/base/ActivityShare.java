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

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.corusen.steponfre.R;

import java.util.ArrayList;

public class ActivityShare extends FragmentActivity {
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tabhost);

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
        mTabsAdapter.addTab(mTabHost.newTabSpec(getString(R.string.facebook)).setIndicator(getString(R.string.facebook)), FragmentFacebook.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec(getString(R.string.email)).setIndicator(getString(R.string.email)), FragmentCSV.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

//        // only for API <== 10
//        if (android.os.Build.VERSION.SDK_INT <= 10) {
//            for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
//                mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.mywhite));
//            }
//            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.mylightgray));
//        }

        TextView tv;  // for all API
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

    private static void setTabColor(TabHost tabhost) { // static does not work?

        TextView tv;
//        // only for API <== 10
//        if (android.os.Build.VERSION.SDK_INT <= 10) {
//            for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
//                tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Pedometer.getInstance().getResources().getColor(R.color.mywhite)); // unselected
//            }
//            tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundResource(R.color.mylightgray); // selected
//        }

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

}



//import java.util.Arrays;
//import java.util.Calendar;
//
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.corusen.accupedo.te.R;
//import com.corusen.accupedo.te.database.Constants;
//
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookAuthorizationException;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.Profile;
//import com.facebook.ProfileTracker;
//import com.facebook.appevents.AppEventsLogger;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.facebook.login.widget.ProfilePictureView;
//import com.facebook.share.ShareApi;
//import com.facebook.share.Sharer;
//import com.facebook.share.model.ShareLinkContent;
//import com.facebook.share.model.SharePhotoContent;
//import com.facebook.share.widget.ShareDialog;
//
//public class ActivityShareTabs extends FragmentActivity {
//
//    private String mMessageStep1;
//    private String mMessageStep2;
//    private String mMessageStep3;
//    private String mMessageStep4;
//
//
//    private static final String PERMISSION = "publish_actions";
//    private final String PENDING_ACTION_BUNDLE_KEY = "com.corusen.accupedo.te:PendingAction";
//
//    private ImageButton postStatusUpdateButton;
//    private ProfilePictureView profilePictureView;
//
//    private PendingAction pendingAction = PendingAction.NONE;
//    private boolean canPresentShareDialog;
//    private boolean canPresentShareDialogWithPhotos;
//    private CallbackManager callbackManager;
//    private ProfileTracker profileTracker;
//    private ShareDialog shareDialog;
//
//    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
//        @Override
//        public void onCancel() {
//            Log.d("HelloFacebook", "Canceled");
//        }
//
//        @Override
//        public void onError(FacebookException error) {
//            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
//            String title = getString(R.string.error);
//            String alertMessage = error.getMessage();
//            showResult(title, alertMessage);
//        }
//
//        @Override
//        public void onSuccess(Sharer.Result result) {
//            Log.d("HelloFacebook", "Success!");
//            if (result.getPostId() != null) {
//                String title = getString(R.string.success);
//                String id = result.getPostId();
//                String alertMessage = getString(R.string.successfully_posted_post, id);
//                showResult(title, alertMessage);
//            }
//        }
//
//        private void showResult(String title, String alertMessage) {
//            new AlertDialog.Builder(ActivityShareTabs.this)
//                    .setTitle(title)
//                    .setMessage(alertMessage)
//                    .setPositiveButton(R.string.ok, null)
//                    .show();
//        }
//    };
//
//    private enum PendingAction {
//        NONE, POST_PHOTO, POST_STATUS_UPDATE
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        FacebookSdk.sdkInitialize(this.getApplicationContext());
//
//        callbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        handlePendingAction();
//                        updateUI();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        if (pendingAction != PendingAction.NONE) {
//                            showAlert();
//                            pendingAction = PendingAction.NONE;
//                        }
//                        updateUI();
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        if (pendingAction != PendingAction.NONE
//                                && exception instanceof FacebookAuthorizationException) {
//                            showAlert();
//                            pendingAction = PendingAction.NONE;
//                        }
//                        updateUI();
//                    }
//
//                    private void showAlert() {
//                        new AlertDialog.Builder(ActivityShareTabs.this)
//                                .setTitle(R.string.cancelled)
//                                .setMessage(R.string.permission_not_granted)
//                                .setPositiveButton(R.string.ok, null)
//                                .show();
//                    }
//                });
//
//        shareDialog = new ShareDialog(this);
//        shareDialog.registerCallback(
//                callbackManager,
//                shareCallback);
//
//        if (savedInstanceState != null) {
//            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
//            pendingAction = PendingAction.valueOf(name);
//        }
//
//        setContentView(AccuService.mScreenFragmentShareFacebook);
//
//        profileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                updateUI();
//                // It's possible that we were waiting for Profile to be populated in order to post a status update.
//                handlePendingAction();
//            }
//        };
//
//        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
//
//        postStatusUpdateButton = (ImageButton)findViewById(R.id.postStatusUpdateButton);
//        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                onClickPostStatusUpdate();
//            }
//        });
//
//        canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
//        canPresentShareDialogWithPhotos = ShareDialog.canShow(
//                SharePhotoContent.class);
//
//        float mfDistanceFactor;
//        String mDistanceUnit;
//        if (Pedometer.mPedometerSettings.isMetric()) {
//            mfDistanceFactor = 1.60934f;
//            mDistanceUnit = getString(R.string.widget_km);
//        } else {
//            mfDistanceFactor = 1.0f;
//            mDistanceUnit = getString(R.string.widget_mi);
//        }
//
//        Calendar today = Calendar.getInstance();
//        int year = today.get(Calendar.YEAR);
//        int month = today.get(Calendar.MONTH) + 1;
//        int day = today.get(Calendar.DATE);
//
//        Pedometer.mDB.open();
//        Cursor c = Pedometer.mDB.queryDayMaxSteps(year, month, day);
//
//        int mColumnIndexSteps, mColumnIndexDistance, mColumnIndexCalories, mColumnIndexSteptime;
//        mColumnIndexSteps = c.getColumnIndex(Constants.KEY_STEPS);
//        mColumnIndexDistance = c.getColumnIndex(Constants.KEY_DISTANCE);
//        mColumnIndexCalories = c.getColumnIndex(Constants.KEY_CALORIES);
//        mColumnIndexSteptime = c.getColumnIndex(Constants.KEY_STEPTIME);
//
//        mMessageStep1 = " \u2022 " + String.format("%d", c.getInt(mColumnIndexSteps)) + " " + getString(R.string.steps);
//        mMessageStep2 = " \u2022 " + String.format("%.2f", c.getFloat(mColumnIndexDistance) * mfDistanceFactor) + mDistanceUnit;
//        mMessageStep3 = " \u2022 " + String.format("%.1f", c.getFloat(mColumnIndexCalories)) + " " + getString(R.string.cal);
//        mMessageStep4 = " \u2022 " + Utils.getHoursMinutesString((int) c.getLong(mColumnIndexSteptime) / 1000) + " " + getString(R.string.hhmm);
//        c.close();
//        Pedometer.mDB.close();
//
//        TextView textStep1, textStep2;
//        textStep1 = (TextView) findViewById(R.id.textStep1);
//        textStep2 = (TextView) findViewById(R.id.textStep2);
//        textStep1.setText(mMessageStep1 + " " + mMessageStep2);
//        textStep2.setText(mMessageStep3 + " " + mMessageStep4);
//
//        postStatusUpdateButton.setContentDescription(getString(R.string.send));
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // Call the 'activateApp' method to log an app event for use in analytics and advertising
//        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
//        // launched into.
//        AppEventsLogger.activateApp(this);
//
//        updateUI();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
//        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
//        // launched into.
//        AppEventsLogger.deactivateApp(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        profileTracker.stopTracking();
//    }
//
//    private void updateUI() {
//        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
//        Profile profile = Profile.getCurrentProfile();
//        if (enableButtons) {
//            postStatusUpdateButton.setImageDrawable(ContextCompat.getDrawable(this, AccuService.mScreenShareFBSend));
//        } else {
//            postStatusUpdateButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fbsend_off_ic));
//        }
//
//        if (enableButtons && profile != null) {
//            profilePictureView.setProfileId(profile.getId());
//            //greeting.setText(getString(R.string.hello_user, profile.getFirstName()));
//        } else {
//            profilePictureView.setProfileId(null);
//            //greeting.setText(null);
//        }
//
//        postStatusUpdateButton.setEnabled(enableButtons && canPresentShareDialog);
//    }
//
//    private void handlePendingAction() {
//        PendingAction previouslyPendingAction = pendingAction; // These actions may re-set pendingAction if they are still pending, but we assume they will succeed.
//        pendingAction = PendingAction.NONE;
//
//        switch (previouslyPendingAction) {
//            case NONE:
//                break;
//            case POST_STATUS_UPDATE:
//                postStatusUpdate();
//                break;
//        }
//    }
//
//    private void onClickPostStatusUpdate() {
//        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
//    }
//
//    private void postStatusUpdate() {
//        Profile profile = Profile.getCurrentProfile();
//        ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                .setContentTitle(getString(R.string.fb_walk_report))
//                .setContentDescription(mMessageStep1 + " " + mMessageStep2 + mMessageStep3 + " " + mMessageStep4)
//                .setContentUrl(Uri.parse("http://accupedo.com/accupedo_fb11"))
//                .build();
//        if (canPresentShareDialog) {
//            shareDialog.show(linkContent);
//        } else if (profile != null && hasPublishPermission()) {
//            ShareApi.share(linkContent, shareCallback);
//        } else {
//            pendingAction = PendingAction.POST_STATUS_UPDATE;
//        }
//    }
//
//    private boolean hasPublishPermission() {
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
//    }
//
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
//                        Arrays.asList(PERMISSION));
//                return;
//            }
//        }
//
//        if (allowNoToken) {
//            pendingAction = action;
//            handlePendingAction();
//        }
//    }
//}






















