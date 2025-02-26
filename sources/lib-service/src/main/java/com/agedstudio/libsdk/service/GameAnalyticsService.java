package com.agedstudio.libsdk.service;

import android.content.Context;
import android.util.Log;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class GameAnalyticsService extends AgedStudioSDKClass {

    private static final String TAG = GameAnalyticsService.class.getSimpleName();

    private static GameAnalyticsService mIns = null;

    private AbstractAnalyticsApi mAnalyticsBusiness = null;

    public void init(Context context, AbstractAnalyticsApi bussiness) {
        super.init(context);
        LogUtil.i(TAG, "init");
        GameAnalyticsService.mIns = this;
        GameAnalyticsService.mIns.mAnalyticsBusiness = bussiness;
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
        if (null == GameAnalyticsService.mIns || null == GameAnalyticsService.mIns.getContext() || null == GameAnalyticsService.mIns.mAnalyticsBusiness) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameAnalyticsService.mIns.mAnalyticsBusiness.onEvent(name, jsonStr);
            }
        });
    }

    public static void reportException(String location, String message, String stack){
        if (null == GameAnalyticsService.mIns || null == GameAnalyticsService.mIns.getContext() || null == GameAnalyticsService.mIns.mAnalyticsBusiness) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameAnalyticsService.mIns.mAnalyticsBusiness.reportException(location, message, stack);
            }
        });
    }

}