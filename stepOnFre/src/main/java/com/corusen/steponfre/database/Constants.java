package com.corusen.steponfre.database;

public class Constants {

	public static final String ACCUPEDO_INTERNAL_DATABASE_PATH 	= "/data/com.corusen.steponfre/databases/";
	public static final String ACCUPEDO_FOLDERNAME 			= "/StepOn";
	public static final String DATABASE_FILENAME 			= "StepOn.db";
	public static final String ACCUPEDO_CSV_FILENAME 		= "StepOn.csv";
	public static final String DATABASE_NAME 	= "datastorage";
	public static final String TABLE_NAME 		= "diaries";
	public static final int DATABASE_VERSION 	= 4;


	public static final String KEY_ID 			= "_id";
	public static final String KEY_LAP 			= "lap";
	public static final String KEY_YEAR 		= "year";
	public static final String KEY_MONTH 		= "month";
	public static final String KEY_DAY 			= "day";
	public static final String KEY_HOUR 		= "hour";
	public static final String KEY_MINUTE 		= "minute";
	public static final String KEY_LAPSTEPS 	= "lapsteps";
	public static final String KEY_LAPDISTANCE 	= "lapdistance";
	public static final String KEY_LAPCALORIES 	="lapcalories";
	public static final String KEY_LAPSTEPTIME 	= "lapsteptime";
	public static final String KEY_STEPS 		= "steps";
	public static final String KEY_DISTANCE 	= "distance";
	public static final String KEY_CALORIES 	= "calories";
	public static final String KEY_SPEED 		= "speed";
	public static final String KEY_PACE 		= "pace";
	public static final String KEY_STEPTIME 	= "steptime";
	public static final String KEY_ACHIEVEMENT	= "achievement";
	public static final String KEY_INDICATOR 	= "indicator";
	public static final String KEY_EXERCISE 	= "exercise";


	//public static final int SPINNER_ITEM_SETTINGSFIRST 	= 11;
	public static final int SPINNER_PEDOMETER 		= 0;
	public static final int SPINNER_CHART 			= 1;
	public static final int SPINNER_HISTORY 		= 2;
	public static final int SPINNER_SHARE 			= 3;
	public static final int SPINNER_EDITSTEPS 		= 4;
	public static final int SPINNER_BACKUP 			= 5;
	public static final int SPINNER_SETTINGS 		= 6;
	public static final int SPINNER_HELP 			= 7;
	public static final int SPINNER_QUIT 			= 8;
	public static final int SPINNER_PURCHASE 		= 9;

	public static final int DIALOG_PAUSE 			= 1;
	public static final int DIALOG_RESUME 			= 2;
	public static final int DIALOG_NEXT_LAP 		= 3;
	public static final int DIALOG_DAILY_RESET 		= 4;

	public static final int NUM_HOURLY_BARS 		= 60;

	public static final int STOP_MODE_NOSAVE 		= 0; // when stop button pressed
	public static final int STOP_MODE_SAVE 			= 1; // when stop button pressed
	public static final int START_MODE 				= 2; // when start button pressed
	public static final int RESUME_MODE		 		= 3; // when resume button pressed
	public static final int PAUSE_MODE 				= 4; // when pause button pressed


	public static final float ADMOB_INTERSTITIAL_DISPLAY_CHANCE = 0.35f;
	public static final String ACCUPEDO_ADMOB_INTERSTITIAL_ID 	= "ca-app-pub-3096958453849564/1905963732";
	public static final String ACCUPEDO_ADMOB_ID 				= "ca-app-pub-3096958453849564/9429230531";

	public static final boolean IS_VERSION_TE			= true;  //true for TE, false for PRO
	public static final boolean IS_GOOGLE_PLAY  		= false;  //true for Play, false for other app stores, used for myfitnesspal and pro button
	public static final boolean IS_ADMOB_INTERSTITIAL	= true;  //true for Admob, false to use Amazon

}



//	public static final String INTERNAL_DATABASE_PATH = "/data/com.corusen.steponfre/databases/";
//	public static final String TABLE_NAME2 	= "trial";
//
//	public static final String KEY_SUMSTEPS = "sumsteps";
//	public static final String KEY_AVESTEPS = "avesteps";
//	// Column indexes
//	public static final int DATE_COLUMN = 1;
//	public static final int AMOUNT_COLUMN = 2;
//	public static final int UNITS_COLUMN = 3;
//
//	// Create the constants used to differentiate between the different URI requests
//	public static final int ENTRIES = 1;
//	public static final int ENTRY_ID = 2;
//	public static final int ENTRIES_GROUP_DATE = 3;
//	public static final int ENTRIES_LATEST = 4;
//public static final String ACCUPEDO_FOLDERNAMWOSLASH = "StepOn";
//public static final int GDRIVE_REQUEST_ACCOUNT_PICKER = 1;
//	public static final int GDRIVE_REQUEST_AUTHORIZATION = 2;
