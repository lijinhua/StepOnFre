package com.corusen.steponfre.database;

import com.corusen.steponfre.R;
import com.corusen.steponfre.base.AccuService;
import com.corusen.steponfre.base.PedometerSettings;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailHistory extends FragmentActivity implements ActionBar.TabListener {
	private static MyDB mDB;

	private static Calendar mCurrent;
	private static Calendar mToday;
	public static String msDistanceUnit;
	public static float mfDistanceFactor;

	private ActionBar mActionBar;

	DetailHistoryPagerAdapter mDetailHistoryPagerAdapter;
	ViewPager mViewPager;

	private static Calendar mFirstDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.light_collection_history_detail);

		mDB = new MyDB(this);
		mDB.open();

		mToday = Calendar.getInstance();
		mCurrent = (Calendar) mToday.clone();

		int[] intArray;
		Bundle extras = getIntent().getExtras();
		if (!(extras == null)) {
			intArray = extras.getIntArray("detail_history");
			mCurrent.set(Calendar.YEAR, intArray[0]);
			mCurrent.set(Calendar.MONTH, intArray[1] - 1);
			mCurrent.set(Calendar.DATE, intArray[2]);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		PedometerSettings pedometerSettings = new PedometerSettings(settings);

		if (pedometerSettings.isMetric()) {
			msDistanceUnit = getString(R.string.km);
			mfDistanceFactor = 1.6f;
		} else {
			msDistanceUnit = getString(R.string.miles);
			mfDistanceFactor = 1.0f;
		}

		mDetailHistoryPagerAdapter = new DetailHistoryPagerAdapter(getSupportFragmentManager());

		mActionBar = getActionBar();
		if (mActionBar != null) {
			mActionBar.setDisplayHomeAsUpEnabled(true);
			mActionBar.setDisplayShowTitleEnabled(true);
			mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.mydarkblue)));
		}

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDetailHistoryPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						mActionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mDetailHistoryPagerAdapter.getCount(); i++) {
			mActionBar.addTab(mActionBar.newTab()
					.setText(mDetailHistoryPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public static class DetailHistoryPagerAdapter extends FragmentStatePagerAdapter {

		public DetailHistoryPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			ListFragment fragment = new DetailHistoryListFragment();
			Bundle args = new Bundle();
			args.putInt(DetailHistoryListFragment.ARG_OBJECT, i);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String string;
			switch (position) {
			case 0:
				string = DateFormat.format("E, MMM dd", mCurrent).toString();
				break;
			case 1:
				string = "Statistics";
				break;
			default:
				string = "Lap info";
				break;
			}
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

	public static class DetailHistoryListFragment extends ListFragment {

		public static final String ARG_OBJECT = "object";
		View mRootView;
		DiaryAdapter myAdapter;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Bundle args = getArguments();
			int position = args.getInt(ARG_OBJECT);
			if (position == 0) {
				mRootView = inflater.inflate(R.layout.light_history_diaries, container, false);
				myAdapter = new DiaryAdapter(inflater);
				this.setListAdapter(myAdapter);
				return mRootView;
			} else {
				mRootView = inflater.inflate(R.layout.light_history_diaries, container, false);
				myAdapter = new DiaryAdapter(inflater);
				this.setListAdapter(myAdapter);
				return mRootView;
			}

		}

		// @Override
		// public void onListItemClick(ListView list, View v, int position, long
		// id) {
		// /**
		// * Toast message will be shown when you click any list element
		// */
		// Toast.makeText(getActivity(),
		// getListView().getItemAtPosition(position).toString(),
		// Toast.LENGTH_LONG).show();
		// }

		private class DiaryAdapter extends BaseAdapter {
			private LayoutInflater mInflater;
			private ArrayList<MyLapDiary> diaries;

			public DiaryAdapter(LayoutInflater inflater) {
				mInflater = inflater;
				diaries = new ArrayList<MyLapDiary>();
				getdata();
			}

			@SuppressWarnings("deprecation")
			public void getdata() {
				int year, month, day, steps, goal, lap, hour, min, exercise;
				float distance, calories;
				long elapsedtime;

				year = mCurrent.get(Calendar.YEAR);
				month = mCurrent.get(Calendar.MONTH) + 1;
				day = mCurrent.get(Calendar.DAY_OF_MONTH);

				if (mDB != null) {  //V551
					mDB.open(); // a few NullPointerException errors
					Cursor c = mDB.queryLapMaxStepsForDay(year, month, day);
					Cursor c2 = mDB.queryLapStartTimeForDay(year, month, day);

					lap = 0;
					if (c != null) {
						if (c.moveToFirst()) {
							do {
								year  = c.getInt(c.getColumnIndex(Constants.KEY_YEAR));
								month = c.getInt(c.getColumnIndex(Constants.KEY_MONTH));
								day = c.getInt(c.getColumnIndex(Constants.KEY_DAY));
								hour  = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
								min = c.getInt(c.getColumnIndex(Constants.KEY_MINUTE));
								lap = c.getInt(c.getColumnIndex(Constants.KEY_LAP));
								steps = c.getInt(c.getColumnIndex(Constants.KEY_LAPSTEPS));
								distance = c.getFloat(c.getColumnIndex(Constants.KEY_LAPDISTANCE));
								calories = c.getFloat(c.getColumnIndex(Constants.KEY_LAPCALORIES));
								elapsedtime = c.getLong(c.getColumnIndex(Constants.KEY_LAPSTEPTIME));
								goal = c.getInt(c.getColumnIndex(Constants.KEY_ACHIEVEMENT));
								exercise = c.getInt(c.getColumnIndex(Constants.KEY_EXERCISE));
								if (lap > 0) {
									MyLapDiary temp = new MyLapDiary(year, month, day, lap, steps, distance, calories, elapsedtime, goal, hour, min, exercise);
									diaries.add(0, temp);
								}
							} while (c.moveToNext());
						}

						if (c2 != null) {
							if (c2.getCount() == c.getCount()) {
								int i = 0;
								if (c2 != null) {
									if (c2.moveToLast()) {
										do {
											lap  = c2.getInt(c.getColumnIndex(Constants.KEY_LAP));
											hour = c2.getInt(c2.getColumnIndex(Constants.KEY_HOUR));
											min  = c2.getInt(c2.getColumnIndex(Constants.KEY_MINUTE));
											if (lap > 0) {
												diaries.get(i).start_hour = hour;
												diaries.get(i).start_min = min;
												i++;
											}
										} while (c2.moveToPrevious());
									}
								}
							}
						}

					}

					mDB.close();
				}
			}

			@Override
			public int getCount() {
				return diaries.size();
			}

			public MyLapDiary getItem(int i) {
				return diaries.get(i);
			}

			public long getItemId(int i) {
				return i;
			}

			public View getView(int arg0, View arg1, ViewGroup arg2) {
				final ViewHolder holder;
				int yr; // WL
				Context context = getActivity();

				View v = arg1;
				if ((v == null) || (v.getTag() == null)) {
					v = mInflater.inflate(R.layout.dark_lapdiaryrow, null);
					holder = new ViewHolder();
					holder.mLap 		= (TextView) v.findViewById(R.id.mLap);
					holder.mSteps 		= (TextView) v.findViewById(R.id.mSteps);
					holder.mDistance 	= (TextView) v.findViewById(R.id.mDistance);
					holder.mCalories 	= (TextView) v.findViewById(R.id.mCalories);
					holder.mSteptime 	= (TextView) v.findViewById(R.id.mSteptime);
					holder.mStarttime 	= (TextView) v.findViewById(R.id.mStarttime);
					holder.mydistanceunit = (TextView) v.findViewById(R.id.distance_unit_text_view);
					holder.mExercise 	= (ImageView) v.findViewById(R.id.image_exercise);
					v.setTag(holder);
				} else {
					holder = (ViewHolder) v.getTag();
				}
				holder.mdiary = getItem(arg0);

				holder.mLap.setText(String.format("%d", holder.mdiary.lap));
				holder.mSteps.setText(String.format("%d", holder.mdiary.steps)); // + " " + getString(R.string.steps));
				holder.mDistance.setText(String.format("%5.2f", holder.mdiary.distance * mfDistanceFactor));
				holder.mCalories.setText(String.format("%5.1f", holder.mdiary.calories));
				holder.mSteptime.setText(getHoursMinutesSecondsString((int) (holder.mdiary.steptime / 1000)));
				holder.mStarttime.setText(getHoursMinutesString(holder.mdiary.start_hour, holder.mdiary.start_min));
				holder.mydistanceunit.setText(msDistanceUnit);

				switch (holder.mdiary.exercise) {
				case AccuService.FLAG_MODE_WALK:
					holder.mExercise.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.walk_ic));
					break;
					case AccuService.FLAG_MODE_RUN:
					holder.mExercise.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.run_ic));
						break;
					case AccuService.FLAG_MODE_HIKE:
					holder.mExercise.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hiking_ic));
						break;
					case AccuService.FLAG_MODE_STAIRWAY:
					holder.mExercise.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.stair_ic));
						break;
				}
				v.setTag(holder);
				return v;
			}

			public class ViewHolder {
				MyLapDiary mdiary;
				TextView mLap;
				TextView mSteps;
				TextView mDistance;
				TextView mCalories;
				TextView mSteptime;
				TextView mStarttime;
				TextView mydistanceunit;
				ImageView mExercise;
			}
		}

		private class MyLapDiary {
			public int year, month, day, steps, achievement, lap, start_hour,
					start_min, exercise;
			public float distance, calories;
			public long steptime;

			public MyLapDiary(int y, int m, int d, int lp, int s, float dis,
					float cal, long st, int ac, int hour, int min, int ex) {
				year 		= y;
				month 		= m;
				day 		= d;
				lap 		= lp;
				steps 		= s;
				distance 	= dis;
				calories 	= cal;
				steptime 	= st;
				achievement = ac;
				start_hour 	= hour;
				start_min 	= min;
				exercise 	= ex;
			}
		}

		private String getHoursMinutesSecondsString(int second) {
			String format = String.format("%%0%dd", 2);
			String minutes = String.format(format, (second % 3600) / 60);
			String hours = String.format(format, second / 3600);
			String string = hours + ":" + minutes; // + ":" + seconds;
			return string;
		}

		private String getHoursMinutesString(int hour, int minute) {
			String format = String.format("%%0%dd", 2);
			String minutes = String.format(format, minute);
			String hours = String.format(format, hour);
			String string = hours + ":" + minutes; // + ":" + seconds;
			return string;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mDB.close();
	}


//	private int getNumberMonths() {
//		int numberMonths;
//		numberMonths = (mToday.get(Calendar.YEAR) - mFirstDay
//				.get(Calendar.YEAR)) * 12;
//		numberMonths = numberMonths - mFirstDay.get(Calendar.MONTH) + 1;
//		numberMonths += mToday.get(Calendar.MONTH);
//		return numberMonths;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}