package com.agedstudio.admob;


import com.agedstudio.admob.ad.AdMobRewardVideo;
import com.agedstudio.base.AbstractRewardVideoApi;
import com.agedstudio.base.ad.BaseRewardVideo;

public class AdMobRewardVideoBusiness extends AbstractRewardVideoApi {

    @Override
    public BaseRewardVideo initAd(String placementID, String params) {
        BaseRewardVideo ad = this.getAd(placementID);
        if (ad == null) {
            ad = new AdMobRewardVideo(placementID);
            ad.init(params);
            this.mAds.put(placementID, ad);
        }
        return ad;
    }
}
