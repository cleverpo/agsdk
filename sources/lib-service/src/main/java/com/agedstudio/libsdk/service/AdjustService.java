package com.agedstudio.libsdk.service;

import android.content.Context;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class AdjustService extends AgedStudioSDKClass {
    private static final String TAG = AdjustService.class.getSimpleName();

    private static AdjustService mIns = null;

    private AbstractAnalyticsApi mAnalyticsBusiness = null;

    public void init(Context context, AbstractAnalyticsApi business) {
        super.init(context);
        AdjustService.mIns = this;
        AdjustService.mIns.mAnalyticsBusiness = business;
    }

    public static AbstractAnalyticsApi getAnalyticsApi(){
        if(mIns == null || mIns.mAnalyticsBusiness == null) return null;

        return mIns.mAnalyticsBusiness;
    }

    /**
     * 发送事件
     */
    public static void onEvent(String name, String jsonStr) {
        if (null == AdjustService.mIns || null == AdjustService.mIns.getContext() || null == AdjustService.mIns.mAnalyticsBusiness) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdjustService.mIns.mAnalyticsBusiness.onEvent(name, jsonStr);
            }
        });
    }
}
