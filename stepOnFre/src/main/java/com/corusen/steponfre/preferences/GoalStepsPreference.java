/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */
package com.corusen.steponfre.preferences;

import com.corusen.steponfre.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class GoalStepsPreference extends EditTextPreference {
	
	protected int mTitleResource;
	protected int mDisplayedValue;
	
	public GoalStepsPreference(Context context) {
		super(context);
	}
	public GoalStepsPreference(Context context, AttributeSet attr) {
		super(context, attr);
		initPreferenceDetails();
	}
	public GoalStepsPreference(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		initPreferenceDetails();
	}

	protected void initPreferenceDetails() {
		mTitleResource = R.string.goal_steps_setting_title;
	}
	
	
	protected void showDialog(Bundle state) {
		setDialogTitle(
				getContext().getString(mTitleResource) 
		);
		setText(String.valueOf(mDisplayedValue));
		super.showDialog(state);
	}
	
	protected void onAddEditTextToDialogView (View dialogView, EditText editText) {
		editText.setRawInputType(
				InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		super.onAddEditTextToDialogView(dialogView, editText);
	}
	public void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			try {
				mDisplayedValue=Integer.valueOf(((CharSequence)(getEditText().getText())).toString());
			}
			catch (NumberFormatException e) {
				this.showDialog(null);
				return;
			}
            String sValue=String.valueOf(mDisplayedValue);

            if (shouldPersist()) {
                persistString(sValue);
            }
		}
		super.onDialogClosed(positiveResult);
	}
	
	
   
	
	@Override
	public CharSequence getSummary() {
//		String summary = super.getSummary().toString();
		return String.format(((Integer) mDisplayedValue).toString());
	}
	
	
	
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }
	
	@Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String sValue = null;

        if (restoreValue) {
            if (defaultValue==null) {
            	sValue=getPersistedString("10000");
            }
            else {
            	sValue=getPersistedString(defaultValue.toString());
            }
        }
        else {
            if (defaultValue==null) {
            	sValue="10000";
            }
            else {
            	sValue=defaultValue.toString();
            }
            if (shouldPersist()) {
                persistString(sValue);
            }
        }
        mDisplayedValue= Integer.parseInt(sValue);
    }
}

