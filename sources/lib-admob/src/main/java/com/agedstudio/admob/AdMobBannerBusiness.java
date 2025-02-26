package com.agedstudio.admob;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;


import com.agedstudio.admob.ad.AdMobBanner;
import com.agedstudio.base.AbstractBannerApi;
import com.agedstudio.base.ad.BaseBanner;
import com.agedstudio.base.utils.SysUtils;
import com.google.android.gms.ads.AdSize;

public class AdMobBannerBusiness extends AbstractBannerApi {

    private static final String TAG = AdMobBannerBusiness.class.getSimpleName();

    @Override
    public BaseBanner initAd(String placementID, String params) {
        BaseBanner ad = this.getAd(placementID);
        if (ad == null) {
            ad = new AdMobBanner(placementID);
            ad.init(params);
            this.mAds.put(placementID, ad);
        }
        return ad;
    }

    @Override
    public int getAdHeight() {
        Activity activity = (Activity) mContext;
        int adWidth = SysUtils.getWindowWidth();
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
        return adSize.getHeightInPixels(activity);
    }

    @Override
    public boolean isTablet() {
        return false;
    }
}
