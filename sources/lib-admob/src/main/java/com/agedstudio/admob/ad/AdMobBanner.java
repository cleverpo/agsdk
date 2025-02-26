package com.agedstudio.admob.ad;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.agedstudio.admob.AdMobOnPaidEventUtils;
import com.agedstudio.base.ad.BaseAd;
import com.agedstudio.base.ad.BaseBanner;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.SysUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;

public class AdMobBanner extends BaseBanner {

    private static final String TAG = AdMobBanner.class.getSimpleName();

    /**
     * 广告实例
     */
    private AdView ad;


    public AdMobBanner(String placementID) {
        super(placementID);
    }

    @Override
    public void load() {
        Log.i(TAG, "load:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == AdMobBanner.this.ad) {
                    AdSize adSize = AdMobBanner.this.getAdSize();
                    AdMobBanner.this.adHeight = adSize.getHeightInPixels(AdMobBanner.this.activity);
                    AdMobBanner.this.ad = new AdView(AdMobBanner.this.activity);
                    AdMobBanner.this.ad.setAdUnitId(AdMobBanner.this.placementID);
                    AdMobBanner.this.ad.setAdSize(adSize);
                    AdMobBanner.this.ad.setAdListener(new AdListener() {
                        @Override
                        public void onAdClicked() {
                            AdMobBanner.this.onAdClicked();
                        }

                        @Override
                        public void onAdClosed() {
                            AdMobBanner.this.onAdClosed();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.i(TAG, loadAdError.toString());
                            AdMobBanner.this.onAdFailedToLoad(loadAdError.getCode(), loadAdError.getMessage());
                        }

                        @Override
                        public void onAdImpression() {
                            AdMobBanner.this.onAdImpression();
                        }

                        @Override
                        public void onAdLoaded() {
                            AdMobBanner.this.onAdLoaded();
                        }

                        @Override
                        public void onAdOpened() {
                            AdMobBanner.this.onAdOpened();
                        }
                    });
                }

                AdMobBanner.this.ad.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(@NonNull AdValue adValue) {
                        AdMobOnPaidEventUtils.onPaidEvent(1, AdMobBanner.this.placementID, ad.getResponseInfo(), adValue);
                    }
                });


                AdMobBanner.this.cancelRetryCountdown();
                if (!AdMobBanner.this.isLoading) {
                    AdMobBanner.this.isLoading = true;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    AdMobBanner.this.ad.loadAd(adRequest);
                }
            }
        });
    }

    @Override
    public void showWithPosition(String position) {
        Log.i(TAG, "show:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AdMobBanner.this.ad != null) {
                    AdSize adSize = AdMobBanner.this.getAdSize();
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adSize.getWidthInPixels(AdMobBanner.this.activity), adSize.getHeightInPixels(AdMobBanner.this.activity));
                    if ("TOP".equals(position)) {
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    } else {
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                    }

                    if (AdMobBanner.this.ad.getParent() != null) {
                        ((ViewGroup) AdMobBanner.this.ad.getParent()).removeView(AdMobBanner.this.ad);
                    }
                    AdMobBanner.this.activity.addContentView(AdMobBanner.this.ad, layoutParams);
                    AdMobBanner.this.ad.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void hide() {
        Log.i(TAG, "hide:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AdMobBanner.this.ad != null) {
                    AdMobBanner.this.ad.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void reshow() {
        Log.i(TAG, "reshow:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AdMobBanner.this.ad != null) {
                    AdMobBanner.this.ad.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void remove() {
        Log.i(TAG, "remove:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AdMobBanner.this.ad != null && AdMobBanner.this.ad.getParent() != null) {
                    ViewParent viewParent = AdMobBanner.this.ad.getParent();
                    ((ViewGroup)viewParent).removeView(AdMobBanner.this.ad);
                }
            }
        });
    }

    @Override
    public boolean isReady () {
        return this.ad != null && this.isAdReady;
    }

    @Override
    public int getAdHeight() {
        return (int)this.adHeight;
    }

    private void onAdClicked() {
        Log.i(AdMobBanner.TAG, "banner loaded:placementID=" + this.placementID);
        this.onCallListenerClick();
    }

    private void onAdClosed() {
        Log.i(AdMobBanner.TAG, "banner closed:placementID=" + this.placementID);
        this.onCallListenerClose();
    }

    private void onAdFailedToLoad(int code, String message) {
        Log.i(AdMobBanner.TAG, "banner failed to load:placementID=" + this.placementID + ",code=" + code + ",message=" + message);
        this.isLoading = false;
        this.isAdReady = false;
        this.onCallListenerLoadFail(BaseAd.FAILED_TO_LOAD, message);
        // retry
        // 30秒后重新加载
        this.restryCountdown = 30;
    }

    private void onAdImpression() {
        Log.i(AdMobBanner.TAG, "banner impression:placementID=" + this.placementID);
    }

    private void onAdLoaded() {
        Log.i(AdMobBanner.TAG, "banner loaded:placementID=" + this.placementID);
        this.isLoading = false;
        this.isAdReady = true;
        this.onCallListenerLoaded();
    }

    private void onAdOpened() {
        Log.i(AdMobBanner.TAG, "banner opened:placementID=" + this.placementID);
        this.onCallListenerShow();
    }

    private AdSize getAdSize() {
        int adWidth = SysUtils.getWindowWidth();
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this.activity, adWidth);
    }
}
