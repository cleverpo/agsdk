package com.agedstudio.max;

import com.agedstudio.base.AbstractBannerApi;
import com.agedstudio.base.ad.BaseBanner;
import com.agedstudio.max.ad.MaxBanner;
import com.applovin.sdk.AppLovinSdkUtils;

public class MaxBannerBusiness extends AbstractBannerApi {

    private static final String TAG = MaxBannerBusiness.class.getSimpleName();

    @Override
    public BaseBanner initAd(String placementID, String params) {
        BaseBanner ad = this.getAd(placementID);
        if (ad == null) {
            ad = new MaxBanner(placementID);
            ad.init(params);
            this.mAds.put(placementID, ad);
        }
        return ad;
    }

    @Override
    public int getAdHeight() {
//        if(this.mAds.size() > 0){
//            BaseBanner banner = this.mAds.elements().nextElement();
//            return banner.getAdHeight();
//        }
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.banner_height);
//        if (AppLovinSdkUtils.isTablet(this.mContext)) {
//            return 90;
//        } else {
//            return 50;
//        }
    }

    @Override
    public boolean isTablet() {
        return AppLovinSdkUtils.isTablet(this.mContext);
    }
}
