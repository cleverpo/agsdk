package com.agedstudio.libsdk.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.agedstudio.base.AbstractBannerApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.ad.BaseBanner;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.AdUtil;
import com.agedstudio.base.utils.CommonUtil;

public class BannerService extends AgedStudioSDKClass {

    private static final String TAG = BannerService.class.getSimpleName();


    private static BannerService mIns = null;
    private AbstractBannerApi mAdMob = null;
    private AbstractBannerApi mMax = null;
    private AbstractBannerApi mYandex = null;

    /**
     * 1:admon 2:yandex 3:amazon
     */
    private int mMediation = 3;

    @Override
    public void init(Context context){
        super.init(context);
        BannerService.mIns = this;
    }

    public void initAdMob(AbstractBannerApi adSource) {
        this.mAdMob = adSource;
    }

    public void initMax(AbstractBannerApi adSource) {
        this.mMax = adSource;
    }

    public void initYandex(AbstractBannerApi adSource) {
        this.mYandex = adSource;
    }

    public static BannerService getIns() {
        return BannerService.mIns;
    }

    /**
     * 获取广告
     * @param placementID
     * @return
     */
    private BaseBanner getAd(String placementID) {
        if (BannerService.mIns.mMediation == 1) {
            return BannerService.mIns.mAdMob.getAd(placementID);
        } else if (BannerService.mIns.mMediation == 2) {
            return BannerService.mIns.mYandex.getAd(placementID);
        } else if (BannerService.mIns.mMediation == 3) {
            return BannerService.mIns.mMax.getAd(placementID);
        }
        return null;
    }

    public static void initAd(String placementID, String params) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseBanner ad = BannerService.mIns.getAd(placementID);
                if (ad == null) {
                    if (BannerService.mIns.mMediation == 1) {
                        ad = BannerService.mIns.mAdMob.initAd(placementID, params);
                    } else if (BannerService.mIns.mMediation == 2) {
                        ad = BannerService.mIns.mYandex.initAd(placementID, params);
                    } else if (BannerService.mIns.mMediation == 3) {
                        ad = BannerService.mIns.mMax.initAd(placementID, params);
                    }
                    if (null != ad) {
                        ad.setLoadListener(new BaseBanner.LoadListener() {
                            @Override
                            public void onLoaded() {
                                AdUtil.onCallback(AGEvents.OnBannerLoaded, placementID);
                            }

                            @Override
                            public void onLoadFail(int errCode, String errMsg) {
                                AdUtil.onCallback(AGEvents.OnBannerLoadFail, placementID, errCode, errMsg);
                            }
                        });
                        ad.setShowListener(new BaseBanner.ShowListener() {
                            @Override
                            public void onShow() {
                                AdUtil.onCallback(AGEvents.OnBannerShow, placementID);
                            }

                            @Override
                            public void onClose() {
                                AdUtil.onCallback(AGEvents.OnBannerClose, placementID);
                            }

                            @Override
                            public void onClick() {
                                AdUtil.onCallback(AGEvents.OnBannerClick, placementID);
                            }
                        });
                    }
                }
            }
        });

    }

    public static void load(String placementID) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseBanner ad = BannerService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.load();
                }
            }
        });
    }

    public static void showWithPosition(String placementID, String position) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseBanner ad = BannerService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.showWithPosition(position);
                }
            }
        });
    }

    public static void hide(String placementID) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseBanner ad = BannerService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.hide();
                }
            }
        });
    }

    public static void reshow(String placementID) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseBanner ad = BannerService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.reshow();
                }
            }
        });
    }

    public static void remove(String placementID) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseBanner ad = BannerService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.remove();
                }
            }
        });
    }

    public static boolean isReady(String placementID) {
        BaseBanner ad = BannerService.mIns.getAd(placementID);
        if (ad != null) {
            return ad.isReady();
        }
        return false;
    }

    /**
     * 刷新
     * @param dt
     */
    public static void update(String placementID, float dt) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseBanner ad = BannerService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.update(dt);
                }
            }
        });
    }

    public static void setMediation(int type) {
        BannerService.mIns.mMediation = type;
    }

    public static boolean isTablet() {
        if (BannerService.mIns.mMediation == 1) {
            return BannerService.mIns.mAdMob.isTablet();
        } else if (BannerService.mIns.mMediation == 2) {
            return BannerService.mIns.mYandex.isTablet();
        } else if (BannerService.mIns.mMediation == 3) {
            return BannerService.mIns.mMax.isTablet();
        }
        return false;
    }

    public static int getAdHeight() {
        int adHeight = 0;
        if (null != BannerService.mIns.mAdMob) {
            adHeight = Math.max(adHeight, BannerService.mIns.mAdMob.getAdHeight());
        }
        if (null != BannerService.mIns.mYandex) {
            adHeight = Math.max(adHeight, BannerService.mIns.mYandex.getAdHeight());
        }
        if (null != BannerService.mIns.mMax) {
            adHeight = Math.max(adHeight, BannerService.mIns.mMax.getAdHeight());
        }
        return adHeight;
    }
}
