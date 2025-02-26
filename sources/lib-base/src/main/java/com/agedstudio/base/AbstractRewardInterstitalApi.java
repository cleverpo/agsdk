package com.agedstudio.base;

import android.content.Context;

import com.agedstudio.base.ad.BaseRewardInterstital;

import java.util.Hashtable;

public abstract class AbstractRewardInterstitalApi {

    protected Context mContext = null;

    protected final Hashtable<String, BaseRewardInterstital> mAds = new Hashtable<String, BaseRewardInterstital>();

    public void init(Context context) {
        mContext = context;
    }

    public abstract BaseRewardInterstital initAd(String placementID, String params);

    public void load(String placementID) {
        BaseRewardInterstital ad = this.getAd(placementID);
        if (ad != null) {
            ad.load();
        }
    }

    public void show(String placementID, String placement) {
        BaseRewardInterstital ad = this.getAd(placementID);
        if (ad != null) {
            ad.show(placement);
        }
    }

    public boolean isReady(String placementID) {
        BaseRewardInterstital ad = this.getAd(placementID);
        if (ad != null) {
            return ad.isReady();
        }
        return false;
    }

    public BaseRewardInterstital getAd(String placementID) {
        if (this.mAds.containsKey(placementID)) {
            return this.mAds.get(placementID);
        }
        return null;
    }

    public void update(String placementID, float dt) {
        BaseRewardInterstital ad = this.getAd(placementID);
        if (ad != null) {
            ad.update(dt);
        }
    }
}
