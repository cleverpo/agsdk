package com.agedstudio.base;

import android.content.Context;

import com.agedstudio.base.ad.BaseRewardVideo;

import java.util.Hashtable;

public abstract class AbstractRewardVideoApi {

    protected Context mContext = null;

    protected final Hashtable<String, BaseRewardVideo> mAds = new Hashtable<String, BaseRewardVideo>();

    public void init(Context context) {
        mContext = context;
    }

    public abstract BaseRewardVideo initAd(String placementID, String params);

    public void load(String placementID) {
        BaseRewardVideo ad = this.getAd(placementID);
        if (ad != null) {
            ad.load();
        }
    }

    public void show(String placementID, String placement) {
        BaseRewardVideo ad = this.getAd(placementID);
        if (ad != null) {
            ad.show(placement);
        }
    }

    public boolean isReady(String placementID) {
        BaseRewardVideo ad = this.getAd(placementID);
        if (ad != null) {
            return ad.isReady();
        }
        return false;
    }

    public BaseRewardVideo getAd(String placementID) {
        if (this.mAds.containsKey(placementID)) {
            return this.mAds.get(placementID);
        }
        return null;
    }

    public void update(String placementID, float dt) {
        BaseRewardVideo ad = this.getAd(placementID);
        if (ad != null) {
            ad.update(dt);
        }
    }
}
