package com.corusen.steponfre.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.corusen.steponfre.database.MyDB;

public class RemoveDataAll extends DialogPreference {

	MyDB dba;

	public RemoveDataAll(Context context) {
		super(context, null);
		dba = new MyDB(getContext());
		dba.open();
	}
	
	public RemoveDataAll(Context context, AttributeSet attr) {
		super(context, attr);
//		initPreferenceDetails();
		dba = new MyDB(getContext());
		dba.open();
	}

	public RemoveDataAll(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
//		initPreferenceDetails();
		dba = new MyDB(getContext());
		dba.open();
	}
	

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			try {
				dba.deleteAllSessions();
//				Log.i("REMOVE", "Remove all history!");
			} catch (NumberFormatException e) {
				this.showDialog(null);
				return;
			}
		}
		
		dba.close();
	}

}
