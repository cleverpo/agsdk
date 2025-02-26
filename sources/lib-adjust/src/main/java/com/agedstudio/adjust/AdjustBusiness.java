package com.agedstudio.adjust;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.AdjustEventFailure;
import com.adjust.sdk.AdjustEventSuccess;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnEventTrackingFailedListener;
import com.adjust.sdk.OnEventTrackingSucceededListener;
import com.adjust.sdk.xiaomi.AdjustXiaomiReferrer;
import com.agedstudio.base.AbstractAnalyticsApi;
import com.agedstudio.base.utils.LogUtil;
import com.agedstudio.base.utils.SysUtils;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdjustBusiness extends AbstractAnalyticsApi {
    private static final String TAG = AdjustBusiness.class.getSimpleName();

    private static Map<String, String> mAdjustTokenMap;

    public void init(Context context, String key, String fbAppId) {
        super.init(context);
        LogUtil.d(TAG, "init---start");
        try {
            mAdjustTokenMap = new HashMap<>();
            mAdjustTokenMap.put("brilliance_bundle", "rfn822");
            mAdjustTokenMap.put("bulb_a", "j57dpz");
            mAdjustTokenMap.put("coin_a", "a5zm16");
            mAdjustTokenMap.put("coin_b", "4g09uf");
            mAdjustTokenMap.put("coin_c", "donloi");
            mAdjustTokenMap.put("coin_d", "vkci5f");
            mAdjustTokenMap.put("coin_e", "8adlmp");
            mAdjustTokenMap.put("coin_f", "cpqm5b");
            mAdjustTokenMap.put("limited_time_gift_a", "n8peht");
            mAdjustTokenMap.put("limited_time_gift_b", "1ej4nh");
            mAdjustTokenMap.put("limited_time_gift_c", "c9xu3w");
            mAdjustTokenMap.put("limited_time_gift_d", "ivjm0i");
            mAdjustTokenMap.put("limited_time_gift_e", "dgjhpg");
            mAdjustTokenMap.put("Maneki_Neko_bank", "8ie9jk");
            mAdjustTokenMap.put("mega_bundle", "5e9ysr");
            mAdjustTokenMap.put("paper_magic_a", "l7bvvh");
            mAdjustTokenMap.put("paper_magic_b", "balz4i");
            mAdjustTokenMap.put("paper_magic_c", "nrlk0z");
            mAdjustTokenMap.put("remove_ads", "5zgjxx");
            mAdjustTokenMap.put("starter_bundle", "xxawcx");
            mAdjustTokenMap.put("super_bundle", "yaieyf");
            mAdjustTokenMap.put("ultimate_bundle", "6y29mt");
            mAdjustTokenMap.put("user_purchase", "xekqxy");
            mAdjustTokenMap.put("vip-week", "vkv0wj");
            mAdjustTokenMap.put("vip-month", "lqey8w");
            mAdjustTokenMap.put("vip-year", "eiqm7f");

            String appToken = key;

//            String environment = AdjustConfig.ENVIRONMENT_SANDBOX; //测试环境
            String environment = SysUtils.isDebugApp() ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION; //生产环境
            AdjustConfig config = new AdjustConfig(context, appToken, environment);
            config.setLogLevel(SysUtils.isDebugApp() ? LogLevel.VERBOSE : LogLevel.WARN); //生产环境
            LogUtil.i(TAG, "AdjustBusiness--init---1");
            //success 回传
            config.setOnEventTrackingSucceededListener(new OnEventTrackingSucceededListener() {
                @Override
                public void onEventTrackingSucceeded(AdjustEventSuccess adjustEventSuccess) {
                    LogUtil.d(TAG, "Event recorded at " + adjustEventSuccess.timestamp);
                }
            });
            //failure 回传
            config.setOnEventTrackingFailedListener(new OnEventTrackingFailedListener() {
                @Override
                public void onEventTrackingFailed(AdjustEventFailure eventFailureResponseData) {
                    LogUtil.d(TAG, "Event recording failed. Response: " + eventFailureResponseData.message);
                }
            });

            //小米插件
            AdjustXiaomiReferrer.readXiaomiReferrer(context);

            //meta插件
            config.setFbAppId(fbAppId);

            Adjust.initSdk(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.d(TAG, "init---end");
    }

    /**
     * 发送事件
     */
    public void onEvent(String name, String jsonStr) {
        LogUtil.d(TAG, "Event name:" + name);
        AdjustEvent adjustEvent = new AdjustEvent(name);
        Adjust.trackEvent(adjustEvent);
    }

    @Override
    public void onEvent(String name, Bundle bundle) {
    }

    @Override
    public void onMaxAdRevenueEvent(Map<String, Object> params) {
        try {
            LogUtil.i(TAG, "onMaxAdRevenueEvent--start");

            int adType = (int) params.get("adType");
            String adUnitId = (String) params.get("adUnitId");
            String adFormat = (String) params.get("adFormat");
            String networkName = (String) params.get("networkName");
            double revenue = (double) params.get("revenue");
            String countryCode = (String) params.get("countryCode");

            LogUtil.i(TAG, "onMaxAdRevenueEvent--" + adType + "  " + adUnitId);

            String placement = "";
            if (adType == 1) {
                placement = "Banner";
            } else if (adType == 2) {
                placement = "Interstitial";
            } else if (adType == 3) {
                placement = "RewardedVideo";
            } else if (adType == 4) {
                placement = "RewardedInterstitial";
            }
            AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue("applovin_max_sdk");
            adjustAdRevenue.setRevenue(revenue, countryCode);
            adjustAdRevenue.setAdRevenueNetwork(networkName);
            adjustAdRevenue.setAdRevenueUnit(adUnitId);
            adjustAdRevenue.setAdRevenuePlacement(placement);
            Adjust.trackAdRevenue( adjustAdRevenue);
            LogUtil.i(TAG, "onMaxAdRevenueEvent--上报广告收益_Max: " + adType + "  " + adUnitId + "  " + revenue);
        } catch (Exception e) {
            LogUtil.i(TAG, "onMaxAdRevenueEvent--error");
            e.printStackTrace();
        }
    }

    @Override
    public void onGoogleBillingEvent(Map<String, Object> params) {
        try{
            LogUtil.i(TAG, "onGoogleBillingEvent Adjust start");

            String currencyCode = (String)params.get("currencyCode");
            int amount = (int)params.get("amount");
            String itemType = (String)params.get("itemType");
            String itemId = (String)params.get("itemId");
            String cartType = (String)params.get("cartType");
            String receipt = (String)params.get("receipt");
            String signature = (String)params.get("signature");
            String planId = (String)params.get("planId");
            String eventToken = "";
            if(!planId.isEmpty()){
                eventToken = mAdjustTokenMap.get(planId.toLowerCase());
            } else {
                eventToken = mAdjustTokenMap.get(itemId.toLowerCase());
            }

            if(amount == 0) return;

            LogUtil.i(TAG, "onGoogleBillingEvent Adjust"
                    + " currencyCode: " + currencyCode
                    + " amount: " + amount
                    + " itemType: " + itemType
                    + " itemId: " + itemId
                    + " planId: " + planId
                    + " cartType: " + cartType
            );

            if(eventToken == null || eventToken.isEmpty()) return;

            LogUtil.i(TAG, "onGoogleBillingEvent Adjust"
                    + " currencyCode: " + currencyCode
                    + " amount: " + amount
                    + " itemType: " + itemType
                    + " itemId: " + itemId
                    + " eventToken: " + eventToken
            );
            AdjustEvent event = new AdjustEvent(eventToken);
            event.setRevenue(amount, currencyCode);
            Adjust.trackEvent(event);
        }catch (Exception e){
            LogUtil.i(TAG, "onGoogleBillingEvent Adjust error");
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

    }
}
