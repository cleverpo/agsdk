package com.agedstudio.firebase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.agedstudio.base.AbstractRCApi;
import com.agedstudio.base.utils.LogUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Set;

public class FirebaseRCBusiness extends AbstractRCApi {

    private static final String TAG = FirebaseRCBusiness.class.getSimpleName();

    @Override
    public void init(Context context) {
        super.init(context);
        LogUtil.i(TAG, "init FirebaseRCBusiness");
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(3600)
                .build();
        FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(configSettings);
    }

    /**
     * 是否存在key
     * @param key
     * @return
     */
    public boolean isExists(String key) {
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        Set<String> keys = firebaseRemoteConfig.getKeysByPrefix(key);
        return keys.contains(key);
    }

    /**
     * 重置所有的数据
     */
    public void reset() {
        FirebaseRemoteConfig.getInstance().reset();
    }

    @Override
    public String getConfig(String key, int type) {
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        Set<String> keys = firebaseRemoteConfig.getKeysByPrefix(key);
        if (!keys.contains(key)) {
            return "";
        }
        JSONObject obj = new JSONObject();
        obj.put("key", key);
        if (type == 1) {
            boolean value = firebaseRemoteConfig.getBoolean(key);
            obj.put("value", value);
        } else if (type == 2) {
            double value = firebaseRemoteConfig.getDouble(key);
            obj.put("value", value);
        } else if (type == 3) {
            long value = firebaseRemoteConfig.getLong(key);
            obj.put("value", value);
        } else if (type == 4) {
            String value = firebaseRemoteConfig.getString(key);
            obj.put("value", value);
        }
        return obj.toJSONString();
    }

    @Override
    public void fetchAndActivate(AbstractRCApi.OnCompleteListener onCompleteListener) {
        LogUtil.i(TAG, "fetchAndActivate");
        FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener((Activity) this.mContext, new com.google.android.gms.tasks.OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                LogUtil.i(TAG, "fetchAndActivate, is successful = " + task.isSuccessful());
                if (null != onCompleteListener) {
                    onCompleteListener.onComplete(task.isSuccessful());
                }
            }
        });
    }
}
