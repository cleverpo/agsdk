package com.agedstudio.max;


import com.agedstudio.base.AbstractInterstitalApi;
import com.agedstudio.base.ad.BaseInterstital;
import com.agedstudio.max.ad.MaxInterstitial;

public class MaxInterstitalBusiness extends AbstractInterstitalApi {

    @Override
    public BaseInterstital initAd(String placementID, String params) {
        BaseInterstital ad = this.getAd(placementID);
        if (ad == null) {
            ad = new MaxInterstitial(placementID);
            ad.init(params);
            this.mAds.put(placementID, ad);
        }
        return ad;
    }
}
