package com.mobon.criteo_sdk;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoBannerAdListener;
import com.criteo.publisher.CriteoBannerView;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.CriteoInitException;
import com.criteo.publisher.CriteoInterstitial;
import com.criteo.publisher.CriteoInterstitialAdListener;
import com.criteo.publisher.model.AdSize;
import com.criteo.publisher.model.AdUnit;
import com.criteo.publisher.model.BannerAdUnit;
import com.criteo.publisher.model.InterstitialAdUnit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CriteoAdapter {

    private static final String TAG = "Mobon_CriteoAdapter";

    private Context mContext;
    private String PLACEMENT_PARAMETER;
    private CriteoBannerView mBannerAdView;
    private int mAdType;
    private CriteoInterstitial mInterstitialAd;
    private AlertDialog mEndingAd;
    private boolean isInterstitialLoaded;
    private boolean isTestMode;
    private View.OnClickListener mEndingListener;
    private View.OnClickListener mInterstitialListener;
    private boolean isLog;
    private Application mApplication;


    public CriteoAdapter(Context context) {
        mContext = context;
    }

    public void setLog(boolean is) {
        isLog = is;
    }

    public void setTestMode(boolean is) {
        isTestMode = is;
    }

    public void setApplication(Application _app) {
        this.mApplication = _app;
    }

    public void close() {

        if (mEndingAd != null && mEndingAd.isShowing())
            mEndingAd.dismiss();
    }

    public void init(String publishId, String key, int adType) {
        if (mApplication == null) {
            System.out.println(TAG + "mApplication null");
            return;
        }
        destroy();

        this.PLACEMENT_PARAMETER = key;
        this.mAdType = adType;




        if (isLog)
            System.out.println(TAG + " : init   key : " + key + " : adtype : " + adType);


        BannerAdUnit bannerAdUnit = null;
        switch (adType) {
            case MediationAdSize.BANNER_320_50:
                bannerAdUnit =
                        new BannerAdUnit(key, new AdSize(320, 50));
                mBannerAdView = new CriteoBannerView(mContext, bannerAdUnit);
                setBannerLayoutParams(320, 50);
                break;
            case MediationAdSize.BANNER_320_100:
                bannerAdUnit = new BannerAdUnit(key, new AdSize(320, 100));
                mBannerAdView = new CriteoBannerView(mContext, bannerAdUnit);
                //  setBannerLayoutParams(320,100);
                break;
            case MediationAdSize.BANNER_300_250:
                bannerAdUnit = new BannerAdUnit(key, new AdSize(300, 250));
                mBannerAdView = new CriteoBannerView(mContext, bannerAdUnit);
                //  setBannerLayoutParams(300,250);
                break;
            case MediationAdSize.INTERSTITIAL:
                InterstitialAdUnit interstitialAdUnit =
                        new InterstitialAdUnit(key);
                mInterstitialAd = new CriteoInterstitial(mContext, interstitialAdUnit);

                break;
            case MediationAdSize.NATIVE:
                //  mNativeAd = new InMobiNative(mContext,PLACEMENT_PARAMETER);
                break;
            case MediationAdSize.ENDING:
                bannerAdUnit =
                        new BannerAdUnit(key, new AdSize(300, 250));
                mBannerAdView = new CriteoBannerView(mContext, bannerAdUnit);
                LayoutInflater inflater2 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view2 = inflater2.inflate(R.layout.criteo_ending_layout, null);
                if (view2 != null) {
                    LinearLayout ad_container = view2.findViewById(R.id.ad_container);
                    ad_container.removeAllViews();
                    ad_container.addView(mBannerAdView);

                    Button cancel = view2.findViewById(R.id.cancel_btn);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mEndingAd != null && mEndingAd.isShowing())
                                mEndingAd.dismiss();

                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLOSE);
                                obj.put("msg", "");
                                v.setTag(obj);
                                mEndingListener.onClick(v);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Button ok = view2.findViewById(R.id.ok_btn);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mEndingAd != null && mEndingAd.isShowing())
                                mEndingAd.dismiss();
                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("code", MediationAdCode.AD_LISTENER_CODE_FINISH_CLICK);
                                obj.put("msg", "");
                                v.setTag(obj);
                                mEndingListener.onClick(v);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
                builder2.setView(view2);
                builder2.setCustomTitle(null);
                mEndingAd = builder2.create();


                mEndingAd.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                            // mEndingAd.dismiss();
                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLOSE);
                                obj.put("msg", "");
                                View v = new View(mContext);
                                v.setTag(obj);
                                mEndingListener.onClick(v);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                });

                break;
            case MediationAdSize.VIDEO:

                break;
            default:
                bannerAdUnit = new BannerAdUnit(key, new AdSize(320, 50));
                mBannerAdView = new CriteoBannerView(mContext, bannerAdUnit);
                //   setBannerLayoutParams(320,50);
                break;
        }

        List<AdUnit> adUnits = new ArrayList<>();
        adUnits.add(bannerAdUnit);

        try{
            Criteo.init(mApplication,publishId,adUnits);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Object getBannerView() {
        return mBannerAdView;
    }

    public Object getInterstitialView() {
        return mInterstitialAd;
    }


    public Object geEndingView() {
        if (mEndingAd != null) {
            return mEndingAd;
        }
        return null;
    }

    public void setAdListener(final View.OnClickListener _listner) {

        final View v = new View(mContext);


        if (mAdType == MediationAdSize.INTERSTITIAL && mInterstitialAd != null) {
            mInterstitialListener = _listner;

            mInterstitialAd.setCriteoInterstitialAdListener(new CriteoInterstitialAdListener() {
                @Override
                public void onAdReceived() {
                    isInterstitialLoaded = true;
                    if (isLog)
                        System.out.println(TAG + "interstitial onAdLoaded with onAdLoadSucceeded: ");
                    Log.d(TAG, "onAdLoadSucceeded");

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_LOAD);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdFailedToReceive(CriteoErrorCode criteoErrorCode) {
                    isInterstitialLoaded = false;
                    if (isLog)
                        System.out.println("interstitial onAdFailedToReceive : " + criteoErrorCode);

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_ERROR);
                        obj.put("msg", criteoErrorCode);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mInterstitialAd = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdClicked() {
                    Log.d(TAG, "interstitial onAdClicked");
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLICK);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdOpened() {

                }

                @Override
                public void onAdClosed() {

                }

                @Override
                public void onAdLeftApplication() {

                }
            });
        } else if (mBannerAdView != null) {
            mBannerAdView.setCriteoBannerAdListener(new CriteoBannerAdListener() {
                @Override
                public void onAdReceived(View view) {
                    if (isLog)
                        System.out.println(TAG + " Banner ad onAdLoadSucceeded  : ");

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_LOAD);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdFailedToReceive(CriteoErrorCode criteoErrorCode) {
                    if (isLog)
                        System.out.println(TAG + "Banner ad failed to load with error  : " + criteoErrorCode);

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_ERROR);
                        obj.put("msg", criteoErrorCode);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdLeftApplication() {
                    System.out.println("onAdLeftApplication : ");
                }

                @Override
                public void onAdClicked() {
                    if (isLog)
                        System.out.println(TAG + "Banner ad onAdClicked  : ");
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLICK);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onAdOpened() {
                    System.out.println("onAdOpened : ");
                }

                @Override
                public void onAdClosed() {
                    System.out.println("onAdClosed : ");
                }
            });

        }

//        final View v = new View(mContext);
//        if (mAdType == MediationAdSize.INTERSTITIAL && mInterstitialAd != null) {
//            mInterstitialListener = _listner;
//            mBannerAdView.setAdListener(new AdListener() {
//                @Override
//                public void onAdFailed(int errorCode) {

//                }
//
//                @Override
//                public void onAdLoaded() {

//                }
//
//                @Override
//                public void onAdClicked() {

//                }
//
//            });
//
//        } else if (mAdType == MediationAdSize.ENDING && mEndingAd != null) {
//            mEndingListener = _listner;
//            mBannerAdView.setAdListener(new AdListener() {
//                @Override
//                public void onAdFailed(int errorCode) {
//                    isInterstitialLoaded = false;
//                    mEndingAd = null;
//                    if (isLog)
//                        System.out.println(TAG + "Ending onAdLoaded with failed  : " + errorCode);
//
//                    try {
//                        JSONObject obj = new JSONObject();
//                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_ERROR);
//                        obj.put("msg", errorCode);
//                        v.setTag(obj);
//                        _listner.onClick(v);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onAdLoaded() {
//                    isInterstitialLoaded = true;
//                    if (isLog)
//                        System.out.println(TAG + "Ending onAdLoadSucceeded ");
//                    Log.d(TAG, "onAdLoadSucceeded");
//                    try {
//                        JSONObject obj = new JSONObject();
//                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_LOAD);
//                        v.setTag(obj);
//                        _listner.onClick(v);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onAdClicked() {
//
//                    try {
//                        JSONObject obj = new JSONObject();
//                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLICK);
//                        v.setTag(obj);
//                        _listner.onClick(v);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } else if (mBannerAdView != null) {
//
//            mBannerAdView.setAdListener(new AdListener() {
//                @Override
//                public void onAdFailed(int errorCode) {

//                }
//
//                @Override
//                public void onAdLoaded() {

//                }
//
//                @Override
//                public void onAdClicked() {
//                }
//
//            });
//
//
//        }


    }

    public void loadAd() {

        if (isLog)
            System.out.println(TAG + "loadAd() call");
        try {
            if (mBannerAdView != null)
                mBannerAdView.loadAd();

            if (mInterstitialAd != null)
                mInterstitialAd.loadAd();


        } catch (Exception e) {
            System.out.println("Criteo loadAd : " + e.getMessage());
        }

    }

    public boolean isLoaded() {
        if (isLog)
            System.out.println(TAG + "isLoaded() call");
        if (mAdType == MediationAdSize.INTERSTITIAL && mInterstitialAd != null && mInterstitialAd.isAdLoaded()) {
            return true;
        } else if (mAdType == MediationAdSize.ENDING && mEndingAd != null) {
            return true;
        }

        return false;
    }

    public boolean show() {
        if (isLog)
            System.out.println(TAG + "show() call");
        if (mAdType == MediationAdSize.INTERSTITIAL && mInterstitialAd != null && mInterstitialAd.isAdLoaded()) {
            mInterstitialAd.show();
            return true;
        } else if (mAdType == MediationAdSize.ENDING && mEndingAd != null) {
            mEndingAd.show();
            return true;
        }


        return false;
    }

    public void destroy() {
        if (isLog)
            System.out.println(TAG + "destory() call");
        if (mBannerAdView != null)
            mBannerAdView.destroy();

        if (mInterstitialAd != null)
            mInterstitialAd = null;

        if (mEndingAd != null)
            mEndingAd = null;
    }


    private void setBannerLayoutParams(int _width, int _height) {
        if (isLog)
            System.out.println(TAG + "setBannerLayoutParams() call");
        int width = toPixelUnits(_width);
        int height = toPixelUnits(_height);
        RelativeLayout.LayoutParams bannerLayoutParams = new RelativeLayout.LayoutParams(width, height);
        bannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mBannerAdView.setLayoutParams(bannerLayoutParams);
    }

    private int toPixelUnits(int dipUnit) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round(dipUnit * density);
    }

}