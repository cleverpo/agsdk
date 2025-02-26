package com.agedstudio.libsdk.service;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsService extends AgedStudioSDKClass {

    private static final String TAG = AnalyticsService.class.getSimpleName();

    public static List<AbstractAnalyticsApi> getAnalyticsApis(){
        List<AbstractAnalyticsApi> ret = new ArrayList<>();

        if(FirebaseAnalyticsService.getAnalyticsApi() != null){
            ret.add(FirebaseAnalyticsService.getAnalyticsApi());
        }
        if(AppsFlyerService.getAnalyticsApi() != null){
            ret.add(AppsFlyerService.getAnalyticsApi());
        }
        if(GameAnalyticsService.getAnalyticsApi() != null){
            ret.add(GameAnalyticsService.getAnalyticsApi());
        }
        if(AdjustService.getAnalyticsApi() != null){
            ret.add(AdjustService.getAnalyticsApi());
        }

        return ret;
    }

    /**
     * 发送事件
     * @param name
     * @param jsonStr
     */
    public static void onEvent(String name, String jsonStr) {
        LogUtil.i(TAG, "onEvent");
        FirebaseAnalyticsService.onEvent(name, jsonStr);
        AppsFlyerService.onEvent(name, jsonStr);
        GameAnalyticsService.onEvent(name, jsonStr);
    }

    public static void onAdjustEvent(String name, String jsonStr) {
        LogUtil.i(TAG, "onAdjustEvent:"+name);
        AdjustService.onEvent(name, jsonStr);
    }

    public static void updateConstent() {
        LogUtil.i(TAG, "updateConstent");
        FirebaseAnalyticsService.updateConstent();
    }

    public static void updateConsentNotRequired() {
        LogUtil.i(TAG, "updateConsentNotRequired");
        FirebaseAnalyticsService.updateConsentNotRequired();
    }

    public static void reportException(String location, String message, String stack){
        GameAnalyticsService.reportException(location, message, stack);
    }
}
