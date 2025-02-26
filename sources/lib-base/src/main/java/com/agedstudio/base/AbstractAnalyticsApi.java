package com.agedstudio.base;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;


public abstract class AbstractAnalyticsApi {

    protected Context mContext;

    public void init(Context context) {
        mContext = context;
    }

    public abstract void onEvent(String name, String jsonStr);

    public abstract void onEvent(String name, Bundle bundle);

    public abstract void onMaxAdRevenueEvent(Map<String, Object> params);

    public abstract void onGoogleBillingEvent(Map<String, Object> params);

    public abstract void updateConsent();

    public abstract void updateConsentNotRequired();

    public abstract void reportException(String location, String message, String stack);
}
