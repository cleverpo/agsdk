package com.agedstudio.max.ad;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.agedstudio.base.ad.BaseBanner;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;
import com.agedstudio.max.MaxOnPaidEventUtils;
import com.agedstudio.max.R;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.DTBAdCallback;
import com.amazon.device.ads.DTBAdRequest;
import com.amazon.device.ads.DTBAdResponse;
import com.amazon.device.ads.DTBAdSize;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;

public class MaxBanner extends BaseBanner {

    private static final String TAG = MaxBanner.class.getSimpleName();

    /**
     * 广告实例
     */
    private MaxAdView ad;
    public MaxBanner(String placementID) {
        super(placementID);
    }

    @Override
    public void load() {
        LogUtil.i(TAG, "load:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == MaxBanner.this.ad) {
                    MaxBanner.this.ad = new MaxAdView(MaxBanner.this.placementID, MaxBanner.this.activity);
                    MaxBanner.this.ad.startAutoRefresh();
                    MaxBanner.this.ad.setListener(new MaxAdViewAdListener() {
                        @Override
                        public void onAdExpanded(MaxAd maxAd) {

                        }

                        @Override
                        public void onAdCollapsed(MaxAd maxAd) {

                        }

                        @Override
                        public void onAdLoaded(MaxAd maxAd) {
                            MaxBanner.this.onAdLoaded();
                        }

                        @Override
                        public void onAdDisplayed(MaxAd maxAd) {
                            MaxBanner.this.onAdImpression();
                        }

                        @Override
                        public void onAdHidden(MaxAd maxAd) {
                            MaxBanner.this.onAdClosed();
                        }

                        @Override
                        public void onAdClicked(MaxAd maxAd) {
                            MaxBanner.this.onAdClicked();
                        }

                        @Override
                        public void onAdLoadFailed(String s, MaxError maxError) {
                            LogUtil.i(TAG, maxError.getWaterfall().toString());
//                            LogUtil.i(TAG, maxError.getAdLoadFailureInfo());
                            LogUtil.i(TAG, maxError.toString());
                            MaxBanner.this.onAdFailedToLoad(maxError.getCode(), maxError.getMessage());
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {

                        }
                    });
                    MaxBanner.this.ad.setRevenueListener(new MaxAdRevenueListener() {
                        @Override
                        public void onAdRevenuePaid(MaxAd maxAd) {
                            MaxOnPaidEventUtils.onPaidEvent(1, MaxBanner.this.placementID, maxAd);
                        }
                    });
                }
                MaxBanner.this.cancelRetryCountdown();
                if (!MaxBanner.this.isLoading) {
                    MaxBanner.this.initAmazonAd();
                    MaxBanner.this.isLoading = true;
                }
            }
        });
    }

    @Override
    public void showWithPosition(String position) {
        LogUtil.i(TAG, "show:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MaxBanner.this.ad != null) {
                    int heightPx = MaxBanner.this.activity.getResources().getDimensionPixelSize(R.dimen.banner_height);
                    MaxBanner.this.adHeight = heightPx;

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx);
                    if ("TOP".equals(position)) {
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    } else {
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                    }

                    if (MaxBanner.this.ad.getParent() != null) {
                        ((ViewGroup)MaxBanner.this.ad.getParent()).removeView(MaxBanner.this.ad);
                    }
                    MaxBanner.this.activity.addContentView(MaxBanner.this.ad, layoutParams);
                    MaxBanner.this.ad.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void hide() {
        LogUtil.i(TAG, "hide:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MaxBanner.this.ad != null) {
                    MaxBanner.this.ad.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void reshow() {
        LogUtil.i(TAG, "reshow:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MaxBanner.this.ad != null) {
                    MaxBanner.this.ad.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void remove() {
        LogUtil.i(TAG, "remove:placementID=" + this.placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MaxBanner.this.ad != null && MaxBanner.this.ad.getParent() != null) {
                    ViewParent viewParent = MaxBanner.this.ad.getParent();
                    ((ViewGroup)viewParent).removeView(MaxBanner.this.ad);
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

    private void initAmazonAd(){
        String amazonAdSlotId = "";
        MaxAdFormat adFormat;
        if (AppLovinSdkUtils.isTablet(MaxBanner.this.activity))
        {
            try{
                amazonAdSlotId = this.mCustomParams.getString("amazonBannerTabletId");
            }catch (Exception e){
                e.printStackTrace();
            }
            adFormat = MaxAdFormat.LEADER;
        } else {
            try{
                amazonAdSlotId = this.mCustomParams.getString("amazonBannerId");
            }catch (Exception e){
                e.printStackTrace();
            }
            adFormat = MaxAdFormat.BANNER;
        }

        if(amazonAdSlotId.isEmpty()){
            MaxBanner.this.ad.loadAd();
            return;
        }

        AppLovinSdkUtils.Size rawSize = adFormat.getSize();
        DTBAdSize size = new DTBAdSize( rawSize.getWidth(), rawSize.getHeight(), amazonAdSlotId );

        DTBAdRequest adLoader = new DTBAdRequest();
        adLoader.setSizes( size );
        adLoader.loadAd( new DTBAdCallback()
        {
            @Override
            public void onSuccess(final DTBAdResponse dtbAdResponse)
            {
                // 'adView' is your instance of MaxAdView
                MaxBanner.this.ad.setLocalExtraParameter( "amazon_ad_response", dtbAdResponse );
                MaxBanner.this.ad.loadAd();
            }

            @Override
            public void onFailure(final AdError adError)
            {
                // 'adView' is your instance of MaxAdView
                MaxBanner.this.ad.setLocalExtraParameter( "amazon_ad_error", adError );
                MaxBanner.this.ad.loadAd();
            }
        } );
    }

    private void onAdClicked() {
        LogUtil.i(MaxBanner.TAG, "banner loaded:placementID=" + this.placementID);
        this.onCallListenerClick();
    }

    private void onAdClosed() {
        LogUtil.i(MaxBanner.TAG, "banner closed:placementID=" + this.placementID);
        this.onCallListenerClose();
    }

    private void onAdFailedToLoad(int code, String message) {
        LogUtil.i(MaxBanner.TAG, "banner failed to load:placementID=" + this.placementID + ",code=" + code + ",message=" + message);
        this.isLoading = false;
        this.isAdReady = false;
        this.onCallListenerLoadFail(code, message);
        // retry
        // 30秒后重新加载
        this.restryCountdown = 30;
    }

    private void onAdImpression() {
        LogUtil.i(MaxBanner.TAG, "banner impression:placementID=" + this.placementID);
        this.onCallListenerShow();
    }

    private void onAdLoaded() {
        LogUtil.i(MaxBanner.TAG, "banner loaded:placementID=" + this.placementID);
        this.isLoading = false;
        this.isAdReady = true;
        this.onCallListenerLoaded();
    }

    private void onAdOpened() {
        LogUtil.i(MaxBanner.TAG, "banner opened:placementID=" + this.placementID);
        this.onCallListenerShow();
    }

//    private AppLovinSdkUtils.Size getAdSize() {
//        if (AppLovinSdkUtils.isTablet(this.activity)) {
//            return MaxAdFormat.LEADER.getSize();
//        } else {
//            return MaxAdFormat.BANNER.getSize();
//        }
//    }
}
