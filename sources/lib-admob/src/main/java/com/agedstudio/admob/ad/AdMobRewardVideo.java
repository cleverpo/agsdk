package com.agedstudio.admob.ad;

import android.util.Log;

import androidx.annotation.NonNull;

import com.agedstudio.admob.AdMobOnPaidEventUtils;
import com.agedstudio.base.ad.BaseRewardVideo;
import com.agedstudio.base.utils.CommonUtil;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdMobRewardVideo extends BaseRewardVideo {

    private static final String TAG = AdMobRewardVideo.class.getSimpleName();

    /**
     * 广告实例
     */
    private RewardedAd ad;

    public AdMobRewardVideo(String placementID) {
        super(placementID);
    }

    /**
     * 加载广告
     */
    @Override
    public void load() {
        Log.i(TAG, "load:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == AdMobRewardVideo.this.ad) {
                    AdMobRewardVideo.this.cancelRetryCountdown();;
                    AdMobRewardVideo.this.isLoading = true;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    RewardedAd.load(AdMobRewardVideo.this.activity, AdMobRewardVideo.this.placementID, adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            AdMobRewardVideo.this.onAdFailedToLoad(loadAdError.getCode(), loadAdError.getMessage());
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            AdMobRewardVideo.this.onAdLoaded(rewardedAd);
                        }
                    });
                }else{
                    AdMobRewardVideo.this.onCallListenerLoaded();
                }
            }
        });
    }

    @Override
    public void show(String placement) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!AdMobRewardVideo.this.isReady()) {
                    AdMobRewardVideo.this.onCallListenerPlayFailed(WAS_NOT_READY_YET, "");
                    return;
                }
                AdMobRewardVideo.this.ad.show(AdMobRewardVideo.this.activity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        AdMobRewardVideo.this.onCallListenerReward();
                    }
                });
            }
        });
    }

    @Override
    public boolean isReady() {
        return this.ad != null && !this.isLoading;
    }

    /**
     * 加载成功
     * @param code
     * @param message
     */
    private void onAdFailedToLoad(int code, String message) {
        Log.i(AdMobRewardVideo.TAG, "reward video failed to load:placementID=" + this.placementID + ",code=" + code + ",message=" + message);
        this.ad = null;
        this.isLoading = false;
        this.onCallListenerLoadFail(code, message);
        // retry
        // 10秒后重新加载
        this.restryCountdown = 10;
    }

    /**
     * 加载失败
     * @param ad
     */
    private void onAdLoaded(@NonNull RewardedAd ad) {
        Log.i(AdMobRewardVideo.TAG, "reward video loaded:placementID=" + this.placementID);
        this.ad = ad;
        this.isLoading = false;
        this.ad.setOnPaidEventListener(new OnPaidEventListener() {
            @Override
            public void onPaidEvent(@NonNull AdValue adValue) {
                AdMobOnPaidEventUtils.onPaidEvent(3, AdMobRewardVideo.this.placementID, ad.getResponseInfo(), adValue);
            }
        });
        this.ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                AdMobRewardVideo.this.onAdClicked();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                AdMobRewardVideo.this.onAdDismissedFullScreenContent();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                AdMobRewardVideo.this.onAdFailedToShowFullScreenContent(adError.getCode(), adError.getMessage());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                AdMobRewardVideo.this.onAdShowedFullScreenContent();
            }
        });
        this.onCallListenerLoaded();
    }

    /**
     * 点击广告
     */
    private void onAdClicked() {
        Log.i(AdMobRewardVideo.TAG, "reward video clicked:placementID=" + this.placementID);
        this.onCallListenerClick();
    }

    /**
     * 展示成功
     */
    private void onAdShowedFullScreenContent() {
        Log.i(AdMobRewardVideo.TAG, "reward video showed:placementID=" + this.placementID);
        this.ad = null;
        this.onCallListenerShow();
    }

    /**
     * 展示失败
     */
    private void onAdFailedToShowFullScreenContent(int code, String message) {
        Log.i(AdMobRewardVideo.TAG, "reward video failed to show:placementID=" + this.placementID);
        this.onCallListenerPlayFailed(code, message);
    }

    /**
     * 关闭广告
     */
    private void onAdDismissedFullScreenContent() {
        Log.i(AdMobRewardVideo.TAG, "reward video dismissed:placementID=" + this.placementID);
        this.onCallListenerClose();
        // preload next
        this.load();
    }
}
