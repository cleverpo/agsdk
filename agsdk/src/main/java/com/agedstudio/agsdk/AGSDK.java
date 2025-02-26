package com.agedstudio.agsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.agedstudio.base.AgedStudioSDKWrapper;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

import com.agedstudio.base.utils.SysUtils;
import com.agedstudio.cmp.CMPBusiness;
import com.agedstudio.gpbilling.GPBillingMgr;
import com.agedstudio.gpbilling.GPBillingOnPaidEventUtils;
import com.agedstudio.libsdk.service.AnalyticsService;
import com.agedstudio.libsdk.service.BannerService;
import com.agedstudio.libsdk.service.BillingService;
import com.agedstudio.libsdk.service.CMPService;
import com.agedstudio.libsdk.service.IntertitalService;
import com.agedstudio.libsdk.service.JSExceptionService;
import com.agedstudio.libsdk.service.RewardInterstitalService;
import com.agedstudio.libsdk.service.RewardVideoService;
import com.agedstudio.libsdk.service.SysService;


import com.agedstudio.firebase.FirebaseAnalyticsBusiness;
import com.agedstudio.firebase.FirebaseRCBusiness;
import com.agedstudio.libsdk.service.FirebaseAnalyticsService;
import com.agedstudio.libsdk.service.FirebaseRCService;

import com.agedstudio.libsdk.service.AppsFlyerService;
import com.agedstudio.af.AppsFlyerBusiness;

import com.agedstudio.libsdk.service.GameAnalyticsService;
import com.agedstudio.ga.GameAnalyticsBusiness;


import com.agedstudio.libsdk.service.AdjustService;
import com.agedstudio.adjust.AdjustBusiness;

import com.agedstudio.max.MaxBannerBusiness;
import com.agedstudio.max.MaxBoot;
import com.agedstudio.max.MaxInterstitalBusiness;
import com.agedstudio.max.MaxOnPaidEventUtils;
import com.agedstudio.max.MaxRewardVideoBusiness;

//import com.agedstudio.admob.AdMobAdBoot;
//import com.agedstudio.admob.AdMobBannerBusiness;
//import com.agedstudio.admob.AdMobInterstitalBusiness;
//import com.agedstudio.admob.AdMobOnPaidEventUtils;
//import com.agedstudio.admob.AdMobRewardVideoBusiness;

//import com.agedstudio.gpbilling.GPBillingMgr;
//import com.agedstudio.gpbilling.GPBillingOnPaidEventUtils;


public class AGSDK {
    private static final String TAG = AGSDK.class.getSimpleName();
    private static AppsFlyerBusiness afBusiness = null;

    public static void initInApplication(Application application){
        LogUtil.i(TAG, "初始化SDK: " +
                "enableAppsFlyer:" + BuildConfig.enableAppsFlyer + "\n" +
                "enableGameAnalytics:" + BuildConfig.enableGameAnalytics + "\n" +
                "enableAdjust:" + BuildConfig.enableAdjust + "\n" +
                "enableCMP:" + BuildConfig.enableCMP + "\n" +
                "enableMax:" + BuildConfig.enableMax + "\n" +
                ""
        );

        if(BuildConfig.enableAppsFlyer){
            AGSDK.initAppsFlyerInApplication(application, BuildConfig.AFDevKey);
        }

        if(BuildConfig.enableAdjust) {
            AGSDK.initAdjustInApplication(application, BuildConfig.AdjustAppToken, BuildConfig.FbAppId);
        }
    }

    public static void initInActivity(Activity activity){
        long currentTimeMillis = System.currentTimeMillis();

        // service
        AGSDK.initSysService(activity);
        AGSDK.initAnalyticsService(activity);
        AGSDK.initJSExceptionService(activity);
        AGSDK.initBannerService(activity);
        AGSDK.initIntertitalService(activity);
        AGSDK.initRewardVideoService(activity);
        AGSDK.initRewardInterstitalService(activity);
        AGSDK.initBillingService(activity);

        //firebase
        AGSDK.initFirebase(activity);
        //af
        if(BuildConfig.enableAppsFlyer) {
            AGSDK.initAppsFlyer(activity);
        }
        //ga
        if(BuildConfig.enableGameAnalytics) {
            AGSDK.initGameAnalytics(activity, BuildConfig.GAGameKey, BuildConfig.GASecretKey);
        }
        //cmp
        if(BuildConfig.enableCMP){
            AGSDK.initCMP(activity);
        }

        LogUtil.i("Profiler", "初始化SDK: " + (System.currentTimeMillis() - currentTimeMillis));
    }

    private static void initSysService(Context context) {
        SysService service = new SysService();
        service.init(context);
        AgedStudioSDKWrapper.getInstance().addSDKClass("SysService", service);
    }

    private static void initAnalyticsService(Context context) {
        AnalyticsService service = new AnalyticsService();
        service.init(context);
        AgedStudioSDKWrapper.getInstance().addSDKClass("AnalyticsService", service);
    }

    private static void initJSExceptionService(Context context) {
        JSExceptionService service = new JSExceptionService();
        service.init(context);
        AgedStudioSDKWrapper.getInstance().addSDKClass("JSExceptionService", service);
    }

    private static void initBannerService(Context context) {
        BannerService service = new BannerService();
        service.init(context);
        AgedStudioSDKWrapper.getInstance().addSDKClass("BannerService", service);
        // admob
//        AdMobBannerBusiness adMobBusiness = new AdMobBannerBusiness();
//        adMobBusiness.init(context);
//        service.initAdMob(adMobBusiness);
        // yandex
        // YandexBannerBusiness yandexBusiness = new YandexBannerBusiness();
        // yandexBusiness.init(context);
        // service.initYandex(yandexBusiness);

        if(BuildConfig.enableMax){
            MaxBannerBusiness maxBusiness = new MaxBannerBusiness();
            maxBusiness.init(context);
            service.initMax(maxBusiness);
        }
    }

    private static void initIntertitalService(Context context) {
        IntertitalService service = new IntertitalService();
        service.init(context);
        IntertitalService.setMediation(3);
        AgedStudioSDKWrapper.getInstance().addSDKClass("IntertitalService", service);
        // admob
//        AdMobInterstitalBusiness adMobBusiness = new AdMobInterstitalBusiness();
//        adMobBusiness.init(context);
//        service.initAdMob(adMobBusiness);
        // yandex
        // YandexInterstitalBusiness yandexBusiness = new YandexInterstitalBusiness();
        // yandexBusiness.init(context);
        // service.initYandex(yandexBusiness);
        // max
        if(BuildConfig.enableMax){
            MaxInterstitalBusiness maxBusiness = new MaxInterstitalBusiness();
            maxBusiness.init(context);
            service.initMax(maxBusiness);
        }
    }

    private static void initRewardVideoService(Context context) {
        LogUtil.i(TAG, "AGSDK-----initRewardVideoService--");
        RewardVideoService service = new RewardVideoService();
        service.init(context);
        RewardVideoService.setMediation(3);
        AgedStudioSDKWrapper.getInstance().addSDKClass("RewardVideoService", service);
        // admob
//        AdMobRewardVideoBusiness business = new AdMobRewardVideoBusiness();
//        business.init(context);
//        service.initAdMob(business);
        // yandex
        // YandexRewardVideoBusiness yandexBusiness = new YandexRewardVideoBusiness();
        // yandexBusiness.init(context);
        // service.initYandex(yandexBusiness);
        // max
        if(BuildConfig.enableMax){
            MaxRewardVideoBusiness maxBusiness = new MaxRewardVideoBusiness();
            maxBusiness.init(context);
            service.initMax(maxBusiness);
        }
    }

    private static void initRewardInterstitalService(Context context) {
        RewardInterstitalService service = new RewardInterstitalService();
        service.init(context);
        RewardInterstitalService.setMediation(3);
        AgedStudioSDKWrapper.getInstance().addSDKClass("RewardInterstitalService", service);
    }

    private static void initBillingService(Context context) {
        BillingService service = new BillingService();

        if(BuildConfig.enableGoogleBilling){
            service.init(context, GPBillingMgr.getInstance());
            GPBillingOnPaidEventUtils.init(context, AnalyticsService.getAnalyticsApis());
        }
    }

    private static void initFirebase(Context context){
        {
            FirebaseAnalyticsService service = new FirebaseAnalyticsService();
            FirebaseAnalyticsBusiness business = FirebaseAnalyticsBusiness.getIns();
            business.init(context);
            service.init(context, business);
            AgedStudioSDKWrapper.getInstance().addSDKClass("FirebaseAnalyticsService", service);
        }
        {
            FirebaseRCService service = new FirebaseRCService();
            FirebaseRCBusiness business = new FirebaseRCBusiness();
            business.init(context);
            service.init(context, business);
            AgedStudioSDKWrapper.getInstance().addSDKClass("FirebaseRCService", service);
        }
    }

    private static void initAppsFlyerInApplication(Context context, String devKey) {
        afBusiness = new AppsFlyerBusiness();
        afBusiness.initInApplication(context, devKey);
    }

    private static void initAppsFlyer(Context context) {
        AppsFlyerService service = new AppsFlyerService();
        service.init(context, afBusiness);
        AgedStudioSDKWrapper.getInstance().addSDKClass("AppsFlyerService", service);
    }

    private static void initGameAnalytics(Context context, String gameKey, String secretKey) {
        GameAnalyticsService service = new GameAnalyticsService();
        GameAnalyticsBusiness business = new GameAnalyticsBusiness();
        business.init(context, gameKey, secretKey);
        service.init(context, business);
        AgedStudioSDKWrapper.getInstance().addSDKClass("GameAnalyticsService", service);
    }

    private static void initAdjustInApplication(Context context, String appToken, String fbAppId) {
        AdjustService service = new AdjustService();
        AdjustBusiness business = new AdjustBusiness();
        business.init(context, appToken, fbAppId);
        service.init(context, business);
        AgedStudioSDKWrapper.getInstance().addSDKClass("AdjustService", service);
    }

    private static void initCMP(Context context) {
        LogUtil.i(TAG, "initCMP");

        // CMP
        CMPBusiness cmpBusiness = new CMPBusiness();
        cmpBusiness.init(context);
        CMPService cmpService = new CMPService();
        cmpService.init(context);
        cmpService.initBussiness(cmpBusiness);
    }

    private static void initMax(Context context, boolean hasUserConsent, String amazonAppId) {
        if(!BuildConfig.enableMax) return;

        LogUtil.i(TAG, "AGSDK-----initMax--");
        // max
        MaxBoot.init(context, hasUserConsent, amazonAppId);
        MaxOnPaidEventUtils.init(context, AnalyticsService.getAnalyticsApis());
    }


    public static void initializeMobileAdsSdk(boolean hasUserConsent, int mediation) {
        Log.i(TAG, "initializeMobileAdsSdk--start");

        BannerService.setMediation(mediation);
        IntertitalService.setMediation(mediation);
        RewardVideoService.setMediation(mediation);
        RewardInterstitalService.setMediation(mediation);

        if (mediation == 1) {
            Log.i(TAG, "initializeMobileAdsSdk--admob");
//            AGSDK.initAdMob(GlobalObject.getContext(), hasUserConsent);
        } else if (mediation == 2) {
            // SDKBoot.initYandex(GlobalObject.getContext(), hasUserConsent);
        } else if (mediation == 3) {
            Log.i(TAG, "initializeMobileAdsSdk--max");
            AGSDK.initMax(CommonUtil.getActivityContext(), hasUserConsent, BuildConfig.AmazonAppId);
        } else {
            Log.i(TAG, "initializeMobileAdsSdk--none");
        }
    }


//    public static void initAdMob(Context context, boolean hasUserConsent) {
//        // adMob
//        AdMobAdBoot.init(context, hasUserConsent);
//        AdMobOnPaidEventUtils.init(context);
//    }



    public static void setGLSurfaceView(GLSurfaceView view, Context context) {
        AgedStudioSDKWrapper.getInstance().setGLSurfaceView(view, context);
    }

    public static void onResume() {
        AgedStudioSDKWrapper.getInstance().onResume();
    }

    public static void onPause() {
        AgedStudioSDKWrapper.getInstance().onPause();
    }

    public static void onDestroy() {
        AgedStudioSDKWrapper.getInstance().onDestroy();
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        AgedStudioSDKWrapper.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    public static void onNewIntent(Intent intent) {
        AgedStudioSDKWrapper.getInstance().onNewIntent(intent);
    }

    public static void onRestart() {
        AgedStudioSDKWrapper.getInstance().onRestart();
    }

    public static void onStop() {
        AgedStudioSDKWrapper.getInstance().onStop();
    }

    public static void onBackPressed() {
        AgedStudioSDKWrapper.getInstance().onBackPressed();
    }

    public static void onConfigurationChanged(Configuration newConfig) {
        AgedStudioSDKWrapper.getInstance().onConfigurationChanged(newConfig);
    }

    public static void onRestoreInstanceState(Bundle savedInstanceState) {
        AgedStudioSDKWrapper.getInstance().onRestoreInstanceState(savedInstanceState);
    }

    public static void onSaveInstanceState(Bundle outState) {
        AgedStudioSDKWrapper.getInstance().onSaveInstanceState(outState);
    }

    public static void onStart() {
        AgedStudioSDKWrapper.getInstance().onStart();
    }

    public static void onLowMemory() {
        AgedStudioSDKWrapper.getInstance().onLowMemory();
    }
}
