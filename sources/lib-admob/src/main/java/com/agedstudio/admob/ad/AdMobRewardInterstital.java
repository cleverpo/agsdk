package com.agedstudio.admob.ad;

import android.util.Log;

import androidx.annotation.NonNull;

import com.agedstudio.admob.AdMobOnPaidEventUtils;
import com.agedstudio.base.ad.BaseAd;
import com.agedstudio.base.ad.BaseRewardInterstital;
import com.agedstudio.base.utils.CommonUtil;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class AdMobRewardInterstital extends BaseRewardInterstital {
    private static final String TAG = AdMobRewardInterstital.class.getSimpleName();

    /**
     * 广告实例
     */
    private RewardedInterstitialAd ad;

    public AdMobRewardInterstital(String placementID) {
        super(placementID);
    }

    /**
     * 加载广告
     */
    public void load() {
        Log.i(TAG, "load:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == AdMobRewardInterstital.this.ad) {
                    AdMobRewardInterstital.this.cancelRetryCountdown();;
                    AdMobRewardInterstital.this.isLoading = true;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    RewardedInterstitialAd.load(AdMobRewardInterstital.this.activity, AdMobRewardInterstital.this.placementID, adRequest, new RewardedInterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            AdMobRewardInterstital.this.onAdFailedToLoad(loadAdError.getCode(), loadAdError.getMessage());
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedInterstitialAd rewardedAd) {
                            AdMobRewardInterstital.this.onAdLoaded(rewardedAd);
                        }
                    });
                }
            }
        });
    }

    public void show(String placement) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!AdMobRewardInterstital.this.isReady()) {
                    AdMobRewardInterstital.this.onAdFailedToShowFullScreenContent(BaseAd.WAS_NOT_READY_YET, "");
                    return;
                }
                AdMobRewardInterstital.this.ad.show(AdMobRewardInterstital.this.activity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        AdMobRewardInterstital.this.onCallListenerReward();
                    }
                });
            }
        });
    }

    public boolean isReady() {
        return this.ad != null && !this.isLoading;
    }

    /**
     * 加载成功
     * @param code
     * @param message
     */
    private void onAdFailedToLoad(int code, String message) {
        Log.i(AdMobRewardInterstital.TAG, "reward interstital failed to load:placementID=" + this.placementID + ",code=" + code + ",message=" + message);
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
    private void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
        Log.i(AdMobRewardInterstital.TAG, "reward interstital loaded:placementID=" + this.placementID);
        this.ad = ad;
        this.isLoading = false;
        this.ad.setOnPaidEventListener(new OnPaidEventListener() {
            @Override
            public void onPaidEvent(@NonNull AdValue adValue) {
                AdMobOnPaidEventUtils.onPaidEvent(4, AdMobRewardInterstital.this.placementID, ad.getResponseInfo(), adValue);
            }
        });
        this.ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                AdMobRewardInterstital.this.onAdClicked();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                AdMobRewardInterstital.this.onAdDismissedFullScreenContent();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                AdMobRewardInterstital.this.onAdFailedToShowFullScreenContent(adError.getCode(), adError.getMessage());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                AdMobRewardInterstital.this.onAdShowedFullScreenContent();
            }
        });
        this.onCallListenerLoaded();
    }

    /**
     * 点击广告
     */
    private void onAdClicked() {
        Log.i(AdMobRewardInterstital.TAG, "reward interstital clicked:placementID=" + this.placementID);
        this.onCallListenerClick();
    }

    /**
     * 展示成功
     */
    private void onAdShowedFullScreenContent() {
        Log.i(AdMobRewardInterstital.TAG, "reward in showed:placementID=" + this.placementID);
        this.ad = null;
        this.onCallListenerShow();
    }

    /**
     * 展示失败
     */
    private void onAdFailedToShowFullScreenContent(int code, String message) {
        Log.i(AdMobRewardInterstital.TAG, "reward inte r s ti ta l failed to show:placementID=" + this.placementID);
        this.onCallListenerPlayFailed(code, message);
    }

    /**
     * 关闭广告
     */
    private void onAdDismissedFullScreenContent() {
        Log.i(AdMobRewardInterstital.TAG, "reward interstital dismissed:placementID=" + this.placementID);
        this.onCallListenerClose();
        // preload next
        this.load();
    }
}
