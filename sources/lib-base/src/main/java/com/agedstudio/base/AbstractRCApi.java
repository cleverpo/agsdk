package com.agedstudio.base;

import android.content.Context;

public abstract class AbstractRCApi {

    protected Context mContext = null;

    public void init(Context context) {
        mContext = context;
    }

    /**
     * 是否存在key
     * @param key
     * @return
     */
    public abstract boolean isExists(String key);

    /**
     * 重置所有的数据
     */
    public abstract void reset();

    /**
     * 获取远程配置
     * @param type 1:boolean 2:double 3:long 4:string
     * @return
     */
    public abstract String getConfig(String key, int type);

    /**
     *  获取并激活远程配置
     */
    public abstract void fetchAndActivate(OnCompleteListener onCompleteListener);

    public interface OnCompleteListener {
        void onComplete(boolean bool);
    }
}

