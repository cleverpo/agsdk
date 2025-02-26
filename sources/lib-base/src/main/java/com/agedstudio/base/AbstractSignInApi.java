package com.agedstudio.base;

import android.content.Context;
import android.content.Intent;

public abstract class AbstractSignInApi {

    protected Context mContext = null;
    protected OnCompleteListener mCompleteListener = null;

    public void init(Context context) {
        mContext = context;
    }

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void signIn(OnCompleteListener listener);

    public abstract void signOut();

    public interface OnCompleteListener {
        void onComplete(boolean success, String msg);
    }
}
