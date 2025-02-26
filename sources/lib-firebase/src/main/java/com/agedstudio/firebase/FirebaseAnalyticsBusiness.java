package com.agedstudio.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.utils.LogUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAnalyticsBusiness extends AbstractAnalyticsApi {
    private static final String TAG = FirebaseAnalyticsBusiness.class.getSimpleName();

    private static FirebaseAnalyticsBusiness mIns = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    private static SharedPreferences mSharedPreferences = null;
    private static final String DATA_KEY = "MaxTaichiTroasCache";

    private FirebaseAnalyticsBusiness() {

    }

    public static FirebaseAnalyticsBusiness getIns() {
        if (FirebaseAnalyticsBusiness.mIns == null) {
            FirebaseAnalyticsBusiness.mIns = new FirebaseAnalyticsBusiness();
        }
        return FirebaseAnalyticsBusiness.mIns;
    }

    @Override
    public void init(Context context) {
        super.init(context);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        mSharedPreferences = context.getSharedPreferences(DATA_KEY, 0);
    }

    @Override
    public void onEvent(String name, String jsonStr) {
        if(mFirebaseAnalytics == null) return;

        LogUtil.d(TAG, "Event name:" + name);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(name, bundle);
    }

    @Override
    public void onEvent(String name,Bundle bundle) {
        if(mFirebaseAnalytics == null) return;

        LogUtil.d(TAG, "Event name:" + name);
        mFirebaseAnalytics.logEvent(name, bundle);
    }

    @Override
    public void onMaxAdRevenueEvent(Map<String, Object> params) {
        try {
            int adType = (int) params.get("adType");
            String adUnitId = (String) params.get("adUnitId");
            String adFormat = (String) params.get("adFormat");
            String networkName = (String) params.get("networkName");
            double revenue = (double) params.get("revenue");
            String countryCode = (String) params.get("countryCode");

            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2Firebase--" + adType + "  " + adUnitId);
            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2Firebase--1");

            FirebaseAnalyticsBusiness analytics = FirebaseAnalyticsBusiness.getIns();
            if (null == analytics || null == mSharedPreferences) {
                return;
            }

            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2Firebase--2");

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "appLovin");
            bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, networkName);
            bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, adFormat);
            bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId);
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
            bundle.putString(FirebaseAnalytics.Param.CURRENCY, countryCode); // All Applovin revenue is sent in USD
            analytics.onEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);// 给ARO用
            analytics.onEvent("Ad_Impression_Revenue", bundle);// 给Taichi用
            float previousTaichiTroasCache = mSharedPreferences.getFloat(DATA_KEY, 0); //App本地存储用于累计tROAS的缓存值,sharedPref只是作为事例，可以选择其它本地存储的方式
            float currentTaichiTroasCache = (float) (previousTaichiTroasCache + revenue);//累加tROAS的缓存值
            //check是否应该发送TaichitROAS事件
            if (currentTaichiTroasCache >= 0.01) {//如果超过0.01就触发一次tROAS taichi事件
                Bundle roasbundle = new Bundle();
                roasbundle.putDouble(FirebaseAnalytics.Param.VALUE,
                        currentTaichiTroasCache);//(Required)tROAS事件必须带Double类型的Value
                roasbundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");//(Required)tROAS事件必须带Currency的币种，如果是USD的话，就写USD，如果不是USD，务必把其他币种换算成USD
                analytics.onEvent("Total_Ads_Revenue_001", roasbundle); // 给Taichi用
                mSharedPreferences.edit().putFloat(DATA_KEY, 0);//重新清零，开始计算
            } else {
                mSharedPreferences.edit().putFloat(DATA_KEY, currentTaichiTroasCache);//先存着直到超过0.01才发送
            }
            mSharedPreferences.edit().commit();
        } catch (Exception e) {
            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2Firebase--4");
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

    public void updateConsentNotRequired() {
    }

    private boolean isPurposeEnabled(int purpose, String purposeConsents) {
        if (null == purposeConsents || purposeConsents.isEmpty()) {
            return  false;
        }
        if (purpose > purposeConsents.length()) {
            return false;
        }
        return purposeConsents.charAt(purpose - 1) == '1';
    }

    private boolean isPurposesEnabled(int[] purposes, String purposeConsents) {
        if (purposes.length == 0) {
            return false;
        }
        for (int i = 0; i < purposes.length; i++) {
            if (!isPurposeEnabled(purposes[i], purposeConsents)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void reportException(String location, String message, String stack) {

    }
}
