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
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class BodyWeightPreference extends EditTextPreference {

	boolean mIsMetric;
	protected int mTitleResource;
	protected int mMetricUnitsResource;
	protected int mImperialUnitsResource;
	protected float mDisplayedValue;

	
	public BodyWeightPreference(Context context) {
		super(context);
	}
	public BodyWeightPreference(Context context, AttributeSet attr) {
		super(context, attr);
		initPreferenceDetails();
	}
	public BodyWeightPreference(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		initPreferenceDetails();
	}

	protected void initPreferenceDetails() {
		mTitleResource = R.string.body_weight_setting_title;
		mMetricUnitsResource = R.string.kilograms;
		mImperialUnitsResource = R.string.pounds;
	}
	
	protected void showDialog(Bundle state) {
		mIsMetric = PreferenceManager.getDefaultSharedPreferences(getContext())
				.getString("units", "imperial").equals("metric");
		setDialogTitle(getContext().getString(mTitleResource)
				+ " ("
				+ getContext().getString(
						mIsMetric ? mMetricUnitsResource
								: mImperialUnitsResource) + ")");
		setText(String.valueOf(mDisplayedValue));
		super.showDialog(state);
	}

	protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
		editText.setRawInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		super.onAddEditTextToDialogView(dialogView, editText);
	}
   
	
	@Override
	public CharSequence getSummary() {
//		String summary = super.getSummary().toString();
		return String.format("%5.1f", mDisplayedValue);
	}
	
	
	
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

		if (positiveResult) {
			try {
				mDisplayedValue=Float.valueOf(((CharSequence) (getEditText().getText()))
						.toString());
			} catch (NumberFormatException e) {
				this.showDialog(null);
				return;
			}
			
            String sValue=String.valueOf(mDisplayedValue);

            if (shouldPersist()) {
                persistString(sValue);
            }
        }
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
            	sValue=getPersistedString("150");
            }
            else {
            	sValue=getPersistedString(defaultValue.toString());
            }
        }
        else {
            if (defaultValue==null) {
            	sValue="150";
            }
            else {
            	sValue=defaultValue.toString();
            }
            if (shouldPersist()) {
                persistString(sValue);
            }
        }
        mDisplayedValue= Float.parseFloat(sValue);
    }
	
	public void setFloat(Float a) {
		mDisplayedValue = a;
		
		String sValue=String.valueOf(mDisplayedValue);
		if (shouldPersist()) {
			persistString(sValue);
		}
//		persistString(new DecimalFormat("#.##").format(mDisplayedValue));
//		if (shouldPersist()) {
//			persistString(String.format("%5.1f", mDisplayedValue)); //cause FC for other locale
//		}
	}
	
	public float getFloat() {
		return mDisplayedValue;
	}
}

