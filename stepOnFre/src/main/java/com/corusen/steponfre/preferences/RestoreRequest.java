package com.corusen.steponfre.preferences;

import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

public class RestoreRequest extends DialogPreference {
	BackupManager backup;
	RestoreObserver restoreObserver;

	public RestoreRequest(Context context) {
		super(context, null);
		backup = new BackupManager(getContext());
//		restoreObserver = new RestoreObserver();
	}
	
	public RestoreRequest(Context context, AttributeSet attr) {
		super(context, attr);
		backup = new BackupManager(getContext());
	}

	public RestoreRequest(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		backup = new BackupManager(getContext());
	}
	

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			try {
				int result;
				result = backup.requestRestore(new RestoreObserver() {
					public void restoreFinished(int error) {
                        /** Done with the restore!  Now draw the new state of our data */
                        Log.i("RequestRestore", "Restore finished, error = " + error);
                    }
				});
				Toast.makeText(getContext(), "request result:"+((Integer) result).toString(),
						Toast.LENGTH_SHORT).show();
			} catch (NumberFormatException e) {
				this.showDialog(null);
				return;
			}
		}
	}

}
