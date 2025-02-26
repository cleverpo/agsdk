package com.agedstudio.admob;

import com.agedstudio.admob.ad.AdMobInterstitial;
import com.agedstudio.base.AbstractInterstitalApi;
import com.agedstudio.base.ad.BaseInterstital;

public class AdMobInterstitalBusiness extends AbstractInterstitalApi {

    public BaseInterstital initAd(String placementID, String params) {
        BaseInterstital ad = this.getAd(placementID);
        if (ad == null) {
            ad = new AdMobInterstitial(placementID);
            ad.init(params);
            this.mAds.put(placementID, ad);
        }
        return ad;
    }
}
