package com.agedstudio.ggsignin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.agedstudio.base.AbstractSignInApi;
import com.agedstudio.base.utils.NetworkUtils;
import com.cocos.lib.CocosActivity;
import com.cocos.lib.GlobalObject;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

public class GoogleSignInBusiness extends AbstractSignInApi {

    private static final String TAG = GoogleSignInBusiness.class.getSimpleName();

    private static GoogleSignInBusiness ins = null;

    private GoogleSignInClient googleSignInClient = null;

    private static final int RC_SIGN_IN = 100000;

    public void init(Context context) {
        super.init(context);
        Log.d(TAG, "init");
        GoogleSignInBusiness.ins = this;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == GoogleSignInBusiness.ins) {
            return;
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            this.handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInBusiness.ins.onSignInSuccess(account);
        } catch (ApiException e) {
            e.printStackTrace();
            GoogleSignInBusiness.ins.onSignInFail(e.getStatusCode(), e.getMessage());
        }
    }

    public void signIn(AbstractSignInApi.OnCompleteListener listener) {
        Log.i(TAG, "signIn");
        mCompleteListener = listener;
        CocosActivity activity = (CocosActivity) mContext;
        NetworkUtils.isAvailableAsync(new NetworkUtils.OnNetworkListener() {
            @Override
            public void OnAccept(Boolean bool) {
                if (!bool) {
                    GoogleSignInBusiness.ins.onSignInFail(-1, "network error");
                } else {
                    GlobalObject.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
                            if (account != null) {
                                GoogleSignInBusiness.ins.onSignInSuccess(account);
                            } else {
                                Log.i(TAG, "signIn");
                                Intent signInIntent = GoogleSignInBusiness.ins.googleSignInClient.getSignInIntent();
                                activity.startActivityForResult(signInIntent, RC_SIGN_IN);
                            }
                        }
                    });
                }
            }
        });
    }

    public void signOut() {
        Log.i(TAG, "signOut");
        CocosActivity activity = (CocosActivity) mContext;
        GlobalObject.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
                if (account != null) {
                    googleSignInClient.signOut();
                }
            }
        });
    }

    public void onSignInSuccess(GoogleSignInAccount account) {

        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("id", "gp_" + account.getId());
        jsonObject.put("name", account.getDisplayName());
        if (account.getPhotoUrl() != null) {
            jsonObject.put("avatarUrl", account.getPhotoUrl().toString().toString());
        } else {
            jsonObject.put("avatarUrl", "");
        }
        String msg = jsonObject.toJSONString();

        Log.i(TAG, "onSignInSuccess msg=" + msg);
        if(mCompleteListener != null){
            mCompleteListener.onComplete(true, msg);
        }
    }

    public void onSignInFail(int code, String message) {
        Log.i(TAG, "onSignInFail, code=" + code + ", message=" + message);

        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", "Sign In Fail");
        String msg = jsonObject.toJSONString();

        if(mCompleteListener != null){
            mCompleteListener.onComplete(false, msg);
        }
    }
}
