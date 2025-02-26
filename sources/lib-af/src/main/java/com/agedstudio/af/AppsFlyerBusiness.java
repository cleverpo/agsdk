package com.agedstudio.af;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;
import com.agedstudio.base.utils.SysUtils;
import com.appsflyer.AFAdRevenueData;
import com.appsflyer.AdRevenueScheme;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.MediationNetwork;
import com.appsflyer.attribution.AppsFlyerRequestListener;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppsFlyerBusiness extends AbstractAnalyticsApi {

    private static final String TAG = AppsFlyerBusiness.class.getSimpleName();

    private AppsFlyerConversionListener mConversionListener = new AppsFlyerConversionListener() {
        @Override
        public void onConversionDataSuccess(Map<String, Object> map) {
            LogUtil.d(TAG, "conversion data success");
        }

        @Override
        public void onConversionDataFail(String s) {
            LogUtil.d(TAG, "conversion data fail:" + s);
        }

        @Override
        public void onAppOpenAttribution(Map<String, String> map) {
            LogUtil.d(TAG, "app open attribution");
        }

        @Override
        public void onAttributionFailure(String s) {
            LogUtil.d(TAG, "attribution failure:" + s);
        }
    };

    private AppsFlyerRequestListener mRequestListener = new AppsFlyerRequestListener() {
        @Override
        public void onSuccess() {
            LogUtil.d(TAG, "success");
        }

        @Override
        public void onError(int code, String message) {
            LogUtil.d(TAG, "error, code=" + code + ", message=" + message);
        }
    };

    public void initInApplication(Context context, String key) {
        try {
            AppsFlyerLib.getInstance().setHost("","appsflyersdk.com");
            AppsFlyerLib.getInstance().init(key, mConversionListener, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Context context, String key) {
        super.init(context);
        try {
            AppsFlyerLib.getInstance().setDebugLog(SysUtils.isDebugApp());
            AppsFlyerLib.getInstance().start(context, key, mRequestListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送事件
     * @param name
     * @param jsonStr
     */
    public void onEvent(String name, String jsonStr) {
        // apps flyer
        LogUtil.d(TAG, "Event name:" + name);
        AppsFlyerLib.getInstance().logEvent(this.mContext, name, null, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                LogUtil.d(TAG, "Event sent successfully");
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "Event failed to sent, code=" + i + ", message=" + s);
            }
        });
    }

    @Override
    public void onEvent(String name, Bundle bundle) {
    }

    @Override
    public void onMaxAdRevenueEvent(Map<String, Object> params) {
        try {
            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2AppsFlyer--");

            int adType = (int) params.get("adType");
            String adUnitId = (String) params.get("adUnitId");
            String networkName = (String) params.get("networkName");
            double revenue = (double) params.get("revenue");
            String countryCode = (String) params.get("countryCode");

            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2AppsFlyer--1" + adType + "  " + adUnitId);

            Map<String, Object> customParams = new HashMap<>();
            customParams.put(AdRevenueScheme.COUNTRY, countryCode);
            customParams.put(AdRevenueScheme.AD_UNIT, adUnitId);
            if (adType == 1) {
                customParams.put(AdRevenueScheme.AD_TYPE, "Banner");
            } else if (adType == 2) {
                customParams.put(AdRevenueScheme.AD_TYPE, "Interstitial");
            } else if (adType == 3) {
                customParams.put(AdRevenueScheme.AD_TYPE, "RewardedVideo");
            } else if (adType == 4) {
                customParams.put(AdRevenueScheme.AD_TYPE, "RewardedInterstitial");
            }
            AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();
            AFAdRevenueData adRevenueData = new AFAdRevenueData(
                    networkName,
                    MediationNetwork.APPLOVIN_MAX,
                    countryCode,
                    revenue
            );
            appsflyer.logAdRevenue(adRevenueData, customParams);
            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2AppsFlyer--2--上报广告收益_Max: " + adType + "  " + adUnitId + "  " + revenue);
        } catch (Exception e) {
            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2AppsFlyer--3");
            e.printStackTrace();
        }
    }

    @Override
    public void onGoogleBillingEvent(Map<String, Object> params) {
        //TODO
    }

    @Override
    public void updateConsent() {

    }

    @Override
    public void updateConsentNotRequired() {

    }

    @Override
    public void reportException(String location, String message, String stack) {

    }
}
