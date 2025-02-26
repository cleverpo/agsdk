package com.agedstudio.admob.ad;

import android.util.Log;

import androidx.annotation.NonNull;

import com.agedstudio.admob.AdMobOnPaidEventUtils;
import com.agedstudio.base.ad.BaseInterstital;
import com.agedstudio.base.utils.CommonUtil;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdMobInterstitial extends BaseInterstital {

    private static final String TAG = AdMobInterstitial.class.getSimpleName();

    /**
     * 广告实例
     */
    private InterstitialAd ad;

    public AdMobInterstitial(String placementID) {
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
                if (null == AdMobInterstitial.this.ad) {
                    AdMobInterstitial.this.cancelRetryCountdown();;
                    AdMobInterstitial.this.isLoading = true;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(AdMobInterstitial.this.activity, AdMobInterstitial.this.placementID, adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            AdMobInterstitial.this.onAdFailedToLoad(loadAdError.getCode(), loadAdError.getMessage());
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            AdMobInterstitial.this.onAdLoaded(interstitialAd);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void show(String placement) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!AdMobInterstitial.this.isReady()) {
                    AdMobInterstitial.this.onCallListenerFailedToShow(WAS_NOT_READY_YET, "");
                    return;
                }
                AdMobInterstitial.this.ad.show(AdMobInterstitial.this.activity);
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
        Log.i(AdMobInterstitial.TAG, "interstitial failed to load:placementID=" + this.placementID + ",code=" + code + ",message=" + message);
        this.ad = null;
        this.isLoading = false;
        this.onCallListenerLoadFail(code, message);
        // retry
        // 10秒后重新加载
        this.restryCountdown = 10;
    }

    /**
     * 加载失败
     * @param interstitialAd
     */
    private void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
        Log.i(AdMobInterstitial.TAG, "interstitial loaded:placementID=" + this.placementID);
        this.ad = interstitialAd;
        this.isLoading = false;
        this.ad.setOnPaidEventListener(new OnPaidEventListener() {
            @Override
            public void onPaidEvent(@NonNull AdValue adValue) {
                AdMobOnPaidEventUtils.onPaidEvent(2, AdMobInterstitial.this.placementID, interstitialAd.getResponseInfo(), adValue);
            }
        });
        this.ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                AdMobInterstitial.this.onAdClicked();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                AdMobInterstitial.this.onAdDismissedFullScreenContent();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                AdMobInterstitial.this.onAdFailedToShowFullScreenContent(adError.getCode(), adError.getMessage());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                AdMobInterstitial.this.onAdShowedFullScreenContent();
            }
        });
        this.onCallListenerLoaded();
    }

    /**
     * 点击广告
     */
    private void onAdClicked() {
        Log.i(AdMobInterstitial.TAG, "interstitial clicked:placementID=" + this.placementID);
        this.onCallListenerClick();
    }

    /**
     * 展示成功
     */
    private void onAdShowedFullScreenContent() {
        Log.i(AdMobInterstitial.TAG, "interstitial showed:placementID=" + this.placementID);
        this.ad = null;
        this.onCallListenerShow();
        // preload next
        this.load();
    }

    /**
     * 展示失败
     */
    private void onAdFailedToShowFullScreenContent(int code, String message) {
        Log.i(AdMobInterstitial.TAG, "interstitial failed to show:placementID=" + this.placementID);
        this.onCallListenerFailedToShow(code, message);
    }

    /**
     * 关闭广告
     */
    private void onAdDismissedFullScreenContent() {
        Log.i(AdMobInterstitial.TAG, "interstitial dismissed:placementID=" + this.placementID);
        this.onCallListenerClose();
    }
}
