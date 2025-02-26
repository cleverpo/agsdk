package com.agedstudio.base.ad;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BaseAd {

    private static final String TAG = BaseAd.class.getSimpleName();

    private JSONObject callbackJsonObj;

    private String callbackJsonStr;

    /**
     * 上下文
     */
    protected Activity activity;
    /**
     * 广告位id
     */
    protected String placementID;

    private static HashMap<Integer, String> errors = new HashMap<Integer, String>();

    protected static Integer OK = 0;
    protected static Integer FAILED_TO_LOAD = -1;
    protected static Integer FAILED_TO_SHOW= -2;
    protected static Integer WAS_NOT_READY_YET = -3;
    protected static Integer LOADING = -4;

    protected String mCustomParamsStr;
    protected JSONObject mCustomParams;

    static {
        BaseAd.errors.put(BaseAd.OK, "ok");
        BaseAd.errors.put(BaseAd.FAILED_TO_LOAD, "The ad failed to load.");
        BaseAd.errors.put(BaseAd.FAILED_TO_SHOW, "The ad failed to show.");
        BaseAd.errors.put(BaseAd.WAS_NOT_READY_YET, "The ad wasn't ready yet.");
        BaseAd.errors.put(BaseAd.LOADING, "The ad is loading.");
    }

    public void init(String customParamsStr) {
        this.setCustomParams(customParamsStr);
    }

    protected void setCustomParams(String jsonStr) {
        if (!TextUtils.equals(this.mCustomParamsStr, jsonStr) && !TextUtils.isEmpty(jsonStr)) {
            try {
                this.mCustomParamsStr = jsonStr;
                this.mCustomParams = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(float dt) {}
}
