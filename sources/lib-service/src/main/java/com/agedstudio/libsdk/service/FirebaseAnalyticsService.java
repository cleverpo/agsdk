package com.agedstudio.libsdk.service;

import android.content.Context;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class FirebaseAnalyticsService extends AgedStudioSDKClass {

    private static final String TAG = FirebaseAnalyticsService.class.getSimpleName();

    private static FirebaseAnalyticsService mIns = null;

    private AbstractAnalyticsApi mAnalyticsBusiness = null;

    public void init(Context context, AbstractAnalyticsApi business) {
        super.init(context);
        LogUtil.d(TAG, "init");
        FirebaseAnalyticsService.mIns = this;
        FirebaseAnalyticsService.mIns.mAnalyticsBusiness = business;
    }

    public static AbstractAnalyticsApi getAnalyticsApi(){
        if(mIns == null || mIns.mAnalyticsBusiness == null) return null;

        return mIns.mAnalyticsBusiness;
    }

    /**
     * 发送事件
     * @param name
     * @param jsonStr
     */
    public static void onEvent(String name, String jsonStr) {
        if (null == FirebaseAnalyticsService.mIns || null == FirebaseAnalyticsService.mIns.getContext() || null == FirebaseAnalyticsService.mIns.mAnalyticsBusiness) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FirebaseAnalyticsService.mIns.mAnalyticsBusiness.onEvent(name, jsonStr);
            }
        });
    }

    public static void updateConstent() {
        if (null == FirebaseAnalyticsService.mIns || null == FirebaseAnalyticsService.mIns.getContext() || null == FirebaseAnalyticsService.mIns.mAnalyticsBusiness) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FirebaseAnalyticsService.mIns.mAnalyticsBusiness.updateConsent();
            }
        });
    }

    public static void updateConsentNotRequired() {
        if (null == FirebaseAnalyticsService.mIns || null == FirebaseAnalyticsService.mIns.getContext() || null == FirebaseAnalyticsService.mIns.mAnalyticsBusiness) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FirebaseAnalyticsService.mIns.mAnalyticsBusiness.updateConsentNotRequired();
            }
        });
    }
}
