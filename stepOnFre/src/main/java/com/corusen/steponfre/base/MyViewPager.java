/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *  Updated for version 1.04
 *  Added one more LPF to filter out driving peaks
 */

package com.corusen.steponfre.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

//import android.support.v4.view.ViewPager;


public class MyViewPager extends android.support.v4.view.ViewPager {

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		//this.enabled = true;
	}

	@Override
	public boolean onInterceptTouchEvent ( MotionEvent event ) {
		if (getAdapter() == null || getAdapter().getCount() <= 0) {
			return false;
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent ( MotionEvent ev ) {
		if (getAdapter() == null || getAdapter().getCount() <= 0) {
			return false;
		}
		return super.onTouchEvent(ev);
	}

}