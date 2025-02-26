package com.agedstudio.base;

import android.content.Context;

import com.agedstudio.base.ad.BaseInterstital;

import java.util.Hashtable;

public abstract class AbstractInterstitalApi {

    protected Context mContext = null;

    protected static final Hashtable<String, BaseInterstital> mAds = new Hashtable<String, BaseInterstital>();

    public void init(Context context) {
        mContext = context;
    }

    public abstract BaseInterstital initAd(String placementID, String params);

    public void load(String placementID) {
        BaseInterstital ad = this.getAd(placementID);
        if (ad != null) {
            ad.load();
        }
    }

    public void show(String placementID, String placement) {
        BaseInterstital ad = this.getAd(placementID);
        if (ad != null) {
            ad.show(placement);
        }
    }
   
    public boolean isReady(String placementID) {
        BaseInterstital ad = this.getAd(placementID);
        if (ad != null) {
            return ad.isReady();
        }
        return false;
    }

    public BaseInterstital getAd(String placementID) {
        if (this.mAds.containsKey((placementID))) {
            return this.mAds.get(placementID);
        }
        return null;
    }

    public void update(String placementID, float dt) {
        BaseInterstital ad = this.getAd(placementID);
        if (ad != null) {
            ad.update(dt);
        }
    }
}
