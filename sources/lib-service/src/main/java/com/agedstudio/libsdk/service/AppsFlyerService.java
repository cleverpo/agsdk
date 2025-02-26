package com.agedstudio.libsdk.service;

import android.content.Context;
import android.util.Log;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.AgedStudioSDKClass;

import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class AppsFlyerService extends AgedStudioSDKClass {

    private static final String TAG = AppsFlyerService.class.getSimpleName();

    private static AppsFlyerService mIns = null;

    private AbstractAnalyticsApi mAnalyticsBusiness = null;

    public void init(Context context, AbstractAnalyticsApi business) {
        super.init(context);
        LogUtil.i(TAG, "init");
        AppsFlyerService.mIns = this;
        AppsFlyerService.mIns.mAnalyticsBusiness = business;
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
        if (null == AppsFlyerService.mIns || null == AppsFlyerService.mIns.getContext() ||  null == AppsFlyerService.mIns.mAnalyticsBusiness) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppsFlyerService.mIns.mAnalyticsBusiness.onEvent(name, jsonStr);
            }
        });
    }
}
