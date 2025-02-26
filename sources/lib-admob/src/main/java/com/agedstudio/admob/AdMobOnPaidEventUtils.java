package com.agedstudio.admob;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.agedstudio.firebase.FirebaseAnalyticsBusiness;
import com.appsflyer.AFAdRevenueData;
import com.appsflyer.AdRevenueScheme;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.MediationNetwork;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.ResponseInfo;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdMobOnPaidEventUtils {

    private static final String TAG = AdMobOnPaidEventUtils.class.getSimpleName();

    private static SharedPreferences mSharedPreferences = null;
    private static final String DATA_KEY = "AdmobTaichiTroasCache";

    public static void init(Context context) {
        mSharedPreferences = context.getSharedPreferences(DATA_KEY, 0);
    }

    /**
     * @param adType 广告类型
     * @param responseInfo
     * @param adValue
     */
    public static void onPaidEvent(int adType,  String adUnitId, ResponseInfo responseInfo, @NonNull AdValue adValue) {
        // AppsFlyer
        AdMobOnPaidEventUtils.logAdRevenue2AppsFlyer(adType, adUnitId, responseInfo, adValue);
        // Firebase
        AdMobOnPaidEventUtils.logAdRevenue2Firebase(adType, adUnitId, responseInfo, adValue);
        //adjust
        AdMobOnPaidEventUtils.logAdRevenue2Adjust(adType, adUnitId, responseInfo, adValue);
    }

    private static void logAdRevenue2AppsFlyer(int adType,  String adUnitId, ResponseInfo responseInfo, @NonNull AdValue adValue) {
        if (null == responseInfo || null == adValue) {
            return;
        }

        try {
            AdapterResponseInfo adapterResponseInfo = responseInfo.getLoadedAdapterResponseInfo();

            String monetizationNetwork = adapterResponseInfo.getAdSourceInstanceName();

            long valueMicros = adValue.getValueMicros();
            Double revenueUSD = (double) valueMicros / 1000000.0;
            //int precision = adValue.getPrecisionType();

            String currencyCode = adValue.getCurrencyCode();

            AFAdRevenueData adRevenueData = new AFAdRevenueData(
                    monetizationNetwork,       // monetizationNetwork
                    MediationNetwork.GOOGLE_ADMOB, // mediationNetwork
                    currencyCode,       // currencyIso4217Code
                    revenueUSD          // revenue
            );

            Map<String, Object> customParams = new HashMap<>();
            customParams.put(AdRevenueScheme.COUNTRY, currencyCode);
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
            AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, customParams);
            Log.i(TAG, "上报广告收益_Admob: "
                    + adType + "  "
                    + adUnitId + "  "
                    + monetizationNetwork + "  "
                    + currencyCode + "  "
                    + revenueUSD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void logAdRevenue2Firebase(int adType,  String adUnitId, ResponseInfo responseInfo, @NonNull AdValue adValue) {
        if (null == responseInfo || null == adValue) {
            return;
        }

        FirebaseAnalyticsBusiness analytics = FirebaseAnalyticsBusiness.getIns();
        if (null == analytics || null == mSharedPreferences) {
            return;
        }
        Bundle params = new Bundle();
        double currentImpressionRevenue = (double) adValue.getValueMicros() / (double) 1000000; //getValueMicros() returns the value of the ad in micro units. For example, a getValueMicros() return value of 5,000 means the ad is estimated to be worth $0.005.
        params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "AdMob");
        params.putDouble(FirebaseAnalytics.Param.VALUE, currentImpressionRevenue);
        params.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
        String precisionType;
        switch(adValue.getPrecisionType()){
            case 0 :
                precisionType = "UNKNOWN";
                break;
            case 1 :
                precisionType = "ESTIMATED";
                break;
            case 2 :
                precisionType = "PUBLISHER_PROVIDED";
                break;
            case 3 :
                precisionType = "PRECISE";
                break;
            default:
                precisionType = "Invalid";
                break;
        }
        params.putString("precisionType", precisionType); //(Optional) 记录PrecisionType
        analytics.onEvent("Ad_Impression_Revenue", params);// 给Taichi用
        float previousTaichiTroasCache = mSharedPreferences.getFloat(DATA_KEY, 0); //App本地存储用于累计tROAS的缓存值,sharedPref只是作为事例，可以选择其它本地存储的方式
        float currentTaichiTroasCache = (float) (previousTaichiTroasCache + currentImpressionRevenue); //累加tROAS的缓存值
        // check是否应该发送TaichitROAS事件
        if (currentTaichiTroasCache >= 0.01) { //如果超过0.01就触发一次tROAS taichi事件
            Bundle roasbundle = new Bundle();
            roasbundle.putDouble(FirebaseAnalytics.Param.VALUE,
                    currentTaichiTroasCache); //(Required)tROAS事件必须带Double类型的Value
            roasbundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD"); //(Required)tROAS事件必须带Currency的币种，如果是USD的话，就写USD，如果不是USD，务必把其他币种换算成USD
            analytics.onEvent("Total_Ads_Revenue_001", roasbundle);
            mSharedPreferences.edit().putFloat(DATA_KEY, 0); //重新清零，开始计算
        } else {
            mSharedPreferences.edit().putFloat(DATA_KEY, currentTaichiTroasCache); // 先存着直到超过0.01才发送
        }
        mSharedPreferences.edit().commit();
    }

    private static void logAdRevenue2Adjust(int adType,  String adUnitId, ResponseInfo responseInfo, @NonNull AdValue adValue) {
        if (null == responseInfo || null == adValue) {
            return;
        }

        try {
            AdapterResponseInfo adapterResponseInfo = responseInfo.getLoadedAdapterResponseInfo();

            long valueMicros = adValue.getValueMicros();
            Double revenueUSD = (double) valueMicros / 1000000.0;
            String currencyCode = adValue.getCurrencyCode();
            String monetizationNetwork = adapterResponseInfo.getAdSourceInstanceName();

            AdjustAdRevenue adRevenue = new AdjustAdRevenue("admob_sdk");
            adRevenue.setRevenue(revenueUSD, currencyCode);
            adRevenue.setAdRevenueNetwork(monetizationNetwork);
            Adjust.trackAdRevenue(adRevenue);

            Log.i(TAG, "上报广告收益_Admob: "
                    + adType + "  "
                    + adUnitId + "  "
                    + monetizationNetwork + "  "
                    + currencyCode + "  "
                    + revenueUSD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
