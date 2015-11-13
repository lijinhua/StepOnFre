/*
 * http://stackoverflow.com/questions/6632282/example-of-how-to-implement-alter-table
 * http://stackoverflow.com/questions/5315997/adding-a-field-to-an-existing-sqlite-database
 * http://stackoverflow.com/questions/3505900/sqliteopenhelper-onupgrade-confusion-android
 * http://www.google.com/codesearch#IrmxZtZAa8k/src/com/android/providers/calendar/CalendarDatabaseHelper.java&type=cs&l=474
 * 
 */

package com.corusen.steponfre.database;

import com.corusen.steponfre.base.PedometerSettings;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.preference.PreferenceManager;
//import android.util.Log;

public class MyDBhelper extends SQLiteOpenHelper {

	private static final String TAG = "MyDBhelper";
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private static float IMPERIAL_WALKING_FACTOR = 0.53f;

	private static final String CREATE_TABLE = "create table " + Constants.TABLE_NAME + " (" + Constants.KEY_ID + " integer primary key autoincrement, "
			+ Constants.KEY_LAP + " int, " + Constants.KEY_YEAR + " int, " + Constants.KEY_MONTH + " int, " + Constants.KEY_DAY + " int, " + Constants.KEY_HOUR
			+ " int, " + Constants.KEY_MINUTE + " int, " + Constants.KEY_LAPSTEPS + " int, " + Constants.KEY_LAPDISTANCE + " float, "
			+ Constants.KEY_LAPCALORIES + " float, " + Constants.KEY_LAPSTEPTIME + " long, " + Constants.KEY_STEPS + " int, " + Constants.KEY_DISTANCE
			+ " float, " + Constants.KEY_CALORIES + " float, " + Constants.KEY_SPEED + " float, " + Constants.KEY_PACE + " int, " + Constants.KEY_STEPTIME
			+ " long, " + Constants.KEY_ACHIEVEMENT + " int, " + Constants.KEY_INDICATOR + " int, " + Constants.KEY_EXERCISE + " int);";

	public MyDBhelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mSettings = PreferenceManager.getDefaultSharedPreferences(context);
		mPedometerSettings = new PedometerSettings(mSettings);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Log.i(TAG, "Creating all the tables");
		try {
			db.execSQL(CREATE_TABLE);
		} catch (SQLiteException ex) {
			//Log.v("Create table exception", ex.getMessage());
		}
	}

	public int getDBVersion(SQLiteDatabase db) {
//		int version = db.getVersion();
//		return version;
		return db.getVersion();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
		// + newVersion);
		//
		// switch (oldVersion) {
		// case 1:
		// convertVersion1to2(db);
		// convertVersion2to3(db);
		// convertVersion3to4(db);
		// break;
		// case 2:
		// convertVersion2to3(db);
		// convertVersion3to4(db);
		// break;
		// case 3:
		// convertVersion3to4(db);
		// break;
		// default:
		// break;
		//
		// }
	}
	//
	// void convertVersion1to2(SQLiteDatabase db) {
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_DISTANCE + " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_CALORIES + " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_STEPTIME + " long ");
	//
	// int keyID, steps;
	// ContentValues updatedValues = new ContentValues();
	// String where;
	// Cursor c = db.query(Constants.TABLE_NAME, null, null, null, null, null,
	// null);
	// // startManagingCursor(c);
	//
	// float stride = mPedometerSettings.getStepLength();
	// float weight = mPedometerSettings.getBodyWeight();
	// if (mPedometerSettings.isMetric()) {
	// stride = mPedometerSettings.getStepLength() / Utils.INCH2CM; // cm to
	// inch
	// weight = mPedometerSettings.getBodyWeight() * Utils.KG2LB; // kg to
	// pounds
	// } else {
	// stride = mPedometerSettings.getStepLength();
	// weight = mPedometerSettings.getBodyWeight();
	// }
	// if (c.moveToFirst()) {
	// do {
	// keyID = c.getInt(c.getColumnIndex(Constants.KEY_ID));
	// steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
	// updatedValues.put(Constants.KEY_DISTANCE, (float) (steps
	// * stride / Utils.MILE2NCH));
	// updatedValues.put(Constants.KEY_CALORIES, (float) (steps
	// * stride / Utils.MILE2NCH * weight * IMPERIAL_WALKING_FACTOR));
	// updatedValues.put(Constants.KEY_STEPTIME,
	// (long) (steps * 0.3 * 1000)); // in milisecond
	// where = Constants.KEY_ID + "=" + keyID;
	// db.update(Constants.TABLE_NAME, updatedValues, where, null);
	// } while (c.moveToNext());
	// }
	// }
	//
	// void convertVersion2to3(SQLiteDatabase db) {
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_ACHIEVEMENT + " int ");
	//
	// int keyID, steps, achievement;
	// ContentValues updatedValues = new ContentValues();
	// String where;
	// Cursor c = db.query(Constants.TABLE_NAME, null, null, null, null, null,
	// null);
	// // startManagingCursor(c);
	//
	// int goal = mPedometerSettings.getGoalSteps();
	// if (c.moveToFirst()) {
	// do {
	// keyID = c.getInt(c.getColumnIndex(Constants.KEY_ID));
	// steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
	// achievement = (steps >= goal) ? 1 : 0;
	// updatedValues.put(Constants.KEY_ACHIEVEMENT, achievement);
	// where = Constants.KEY_ID + "=" + keyID;
	// db.update(Constants.TABLE_NAME, updatedValues, where, null);
	// } while (c.moveToNext());
	// }
	//
	// }
	//
	// void convertVersion3to4(SQLiteDatabase db) {
	//
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAP + " int ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAPSTEPS + " int ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAPDISTANCE + " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAPCALORIES + " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAPSTEPTIME + " long ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_SPEED+ " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_PACE + " int ");
	//
	// ContentValues updatedValues = new ContentValues();
	// String where;
	// Cursor c = db.query(Constants.TABLE_NAME, null, null, null, null, null,
	// null);
	// // startManagingCursor(c);
	// int goalSteps = mPedometerSettings.getGoalSteps();
	// if (c.moveToFirst()) {
	// do {
	// updatedValues.put(Constants.KEY_LAP, 1);
	// updatedValues.put(Constants.KEY_LAPSTEPS,
	// c.getInt(c.getColumnIndex(Constants.KEY_STEPS)));
	// updatedValues.put(Constants.KEY_LAPDISTANCE,
	// c.getFloat(c.getColumnIndex(Constants.KEY_LAPDISTANCE)));
	// updatedValues.put(Constants.KEY_LAPCALORIES,
	// c.getFloat(c.getColumnIndex(Constants.KEY_CALORIES)));
	// updatedValues.put(Constants.KEY_LAPSTEPTIME,
	// c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME)));
	// updatedValues.put(Constants.KEY_SPEED, 1.0);
	// updatedValues.put(Constants.KEY_PACE, 1);
	// updatedValues.put(Constants.KEY_ACHIEVEMENT, goalSteps);
	// where = Constants.KEY_ID + "=" +
	// c.getInt(c.getColumnIndex(Constants.KEY_ID));;
	// db.update(Constants.TABLE_NAME, updatedValues, where, null);
	// } while (c.moveToNext());
	// }
	//
	// }
	//
	// void convertVersion1to2(SQLiteDatabase db) {
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_DISTANCE + " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_CALORIES + " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_STEPTIME + " long ");
	//
	// int keyID, steps;
	// ContentValues updatedValues = new ContentValues();
	// String where;
	// Cursor c = db.query(Constants.TABLE_NAME, null, null, null, null, null,
	// null);
	// // startManagingCursor(c);
	//
	// float stride = mPedometerSettings.getStepLength();
	// float weight = mPedometerSettings.getBodyWeight();
	// if (mPedometerSettings.isMetric()) {
	// stride = mPedometerSettings.getStepLength() / Utils.INCH2CM; // cm to
	// inch
	// weight = mPedometerSettings.getBodyWeight() * Utils.KG2LB; // kg to
	// pounds
	// } else {
	// stride = mPedometerSettings.getStepLength();
	// weight = mPedometerSettings.getBodyWeight();
	// }
	// if (c.moveToFirst()) {
	// do {
	// keyID = c.getInt(c.getColumnIndex(Constants.KEY_ID));
	// steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
	// updatedValues.put(Constants.KEY_DISTANCE, (float) (steps
	// * stride / Utils.MILE2NCH));
	// updatedValues.put(Constants.KEY_CALORIES, (float) (steps
	// * stride / Utils.MILE2NCH * weight * IMPERIAL_WALKING_FACTOR));
	// updatedValues.put(Constants.KEY_STEPTIME,
	// (long) (steps * 0.3 * 1000)); // in milisecond
	// where = Constants.KEY_ID + "=" + keyID;
	// db.update(Constants.TABLE_NAME, updatedValues, where, null);
	// } while (c.moveToNext());
	// }
	// }
	//
	// void convertVersion2to3(SQLiteDatabase db) {
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_ACHIEVEMENT + " int ");
	//
	// int keyID, steps, achievement;
	// ContentValues updatedValues = new ContentValues();
	// String where;
	// Cursor c = db.query(Constants.TABLE_NAME, null, null, null, null, null,
	// null);
	// // startManagingCursor(c);
	//
	// int goal = mPedometerSettings.getGoalSteps();
	// if (c.moveToFirst()) {
	// do {
	// keyID = c.getInt(c.getColumnIndex(Constants.KEY_ID));
	// steps = c.getInt(c.getColumnIndex(Constants.KEY_STEPS));
	// achievement = (steps >= goal) ? 1 : 0;
	// updatedValues.put(Constants.KEY_ACHIEVEMENT, achievement);
	// where = Constants.KEY_ID + "=" + keyID;
	// db.update(Constants.TABLE_NAME, updatedValues, where, null);
	// } while (c.moveToNext());
	// }
	//
	// }
	//
	// void convertVersion3to4(SQLiteDatabase db) {
	//
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAP + " int ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAPSTEPS + " int ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAPDISTANCE + " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAPCALORIES + " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_LAPSTEPTIME + " long ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_SPEED+ " float ");
	// db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN "
	// + Constants.KEY_PACE + " int ");
	//
	// ContentValues updatedValues = new ContentValues();
	// String where;
	// Cursor c = db.query(Constants.TABLE_NAME, null, null, null, null, null,
	// null);
	// // startManagingCursor(c);
	// int goalSteps = mPedometerSettings.getGoalSteps();
	// if (c.moveToFirst()) {
	// do {
	// updatedValues.put(Constants.KEY_LAP, 1);
	// updatedValues.put(Constants.KEY_LAPSTEPS,
	// c.getInt(c.getColumnIndex(Constants.KEY_STEPS)));
	// updatedValues.put(Constants.KEY_LAPDISTANCE,
	// c.getFloat(c.getColumnIndex(Constants.KEY_LAPDISTANCE)));
	// updatedValues.put(Constants.KEY_LAPCALORIES,
	// c.getFloat(c.getColumnIndex(Constants.KEY_CALORIES)));
	// updatedValues.put(Constants.KEY_LAPSTEPTIME,
	// c.getLong(c.getColumnIndex(Constants.KEY_STEPTIME)));
	// updatedValues.put(Constants.KEY_SPEED, 1.0);
	// updatedValues.put(Constants.KEY_PACE, 1);
	// updatedValues.put(Constants.KEY_ACHIEVEMENT, goalSteps);
	// where = Constants.KEY_ID + "=" +
	// c.getInt(c.getColumnIndex(Constants.KEY_ID));;
	// db.update(Constants.TABLE_NAME, updatedValues, where, null);
	// } while (c.moveToNext());
	// }
	//
	// }

}
