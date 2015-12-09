package com.corusen.steponfre.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.format.Time;

//import android.util.Log;

public class MyDB {

	private static final String TAG = "MyDB";

	private SQLiteDatabase mDB;
	private final Context context;
	private final MyDBhelper dbhelper;

	public static final Object[] dbLock = new Object[0];

	public MyDB(Context c) {
		context = c;
		dbhelper = new MyDBhelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
	}

	public void close() {
		mDB.close();
		dbhelper.close();
	}

	public void open() throws SQLiteException {
		try {
			mDB = dbhelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			// Log.v("Open database exception caught", ex.getMessage());
			mDB = dbhelper.getReadableDatabase();
		}
	}

	public int getDBVersion() { return dbhelper.getDBVersion(mDB); }

	public void upgradeDBVersion() { dbhelper.onUpgrade(mDB, getDBVersion(), Constants.DATABASE_VERSION ); }

	public long saveStepsToDatabase(int session, int lap, int lapsteps, float lapdistance, float lapcalories, long lapsteptime, int steps, float distance,
			float calories, float speed, int pace, long steptime, int achievement, int indicator, int exercise) {
		Time time = new Time();
		time.setToNow();
		try {
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(Constants.KEY_LAP, lap);
			newTaskValue.put(Constants.KEY_YEAR, time.year);
			newTaskValue.put(Constants.KEY_MONTH, time.month + 1); // month[0-11]
			newTaskValue.put(Constants.KEY_DAY, time.monthDay);
			newTaskValue.put(Constants.KEY_HOUR, time.hour);
			newTaskValue.put(Constants.KEY_MINUTE, time.minute);
			newTaskValue.put(Constants.KEY_LAPSTEPS, lapsteps);
			newTaskValue.put(Constants.KEY_LAPDISTANCE, lapdistance);
			newTaskValue.put(Constants.KEY_LAPCALORIES, lapcalories);
			newTaskValue.put(Constants.KEY_LAPSTEPTIME, lapsteptime);
			newTaskValue.put(Constants.KEY_STEPS, steps);
			newTaskValue.put(Constants.KEY_DISTANCE, distance);
			newTaskValue.put(Constants.KEY_CALORIES, calories);
			newTaskValue.put(Constants.KEY_SPEED, speed);
			newTaskValue.put(Constants.KEY_PACE, pace);
			newTaskValue.put(Constants.KEY_STEPTIME, steptime);
			newTaskValue.put(Constants.KEY_ACHIEVEMENT, achievement);
			newTaskValue.put(Constants.KEY_INDICATOR, indicator);
			newTaskValue.put(Constants.KEY_EXERCISE, exercise);
			return mDB.insert(Constants.TABLE_NAME, null, newTaskValue);
		} catch (SQLiteException ex) {
			// Log.v("Insert into database exception caught", ex.getMessage());
			return -1;
		}
	}

	public Cursor queryDataBase() {
		Cursor c = mDB.query(Constants.TABLE_NAME, null, null, null, null, null, null);
		return c;
	}

	public Cursor queryDaySteps(int year, int month, int day) throws SQLException {

		try {
			String where = "SELECT " + Constants.KEY_ID + ", " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", "
					+ Constants.KEY_HOUR + ", " + Constants.KEY_MINUTE + ", " + Constants.KEY_STEPS + ", " + Constants.KEY_ACHIEVEMENT + " FROM "
					+ Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR + " = ?" + " AND " + Constants.KEY_MONTH + " = ?" + " AND " + Constants.KEY_DAY
					+ " = ?" + " AND " + Constants.KEY_INDICATOR + " >= ?";

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString(), "1" };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor queryDayAll(int year, int month, int day) throws SQLException {

		try {
			String where = "SELECT " + Constants.KEY_ID + ", " + Constants.KEY_LAP + ", " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", "
					+ Constants.KEY_DAY + ", " + Constants.KEY_HOUR + ", " + Constants.KEY_MINUTE + ", " + Constants.KEY_LAPSTEPS + ", "
					+ Constants.KEY_LAPDISTANCE + ", " + Constants.KEY_LAPCALORIES + ", " + Constants.KEY_LAPSTEPTIME + ", " + Constants.KEY_STEPS + ", "
					+ Constants.KEY_DISTANCE + ", " + Constants.KEY_CALORIES + ", " + Constants.KEY_SPEED + ", " + Constants.KEY_PACE + ", "
					+ Constants.KEY_STEPTIME + ", " + Constants.KEY_ACHIEVEMENT + ", " + Constants.KEY_INDICATOR + " FROM " + Constants.TABLE_NAME + " WHERE "
					+ Constants.KEY_YEAR + " = ?" + " AND " + Constants.KEY_MONTH + " = ?" + " AND " + Constants.KEY_DAY + " = ?";

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString() };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToLast(); //V131   c.moveToFirst);
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor queryAllDayMaxSteps() {
		try {

			String where = "SELECT " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", " + Constants.KEY_STEPS + ", "
					+ Constants.KEY_DISTANCE + ", " + Constants.KEY_CALORIES + ", " + Constants.KEY_STEPTIME + ", " + Constants.KEY_ACHIEVEMENT + ", " + "MAX("
					+ Constants.KEY_ID + ") as " + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME + " GROUP BY " + Constants.KEY_YEAR + " , "
					+ Constants.KEY_MONTH + " , " + Constants.KEY_DAY;

			String[] whereArgs = null;

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor queryAllDayMaxSteps(int year) {
		try {

			String where = "SELECT " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", " + Constants.KEY_STEPS + ", "
					+ Constants.KEY_DISTANCE + ", " + Constants.KEY_CALORIES + ", " + Constants.KEY_STEPTIME + ", " + Constants.KEY_ACHIEVEMENT + ", " + "MAX("
					+ Constants.KEY_ID + ") as " + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR + " = ?" + " AND "
					+ Constants.KEY_INDICATOR + " >= ?" + " GROUP BY " + Constants.KEY_YEAR + " , " + Constants.KEY_MONTH + " , " + Constants.KEY_DAY;

			String[] whereArgs = { ((Integer) year).toString(), "1" };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor queryMonthDayMaxSteps(int year, int month) {
		try {

			String where = "SELECT " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", " + Constants.KEY_STEPS + ", "
					+ Constants.KEY_DISTANCE + ", " + Constants.KEY_CALORIES + ", " + Constants.KEY_STEPTIME + ", " + Constants.KEY_ACHIEVEMENT + ", " + "MAX("
					+ Constants.KEY_ID + ") as " + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR + " = ?" + " AND "
					+ Constants.KEY_MONTH + " = ?" + " GROUP BY " + Constants.KEY_DAY;

			// + " AND " + Constants.KEY_INDICATOR + " = ?"
			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString() };
			// , "0" };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor queryDayMaxSteps(int year, int month, int day) throws SQLException {
		try {

			String where = "SELECT " + Constants.KEY_ID + ", " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", "
					+ Constants.KEY_STEPS + ", " + Constants.KEY_DISTANCE + ", " + Constants.KEY_CALORIES + ", " + Constants.KEY_STEPTIME + ", " + "MAX("
					+ Constants.KEY_ID + ") as " + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR + " = ?" + " AND "
					+ Constants.KEY_MONTH + " = ?" + " AND " + Constants.KEY_DAY + " = ?" + " AND " + Constants.KEY_INDICATOR + " >= ?";

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString(), "1" };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}

	}

	public void updateDayMaxSteps(int y, int m, int d, int s, float dis, float cal, long st) {

		Cursor c = queryDayAll(y, m, d);
		if (c != null) {
			c.moveToLast();
		} else {
			return;
		}

		int key, lap, year, month, day, hour, minute, lapsteps, steps, pace, achievement, indicator;
		float lapdistance, distance, lapcalories, calories, speed;
		long lapsteptime, steptime;

		key = c.getInt(c.getColumnIndex(Constants.KEY_ID));
		year = c.getInt(c.getColumnIndex(Constants.KEY_YEAR));
		month = c.getInt(c.getColumnIndex(Constants.KEY_MONTH));
		day = c.getInt(c.getColumnIndex(Constants.KEY_DAY));
		hour = c.getInt(c.getColumnIndex(Constants.KEY_HOUR));
		minute = c.getInt(c.getColumnIndex(Constants.KEY_MINUTE));
		lap = c.getInt(c.getColumnIndex(Constants.KEY_LAP));
		lapsteps = c.getInt(c.getColumnIndex(Constants.KEY_LAPSTEPS));
		lapdistance = c.getFloat(c.getColumnIndex(Constants.KEY_LAPDISTANCE));
		lapcalories = c.getFloat(c.getColumnIndex(Constants.KEY_LAPCALORIES));
		lapsteptime = c.getLong(c.getColumnIndex(Constants.KEY_LAPSTEPTIME));
		steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
		distance = c.getFloat(c.getColumnIndex(Constants.KEY_DISTANCE));
		calories = c.getFloat(c.getColumnIndex(Constants.KEY_CALORIES));
		speed = c.getFloat(c.getColumnIndex(Constants.KEY_SPEED));
		pace = c.getInt(c.getColumnIndex(Constants.KEY_PACE));
		steptime = c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME));
		achievement = c.getInt(c.getColumnIndex(Constants.KEY_ACHIEVEMENT));
		indicator = c.getInt(c.getColumnIndex(Constants.KEY_INDICATOR));

		// new step value
		steps = s;
		distance = dis;
		calories = cal;
		steptime = st;

		switch (indicator) {
		case 0: // when lap is 0, not going to happen
			break;
		case 1: // one minute sample data
			indicator = 3;
			break;
		case 2: // start and stop
			indicator = 2;
			break;
		case 3: // other data
			indicator = 3;
		default:
			indicator = 3;
			break;

		}

		ContentValues newTaskValue = new ContentValues();
		try {
			newTaskValue.put(Constants.KEY_LAP, lap);
			newTaskValue.put(Constants.KEY_YEAR, year);
			newTaskValue.put(Constants.KEY_MONTH, month); // month[0-11]
			newTaskValue.put(Constants.KEY_DAY, day);
			newTaskValue.put(Constants.KEY_HOUR, hour);
			newTaskValue.put(Constants.KEY_MINUTE, minute);
			newTaskValue.put(Constants.KEY_LAPSTEPS, lapsteps);
			newTaskValue.put(Constants.KEY_LAPDISTANCE, lapdistance);
			newTaskValue.put(Constants.KEY_LAPCALORIES, lapcalories);
			newTaskValue.put(Constants.KEY_LAPSTEPTIME, lapsteptime);
			newTaskValue.put(Constants.KEY_STEPS, steps);
			newTaskValue.put(Constants.KEY_DISTANCE, distance);
			newTaskValue.put(Constants.KEY_CALORIES, calories);
			newTaskValue.put(Constants.KEY_SPEED, speed);
			newTaskValue.put(Constants.KEY_PACE, pace);
			newTaskValue.put(Constants.KEY_STEPTIME, steptime);
			newTaskValue.put(Constants.KEY_ACHIEVEMENT, achievement);
			newTaskValue.put(Constants.KEY_INDICATOR, indicator);

		} catch (SQLiteException ex) {
			// Log.v("Insert into database exception caught", ex.getMessage());
			return;
		}

		mDB.update(Constants.TABLE_NAME, newTaskValue, Constants.KEY_ID + "=" + key, null);
	}

	public void insertDayMaxSteps(int y, int m, int d, int s, float dis, float cal, long st) {

		Cursor c = queryDayAll(y, m, d);
		if (c != null) {
			c.moveToLast();
		} else {
			return;
		}

		int lap, year, month, day, hour, minute, lapsteps, steps, pace, achievement;
		float lapdistance, distance, lapcalories, calories, speed;
		long lapsteptime, steptime;

		year = y;
		month = m;
		day = d;
		hour = 23;
		minute = 0;
		lap = 0;
		lapsteps = 0;
		lapdistance = 0;
		lapcalories = 0;
		lapsteptime = 0;
		steps = s;
		distance = dis;
		calories = cal;
		speed = 0;
		pace = 0;
		steptime = st;
		achievement = 0;

		ContentValues newTaskValue = new ContentValues();
		try {
			newTaskValue.put(Constants.KEY_LAP, lap);
			newTaskValue.put(Constants.KEY_YEAR, year);
			newTaskValue.put(Constants.KEY_MONTH, month); // month[0-11]
			newTaskValue.put(Constants.KEY_DAY, day);
			newTaskValue.put(Constants.KEY_HOUR, hour);
			newTaskValue.put(Constants.KEY_MINUTE, minute);
			newTaskValue.put(Constants.KEY_LAPSTEPS, lapsteps);
			newTaskValue.put(Constants.KEY_LAPDISTANCE, lapdistance);
			newTaskValue.put(Constants.KEY_LAPCALORIES, lapcalories);
			newTaskValue.put(Constants.KEY_LAPSTEPTIME, lapsteptime);
			newTaskValue.put(Constants.KEY_STEPS, steps);
			newTaskValue.put(Constants.KEY_DISTANCE, distance);
			newTaskValue.put(Constants.KEY_CALORIES, calories);
			newTaskValue.put(Constants.KEY_SPEED, speed);
			newTaskValue.put(Constants.KEY_PACE, pace);
			newTaskValue.put(Constants.KEY_STEPTIME, steptime);
			newTaskValue.put(Constants.KEY_ACHIEVEMENT, achievement);
			mDB.insert(Constants.TABLE_NAME, null, newTaskValue);
		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}
	}


	public Cursor queryDayMaxStepsForMonth(int year, int month) throws SQLException {
		try {

			String where = "SELECT " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", " + Constants.KEY_STEPS + ", " + "MAX("
					+ Constants.KEY_ID + ") as " + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR + " = ?" + " AND "
					+ Constants.KEY_MONTH + " = ?" + " AND " + Constants.KEY_INDICATOR + " >= ?" + " GROUP BY " + Constants.KEY_DAY;

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), "1" };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor quearyDayMaxStepsForYear(int year) throws SQLException {
		try {

			String where = "SELECT " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", " + Constants.KEY_STEPS + ", " + "MAX("
					+ Constants.KEY_ID + ") as " + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR + " = ?" + " GROUP BY "
					+ Constants.KEY_MONTH + " , " + Constants.KEY_DAY;

			String[] whereArgs = { ((Integer) year).toString() };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor queryLapStepsForDay(int year, int month, int day, int lap) throws SQLException {

		try {
			String where = "SELECT " + Constants.KEY_ID + ", " + Constants.KEY_HOUR + ", " + Constants.KEY_MINUTE + ", " + Constants.KEY_LAPSTEPS + ", "
					+ Constants.KEY_LAPDISTANCE + ", " + Constants.KEY_LAPCALORIES + ", " + Constants.KEY_LAPSTEPTIME + " FROM " + Constants.TABLE_NAME
					+ " WHERE " + Constants.KEY_YEAR + " = ?" + " AND " + Constants.KEY_MONTH + " = ?" + " AND " + Constants.KEY_DAY + " = ?" + " AND "
					+ Constants.KEY_LAP + " = ?"; // + " AND " + Constants.KEY_INDICATOR + " = ?"; //V131

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString(), ((Integer) lap).toString() }; //, "1" };  //V131

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToLast(); //V131  c.moveToFirst()
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor queryLapMaxStepsForDay(int year, int month, int day) throws SQLException {

		try {
			String where = "SELECT " + Constants.KEY_ID + ", " + Constants.KEY_LAP + ", " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", "
					+ Constants.KEY_DAY + ", " + Constants.KEY_HOUR + ", " + Constants.KEY_MINUTE + ", " + Constants.KEY_LAPSTEPS + ", "
					+ Constants.KEY_LAPDISTANCE + ", " + Constants.KEY_LAPCALORIES + ", " + Constants.KEY_LAPSTEPTIME + ", " + Constants.KEY_STEPS + ", "
					+ Constants.KEY_DISTANCE + ", " + Constants.KEY_CALORIES + ", " + Constants.KEY_STEPTIME + ", " + Constants.KEY_ACHIEVEMENT + ", "
					+ Constants.KEY_EXERCISE + ", " + "MAX(" + Constants.KEY_ID + ") as " + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME + " WHERE "
					+ Constants.KEY_YEAR + " = ?" + " AND " + Constants.KEY_MONTH + " = ?" + " AND " + Constants.KEY_DAY + " = ?" + " AND "
					+ Constants.KEY_INDICATOR + " >= ?" + " AND " + Constants.KEY_LAP + " >= ?" + " GROUP BY " + Constants.KEY_LAP;

			// + " AND " + Constants.KEY_INDICATOR + " = ?"
			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString(), "1", "1" };
			// , "1"
			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	// public Cursor queryLapCurrentStepsForDay(int year, int month, int day,
	// int lap) throws SQLException {
	//
	// try {
	// String where = "SELECT " + Constants.KEY_ID + ", "
	// + Constants.KEY_LAP + ", " + Constants.KEY_YEAR + ", "
	// + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", "
	// + Constants.KEY_HOUR + ", " + Constants.KEY_MINUTE + ", "
	// + Constants.KEY_LAPSTEPS + ", " + Constants.KEY_LAPDISTANCE
	// + ", " + Constants.KEY_LAPCALORIES + ", "
	// + Constants.KEY_LAPSTEPTIME + ", " + Constants.KEY_STEPS
	// + ", " + Constants.KEY_DISTANCE + ", "
	// + Constants.KEY_CALORIES + ", " + Constants.KEY_STEPTIME
	// + ", " + Constants.KEY_ACHIEVEMENT + ", "
	// + Constants.KEY_EXERCISE + ", " + "MAX(" + Constants.KEY_ID
	// + ") as " + Constants.KEY_ID + " FROM "
	// + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR
	// + " = ?" + " AND " + Constants.KEY_MONTH + " = ?" + " AND "
	// + Constants.KEY_DAY + " = ?" + " AND " + Constants.KEY_LAP
	// + " = ?" + " GROUP BY " + Constants.KEY_LAP;
	//
	// // + " AND " + Constants.KEY_INDICATOR + " = ?"
	// String[] whereArgs = { ((Integer) year).toString(),
	// ((Integer) month).toString(), ((Integer) day).toString(),
	// ((Integer) lap).toString() };
	// // , "1"
	// Cursor c = mDB.rawQuery(where, whereArgs);
	//
	// if (c != null) {
	// c.moveToLast();
	// }
	// return c;
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	// }

	public Cursor queryLapStartTimeForDay(int year, int month, int day) throws SQLException {

		try {
			String where = "SELECT " + Constants.KEY_LAP + ", " + Constants.KEY_HOUR + ", " + Constants.KEY_MINUTE + ", " + "MIN(" + Constants.KEY_ID + ") as "
					+ Constants.KEY_ID + " FROM " + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR + " = ?" + " AND " + Constants.KEY_MONTH + " = ?"
					+ " AND " + Constants.KEY_DAY + " = ?" + " AND " + Constants.KEY_INDICATOR + " = ?" + " AND " + Constants.KEY_LAP + " >= ?" + " GROUP BY "
					+ Constants.KEY_LAP;

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString(), "1", "1" };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public void queryBeginStepAtSession(int session) {

	}

	public Cursor queryLapNumber(int year, int month, int day) throws SQLException {

		try {
			String where = "SELECT " + Constants.KEY_LAP + ", "
//					+ "MAX(" + Constants.KEY_ID + ") as "
//					+ Constants.KEY_ID + " FROM "

					+ "MAX(" + Constants.KEY_LAP + ") as "
					+ Constants.KEY_LAP + " FROM "
					+ Constants.TABLE_NAME
					+ " WHERE " + Constants.KEY_YEAR + " = ?"
					+ " AND " + Constants.KEY_MONTH + " = ?"
					+ " AND " + Constants.KEY_DAY + " = ?";

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString() };

			Cursor c = mDB.rawQuery(where, whereArgs);
			int a, b;
			if (c != null) {
				c.moveToFirst();
				a = c.getCount();
				b= a;
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public Cursor queryFirstDay() {

		int key = 1;
		try {
			String where = "SELECT " + "MIN(" + Constants.KEY_ID + ") as " + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME;

			String[] whereArgs = {};
			Cursor c = mDB.rawQuery(where, whereArgs);
			if (c != null) {
				c.moveToFirst();
				key = c.getInt(c.getColumnIndex(Constants.KEY_ID));
			} else {
				return null;
			}
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}

		Cursor c1 = queryDayByKey(key);
		return c1;

	}

	public Cursor queryDayByKey(int keyID) throws SQLException {

		try {
			String where = "SELECT " + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + " FROM " + Constants.TABLE_NAME + " WHERE "
					+ Constants.KEY_ID + " = ?";

			String[] whereArgs = { ((Integer) keyID).toString() };

			Cursor c = mDB.rawQuery(where, whereArgs);

			if (c != null) {
				c.moveToFirst();
			}
			return c;
		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
			return null;
		}
	}

	public void deleteAllSessions() {
		try {
			String where = "DELETE FROM " + Constants.TABLE_NAME;

			String[] whereArgs = {};

			mDB.rawQuery(where, whereArgs);

		} catch (SQLiteException ex) {
			// Log.i(TAG, ex.getMessage());
		}
	}

	public void deleteSessionsByMonth(int year, int month) {
		// Log.i("DeleteByMonth", ((Integer) year).toString() + "," + ((Integer) month).toString());
		try {

			String where = Constants.KEY_YEAR + " = ?" + " AND " + Constants.KEY_MONTH + " = ?";

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString() };

			mDB.delete(Constants.TABLE_NAME, where, whereArgs); // rawQuery for
																// DELETE does
																// not work.

		} catch (SQLiteException ex) {
			// /Log.i(TAG, ex.getMessage());
		}
	}

	public void deleteSessionsByDay(int year, int month, int day) {
		//Log.i("DeleteByDay", ((Integer) year).toString() + "," + ((Integer) month).toString());
		try {

			String where = Constants.KEY_YEAR + " = ?" + " AND " + Constants.KEY_MONTH + " = ?" + " AND " + Constants.KEY_DAY + " = ?";

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString() };

			mDB.delete(Constants.TABLE_NAME, where, whereArgs); // rawQuery for
																// DELETE does
																// not work.

		} catch (SQLiteException ex) {
			//Log.i(TAG, ex.getMessage());
		}
	}

	public void deleteSessionsByDayBefore(int day) {
		// Log.i("DeleteByDay", ((Integer) year).toString() + ","
		// + ((Integer) month).toString());
		try {

			String where = Constants.KEY_INDICATOR + " = ?" + " AND " + Constants.KEY_DAY + " <= ?";

			String[] whereArgs = { "1", ((Integer) day).toString() };

			mDB.delete(Constants.TABLE_NAME, where, whereArgs); // rawQuery for
																// DELETE does
																// not work.

		} catch (SQLiteException ex) {
			//Log.i(TAG, ex.getMessage());
		}
	}

	public void deleteLap(int year, int month, int day, int lap) {
		// Log.i("DeleteLap", ((Integer) year).toString() + ","
		// + ((Integer) month).toString());
		try {

			String where = Constants.KEY_YEAR + " = ?" + " AND " + Constants.KEY_MONTH + " = ?" + " AND " + Constants.KEY_DAY + " = ?" + " AND "
					+ Constants.KEY_LAP + " = ?";

			String[] whereArgs = { ((Integer) year).toString(), ((Integer) month).toString(), ((Integer) day).toString(), ((Integer) lap).toString() };

			mDB.delete(Constants.TABLE_NAME, where, whereArgs); // rawQuery for
																// DELETE does
																// not work.

		} catch (SQLiteException ex) {
			//Log.i(TAG, ex.getMessage());
		}
	}

	public void dbInsert(ContentValues newTaskValue) {
		try {

			mDB.insert(Constants.TABLE_NAME, null, newTaskValue);

		} catch (SQLiteException ex) {
			//Log.v("Insert into database exception caught", ex.getMessage());
		}
	}

	/*
	 * 
	 * 
	 * 
	 */

	// public long insertSteps(int steps, float distance, float calories,
	// long steptime, int achievement) {
	// Time time = new Time();
	// time.setToNow();
	// try {
	// ContentValues newTaskValue = new ContentValues();
	// newTaskValue.put(Constants.KEY_YEAR, time.year);
	// newTaskValue.put(Constants.KEY_MONTH, time.month + 1); // month[0-11]
	// newTaskValue.put(Constants.KEY_DAY, time.monthDay);
	// newTaskValue.put(Constants.KEY_HOUR, time.hour);
	// newTaskValue.put(Constants.KEY_MINUTE, time.minute);
	// newTaskValue.put(Constants.KEY_STEPS, steps);
	// newTaskValue.put(Constants.KEY_DISTANCE, distance);
	// newTaskValue.put(Constants.KEY_CALORIES, calories);
	// newTaskValue.put(Constants.KEY_STEPTIME, steptime);
	// newTaskValue.put(Constants.KEY_ACHIEVEMENT, achievement);
	// return db.insert(Constants.TABLE_NAME, null, newTaskValue);
	// } catch (SQLiteException ex) {
	// Log.v("Insert into database exception caught", ex.getMessage());
	// return -1;
	// }
	// }
	//
	// public Cursor getAllSteps() {
	// Cursor c = db.query(Constants.TABLE_NAME, null, null, null, null, null,
	// null);
	// return c;
	// }
	//
	// public Cursor getAllDayMaxSteps() {
	// try {
	//
	// String where = "SELECT " + Constants.KEY_YEAR + ", "
	// + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", "
	// + Constants.KEY_ACHIEVEMENT + ", " + Constants.KEY_STEPS
	// + ", " + Constants.KEY_DISTANCE + ", "
	// + Constants.KEY_CALORIES + ", " + Constants.KEY_STEPTIME
	// + ", " + "MAX(" + Constants.KEY_ID + ") as "
	// + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME
	// + " GROUP BY " + Constants.KEY_YEAR + " , "
	// + Constants.KEY_MONTH + " , " + Constants.KEY_DAY;
	//
	// String[] whereArgs = null;
	//
	// Cursor c = db.rawQuery(where, whereArgs);
	//
	// if (c != null) {
	// c.moveToFirst();
	// }
	// return c;
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	// }
	//
	// public Cursor getDayByKey(int keyID) throws SQLException {
	//
	// try {
	// String where = "SELECT " + Constants.KEY_YEAR + ", "
	// + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + " FROM "
	// + Constants.TABLE_NAME + " WHERE " + Constants.KEY_ID
	// + " = ?";
	//
	// String[] whereArgs = { ((Integer) keyID).toString() };
	//
	// Cursor c = db.rawQuery(where, whereArgs);
	//
	// if (c != null) {
	// c.moveToFirst();
	// }
	// return c;
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	// }
	//
	// public Cursor getFirstDay() {
	//
	// int key = 1;
	// try {
	// String where = "SELECT " + "MIN(" + Constants.KEY_ID + ") as "
	// + Constants.KEY_ID + " FROM " + Constants.TABLE_NAME;
	//
	// String[] whereArgs = {};
	// Cursor c = db.rawQuery(where, whereArgs);
	// if (c != null) {
	// c.moveToFirst();
	// key = c.getInt(c.getColumnIndex(Constants.KEY_ID));
	// } else {
	// return null;
	// }
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	//
	// Cursor c1 = getDayByKey(key);
	// return c1;
	//
	// }
	//
	// public Cursor getDaySteps(int year, int month, int day) throws
	// SQLException {
	//
	// try {
	// String where = "SELECT " + Constants.KEY_ID + ", "
	// + Constants.KEY_YEAR + ", " + Constants.KEY_MONTH + ", "
	// + Constants.KEY_DAY + ", " + Constants.KEY_HOUR + ", "
	// + Constants.KEY_MINUTE + ", " + Constants.KEY_STEPS
	// + " FROM " + Constants.TABLE_NAME + " WHERE "
	// + Constants.KEY_YEAR + " = ?" + " AND "
	// + Constants.KEY_MONTH + " = ?" + " AND "
	// + Constants.KEY_DAY + " = ?";
	//
	// String[] whereArgs = { ((Integer) year).toString(),
	// ((Integer) month).toString(), ((Integer) day).toString() };
	//
	// Cursor c = db.rawQuery(where, whereArgs);
	//
	// if (c != null) {
	// c.moveToFirst();
	// }
	// return c;
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	// }
	//
	// public Cursor getDayMaxSteps(int year, int month, int day)
	// throws SQLException {
	// try {
	//
	// String where = "SELECT " + Constants.KEY_YEAR + ", "
	// + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", "
	// + Constants.KEY_STEPS + ", " + Constants.KEY_DISTANCE
	// + ", " + Constants.KEY_CALORIES + ", "
	// + Constants.KEY_STEPTIME + ", " + "MAX(" + Constants.KEY_ID
	// + ") as " + Constants.KEY_ID + " FROM "
	// + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR
	// + " = ?" + " AND " + Constants.KEY_MONTH + " = ?" + " AND "
	// + Constants.KEY_DAY + " = ?";
	//
	// String[] whereArgs = { ((Integer) year).toString(),
	// ((Integer) month).toString(), ((Integer) day).toString() };
	//
	// Cursor c = db.rawQuery(where, whereArgs);
	//
	// if (c != null) {
	// c.moveToFirst();
	// }
	// return c;
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	//
	// }
	//
	// public Cursor getDayMaxStepsForMonth(int year, int month)
	// throws SQLException {
	// try {
	//
	// String where = "SELECT " + Constants.KEY_YEAR + ", "
	// + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", "
	// + Constants.KEY_STEPS + ", " + "MAX(" + Constants.KEY_ID
	// + ") as " + Constants.KEY_ID + " FROM "
	// + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR
	// + " = ?" + " AND " + Constants.KEY_MONTH + " = ?"
	// + " GROUP BY " + Constants.KEY_DAY;
	//
	// String[] whereArgs = { ((Integer) year).toString(),
	// ((Integer) month).toString() };
	//
	// Cursor c = db.rawQuery(where, whereArgs);
	//
	// if (c != null) {
	// c.moveToFirst();
	// }
	// return c;
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	// }
	//
	// public Cursor getDayMaxStepsForYear(int year) throws SQLException {
	// try {
	//
	// String where = "SELECT " + Constants.KEY_YEAR + ", "
	// + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + ", "
	// + Constants.KEY_STEPS + ", " + "MAX(" + Constants.KEY_ID
	// + ") as " + Constants.KEY_ID + " FROM "
	// + Constants.TABLE_NAME + " WHERE " + Constants.KEY_YEAR
	// + " = ?" + " GROUP BY " + Constants.KEY_MONTH + " , "
	// + Constants.KEY_DAY;
	//
	// String[] whereArgs = { ((Integer) year).toString() };
	//
	// Cursor c = db.rawQuery(where, whereArgs);
	//
	// if (c != null) {
	// c.moveToFirst();
	// }
	// return c;
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	// }
	//
	//
	//
	// public void copyTableOne(Cursor c) {
	// ContentValues newTaskValue = new ContentValues();
	// try {
	// if (c.moveToFirst()) {
	// do {
	// newTaskValue.clear();
	// newTaskValue.put(Constants.KEY_YEAR,
	// c.getInt(c.getColumnIndex(Constants.KEY_YEAR)));
	// newTaskValue.put(Constants.KEY_MONTH,
	// c.getInt(c.getColumnIndex(Constants.KEY_MONTH))); // month[0-11]
	// newTaskValue.put(Constants.KEY_DAY,
	// c.getInt(c.getColumnIndex(Constants.KEY_DAY)));
	// newTaskValue.put(Constants.KEY_HOUR,
	// c.getInt(c.getColumnIndex(Constants.KEY_HOUR)));
	// newTaskValue.put(Constants.KEY_MINUTE,
	// c.getInt(c.getColumnIndex(Constants.KEY_MINUTE)));
	// newTaskValue.put(Constants.KEY_STEPS,
	// c.getInt(c.getColumnIndex(Constants.KEY_STEPS)));
	// newTaskValue.put(Constants.KEY_DISTANCE, c.getFloat(c
	// .getColumnIndex(Constants.KEY_DISTANCE)));
	// newTaskValue.put(Constants.KEY_CALORIES, c.getFloat(c
	// .getColumnIndex(Constants.KEY_CALORIES)));
	// newTaskValue
	// .put(Constants.KEY_STEPTIME, c.getLong(c
	// .getColumnIndex(Constants.KEY_STEPTIME)));
	// newTaskValue.put(Constants.KEY_ACHIEVEMENT, c.getInt(c
	// .getColumnIndex(Constants.KEY_ACHIEVEMENT)));
	//
	// db.insert(Constants.TABLE_NAME, null, newTaskValue);
	// } while (c.moveToNext());
	// }
	// } catch (SQLiteException ex) {
	// Log.v("Insert into database exception caught", ex.getMessage());
	// }
	// }
	//
	// public long insertTrialDay() {
	// Time time = new Time();
	// time.setToNow();
	// try {
	// ContentValues newTaskValue = new ContentValues();
	// newTaskValue.put(Constants.KEY_YEAR, time.year);
	// newTaskValue.put(Constants.KEY_MONTH, time.month + 1); // month[0-11]
	// newTaskValue.put(Constants.KEY_DAY, time.monthDay);
	// return db.insert(Constants.TABLE_NAME2, null, newTaskValue);
	// } catch (SQLiteException ex) {
	// Log.v("Insert into database exception caught", ex.getMessage());
	// return -1;
	// }
	// }
	//
	// public Cursor getTrialDayByKey(int keyID) throws SQLException {
	//
	// try {
	// String where = "SELECT " + Constants.KEY_YEAR + ", "
	// + Constants.KEY_MONTH + ", " + Constants.KEY_DAY + " FROM "
	// + Constants.TABLE_NAME2 + " WHERE " + Constants.KEY_ID
	// + " = ?";
	//
	// String[] whereArgs = { ((Integer) keyID).toString() };
	//
	// Cursor c = db.rawQuery(where, whereArgs);
	//
	// if (c != null) {
	// c.moveToFirst();
	// }
	// return c;
	// } catch (SQLiteException ex) {
	// Log.i(TAG, ex.getMessage());
	// return null;
	// }
	// }
	//
	// public void deleteAll() {
	// String DELETE_DATA = "delete from " + Constants.TABLE_NAME;
	// try {
	// db.execSQL(DELETE_DATA);
	// } catch (SQLiteException ex) {
	// // Log.i(TAG, ex.getMessage());
	// }
	//
	// }
	//
	// public void dbInsert(ContentValues newTaskValue) {
	// try {
	//
	// db.insert(Constants.TABLE_NAME, null, newTaskValue);
	//
	// } catch (SQLiteException ex) {
	// Log.v("Insert into database exception caught", ex.getMessage());
	// }
	// }

}

// public long copyTableOne(Cursor c) {
// try {
// ContentValues newTaskValue = new ContentValues();
// newTaskValue.put(Constants.KEY_YEAR,
// c.getInt(c.getColumnIndex(Constants.KEY_YEAR)));
// newTaskValue.put(Constants.KEY_MONTH,
// c.getInt(c.getColumnIndex(Constants.KEY_MONTH))); // month[0-11]
// newTaskValue.put(Constants.KEY_DAY,
// c.getInt(c.getColumnIndex(Constants.KEY_DAY)));
// newTaskValue.put(Constants.KEY_HOUR,
// c.getInt(c.getColumnIndex(Constants.KEY_HOUR)));
// newTaskValue.put(Constants.KEY_MINUTE,
// c.getInt(c.getColumnIndex(Constants.KEY_MINUTE)));
// newTaskValue.put(Constants.KEY_STEPS,
// c.getInt(c.getColumnIndex(Constants.KEY_STEPS)));
// newTaskValue.put(Constants.KEY_DISTANCE,
// c.getFloat(c.getColumnIndex(Constants.KEY_DISTANCE)));
// newTaskValue.put(Constants.KEY_CALORIES,
// c.getFloat(c.getColumnIndex(Constants.KEY_CALORIES)));
// newTaskValue.put(Constants.KEY_STEPTIME,
// c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME)));
// newTaskValue.put(Constants.KEY_ACHIEVEMENT,
// c.getInt(c.getColumnIndex(Constants.KEY_ACHIEVEMENT)));
//
// return db.insert(Constants.TABLE_NAME, null, newTaskValue);
// } catch (SQLiteException ex) {
// Log.v("Insert into database exception caught", ex.getMessage());
// return -1;
// }
// }