package com.agedstudio.ga;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.utils.LogUtil;
import com.agedstudio.base.utils.SysUtils;
import com.gameanalytics.sdk.GAErrorSeverity;
import com.gameanalytics.sdk.GameAnalytics;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GameAnalyticsBusiness extends AbstractAnalyticsApi {

    private static final String TAG = GameAnalyticsBusiness.class.getSimpleName();

    public void init(Context context, String key, String secret) {
        super.init(context);
        try {
            GameAnalytics.configureBuild(SysUtils.getVersionName() + " " + SysUtils.getVersionCode());
            GameAnalytics.initialize((Activity) context, key, secret);
            GameAnalytics.setEnabledEventSubmission(true);
            GameAnalytics.setEnabledInfoLog(SysUtils.isDebugApp());
            GameAnalytics.setEnabledVerboseLog(SysUtils.isDebugApp());
            // If you for GDPR purposes need to disable event submission you can call the following
            // GameAnalytics.setEnabledEventSubmission(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(String name, String jsonStr) {
        LogUtil.d(TAG, "Event name:" + name);
        GameAnalytics.addDesignEvent(name);
    }

    @Override
    public void onEvent(String name, Bundle bundle) {
        LogUtil.d(TAG, "Event name:" + name);
        GameAnalytics.addDesignEvent(name);
    }

    @Override
    public void onMaxAdRevenueEvent(Map<String, Object> params) {
        try {
            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2GameAnalystics--");

            int adType = (int) params.get("adType");
            String adUnitId = (String) params.get("adUnitId");
            String adFormat = (String) params.get("adFormat");
            String networkName = (String) params.get("networkName");
            double revenue = (double) params.get("revenue");
            String countryCode = (String) params.get("countryCode");
            String placement = (String) params.get("placement");
            String creativeId = (String) params.get("creativeId");
            String version = (String) params.get("version");

            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2GameAnalystics-1-" + adType + "  " + adUnitId);

            JSONObject json = new JSONObject();
            json.put("country", countryCode);
            json.put("network_name", networkName);
            json.put("adunit_id", adUnitId);
            json.put("adunit_format", adFormat);
            json.put("placement", placement);
            json.put("creative_id", creativeId);
            json.put("revenue", revenue);
            GameAnalytics.addImpressionMaxEvent(version, json);
        } catch (Exception e) {
            LogUtil.i(TAG, "MaxOnPaidEventUtils----logAdRevenue2GameAnalystics--3");
            e.printStackTrace();
        }
    }

    @Override
    public void onGoogleBillingEvent(Map<String, Object> params) {
        LogUtil.i(TAG, "onGoogleBillingEvent GameAnalystics start");
        try{
            String currencyCode = (String)params.get("currencyCode");
            long amount = (long)params.get("amount");
            String itemType = (String)params.get("itemType");
            String itemId = (String)params.get("itemId");
            String cartType = (String)params.get("cartType");
            String receipt = (String)params.get("receipt");
            String signature = (String)params.get("signature");
            String planId = (String)params.get("planId");

            if(amount == 0) return;

            int iAmount = (int)amount / 10000;

            LogUtil.i(TAG, "onGoogleBillingEvent GameAnalystics"
                    + " currencyCode: " + currencyCode
                    + " amount: " + iAmount
                    + " itemType: " + itemType
                    + " itemId: " + itemId
                    + " cartType: " + cartType
            );
            GameAnalytics.addBusinessEvent(currencyCode, iAmount, itemType, itemId, cartType, receipt, "google_play", signature);
        }catch (Exception e){
            LogUtil.i(TAG, "onGoogleBillingEvent GameAnalystics error");
        }
    }

    @Override
    public void updateConsent() {

    }

    @Override
    public void updateConsentNotRequired() {

    }

    @Override
    public void reportException(String location, String message, String stack) {
        LogUtil.d(TAG, "reportException:" + message);
        Map<String, Object> infos = new HashMap<>();
        infos.put("location", location);
        infos.put("message", message);
        infos.put("stack", stack);
        GameAnalytics.addErrorEvent(GAErrorSeverity.Error, stack, infos);
    }
}
