/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.corusen.steponfre.base;

import java.util.ArrayList;

public class PaceNotifier implements StepListener {

	public interface Listener {
		public void paceChanged(int value);
		public void passValue();
	}

	private ArrayList<Listener> mListeners = new ArrayList<Listener>();

	private long mLastStepTime = 0;
	private long[] mLastStepDeltas = { -1, -1, -1, -1 };
	private int mLastStepDeltasIndex = 0;
	private long mPace = 0;

	public PaceNotifier() {
		reloadSettings();
	}

	public void setPace(int pace) {
		mPace = pace;
		int avg = (int) (60 * 1000.0 / mPace);
		for (int i = 0; i < mLastStepDeltas.length; i++) {
			mLastStepDeltas[i] = avg;
		}
		notifyListener();
	}

	public void reloadSettings() {
		notifyListener();
	}

	public void addListener(Listener l) {
		mListeners.add(l);
	}

	public void onStep() {
		long thisStepTime = System.currentTimeMillis();

		// Calculate pace based on last x steps
		if (mLastStepTime > 0) {
			long delta = thisStepTime - mLastStepTime;

			mLastStepDeltas[mLastStepDeltasIndex] = delta;
			mLastStepDeltasIndex = (mLastStepDeltasIndex + 1)
					% mLastStepDeltas.length;

			long sum = 0;
			boolean isMeaningfull = true;
			for (int i = 0; i < mLastStepDeltas.length; i++) {
				if (mLastStepDeltas[i] < 0) {
					isMeaningfull = false;
					break;
				}
				sum += mLastStepDeltas[i];
			}

			if (isMeaningfull && sum > 0) {
				long avg = sum / mLastStepDeltas.length;
				mPace = 60 * 1000 / avg;
			}
		}
		mLastStepTime = thisStepTime;
		notifyListener();
	}

	public void onStep13() {

	}
	
	public void onStepEstimated(int steps) {

	}

	private void notifyListener() {
		for (Listener listener : mListeners) {
			listener.paceChanged((int) mPace);
		}
	}

	public void passValue() {
		// Not used
	}

}
