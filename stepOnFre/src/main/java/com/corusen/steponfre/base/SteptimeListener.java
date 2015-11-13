/*
 *  AccuPedo - Android App
 *  Copyright (C) 2011 Corusen, LLC.
 *
 */

package com.corusen.steponfre.base;

public interface SteptimeListener {
    public void onSteptime(long millisecond);
    public void onSteptime13(long millisecond);
    public void onSteptimeEstimated(long millisecond);
    public void passValue();
}

