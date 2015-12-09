package com.corusen.steponfre.database;

import com.corusen.steponfre.base.AnalyticsSampleApp;
import com.corusen.steponfre.R;
import com.corusen.steponfre.base.AccuService;
import com.corusen.steponfre.base.ActivityHistoryEditsteps;
import com.corusen.steponfre.base.Pedometer;
import com.corusen.steponfre.base.PedometerSettings;
import com.corusen.steponfre.base.Utils;
import com.corusen.steponfre.base.AnalyticsSampleApp.TrackerName;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class History extends FragmentActivity {
	// private static MyDB mDB;
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;

	private static Calendar mCurrent;
	private static Calendar mToday;
	private static Calendar mFirstDay;
	private static Calendar mMonthToDelete;
	private static Calendar mDayToDelete;
	private static int mStepsToDelete;
	private static int mNumberMonths;
	private static int mCurrentPage;

	public static int mDateFormat;
	public static String msDistanceUnit;
	public static float mfDistanceFactor;

	private int mItem;

	private ActionBar mActionBar;
	private static Handler mHandler;

	public static final int MESSAGE_DELETE_DAY = 1;

	HistoryCollectionPagerAdapter mHistoryCollectionPagerAdapter;
	static ViewPager mViewPager;

	//private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.light_collection_history);
		// setContentView(R.layout.diaries);

		mHistoryCollectionPagerAdapter = new HistoryCollectionPagerAdapter(getSupportFragmentManager());

		mActionBar = getActionBar();
		if (mActionBar != null) {
			mActionBar.setTitle(R.string.history);
			mActionBar.setDisplayHomeAsUpEnabled(true);
			mActionBar.setDisplayShowTitleEnabled(true);
			mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(AccuService.mScreenAcitionBarColor)));
		}

//		actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setHomeButtonEnabled(true);
//		actionBar.setDisplayShowTitleEnabled(true);
//		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(AccuService.mScreenAcitionBarColor)));

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mHistoryCollectionPagerAdapter);
		mCurrentPage = -1; // no specific page , i.e. last page

		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);

		if (mPedometerSettings.isMetric()) {
			msDistanceUnit = getString(R.string.km);
			mfDistanceFactor = Utils.MILE2KM; // 1.6f;
		} else {
			msDistanceUnit = getString(R.string.widget_mi);
			mfDistanceFactor = 1.0f;
		}

		mHandler = new hHandler(this);

		// myAdapter = new DiaryAdapter(this);
		// this.setListAdapter(myAdapter);

		// JS V115
		final UncaughtExceptionHandler defaultHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				if (thread.getName().startsWith("AdWorker")) {
					Log.w("ADMOB", "AdWorker thread thrown an exception.", ex);
				} else if (defaultHandler != null) {
					defaultHandler.uncaughtException(thread, ex);
				} else {
					throw new RuntimeException(
							"No default uncaught exception handler.", ex);
				}
			}
		});

		AdView adView;
		if (Pedometer.getInstance() != null) {
			LocationManager coarseLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			Location coarseLocation = coarseLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Date birthday = Pedometer.mPedometerSettings.getBirthDate();
			int gender = Pedometer.mPedometerSettings.getGender();

			adView = new AdView(this);
			adView.setAdSize(AdSize.SMART_BANNER);
			adView.setAdUnitId(Constants.ACCUPEDO_ADMOB_ID);
			LinearLayout layout = (LinearLayout) findViewById(R.id.adView);
			layout.addView(adView);

			AdRequest adRequest = new AdRequest.Builder().setLocation(coarseLocation).setGender(gender).setBirthday(birthday).build();
			adView.loadAd(adRequest);
			
			if (!mPedometerSettings.isOpenHistoryUpdateVersion401()) {
				openHistoryUpdateVersion401AlertDialog();
				mPedometerSettings.setOpenHistoryUpdateVersion401(true);
			} else {
			}
		}
	}

	private void openHistoryUpdateVersion401AlertDialog() {
		new AlertDialog.Builder(this).setMessage(R.string.alert_history_update_version_401_message)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
		// .setNegativeButton(R.string.dialog_no,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// }
		// }).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.history_activity, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		Tracker t = ((AnalyticsSampleApp) this.getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("History");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// mDB = new MyDB(this);
		// mDB.open();

		Pedometer.mDB.open();
		Cursor c = Pedometer.mDB.queryFirstDay();
		mToday = Calendar.getInstance();
		mDayToDelete = (Calendar) mToday.clone();
		mFirstDay = (Calendar) mToday.clone();

		// JS added to check if c == null :V351
		if ((c != null) & c.getCount() > 0) {
			mFirstDay.set(Calendar.YEAR, c.getInt(c.getColumnIndex(Constants.KEY_YEAR)));
			mFirstDay.set(Calendar.MONTH, c.getInt(c.getColumnIndex(Constants.KEY_MONTH)) - 1);
			mFirstDay.set(Calendar.DATE, c.getInt(c.getColumnIndex(Constants.KEY_DAY)));
			c.close();
		}

		Pedometer.mDB.close();

		mNumberMonths = getNumberMonths();
		if (mCurrentPage < 0) { // go to the last page
			mViewPager.setCurrentItem(mNumberMonths - 1);
		} else {
			mViewPager.setCurrentItem(mCurrentPage);
		}
		mViewPager.getAdapter().notifyDataSetChanged();
		// Log.i("Chart_Fragment", "Resume");
	}

	public static class HistoryCollectionPagerAdapter extends FragmentStatePagerAdapter {

		public HistoryCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			ListFragment fragment = new HistoryListFragment();
			Bundle args = new Bundle();
			args.putInt(HistoryListFragment.ARG_OBJECT, i); 
			// i+1 Our object is just an integer :-P
			// Log.i("Chart_Fragment", "..." + ((Integer) i).toString());
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return mNumberMonths;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Calendar current = (Calendar) mToday.clone();
			String string;
			current.add(Calendar.MONTH, -(mNumberMonths - 1 - position));
			string = DateFormat.format("MMM, yyyy", current).toString();
			return string;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			return container;
		}

	}

	public static class HistoryListFragment extends ListFragment {

		public static final String ARG_OBJECT = "object";
		View mRootView;
		DiaryAdapter myAdapter;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			mRootView = inflater.inflate(AccuService.mScreenCollectionHistory, container, false);

			Bundle args = getArguments();
			int position = args.getInt(ARG_OBJECT);

			if (mToday == null) { // NullPoint error check from crash
				mToday = Calendar.getInstance();
			}
			mCurrent = (Calendar) mToday.clone();
			mCurrent.add(Calendar.MONTH, -(mNumberMonths - 1 - position));

			myAdapter = new DiaryAdapter(inflater);
			this.setListAdapter(myAdapter);

			final ListView listView = (ListView) mRootView.findViewById(android.R.id.list);
			listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// Toast.makeText(getActivity(), "On long click listener",
					// Toast.LENGTH_LONG).show();
					mCurrentPage = mViewPager.getCurrentItem();
					HistoryListFragment.DiaryAdapter.ViewHolder holder;
					holder = (HistoryListFragment.DiaryAdapter.ViewHolder) arg1.getTag();
					mDayToDelete.set(Calendar.YEAR, holder.mydiary.year);
					mDayToDelete.set(Calendar.MONTH, holder.mydiary.month - 1);
					mDayToDelete.set(Calendar.DATE, holder.mydiary.day);
					mStepsToDelete = holder.mydiary.steps;
					mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_DELETE_DAY, 0, 0));
					return true;
				}
			});

			return mRootView;
		}

		@Override
		public void onListItemClick(ListView list, View v, int position, long id) {

			// Log.i("History_Fragment", "Long press");
			mCurrentPage = mViewPager.getCurrentItem();

			HistoryListFragment.DiaryAdapter.ViewHolder holder;
			holder = (HistoryListFragment.DiaryAdapter.ViewHolder) v.getTag();

			int[] array = new int[4];
			array[0] = holder.mydiary.year;
			array[1] = holder.mydiary.month;
			array[2] = holder.mydiary.day;
			array[3] = holder.mydiary.steps;

			Intent intent = new Intent(v.getContext(), com.corusen.steponfre.database.DetailHistory.class);
			intent.putExtra("detail_history", array);
			startActivity(intent);
		}

		private class DiaryAdapter extends BaseAdapter {
			private LayoutInflater mInflater;
			public ArrayList<MyDiary> diaries;

			public DiaryAdapter(LayoutInflater inflater) {
				mInflater = inflater;
				diaries = new ArrayList<MyDiary>();
				getdata();
			}

			public void getdata() {
				int year, month, day, steps, goal;
				float distance, calories;
				long elapsedtime;

				if (Pedometer.mDB != null) {
					Pedometer.mDB.open(); // a few NullPointerException errors
											// TE
					Cursor c = Pedometer.mDB.queryMonthDayMaxSteps(mCurrent.get(Calendar.YEAR), mCurrent.get(Calendar.MONTH) + 1);

					if (c.moveToFirst()) {
						do {
							year = c.getInt(c.getColumnIndex(Constants.KEY_YEAR));
							month = c.getInt(c.getColumnIndex(Constants.KEY_MONTH));
							day = c.getInt(c.getColumnIndex(Constants.KEY_DAY));
							steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
							distance = c.getFloat(c.getColumnIndex(Constants.KEY_DISTANCE));
							calories = c.getFloat(c.getColumnIndex(Constants.KEY_CALORIES));
							elapsedtime = c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME));
							goal = c.getInt(c.getColumnIndex(Constants.KEY_ACHIEVEMENT));

							MyDiary temp = new MyDiary(year, month, day, steps, distance, calories, elapsedtime, goal);
							diaries.add(0, temp);
						} while (c.moveToNext());
						c.close();
					}
					Pedometer.mDB.close();
				}
			}

			@Override
			public int getCount() {
				return diaries.size();
			}

			public MyDiary getItem(int i) {
				return diaries.get(i);
			}

			public long getItemId(int i) {
				return i;
			}

			// public OnClickListener myClickListener = new OnClickListener() {
			// public void onClick(View v) {
			// Log.i("History_Fragment", "Long press");
			// HistoryListFragment.DiaryAdapter.ViewHolder holder;
			// holder = (HistoryListFragment.DiaryAdapter.ViewHolder) v
			// .getTag();
			// int[] array = new int[3];
			// array[0] = holder.mydiary.year;
			// array[1] = holder.mydiary.month;
			// array[2] = holder.mydiary.day;
			// Intent intent = new Intent(
			// v.getContext(),
			// com.corusen.steponfre.database.DetailHistory.class);
			// intent.putExtra("detail_history", array);
			// startActivity(intent);
			// }
			// };

			public View getView(int arg0, View arg1, ViewGroup arg2) {
				final ViewHolder holder;

				int i;
				View v = arg1;
				if ((v == null) || (v.getTag() == null)) {

					v = mInflater.inflate(AccuService.mScreenHistoryDiaryRow, null);
					// This holds true only when you are customizing the Adapter
					// by
					// extending BaseAdapter.
					// v.setClickable(true);
					// v.setOnClickListener(myClickListener);

					holder = new ViewHolder();
					holder.mydate = (TextView) v.findViewById(R.id.date_text_view);
					holder.myweekdate = (TextView) v.findViewById(R.id.weekdate_text_view);
					holder.mysteps = (TextView) v.findViewById(R.id.steps_text_view);
					holder.mydistance = (TextView) v.findViewById(R.id.distance_text_view);
					holder.mycalories = (TextView) v.findViewById(R.id.calories_text_view);
					holder.mysteptime = (TextView) v.findViewById(R.id.steptime_text_view);

					holder.mydistanceunit = (TextView) v.findViewById(R.id.distance_unit_text_view);
					holder.mystars = (ImageView) v.findViewById(R.id.star_image_view);

					// holder.myimagebutton = (ImageButton) v
					// .findViewById(R.id.delete_day_button);
					// holder.myimagebutton
					// .setOnClickListener(new OnClickListener() {
					// @Override
					// public void onClick(View v) {
					// // HistoryListFragment.DiaryAdapter.ViewHolder
					// // vholder;
					// // vholder =
					// // (HistoryListFragment.DiaryAdapter.ViewHolder)
					// // v.getTag();
					// mDayToDelete.set(Calendar.YEAR,
					// holder.mydiary.year);
					// mDayToDelete.set(Calendar.MONTH,
					// holder.mydiary.month - 1);
					// mDayToDelete.set(Calendar.DATE,
					// holder.mydiary.day);
					// mHandler.sendMessage(mHandler
					// .obtainMessage(MESSAGE_DELETE_DAY,
					// 0, 0));
					// }
					// });

					v.setTag(holder);
				} else {
					holder = (ViewHolder) v.getTag();
				}
				holder.mydiary = getItem(arg0);

				float percent = 0.0f;
				if (holder.mydiary.goalsteps == 0) {
					percent = 50.0f;
				} else {
					percent = ((float) holder.mydiary.steps / (float) holder.mydiary.goalsteps) * 100.0f;
				}

				if (percent < 10f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star0));
				} else if (percent < 20f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star1));
				} else if (percent < 30f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star2));
				} else if (percent < 40f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star3));
				} else if (percent < 50f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star4));
				} else if (percent < 60f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star5));
				} else if (percent < 70f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star6));
				} else if (percent < 80f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star7));
				} else if (percent < 90f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star8));
				} else if (percent < 100f) {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star9));
				} else {
					holder.mystars.setImageDrawable(getResources().getDrawable(R.drawable.star10));
				}

				holder.mydate.setText(((Integer) holder.mydiary.day).toString());

				Calendar today = Calendar.getInstance();
				today.set(Calendar.YEAR, holder.mydiary.year);
				today.set(Calendar.MONTH, holder.mydiary.month - 1);
				today.set(Calendar.DATE, holder.mydiary.day);
				i = today.get(Calendar.DAY_OF_WEEK);
				if (i == Calendar.SUNDAY) {
					holder.mydate.setTextColor(getResources().getColor(R.color.myred));
				} else {
					holder.mydate.setTextColor(getResources().getColor(R.color.mywhite));
				}

				holder.myweekdate.setText(getWeekDate(i));
				holder.mysteps.setText(((Integer) holder.mydiary.steps).toString());
				holder.mydistance.setText(String.format("%5.2f", holder.mydiary.distance * mfDistanceFactor));
				holder.mycalories.setText(String.format("%d", (int) holder.mydiary.calories));
				holder.mysteptime.setText(Utils.getHoursMinutesString((int) (holder.mydiary.steptime / 1000)));
				holder.mydistanceunit.setText(msDistanceUnit);

				v.setTag(holder);
				return v;
			}

			public class ViewHolder {
				MyDiary mydiary;
				TextView mydate;
				TextView myweekdate;
				TextView mysteps;
				TextView mydistance;
				TextView mycalories;
				TextView mysteptime;
				TextView mydistanceunit;
				ImageView mystars;
				ImageButton myimagebutton;
			}

			public String getWeekDate(int day) {
				String weekdate;

				switch (day) {
				case Calendar.SUNDAY:
					weekdate = getString(R.string.sun);
					break;
				case Calendar.MONDAY:
					weekdate = getString(R.string.mon);
					break;
				case Calendar.TUESDAY:
					weekdate = getString(R.string.tue);
					break;
				case Calendar.WEDNESDAY:
					weekdate = getString(R.string.wed);
					break;
				case Calendar.THURSDAY:
					weekdate = getString(R.string.thu);
					break;
				case Calendar.FRIDAY:
					weekdate = getString(R.string.fri);
					break;
				case Calendar.SATURDAY:
					weekdate = getString(R.string.sat);
					break;
				default:
					weekdate = getString(R.string.sun);
					break;
				}
				return weekdate;
			}
		}

		private class MyDiary {
			public int year, month, day, steps, goalsteps;
			public float distance, calories;
			public long steptime;

			public MyDiary(int y, int m, int d, int s, float dis, float cal, long st, int gl) {
				year = y;
				month = m;
				day = d;
				steps = s;
				distance = dis;
				calories = cal;
				steptime = st;
				goalsteps = gl;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// mDB.close();

	}

	@Override
	protected void onDestroy() {
		// if (adView != null) {
		// adView.destroy(); // this cause error : WebView.destroy() called while still attached
		// }
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent upIntent = new Intent(this, Pedometer.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.from(this).addNextIntent(upIntent).startActivities();
				finish();
			} else {
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		} else if (id == R.id.menu_delete_month) {
			mMonthToDelete = (Calendar) mToday.clone();
			int position = mViewPager.getCurrentItem();
			mMonthToDelete.add(Calendar.MONTH, -(mNumberMonths - 1 - position));

			openDeleteMonthAlertDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent;
			int[] array = new int[2];
			array[0] = Pedometer.SPINNER_ITEM_PEDOMETER;
			intent = new Intent(getBaseContext(), Pedometer.class);
			intent.putExtra("navigation_intent", array);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void openDeleteMonthAlertDialog() {
		new AlertDialog.Builder(this)
				// .setTitle(R.string.alert_pause_title)
				.setMessage(getString(R.string.alert_delete_month_message) + "\n" + DateFormat.format("MMM. yyyy", mMonthToDelete).toString())
				.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Pedometer.mDB.open();
						Pedometer.mDB.deleteSessionsByMonth(mMonthToDelete.get(Calendar.YEAR), mMonthToDelete.get(Calendar.MONTH) + 1);
						Pedometer.mDB.close();
						mViewPager.getAdapter().notifyDataSetChanged();

					}
				}).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();

	}

	private void openDeleteDayAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(History.this);

		final CharSequence[] items = { getString(R.string.editsteps), getString(R.string.delete) };

		mItem = mPedometerSettings.getHistoryEditOption();
		builder.setSingleChoiceItems(items, mItem, new DialogInterface.OnClickListener() {
			// When you click the radio button
			@Override
			public void onClick(DialogInterface dialog, int item) {

				mItem = item;
			}
		});

		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				int year, month, day;
				year = mDayToDelete.get(Calendar.YEAR);
				month = mDayToDelete.get(Calendar.MONTH) + 1;
				day = mDayToDelete.get(Calendar.DATE);

				switch (mItem) {
				case 0: // edit
					Intent intent = new Intent(getBaseContext(), ActivityHistoryEditsteps.class);
					int[] array = new int[4];
					array[0] = year;
					array[1] = month;
					array[2] = day;
					array[3] = mStepsToDelete;
					intent.putExtra("edit_history", array);
					startActivity(intent);
					break;
				case 1: // delete
					// Log.i("History_Fragment",
					// ((Integer) year).toString() + ","
					// + ((Integer) month).toString()
					// + "," + ((Integer) day).toString());
					Pedometer.mDB.open();
					Pedometer.mDB.deleteSessionsByDay(year, month, day);
					Pedometer.mDB.close();
					mViewPager.getAdapter().notifyDataSetChanged();
					break;
				default:
					// Log.i(TAG, "Facebook mode3");
					break;

				}
				mPedometerSettings.setHistoryEditOption(mItem);
			}
		});
		builder.setNegativeButton(getString(R.string.cancelled), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

		// new AlertDialog.Builder(this)
		// // .setTitle(R.string.alert_pause_title)
		// .setMessage(
		// getString(R.string.alert_delete_day_message)
		// + "\n"
		// + DateFormat.format("E, MMM. dd yyyy",
		// mDayToDelete).toString())
		// .setPositiveButton(R.string.dialog_yes,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		//
		// int year, month, day;
		// year = mDayToDelete.get(Calendar.YEAR);
		// month = mDayToDelete.get(Calendar.MONTH) + 1;
		// day = mDayToDelete.get(Calendar.DATE);
		// Log.i("History_Fragment",
		// ((Integer) year).toString() + ","
		// + ((Integer) month).toString()
		// + ","
		// + ((Integer) day).toString());
		// Pedometer.mDB.open();
		// Pedometer.mDB.deleteSessionsByDay(year, month,
		// day);
		// Pedometer.mDB.close();
		// mViewPager.getAdapter().notifyDataSetChanged();
		// }
		// })
		// .setNegativeButton(R.string.dialog_no,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// }
		// }).show();

	}

	private int getNumberMonths() {
		int numberMonths;
		numberMonths = (mToday.get(Calendar.YEAR) - mFirstDay.get(Calendar.YEAR)) * 12;
		numberMonths = numberMonths - mFirstDay.get(Calendar.MONTH) + 1;
		numberMonths += mToday.get(Calendar.MONTH);
		return numberMonths;
	}

	static class hHandler extends Handler {
		private final WeakReference<History> mTarget;

		hHandler(History target) {
			mTarget = new WeakReference<History>(target);
		}

		@Override
		public void handleMessage(Message msg) {
			History target = mTarget.get();
			switch (msg.what) {
			case MESSAGE_DELETE_DAY:
				//Log.i("Chart_Fragment", "ha ha");

				target.openDeleteDayAlertDialog();
				// target.invalidateOptionsMenu();
				break;
			}
		}
	}

}