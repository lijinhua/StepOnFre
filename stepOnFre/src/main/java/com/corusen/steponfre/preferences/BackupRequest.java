package com.corusen.steponfre.preferences;

import android.app.backup.BackupManager;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class BackupRequest extends DialogPreference {
	BackupManager backup;

	public BackupRequest(Context context) {
		super(context, null);
		backup = new BackupManager(getContext());
	}
	
	public BackupRequest(Context context, AttributeSet attr) {
		super(context, attr);
		backup = new BackupManager(getContext());
	}

	public BackupRequest(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		backup = new BackupManager(getContext());
	}
	

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			try {
				backup.dataChanged();
			} catch (NumberFormatException e) {
				this.showDialog(null);
				return;
			}
		}
	}

}
