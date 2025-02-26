package com.agedstudio.libsdk.service;

import android.content.Context;

import com.agedstudio.base.AbstractRDBApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class FirebaseRDBService extends AgedStudioSDKClass {
    private static final String TAG = FirebaseRDBService.class.getSimpleName();
    private static FirebaseRDBService mIns = null;
    private AbstractRDBApi mRDBImp = null;

    public void init(Context context, AbstractRDBApi imp) {
        super.init(context);
        LogUtil.d(TAG, "init");
        FirebaseRDBService.mIns = this;
        FirebaseRDBService.mIns.mRDBImp = imp;
    }

    public static void getData(String jsMethodKey, String dbName, String id) {
        LogUtil.i(TAG, "get data:" + " dbName=" + dbName + "   id=" + id);
        if (null == FirebaseRDBService.mIns || null == FirebaseRDBService.mIns.getContext() || null == FirebaseRDBService.mIns.mRDBImp) {
            FirebaseRDBService.onGetDataFail(jsMethodKey, -1, "error");
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FirebaseRDBService.mIns.mRDBImp.getData(jsMethodKey, dbName, id, new AbstractRDBApi.OnGetDataListener() {
                    @Override
                    public void onGetDataFail(String jsMethodKey, int code, String message) {
                        FirebaseRDBService.onGetDataFail(jsMethodKey, code, message);
                    }

                    @Override
                    public void onGetDataSuccess(String jsMethodKey, String jsonData) {
                        FirebaseRDBService.onGetDataSuccess(jsMethodKey, jsonData);
                    }
                });
            }
        });
    }

    public static void setData(String jsMethodKey, String dbName, String id, String data) {
        LogUtil.i(TAG, "set data");
        if (null == FirebaseRDBService.mIns || null == FirebaseRDBService.mIns.getContext() || null ==FirebaseRDBService.mIns.mRDBImp) {
            return;
        }

        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FirebaseRDBService.mIns.mRDBImp.setData(jsMethodKey, dbName, id, data, new AbstractRDBApi.OnSetDataListener() {
                    @Override
                    public void onSetDataFail(String jsMethodKey, int code, String message) {
                        FirebaseRDBService.onSetDataFail(jsMethodKey, code, message);
                    }

                    @Override
                    public void onSetDataSuccess(String jsMethodKey) {
                        FirebaseRDBService.onSetDataSuccess(jsMethodKey);
                    }
                });
            }
        });
    }

    private static void onGetDataSuccess(String jsMethodKey, String jsonData) {
        LogUtil.i(TAG, "get data success");
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("methodKey", jsMethodKey);
                jsonObject.put("data", jsonData);
                CommonUtil.eventCallback(AGEvents.OnGetDataSuccess + "('" + jsonObject.toJSONString() + "')");
            }
        });
    }

    private static void onGetDataFail(String jsMethodKey, int code, String message) {
        LogUtil.i(TAG, "get data fail");
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("methodKey", jsMethodKey);
                jsonObject.put("code", code);
                jsonObject.put("message", message);
                CommonUtil.eventCallback(AGEvents.OnGetDataFail + "('" + jsonObject.toJSONString() + "')");
            }
        });
    }

    private static void onSetDataSuccess(String jsMethodKey) {
        LogUtil.i(TAG, "set data success");
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("methodKey", jsMethodKey);
                CommonUtil.eventCallback(AGEvents.OnSetDataSuccess + "('" + jsonObject.toJSONString() + "')");
            }
        });
    }

    private static void onSetDataFail(String jsMethodKey, int code, String message) {
        LogUtil.i(TAG, "set data fail");
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("methodKey", jsMethodKey);
                jsonObject.put("code", code);
                jsonObject.put("message", message);
                CommonUtil.eventCallback(AGEvents.OnSetDataFail + "('" + jsonObject.toJSONString() + "')");
            }
        });
    }
}
