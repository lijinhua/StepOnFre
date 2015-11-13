//package com.corusen.steponfre;
//
///*
// * http://stackoverflow.com/questions/7048470/easy-way-to-setonclicklistener-on-all-activity-buttons
// */
//
//import com.corusen.steponfre.R;
//
//import android.app.Activity;
////import android.app.Activity;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//
//public class SettingsFirst extends Activity {
//
//	private static final String TAG = "ProgramSelector";
//	private SharedPreferences mSettings;
//	private PedometerSettings mPedometerSettings;
//
//	private RadioGroup mRadioGroup = null;
//	private RadioButton mRadioMetric;
//	private RadioButton mRadioEnglish;
//
//	private TextView mTextWeightValue;
//	private TextView mTextWalkValue;
//	private TextView mTextRunValue;
//	private SeekBar mSeekBarWeight = null;
//	private SeekBar mSeekBarWalk = null;
//	private SeekBar mSeekBarRun = null;
//	private Button mButtonOk;
//
//	private boolean mIsMetric;
//	private String mUnit;
//	private float mWeight;
//	private float mWalk;
//	private float mRun;
//
//	private String mUnitTextWeight;
//	private String mUnitTextStride;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		setContentView(R.layout.light_settingsfirst);
//		// getWindow().setLayout(450, 500);
//		super.onCreate(savedInstanceState);
//
//		setTitleColor(getResources().getColor(R.color.myllightgray));
//		setTitle(getString(R.string.app_name));
//
//		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
//		mPedometerSettings = new PedometerSettings(mSettings);
//
//		mUnitTextWeight = getString(R.string.kilograms);
//		mUnitTextStride = getString(R.string.centimeters);
//
//		OnClickListener listener = new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				RadioButton rb = (RadioButton) v;
//				boolean checked = rb.isChecked();
//				switch (rb.getId()) {
//				case R.id.radioMetric:
//					if (checked) {
//						mUnit = "metric";
//						mWalk = mWalk * Utils.INCH2CM;
//						mRun = mRun * Utils.INCH2CM;
//						mWeight = mWeight * Utils.LB2KG;
//						mUnitTextWeight = getString(R.string.kilograms);
//						mUnitTextStride = getString(R.string.centimeters);
//						mSeekBarWeight.setMax(200);
//						mSeekBarWalk.setMax(200);
//						mSeekBarRun.setMax(200);
//					}
//					break;
//				case R.id.radioEnglish:
//					if (checked) {
//						mUnit = "imperial";
//						mWalk = mWalk * Utils.CM2INCH;
//						mRun = mRun * Utils.CM2INCH;
//						mWeight = mWeight * Utils.KG2LB;
//						mUnitTextWeight = getString(R.string.pounds);
//						mUnitTextStride = getString(R.string.inches);
//						mSeekBarWeight.setMax((int) (200 * Utils.KG2LB));
//						mSeekBarWalk.setMax((int) (200 * Utils.CM2INCH));
//						mSeekBarRun.setMax((int) (200 * Utils.CM2INCH));
//					}
//					break;
//				}
//				updateDisplay();
//			}
//		};
//
//		mTextWeightValue = (TextView) findViewById(R.id.textWeightValue);
//		mSeekBarWeight = (SeekBar) findViewById(R.id.seekBarWeight);
//		mSeekBarWeight
//				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//					int progresschange;
//
//					public void onProgressChanged(SeekBar seekBar,
//							int progress, boolean fromUser) {
//						progresschange = progress;
//						mTextWeightValue.setText("" + progresschange + " "
//								+ mUnitTextWeight);
//					}
//
//					public void onStartTrackingTouch(SeekBar seekBar) {
//					}
//
//					public void onStopTrackingTouch(SeekBar seekBar) {
//						mWeight = (float) progresschange;
//						mTextWeightValue.setText("" + progresschange + " "
//								+ mUnitTextWeight);
//					}
//				});
//
//		mTextWalkValue = (TextView) findViewById(R.id.textWalkValue);
//		mSeekBarWalk = (SeekBar) findViewById(R.id.seekBarWalk);
//		mSeekBarWalk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//			int progresschange;
//
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				progresschange = progress;
//				mTextWalkValue.setText("" + progresschange + " "
//						+ mUnitTextStride);
//			}
//
//			public void onStartTrackingTouch(SeekBar seekBar) {
//			}
//
//			public void onStopTrackingTouch(SeekBar seekBar) {
//				mWalk = (float) progresschange;
//				mTextWalkValue.setText("" + progresschange + " "
//						+ mUnitTextStride);
//			}
//		});
//
//		mTextRunValue = (TextView) findViewById(R.id.textRunValue);
//		mSeekBarRun = (SeekBar) findViewById(R.id.seekBarRun);
//		mSeekBarRun.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//			int progresschange;
//
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				progresschange = progress;
//				mTextRunValue.setText("" + progresschange + " "
//						+ mUnitTextStride);
//			}
//
//			public void onStartTrackingTouch(SeekBar seekBar) {
//			}
//
//			public void onStopTrackingTouch(SeekBar seekBar) {
//				mRun = (float) progresschange;
//				mTextRunValue.setText("" + progresschange + " "
//						+ mUnitTextStride);
//			}
//		});
//
//		mButtonOk = (Button) findViewById(R.id.buttonOK);
//		mButtonOk.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				mPedometerSettings.setUnits(mUnit);
//				mPedometerSettings.setStepLength(mWalk);
//				mPedometerSettings.setRunLength(mRun);
//				mPedometerSettings.setBodyWeight(mWeight);
//				finish();
//			}
//		});
//
//		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//		mRadioMetric = (RadioButton) findViewById(R.id.radioMetric);
//		mRadioMetric.setOnClickListener(listener);
//
//		mRadioEnglish = (RadioButton) findViewById(R.id.radioEnglish);
//		mRadioEnglish.setOnClickListener(listener);
//
//		mIsMetric = mPedometerSettings.isMetric();
//		mWeight = mPedometerSettings.getBodyWeight();
//		mWalk = mPedometerSettings.getStepLength();
//		mRun = mPedometerSettings.getRunLength();
//		if (mIsMetric) { // default setting is metric
//			mUnit = "metric";
//			mRadioMetric.setChecked(true);
//			mRadioEnglish.setChecked(false);
//			mUnitTextWeight = getString(R.string.kilograms);
//			mUnitTextStride = getString(R.string.centimeters);
//			mSeekBarWeight.setMax(200);
//			mSeekBarWalk.setMax(200);
//			mSeekBarRun.setMax(200);
//		} else {
//			mUnit = "imperial";
//			mRadioMetric.setChecked(false);
//			mRadioEnglish.setChecked(true);
//			mWalk = mWalk * Utils.CM2INCH;
//			mRun = mRun * Utils.CM2INCH;
//			mWeight = mWeight * Utils.KG2LB;
//			mUnitTextWeight = getString(R.string.pounds);
//			mUnitTextStride = getString(R.string.inches);
//			mSeekBarWeight.setMax((int) (200 * Utils.KG2LB));
//			mSeekBarWalk.setMax((int) (200 * Utils.CM2INCH));
//			mSeekBarRun.setMax((int) (200 * Utils.CM2INCH));
//		}
//
//		updateDisplay();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
//
//	void updateDisplay() {
//		mSeekBarWeight.setProgress((int) mWeight);
//		mTextWeightValue.setText("" + (int) mWeight + " " + mUnitTextWeight);
//
//		mSeekBarWalk.setProgress((int) mWalk);
//		mTextWalkValue.setText("" + (int) mWalk + " " + mUnitTextStride);
//
//		mSeekBarRun.setProgress((int) mRun);
//		mTextRunValue.setText("" + (int) mRun + " " + mUnitTextStride);
//	}
//
//}
