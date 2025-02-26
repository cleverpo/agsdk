package com.agedstudio.max.ad;

import android.util.Log;

import com.agedstudio.base.ad.BaseInterstital;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;
import com.agedstudio.max.MaxOnPaidEventUtils;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.DTBAdCallback;
import com.amazon.device.ads.DTBAdRequest;
import com.amazon.device.ads.DTBAdResponse;
import com.amazon.device.ads.DTBAdSize;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

public class MaxInterstitial extends BaseInterstital {

    private static final String TAG = MaxInterstitial.class.getSimpleName();

    /**
     * 广告实例
     */
    private MaxInterstitialAd ad;

    private boolean m_isFirstLoaded = true;

    public MaxInterstitial(String placementID) {
        super(placementID);
    }

    /**
     * 加载广告
     */
    @Override
    public void load() {
        LogUtil.i(TAG, "MaxInterstitial----load:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == MaxInterstitial.this.ad) {
                    MaxInterstitial.this.ad = new MaxInterstitialAd( MaxInterstitial.this.placementID, MaxInterstitial.this.activity);
                    MaxInterstitial.this.ad.setListener(new MaxAdListener() {
                        @Override
                        public void onAdLoaded(MaxAd maxAd) {
                            MaxInterstitial.this.onAdLoaded();
                        }

                        @Override
                        public void onAdDisplayed(MaxAd maxAd) {
                            MaxInterstitial.this.onAdShowedFullScreenContent();
                        }

                        @Override
                        public void onAdHidden(MaxAd maxAd) {
                            MaxInterstitial.this.onAdDismissedFullScreenContent();
                        }

                        @Override
                        public void onAdClicked(MaxAd maxAd) {
                            MaxInterstitial.this.onAdClicked();
                        }

                        @Override
                        public void onAdLoadFailed(String s, MaxError maxError) {
                            MaxInterstitial.this.onAdFailedToLoad(maxError.getCode(), maxError.getMessage());
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                            MaxInterstitial.this.onAdFailedToShowFullScreenContent(maxError.getCode(), maxError.getMessage());
                        }
                    });
                    MaxInterstitial.this.ad.setRevenueListener(new MaxAdRevenueListener() {
                        @Override
                        public void onAdRevenuePaid(MaxAd maxAd) {
                            MaxOnPaidEventUtils.onPaidEvent(2, MaxInterstitial.this.placementID, maxAd);
                        }
                    });

                }

                if (MaxInterstitial.this.isLoading) {
                    return;
                }

                if (MaxInterstitial.this.isReady()) {
                    MaxInterstitial.this.onAdLoaded();
                    return;
                }
                MaxInterstitial.this.cancelRetryCountdown();;
                MaxInterstitial.this.isLoading = true;

                if(MaxInterstitial.this.m_isFirstLoaded){
                    MaxInterstitial.this.initAmazonAd();
                }else{
                    MaxInterstitial.this.ad.loadAd();
                }
                MaxInterstitial.this.m_isFirstLoaded = false;
            }
        });
    }

    @Override
    public void show(String placement) {
        LogUtil.i(TAG, "MaxInterstitial----show:placement=" + placement);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "MaxInterstitial----show--1");
                if (!MaxInterstitial.this.isReady()) {
                    LogUtil.i(TAG, "MaxInterstitial----show--2");
                    MaxInterstitial.this.onCallListenerFailedToShow(WAS_NOT_READY_YET, "");
                    return;
                }
                LogUtil.i(TAG, "MaxInterstitial----show--3");
                MaxInterstitial.this.ad.showAd(placement, "", null);
            }
        });
    }

    @Override
    public boolean isReady() {
        return this.ad != null && !this.isLoading && this.ad.isReady();
    }

    private void initAmazonAd(){
        String amazonAdSlotId = "";
        try{
            amazonAdSlotId = this.mCustomParams.getString("amazonInterstitialId");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(amazonAdSlotId.isEmpty()){
            MaxInterstitial.this.ad.loadAd();
            return;
        }
        DTBAdRequest adLoader = new DTBAdRequest();
        adLoader.setSizes(new DTBAdSize.DTBInterstitialAdSize(amazonAdSlotId));
        adLoader.loadAd(new DTBAdCallback()
        {
            @Override
            public void onSuccess(final DTBAdResponse dtbAdResponse)
            {
                // 'interstitialAd' is your instance of MaxInterstitialAd
                MaxInterstitial.this.ad.setLocalExtraParameter( "amazon_ad_response", dtbAdResponse );
                MaxInterstitial.this.ad.loadAd();
            }

            @Override
            public void onFailure(final AdError adError)
            {
                // 'interstitialAd' is your instance of MaxInterstitialAd
                MaxInterstitial.this.ad.setLocalExtraParameter( "amazon_ad_error", adError );
                MaxInterstitial.this.ad.loadAd();
            }
        } );
    }

    /**
     * 加载成功
     * @param code
     * @param message
     */
    private void onAdFailedToLoad(int code, String message) {
        LogUtil.i(MaxInterstitial.TAG, "interstitial failed to load:placementID=" + this.placementID + ",code=" + code + ",message=" + message);
        this.isLoading = false;
        this.onCallListenerLoadFail(code, message);
        // retry
        // 10秒后重新加载
        this.restryCountdown = 10;
    }

    /**
     * 加载失败
     */
    private void onAdLoaded() {
        LogUtil.i(MaxInterstitial.TAG, "MaxInterstitial----onAdLoaded:placementID=" + this.placementID);
        this.isLoading = false;
        this.onCallListenerLoaded();
    }

    /**
     * 点击广告
     */
    private void onAdClicked() {
        LogUtil.i(MaxInterstitial.TAG, "MaxInterstitial----interstitial clicked:placementID=" + this.placementID);
        this.onCallListenerClick();
    }

    /**
     * 展示成功
     */
    private void onAdShowedFullScreenContent() {
        LogUtil.i(MaxInterstitial.TAG, "MaxInterstitial----onAdShowedFullScreenContent:placementID=" + this.placementID);
        this.onCallListenerShow();
    }

    /**
     * 展示失败
     */
    private void onAdFailedToShowFullScreenContent(int code, String message) {
        LogUtil.i(MaxInterstitial.TAG, "MaxInterstitial----onAdFailedToShowFullScreenContent:placementID=" + this.placementID);
        this.onCallListenerFailedToShow(code, message);
        this.load();
    }

    /**
     * 关闭广告
     */
    private void onAdDismissedFullScreenContent() {
        LogUtil.i(MaxInterstitial.TAG, "MaxInterstitial----onAdDismissedFullScreenContent:placementID=" + this.placementID);
        this.onCallListenerClose();
        this.load();
    }
}
