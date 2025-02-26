package com.agedstudio.max;


import com.agedstudio.base.AbstractRewardVideoApi;
import com.agedstudio.base.ad.BaseRewardVideo;
import com.agedstudio.max.ad.MaxRewardVideo;

public class MaxRewardVideoBusiness extends AbstractRewardVideoApi {

    @Override
    public BaseRewardVideo initAd(String placementID, String params) {
        BaseRewardVideo ad = this.getAd(placementID);
        if (ad == null) {
            ad = new MaxRewardVideo(placementID);
            ad.init(params);
            this.mAds.put(placementID, ad);
        }
        return ad;
    }
}
