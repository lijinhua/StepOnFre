//package com.corusen.steponfre.base;
//
////import me.kiip.sdk.Kiip;
////import me.kiip.sdk.KiipFragmentCompat;
////import me.kiip.sdk.Poptart;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//
//public class BaseActivity extends FragmentActivity {
//    private final static String KIIP_TAG = "kiip_fragment_tag";
//
////    private KiipFragmentCompat mKiipFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
////        // Create or re-use KiipFragment.
////        if (savedInstanceState != null) {
////            mKiipFragment = (KiipFragmentCompat) getSupportFragmentManager().findFragmentByTag(KIIP_TAG);
////        } else {
////            mKiipFragment = new KiipFragmentCompat();
////            getSupportFragmentManager().beginTransaction().add(mKiipFragment, KIIP_TAG).commit();
////        }
////        //galaxy3 google ads id: c9df8776-7046-4765-89e6-71aab1bddfb7
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
////        Kiip.getInstance().startSession(new Kiip.Callback() {
////
////            @Override
////            public void onFailed(Kiip kiip, Exception exception) {
////                // handle failure
////            }
////
////            @Override
////            public void onFinished(Kiip kiip, Poptart poptart) {
////                onPoptart(poptart);
////            }
////        });
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
////        Kiip.getInstance().endSession(new Kiip.Callback() {
////            @Override
////            public void onFailed(Kiip kiip, Exception exception) {
////                // handle failure
////            }
////
////            @Override
////            public void onFinished(Kiip kiip, Poptart poptart) {
////                onPoptart(poptart);
////            }
////        });
//    }
//
////    public void onPoptart(Poptart poptart) {
////        mKiipFragment.showPoptart(poptart);
////    }
//}