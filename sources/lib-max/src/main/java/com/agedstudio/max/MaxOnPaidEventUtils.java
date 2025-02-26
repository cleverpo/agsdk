package com.agedstudio.max;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.utils.LogUtil;
import com.applovin.mediation.MaxAd;
import com.applovin.sdk.AppLovinSdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxOnPaidEventUtils {

    private static final String TAG = MaxOnPaidEventUtils.class.getSimpleName();

    private static Context mContext = null;
    private static List<AbstractAnalyticsApi> mAnalyticsApis = null;

    public static void init(Context context, List<AbstractAnalyticsApi> analyticsApiList) {
        mContext = context;
        mAnalyticsApis = analyticsApiList;
    }

    /**
     * @param adType 广告类型
     * @param adUnitId
     * @param maxAd
     */
    public static void onPaidEvent(int adType, String adUnitId, MaxAd maxAd) {
        if (null == maxAd) {
            return;
        }

        LogUtil.i(TAG, "MaxOnPaidEventUtils----onPaidEvent--" + adType + "  " + adUnitId);
        Map<String, Object> params = new HashMap<>();
        params.put("adType", adType);
        params.put("adUnitId", adUnitId);
        params.put("adFormat", maxAd.getFormat().getDisplayName());
        params.put("revenue", maxAd.getRevenue());
        params.put("countryCode", AppLovinSdk.getInstance(mContext).getConfiguration().getCountryCode());
        params.put("placement", maxAd.getPlacement());
        params.put("creativeId", maxAd.getCreativeId());
        params.put("version", AppLovinSdk.VERSION);

        for(AbstractAnalyticsApi api : mAnalyticsApis){
            api.onMaxAdRevenueEvent(params);
        }
    }
}
