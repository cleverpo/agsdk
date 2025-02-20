package com.agedstudio.agsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

//import com.agedstudio.adjust.AdjustBusiness;
//import com.agedstudio.admob.AdMobAdBoot;
//import com.agedstudio.admob.AdMobBannerBusiness;
//import com.agedstudio.admob.AdMobInterstitalBusiness;
//import com.agedstudio.admob.AdMobOnPaidEventUtils;
//import com.agedstudio.admob.AdMobRewardVideoBusiness;
//import com.agedstudio.af.AppsFlyerBusiness;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.AgedStudioSDKWrapper;
//import com.agedstudio.cmp.CMPBusiness;
import com.agedstudio.firebase.FirebaseAnalyticsBusiness;
import com.agedstudio.firebase.FirebaseRCBusiness;
//import com.agedstudio.ga.GameAnalyticsBusiness;
//import com.agedstudio.gpbilling.GPBillingMgr;
//import com.agedstudio.gpbilling.GPBillingOnPaidEventUtils;
import com.agedstudio.libsdk.service.AnalyticsService;
import com.agedstudio.libsdk.service.FirebaseAnalyticsService;
import com.agedstudio.libsdk.service.FirebaseRCService;
import com.agedstudio.libsdk.service.SysService;

import java.util.Map;
//import com.agedstudio.max.MaxBannerBusiness;
//import com.agedstudio.max.MaxBoot;
//import com.agedstudio.max.MaxInterstitalBusiness;
//import com.agedstudio.max.MaxOnPaidEventUtils;
//import com.agedstudio.max.MaxRewardVideoBusiness;

public class AGSDK {
    private static final String TAG = AGSDK.class.getSimpleName();
//    private static AppsFlyerBusiness afBusiness = null;

    public static void initInApplication(Application application){
//        AGSDK.initAppsFlyerBusiness(application, BuildConfig.AFDevKey);
//        AGSDK.initAdjustService(application, BuildConfig.AdjustAppToken, BuildConfig.FbAppId);
    }

    public static void initInActivity(Activity activity){
        long currentTimeMillis = System.currentTimeMillis();

        // sdk
        AGSDK.initSysService(activity);
        AGSDK.initFirebaseAnalyticsService(activity);
        AGSDK.initFirebaseRCService(activity);

//        AGSDK.initCMP(activity);
//        AGSDK.initAppsFlyerService(activity);
//        AGSDK.initGameAnalyticsService(activity, BuildConfig.GAGameKey, BuildConfig.GAGameKey);
//        AGSDK.initAnalyticsService(activity);
//        AGSDK.initBannerService(activity);
//        AGSDK.initIntertitalService(activity);
//        AGSDK.initRewardVideoService(activity);
//        AGSDK.initRewardInterstitalService(activity);
//        AGSDK.initJSExceptionService(activity);
//        AGSDK.initBillingService(activity);

        Log.i("Profiler", "初始化SDK: " + (System.currentTimeMillis() - currentTimeMillis));
    }

//    public static void initCMP(Context context) {
//        Log.i(TAG, "initCMP");
//
//        // CMP
//        CMPBusiness cmpBusiness = new CMPBusiness();
//        cmpBusiness.init(context);
//        CMPService cmpService = new CMPService();
//        cmpService.init(context);
//        cmpService.initBussiness(cmpBusiness);
//        if (SysUtils.isDebugApp()) {
//            CMPService.setDebug(true, "667EAF02139B5337781A8EA71CF4AD3A");
//        }
//    }

//    public static void initAdMob(Context context, boolean hasUserConsent) {
//        // adMob
//        AdMobAdBoot.init(context, hasUserConsent);
//        AdMobOnPaidEventUtils.init(context);
//    }

//    public static void initMax(Context context, boolean hasUserConsent, String amazonAppId) {
//        Log.i(TAG, "AGSDK-----initMax--");
//        // max
//        MaxBoot.init(context, hasUserConsent, amazonAppId);
//        MaxOnPaidEventUtils.init(context);
//    }

    private static void initSysService(Context context) {
        SysService service = new SysService();
        service.init(context);
        AgedStudioSDKWrapper.getInstance().addSDKClass("SysService", service);
    }

//    private static void initAppsFlyerBusiness(Context context, String devKey) {
//        afBusiness = new AppsFlyerBusiness();
//        afBusiness.initInApplication(context, devKey);
//    }

//    private static void initAppsFlyerService(Context context) {
//        AppsFlyerService service = new AppsFlyerService();
//        service.init(context, afBusiness);
//        AgedStudioSDKWrapper.getInstance().addSDKClass("AppsFlyerService", service);
//    }
//
//    private static void initGameAnalyticsService(Context context, String gameKey, String secretKey) {
//        GameAnalyticsService service = new GameAnalyticsService();
//        GameAnalyticsBusiness business = new GameAnalyticsBusiness();
//        business.init(context, gameKey, secretKey);
//        service.init(context, business);
//        AgedStudioSDKWrapper.getInstance().addSDKClass("GameAnalyticsService", service);
//    }

    private static void initFirebaseAnalyticsService(Context context) {
        FirebaseAnalyticsService service = new FirebaseAnalyticsService();
        FirebaseAnalyticsBusiness business = FirebaseAnalyticsBusiness.getIns();
        business.init(context);
        service.init(context, business);
        AgedStudioSDKWrapper.getInstance().addSDKClass("FirebaseAnalyticsService", service);
    }

    private static void initFirebaseRCService(Context context) {
        FirebaseRCService service = new FirebaseRCService();
        FirebaseRCBusiness business = new FirebaseRCBusiness();
        business.init(context);
        service.init(context, business);
        AgedStudioSDKWrapper.getInstance().addSDKClass("FirebaseRCService", service);
    }

//    private static void initAdjustService(Context context, String appToken, String fbAppId) {
//        Log.i(TAG, "AGSDK-----initAdjustService--");
//        AdjustService service = new AdjustService();
//        AdjustBusiness business = new AdjustBusiness();
//        business.init(context, appToken, fbAppId);
//        service.init(context, business);
//        AgedStudioSDKWrapper.getInstance().addSDKClass("AdjustService", service);
//    }

    private static void initAnalyticsService(Context context) {
        AnalyticsService service = new AnalyticsService();
        service.init(context);
        AgedStudioSDKWrapper.getInstance().addSDKClass("AnalyticsService", service);
    }

//    private static void initBannerService(Context context) {
//        BannerService service = new BannerService();
//        service.init(context);
//        AgedStudioSDKWrapper.getInstance().addSDKClass("BannerService", service);
//        // admob
////        AdMobBannerBusiness adMobBusiness = new AdMobBannerBusiness();
////        adMobBusiness.init(context);
////        service.initAdMob(adMobBusiness);
//        // yandex
//        // YandexBannerBusiness yandexBusiness = new YandexBannerBusiness();
//        // yandexBusiness.init(context);
//        // service.initYandex(yandexBusiness);
//        // max
//        MaxBannerBusiness maxBusiness = new MaxBannerBusiness();
//        maxBusiness.init(context);
//        service.initMax(maxBusiness);
//    }

//    private static void initIntertitalService(Context context) {
//        IntertitalService service = new IntertitalService();
//        service.init(context);
//        IntertitalService.setMediation(3);
//        AgedStudioSDKWrapper.getInstance().addSDKClass("IntertitalService", service);
//        // admob
////        AdMobInterstitalBusiness adMobBusiness = new AdMobInterstitalBusiness();
////        adMobBusiness.init(context);
////        service.initAdMob(adMobBusiness);
//        // yandex
//        // YandexInterstitalBusiness yandexBusiness = new YandexInterstitalBusiness();
//        // yandexBusiness.init(context);
//        // service.initYandex(yandexBusiness);
//        // max
//        MaxInterstitalBusiness maxBusiness = new MaxInterstitalBusiness();
//        maxBusiness.init(context);
//        service.initMax(maxBusiness);
//    }

//    private static void initRewardVideoService(Context context) {
//        Log.i(TAG, "AGSDK-----initRewardVideoService--");
//        RewardVideoService service = new RewardVideoService();
//        service.init(context);
//        RewardVideoService.setMediation(3);
//        AgedStudioSDKWrapper.getInstance().addSDKClass("RewardVideoService", service);
//        // admob
////        AdMobRewardVideoBusiness business = new AdMobRewardVideoBusiness();
////        business.init(context);
////        service.initAdMob(business);
//        // yandex
//        // YandexRewardVideoBusiness yandexBusiness = new YandexRewardVideoBusiness();
//        // yandexBusiness.init(context);
//        // service.initYandex(yandexBusiness);
//        // max
//        MaxRewardVideoBusiness maxBusiness = new MaxRewardVideoBusiness();
//        maxBusiness.init(context);
//        service.initMax(maxBusiness);
//    }

//    private static void initRewardInterstitalService(Context context) {
//        RewardInterstitalService service = new RewardInterstitalService();
//        service.init(context);
//        RewardInterstitalService.setMediation(3);
//        AgedStudioSDKWrapper.getInstance().addSDKClass("RewardInterstitalService", service);
//    }

//    private static void initJSExceptionService(Context context) {
//        JSExceptionService service = new JSExceptionService();
//        service.init(context);
//        AgedStudioSDKWrapper.getInstance().addSDKClass("JSExceptionService", service);
//    }
//
//    private  static void initBillingService(Context context) {
//        BillingService service = new BillingService();
//        service.init(context, GPBillingMgr.getInstance());
//
//        GPBillingOnPaidEventUtils.init(context);
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
