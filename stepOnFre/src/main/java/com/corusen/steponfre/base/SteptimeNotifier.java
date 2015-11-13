/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */
package com.corusen.steponfre.base;

public class SteptimeNotifier implements SteptimeListener {

    public interface Listener {
        public void valueChanged(long lapsteptime, long steptime);
        public void passValue();
    }
    
    private static final long MANUAL_STEP_TIME = 350L;  //300L
    private Listener mListener;
    private long mLapsteptime = 0;
    private long mSteptime = 0;

    public SteptimeNotifier(Listener listener) {
        mListener = listener;
        reloadSettings();
    }
    public void setSteptime(long lapsteptime, long steptime) {
    	mLapsteptime = lapsteptime;
        mSteptime = steptime;
        notifyListener();
    }
    
    public void reloadSettings() {
        notifyListener();
    }
    
    public void onSteptime(long millisecond) {   
    	mLapsteptime += millisecond;
    	mSteptime += millisecond;
        notifyListener();
    }
    
    public void onSteptime13(long millisecond) {
    	mLapsteptime += millisecond;
    	mSteptime += millisecond;  
        notifyListener();
	}
    
    public void onSteptimeEstimated(long millisecond) {
    	mLapsteptime += millisecond;
    	mSteptime += millisecond;  
        notifyListener();
	}
    
    public void onStep100(int steps) {
    	mSteptime = MANUAL_STEP_TIME * steps;
    	notifyListener();
	}
    
    public long onStep100ForHistory(int steps) {
    	return MANUAL_STEP_TIME * steps;
	}
    
    private void notifyListener() {
        mListener.valueChanged(mLapsteptime, mSteptime);
    }
        
    public void passValue() {
    }


}

