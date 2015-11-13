package com.corusen.steponfre.preferences;

import java.util.Calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;


public class RemoveDataYear extends DialogPreference {
	    protected int lastHour=0;
	    protected int lastMinute=0;
	    protected boolean is24HourFormat;
	    protected DatePicker picker=null;
	    protected TextView timeDisplay;
	    
	    private long mDate;

	    public RemoveDataYear(Context ctxt) {
	        this(ctxt, null);
	    }

	    public RemoveDataYear(Context ctxt, AttributeSet attrs) {
	        this(ctxt, attrs, 0);
	    }

	    public RemoveDataYear(Context ctxt, AttributeSet attrs, int defStyle) {
	        super(ctxt, attrs, defStyle);

	        is24HourFormat = DateFormat.is24HourFormat(ctxt);
	        setPositiveButtonText("Set");
	        setNegativeButtonText("Cancel");
	    }

	    @Override
	    public String toString() {
	        if(is24HourFormat) {
	            return ((lastHour < 10) ? "0" : "")
	                    + Integer.toString(lastHour)
	                    + ":" + ((lastMinute < 10) ? "0" : "")
	                    + Integer.toString(lastMinute);
	        } else {
	            int myHour = lastHour % 12;
	            return ((myHour == 0) ? "12" : ((myHour < 10) ? "0" : "") + Integer.toString(myHour))
	                    + ":" + ((lastMinute < 10) ? "0" : "") 
	                    + Integer.toString(lastMinute) 
	                    + ((lastHour >= 12) ? " PM" : " AM");
	        }
	    }

	    @Override
	    protected View onCreateDialogView() {
	        picker=new DatePicker(getContext().getApplicationContext());
//	        mDate = getPersistedLong(System.currentTimeMillis());
	        
	        mDate = System.currentTimeMillis();
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(mDate);

//	        int day = calendar.get(Calendar.DAY_OF_MONTH);
//	        int month = calendar.get(Calendar.MONTH);
//	        int year = calendar.get(Calendar.YEAR);
	        
//	        int day = 2010;
//	        int month = 12;
//	        int year = 25;
	        return(picker);
	    }

	    @Override
	    protected void onBindDialogView(View v) {
	        super.onBindDialogView(v);
//	        picker.set
//	        picker.setCurrentYear(lastHour);
//	        picker.setCurrentMinute(lastMinute);
	    }

	    @Override
	    public void onBindView(View view) {
	        View widgetLayout;
	        int childCounter = 0;
	        do {
	            widgetLayout = ((ViewGroup) view).getChildAt(childCounter);
	            childCounter++;
	        } while (widgetLayout.getId() != android.R.id.widget_frame); 
	        ((ViewGroup) widgetLayout).removeAllViews();
//	        timeDisplay = new TextView(widgetLayout.getContext());
//	        timeDisplay.setText(toString());
//	        ((ViewGroup) widgetLayout).addView(timeDisplay);
	        super.onBindView(view);
	    }

	    @Override
	    protected void onDialogClosed(boolean positiveResult) {
	        super.onDialogClosed(positiveResult);

//	        if (positiveResult) {
//	            picker.clearFocus();
//	            lastHour=picker.getCurrentHour();
//	            lastMinute=picker.getCurrentMinute();
//
//	            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);
//
//	            if (callChangeListener(time)) {
//	                persistString(time);
//	                timeDisplay.setText(toString());
//	            }
//	        }
	    }

	    @Override
	    protected Object onGetDefaultValue(TypedArray a, int index) {
	        return(a.getString(index));
	    }

	    @Override
	    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
//	        String time=null;
//
//	        if (restoreValue) {
//	            if (defaultValue==null) {
//	                time=getPersistedString("00:00");
//	            }
//	            else {
//	                time=getPersistedString(defaultValue.toString());
//	            }
//	        }
//	        else {
//	            if (defaultValue==null) {
//	                time="00:00";
//	            }
//	            else {
//	                time=defaultValue.toString();
//	            }
//	            if (shouldPersist()) {
//	                persistString(time);
//	            }
//	        }
//
//	        String[] timeParts=time.split(":");
//	        lastHour=Integer.parseInt(timeParts[0]);
//	        lastMinute=Integer.parseInt(timeParts[1]);;
	    }
	}