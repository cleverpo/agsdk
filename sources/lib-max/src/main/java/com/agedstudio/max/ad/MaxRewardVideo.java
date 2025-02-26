package com.agedstudio.max.ad;

import android.util.Log;

import com.agedstudio.base.ad.BaseRewardVideo;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;
import com.agedstudio.max.MaxOnPaidEventUtils;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.DTBAdCallback;
import com.amazon.device.ads.DTBAdRequest;
import com.amazon.device.ads.DTBAdResponse;
import com.amazon.device.ads.DTBAdSize;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;

public class MaxRewardVideo extends BaseRewardVideo {

    private static final String TAG = MaxRewardVideo.class.getSimpleName();

    /**
     * 广告实例
     */
    private MaxRewardedAd ad;

    private boolean m_isFirstLoaded = true;

    public MaxRewardVideo(String placementID) {
        super(placementID);
    }

    /**
     * 加载广告
     */
    @Override
    public void load() {
        LogUtil.i(TAG, "MaxRewardVideo---load:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "MaxRewardVideo---load-----");
                if (null == MaxRewardVideo.this.ad) {
                    LogUtil.i(TAG, "MaxRewardVideo---load-----1");
                    MaxRewardVideo.this.ad = MaxRewardedAd.getInstance( MaxRewardVideo.this.placementID, MaxRewardVideo.this.activity );
                    MaxRewardVideo.this.ad.setListener(new MaxRewardedAdListener() {
                        @Override
                        public void onUserRewarded(MaxAd maxAd, MaxReward maxReward) {
                            LogUtil.i(TAG, "MaxRewardVideo---load---onUserRewarded--2");
                            MaxRewardVideo.this.onCallListenerReward();
                        }

//                        @Override
//                        public void onRewardedVideoStarted(MaxAd maxAd) {
//
//                        }

//                        @Override
//                        public void onRewardedVideoCompleted(MaxAd maxAd) {
//
//                        }

                        @Override
                        public void onAdLoaded(MaxAd maxAd) {
                            LogUtil.i(TAG, "MaxRewardVideo---load---onAdLoaded--3");
                            MaxRewardVideo.this.onAdLoaded();
                        }

                        @Override
                        public void onAdDisplayed(MaxAd maxAd) {
                            LogUtil.i(TAG, "MaxRewardVideo---load---onAdDisplayed--4");
                            MaxRewardVideo.this.onAdShowedFullScreenContent();
                        }

                        @Override
                        public void onAdHidden(MaxAd maxAd) {
                            LogUtil.i(TAG, "MaxRewardVideo---load---onAdHidden--5");
                            MaxRewardVideo.this.onAdDismissedFullScreenContent();
                        }

                        @Override
                        public void onAdClicked(MaxAd maxAd) {
                            LogUtil.i(TAG, "MaxRewardVideo---load---onAdClicked--6");
                            MaxRewardVideo.this.onAdClicked();
                        }

                        @Override
                        public void onAdLoadFailed(String s, MaxError maxError) {
                            LogUtil.i(TAG, "MaxRewardVideo---load---onAdLoadFailed--7");
                            MaxRewardVideo.this.onAdFailedToLoad(maxError.getCode(), maxError.getMessage());
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                            LogUtil.i(TAG, "MaxRewardVideo---load---onAdDisplayFailed--8");
                            MaxRewardVideo.this.onAdFailedToShowFullScreenContent(maxError.getCode(), maxError.getMessage());
                        }
                    });
                    MaxRewardVideo.this.ad.setRevenueListener(new MaxAdRevenueListener() {
                        @Override
                        public void onAdRevenuePaid(MaxAd maxAd) {
                            MaxOnPaidEventUtils.onPaidEvent(3, MaxRewardVideo.this.placementID, maxAd);
                        }
                    });

                }

                if (MaxRewardVideo.this.isLoading) {
                    LogUtil.i(TAG, "MaxRewardVideo---load-----2");
                    return;
                }
                if (MaxRewardVideo.this.isReady()) {
                    LogUtil.i(TAG, "MaxRewardVideo---load-----3");
                    MaxRewardVideo.this.onAdLoaded();
                    return;
                }
                LogUtil.i(TAG, "MaxRewardVideo---load-----4");
                MaxRewardVideo.this.cancelRetryCountdown();;
                MaxRewardVideo.this.isLoading = true;

                if(MaxRewardVideo.this.m_isFirstLoaded){
                    MaxRewardVideo.this.initAmazonAd();
                }else{
                    MaxRewardVideo.this.ad.loadAd();
                }
                MaxRewardVideo.this.m_isFirstLoaded = false;
            }
        });
    }

    @Override
    public void show(String placement) {
        LogUtil.i(TAG, "MaxRewardVideo---show-----");
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "MaxRewardVideo---show-----1");
                if (!MaxRewardVideo.this.isReady()) {
                    LogUtil.i(TAG, "MaxRewardVideo---show-----2");
                    MaxRewardVideo.this.onCallListenerPlayFailed(WAS_NOT_READY_YET, "");
                    return;
                }
                LogUtil.i(TAG, "MaxRewardVideo---show-----3");
                MaxRewardVideo.this.ad.showAd(placement, "", null);
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
            amazonAdSlotId = this.mCustomParams.getString("amazonRewardAdId");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(amazonAdSlotId.isEmpty()){
            MaxRewardVideo.this.ad.loadAd();
            return;
        }
        DTBAdRequest adLoader = new DTBAdRequest();
        // Switch video player width and height values(320, 480) depending on device orientation
        adLoader.setSizes( new DTBAdSize.DTBVideo(320,480,amazonAdSlotId) );
        adLoader.loadAd( new DTBAdCallback()
        {
            @Override
            public void onSuccess(final DTBAdResponse dtbAdResponse)
            {
                // 'rewardedAd' is your instance of MaxRewardedAd
                MaxRewardVideo.this.ad.setLocalExtraParameter( "amazon_ad_response", dtbAdResponse );
                MaxRewardVideo.this.ad.loadAd();
            }

            @Override
            public void onFailure(final AdError adError)
            {
                // 'rewardedAd' is your instance of MaxRewardedAd
                MaxRewardVideo.this.ad.setLocalExtraParameter( "amazon_ad_error", adError );
                MaxRewardVideo.this.ad.loadAd();
            }
        } );
    }
    /**
     * 加载成功
     * @param code
     * @param message
     */
    private void onAdFailedToLoad(int code, String message) {
        LogUtil.i(MaxRewardVideo.TAG, "MaxRewardVideo---onAdFailedToLoad:placementID=" + this.placementID + ",code=" + code + ",message=" + message);
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
        LogUtil.i(MaxRewardVideo.TAG, "MaxRewardVideo---onAdLoaded:placementID=" + this.placementID);
        this.isLoading = false;
        this.onCallListenerLoaded();
    }

    /**
     * 点击广告
     */
    private void onAdClicked() {
        LogUtil.i(MaxRewardVideo.TAG, "MaxRewardVideo---onAdClicked:placementID=" + this.placementID);
        this.onCallListenerClick();
    }

    /**
     * 展示成功
     */
    private void onAdShowedFullScreenContent() {
        LogUtil.i(MaxRewardVideo.TAG, "MaxRewardVideo---onAdShowedFullScreenContent:placementID=" + this.placementID);
        this.onCallListenerShow();
    }

    /**
     * 展示失败
     */
    private void onAdFailedToShowFullScreenContent(int code, String message) {
        LogUtil.i(MaxRewardVideo.TAG, "MaxRewardVideo---onAdFailedToShowFullScreenContent:placementID=" + this.placementID);
        this.onCallListenerPlayFailed(code, message);
        // preload next
        this.load();
    }

    /**
     * 关闭广告
     */
    private void onAdDismissedFullScreenContent() {
        LogUtil.i(MaxRewardVideo.TAG, "MaxRewardVideo---onAdDismissedFullScreenContent:placementID=" + this.placementID);
        this.onCallListenerClose();
        // preload next
        this.load();
    }
}
