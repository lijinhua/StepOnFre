package com.corusen.steponfre.base;

/*
 * http://stackoverflow.com/questions/7048470/easy-way-to-setonclicklistener-on-all-activity-buttons
 */

import java.util.Calendar;

import com.corusen.steponfre.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FragmentSettingsFirst extends Fragment {

	private static final int DIALOG_ID_CHANGE_DATE = 901;
	private static final int DIALOG_FRAGMENT_DATE_PIKCER = 1;

	private static final int FEMALE_HEIGHT = 160;
	private static final int FEMALE_WEIGHT = 55;
	private static final int MALE_HEIGHT = 175;
	private static final int MALE_WEIGHT = 70;
	
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;

	//private RadioGroup mRadioGroup = null;
	private RadioButton mRadioMale;
	private RadioButton mRadioFemale;
	private RadioButton mRadioMetric;
	private RadioButton mRadioEnglish;

	private TextView mTextHeightValue;
	private TextView mTextWeightValue;
	private TextView mTextWalkValue;
	private TextView mTextRunValue;
	private SeekBar mSeekBarHeight = null;
	private SeekBar mSeekBarWeight = null;
//	private SeekBar mSeekBarWalk = null;
//	private SeekBar mSeekBarRun = null;
	private Button mButtonOk;
	private Button mButtonBirthday;

	private boolean mIsMetric;
	private String mUnit;
	private float mHeight;
	private float mWeight;
	private float mWalk;
	private float mRun;

	private int mYear;
	private int mMonth;
	private int mDay;

	private String mUnitTextWeight;
	private String mUnitTextStride;

	private View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.light_settingsfirst, container, false);
		Calendar today = Calendar.getInstance();

		// setTitleColor(getResources().getColor(R.color.myllightgray));
		// setTitle(getString(R.string.app_name));

		mSettings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		mPedometerSettings = new PedometerSettings(mSettings);

		mUnitTextWeight = getString(R.string.kilograms);
		mUnitTextStride = getString(R.string.centimeters);

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				RadioButton rb = (RadioButton) v;
				boolean checked = rb.isChecked();
				switch (rb.getId()) {
				case R.id.radioMetric:
					if (checked) {
						if (!mIsMetric) { //prevent from double click
							mIsMetric = true;
							mUnit = "metric";
							mWalk = mWalk * Utils.INCH2CM;
							mRun = mRun * Utils.INCH2CM;
							mHeight = mHeight * Utils.INCH2CM;
							mWeight = mWeight * Utils.LB2KG;
							mUnitTextWeight = getString(R.string.kilograms);
							mUnitTextStride = getString(R.string.centimeters);
							mSeekBarHeight.setMax(FragmentSettings.HEIGHT_MAX - FragmentSettings.HEIGHT_MIN);
							mSeekBarWeight.setMax(FragmentSettings.WEIGHT_MAX - FragmentSettings.WEIGHT_MIN);
						}
						// mSeekBarWalk.setMax(200);
						// mSeekBarRun.setMax(200);
					}
					break;
				case R.id.radioEnglish:
					if (checked) {
						if (mIsMetric) { //prevent from double click
							mIsMetric = false;
							mUnit = "imperial";
							mWalk = mWalk * Utils.CM2INCH;
							mRun = mRun * Utils.CM2INCH;
							mHeight = mHeight * Utils.CM2INCH;
							mWeight = mWeight * Utils.KG2LB;
							mUnitTextWeight = getString(R.string.pounds);
							mUnitTextStride = getString(R.string.inches);
							mSeekBarHeight.setMax((int) ((FragmentSettings.HEIGHT_MAX - FragmentSettings.HEIGHT_MIN) * Utils.CM2INCH));
							mSeekBarWeight.setMax((int) ((FragmentSettings.WEIGHT_MAX - FragmentSettings.WEIGHT_MIN) * Utils.KG2LB));
						}
						// mSeekBarWalk.setMax((int) (200 * Utils.CM2INCH));
						// mSeekBarRun.setMax((int) (200 * Utils.CM2INCH));
					}
					break;
				case R.id.radioMale:
					if (checked) {
						mPedometerSettings.setGender("male");
						if (mIsMetric) {
							mHeight = (float) MALE_HEIGHT;
							mWeight = (float) MALE_WEIGHT;
						} else {
							mHeight = (float) MALE_HEIGHT * Utils.CM2INCH;
							mWeight = (float) MALE_WEIGHT * Utils.KG2LB;
						}
					}
					break;
				case R.id.radioFemale:
					if (checked) {
						mPedometerSettings.setGender("female");
						if (mIsMetric) {
							mHeight = (float) FEMALE_HEIGHT;
							mWeight = (float) FEMALE_WEIGHT;
						} else {
							mHeight = (float) FEMALE_HEIGHT * Utils.CM2INCH;
							mWeight = (float) FEMALE_WEIGHT * Utils.KG2LB;
						}
					}
					break;
				}
				updateDisplay();
			}
		};

		mTextHeightValue = (TextView) mView.findViewById(R.id.textHeightValue);
		mSeekBarHeight = (SeekBar) mView.findViewById(R.id.seekBarHeight);
		mSeekBarHeight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progresschange;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				// mTextHeightValue.setText("" + progresschange + " " + mUnitTextStride);
				if (mIsMetric) {
					progresschange = progress + FragmentSettings.HEIGHT_MIN;
					mTextHeightValue.setText("" + progresschange + " " + mUnitTextStride);
				} else {
					progresschange = progress + (int) ((float) FragmentSettings.HEIGHT_MIN * Utils.CM2INCH);
					int feet = (int) (progresschange / 12.0);
					int inch = (int) (progresschange - (feet * 12));
					mTextHeightValue.setText("" + feet + " " + "ft" + " " + inch + " " + mUnitTextStride);
				}
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				mHeight = (float) progresschange;
				if (mIsMetric) {
					mTextHeightValue.setText("" + (int) mHeight + " " + mUnitTextStride);
				} else {
					int feet = (int) (mHeight / 12.0);
					int inch = (int) (mHeight - (float) (feet * 12));
					mTextHeightValue.setText("" + feet + " " + "ft" + " " + inch + " " + mUnitTextStride);
				}
			}
		});

		mTextWeightValue = (TextView) mView.findViewById(R.id.textWeightValue);
		mSeekBarWeight = (SeekBar) mView.findViewById(R.id.seekBarWeight);
		mSeekBarWeight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progresschange;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (mIsMetric) {
					progresschange = progress + FragmentSettings.WEIGHT_MIN;
				} else {
					progresschange = progress + (int) ((float) FragmentSettings.WEIGHT_MIN * Utils.KG2LB);
				}
				mTextWeightValue.setText("" + progresschange + " " + mUnitTextWeight);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				mWeight = (float) progresschange;
				mTextWeightValue.setText("" + progresschange + " " + mUnitTextWeight);
			}
		});

		// mTextWalkValue = (TextView) mView.findViewById(R.id.textWalkValue);
		// mSeekBarWalk = (SeekBar) mView.findViewById(R.id.seekBarWalk);
		// mSeekBarWalk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		// int progresschange;
		//
		// public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// progresschange = progress;
		// mTextWalkValue.setText("" + progresschange + " " + mUnitTextStride);
		// }
		//
		// public void onStartTrackingTouch(SeekBar seekBar) {
		// }
		//
		// public void onStopTrackingTouch(SeekBar seekBar) {
		// mWalk = (float) progresschange;
		// mTextWalkValue.setText("" + progresschange + " " + mUnitTextStride);
		// }
		// });
		//
		// mTextRunValue = (TextView) mView.findViewById(R.id.textRunValue);
		// mSeekBarRun = (SeekBar) mView.findViewById(R.id.seekBarRun);
		// mSeekBarRun.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		// int progresschange;
		//
		// public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// progresschange = progress;
		// mTextRunValue.setText("" + progresschange + " " + mUnitTextStride);
		// }
		//
		// public void onStartTrackingTouch(SeekBar seekBar) {
		// }
		//
		// public void onStopTrackingTouch(SeekBar seekBar) {
		// mRun = (float) progresschange;
		// mTextRunValue.setText("" + progresschange + " " + mUnitTextStride);
		// }
		// });

		// mTextDisplayDate = (TextView) mView.findViewById(R.id.textDate);
		mButtonBirthday = (Button) mView.findViewById(R.id.btnChangeDate);
		mButtonBirthday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// getActivity().showDialog(DIALOG_ID_CHANGE_DATE);
				showDialog(DIALOG_FRAGMENT_DATE_PIKCER);
			}
		});

		mButtonOk = (Button) mView.findViewById(R.id.buttonOK);
		mButtonOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				mWalk = mHeight * FragmentSettings.HEIGHT_TO_WALK;
				mRun = mWalk * FragmentSettings.WALK_TO_RUN;
				mPedometerSettings.setUnits(mUnit);
				mPedometerSettings.setStepLength(mWalk);
				mPedometerSettings.setRunLength(mRun);
				mPedometerSettings.setBodyHeight(mHeight);
				mPedometerSettings.setBodyWeight(mWeight);
				mPedometerSettings.setNewInstallationStatusFalse();  //for Pro: this needs to be done after importing db file
				mPedometerSettings.setNewInstallationStatusFalse117();
				Pedometer.getInstance().enableNavigationDrawMenu();
				Pedometer.getInstance().displayView(Pedometer.SPINNER_ITEM_PEDOMETER);
				Pedometer.getInstance().invalidateOptionsMenu();
				// finish();
			}
		});

		// mRadioGroup1 = (RadioGroup) mView.findViewById(R.id.radioGroup1);

		mRadioMale = (RadioButton) mView.findViewById(R.id.radioMale);
		mRadioMale.setOnClickListener(listener);
		mRadioFemale = (RadioButton) mView.findViewById(R.id.radioFemale);
		mRadioFemale.setOnClickListener(listener);

		mRadioMetric = (RadioButton) mView.findViewById(R.id.radioMetric);
		mRadioMetric.setOnClickListener(listener);
		mRadioEnglish = (RadioButton) mView.findViewById(R.id.radioEnglish);
		mRadioEnglish.setOnClickListener(listener);

		mIsMetric = mPedometerSettings.isMetric();
		mHeight = mPedometerSettings.getBodyHeight();
		mWeight = mPedometerSettings.getBodyWeight();
		mWalk = mPedometerSettings.getStepLength();
		mRun = mPedometerSettings.getRunLength();
		if (mIsMetric) { // default setting is metric
			mUnit = "metric";
			mRadioMetric.setChecked(true);
			mRadioEnglish.setChecked(false);
			mUnitTextWeight = getString(R.string.kilograms);
			mUnitTextStride = getString(R.string.centimeters);
			mSeekBarHeight.setMax(FragmentSettings.HEIGHT_MAX - FragmentSettings.HEIGHT_MIN);
			mSeekBarWeight.setMax(FragmentSettings.WEIGHT_MAX - FragmentSettings.WEIGHT_MIN);
			// mSeekBarWalk.setMax(200);
			// mSeekBarRun.setMax(200);
		} else {
			mUnit = "imperial";
			mRadioMetric.setChecked(false);
			mRadioEnglish.setChecked(true);
			mWalk = mWalk * Utils.CM2INCH;
			mRun = mRun * Utils.CM2INCH;
			mHeight = mHeight * Utils.CM2INCH;
			mWeight = mWeight * Utils.KG2LB;
			mUnitTextWeight = getString(R.string.pounds);
			mUnitTextStride = getString(R.string.inches);
			mSeekBarHeight.setMax((int) ((FragmentSettings.HEIGHT_MAX - FragmentSettings.HEIGHT_MIN) * Utils.CM2INCH));
			mSeekBarWeight.setMax((int) ((FragmentSettings.WEIGHT_MAX - FragmentSettings.WEIGHT_MIN) * Utils.KG2LB));
			// mSeekBarWalk.setMax((int) (200 * Utils.CM2INCH));
			// mSeekBarRun.setMax((int) (200 * Utils.CM2INCH));
		}

		if (mPedometerSettings.isGenderMale()) {
			mRadioMale.setChecked(true);
			mRadioFemale.setChecked(false);
		} else {
			mRadioMale.setChecked(false);
			mRadioFemale.setChecked(true);
		}

		mYear = mPedometerSettings.getBirthYear();
		mMonth = mPedometerSettings.getBirthMonth();
		mDay = mPedometerSettings.getBirthDay();

		updateDisplay();

		return mView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	void updateDisplay() {
		mButtonBirthday.setText(new StringBuilder().append(mYear).append("-").append(mMonth).append("-").append(mDay).append(" "));

		if (mIsMetric) {
			mSeekBarHeight.setProgress((int) mHeight - FragmentSettings.HEIGHT_MIN);
			mSeekBarWeight.setProgress((int) mWeight - FragmentSettings.WEIGHT_MIN);
			mTextHeightValue.setText("" + (int) mHeight + " " + mUnitTextStride);
		} else {
			mSeekBarHeight.setProgress((int) mHeight - (int) ((float) FragmentSettings.HEIGHT_MIN * Utils.CM2INCH));
			mSeekBarWeight.setProgress((int) mWeight - (int) ((float) FragmentSettings.WEIGHT_MIN * Utils.KG2LB));
			int feet = (int) (mHeight / 12.0);
			int inch = (int) (mHeight - (float) (feet * 12));
			mTextHeightValue.setText("" + feet + " " + "ft" + " " + inch + " " + mUnitTextStride);
		}

		mTextWeightValue.setText("" + (int) mWeight + " " + mUnitTextWeight);

		// mSeekBarWalk.setProgress((int) mWalk);
		// mTextWalkValue.setText("" + (int) mWalk + " " + mUnitTextStride);
		//
		// mSeekBarRun.setProgress((int) mRun);
		// mTextRunValue.setText("" + (int) mRun + " " + mUnitTextStride);
	}

	// private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
	//
	// // when dialog box is closed, below method will be called.
	// @Override
	// public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
	// mYear = selectedYear;
	// mMonth = selectedMonth;
	// mDay = selectedDay;
	//
	// // set selected date into textview
	// mTextDisplayDate.setText(new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));
	//
	// // set selected date into datepicker also
	// //dpResult.init(mYear, mMonth, mDay, null);
	//
	// }
	//
	// };
	//
	private void showDialog(int type) {
		// mStackLevel++;
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		switch (type) {
		case DIALOG_FRAGMENT_DATE_PIKCER:
			FragmentDialogBirthDatePicker dialogFragPause = new FragmentDialogBirthDatePicker();
			dialogFragPause.setTargetFragment(this, DIALOG_FRAGMENT_DATE_PIKCER);
			dialogFragPause.show(getFragmentManager().beginTransaction(), "dialog");
			break;

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case DIALOG_FRAGMENT_DATE_PIKCER:
			if (resultCode == Activity.RESULT_OK) {
				mYear = Pedometer.mPedometerSettings.getBirthYear();
				mMonth = Pedometer.mPedometerSettings.getBirthMonth();
				mDay = Pedometer.mPedometerSettings.getBirthDay();
				mButtonBirthday.setText(new StringBuilder().append(mYear).append("-").append(mMonth).append("-").append(mDay).append(" "));
			} else if (resultCode == Activity.RESULT_CANCELED) {
			}
			break;
		}
	}

}
