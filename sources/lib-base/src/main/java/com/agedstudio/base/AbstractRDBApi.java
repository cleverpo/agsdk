package com.agedstudio.base;

import android.content.Context;

public abstract class AbstractRDBApi {

    protected Context mContext;

    public void init(Context context) {
        mContext = context;
    }

    public abstract void getData(String jsMethodKey, String dbName, String id, OnGetDataListener listener);

    public abstract void setData(String jsMethodKey, String dbName, String id, String data, OnSetDataListener listener);


    public interface OnGetDataListener {
        void onGetDataFail(String jsMethodKey, int code, String message);
        void onGetDataSuccess(String jsMethodKey, String jsonData);
    }

    public interface OnSetDataListener {
        void onSetDataFail(String jsMethodKey, int code, String message);
        void onSetDataSuccess(String jsMethodKey);
    }
}
