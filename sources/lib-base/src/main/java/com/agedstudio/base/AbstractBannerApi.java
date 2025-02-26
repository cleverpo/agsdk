package com.agedstudio.base;

import android.content.Context;
import com.agedstudio.base.ad.BaseBanner;

import java.util.Hashtable;

public abstract class AbstractBannerApi {

    protected Context mContext = null;

    protected final Hashtable<String, BaseBanner> mAds = new Hashtable<String, BaseBanner>();

    public void init(Context context) {
        mContext = context;
    }

    public abstract BaseBanner initAd(String placementID, String params);

    public void load(String placementID) {
        BaseBanner ad = this.getAd(placementID);
        if (ad != null) {
            ad.load();
        }
    }

    public void showWithPosition(String placementID, String position) {
        BaseBanner ad = this.getAd(placementID);
        if (ad != null) {
            ad.showWithPosition(position);
        }
    }

    public void hide(String placementID) {
        BaseBanner ad = this.getAd(placementID);
        if (ad != null) {
            ad.hide();
        }
    }

    public void reshow(String placementID) {
        BaseBanner ad = this.getAd(placementID);
        if (ad != null) {
            ad.reshow();
        }
    }

    public void remove(String placementID) {
        BaseBanner ad = this.getAd(placementID);
        if (ad != null) {
            ad.remove();
            this.mAds.remove(placementID);
        }
    }

    public boolean isReady(String placementID) {
        BaseBanner ad = this.getAd(placementID);
        if (ad != null) {
            return ad.isReady();
        }
        return false;
    }

    public abstract int getAdHeight();

    public BaseBanner getAd(String placementID) {
        if (this.mAds.containsKey(placementID)) {
            return this.mAds.get(placementID);
        }
        return null;
    }

    public void update(String placementID, float dt) {
        BaseBanner ad = this.getAd(placementID);
        if (ad != null) {
            ad.update(dt);
        }
    }

    public abstract boolean isTablet();
}
