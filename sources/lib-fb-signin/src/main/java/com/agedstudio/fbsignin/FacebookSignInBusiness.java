package com.agedstudio.fbsignin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agedstudio.base.AbstractSignInApi;
import com.agedstudio.base.utils.NetworkUtils;
import com.cocos.lib.CocosActivity;
import com.cocos.lib.GlobalObject;
import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class FacebookSignInBusiness extends AbstractSignInApi {

    private static final String TAG = FacebookSignInBusiness.class.getSimpleName();

    private static FacebookSignInBusiness ins = null;

    private static final int RC_SIGN_IN = 200000;

    private CallbackManager callbackManager = null;
    private Timer timer = null;
    private LoginButton mButton = null;

    public void init(Context context) {
        super.init(context);
        Log.d(TAG, "init");
        FacebookSignInBusiness.ins = this;

        FacebookSdk.sdkInitialize(GlobalObject.getActivity().getApplication());
        AppEventsLogger.activateApp(GlobalObject.getActivity().getApplication());

        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.GRAPH_API_DEBUG_INFO);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.GRAPH_API_DEBUG_WARNING);
        this.callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                if (profile == null) {
                    Log.i(TAG, "refresh current access token async");
                    AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback() {
                        @Override
                        public void OnTokenRefreshed(@Nullable AccessToken accessToken) {
                            FacebookSignInBusiness.ins.OnTokenRefreshed(accessToken);
                        }

                        @Override
                        public void OnTokenRefreshFailed(@Nullable FacebookException e) {
                            FacebookSignInBusiness.ins.OnTokenRefreshFailed(e);
                        }
                    });
                } else {
                    FacebookSignInBusiness.ins.onSignInSuccess(profile);
                }
            }

            @Override
            public void onCancel() {
                FacebookSignInBusiness.ins.onSignInFail(-1, "cancel");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                FacebookSignInBusiness.ins.onSignInFail(-1, e.getMessage());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == FacebookSignInBusiness.ins) {
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void signIn(AbstractSignInApi.OnCompleteListener listener) {
        Log.i(TAG, "signIn");
        mCompleteListener = listener;

        CocosActivity activity = (CocosActivity) mContext;
        FacebookSignInBusiness.ins.cancelTimer();
        NetworkUtils.isAvailableAsync(new NetworkUtils.OnNetworkListener() {
            @Override
            public void OnAccept(Boolean bool) {
                if (!bool) {
                    FacebookSignInBusiness.ins.onSignInFail(-1, "network error");
                } else {
                    GlobalObject.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                            if (isLoggedIn) {
                                Log.i(TAG, "isLoggedIn accessToken: "+accessToken);
                                FacebookSignInBusiness.ins.handleSignInResult(accessToken);
                            } else {
                                Log.i(TAG, "logInWithReadPermissions");
//                                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
//                                mLoginButton.performClick();
                                LoginManager.getInstance().logIn(activity, Arrays.asList("public_profile"));
                            }
                        }
                    });
                }
            }
        });
    }

    public void signOut(){
        Log.i(TAG, "signOut");

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            LoginManager.getInstance().logOut();
        }
    }

    private void handleSignInResult(AccessToken accessToken) {
        if (accessToken == null || accessToken.isExpired()) {
            FacebookSignInBusiness.ins.onSignInFail(-1, "access token error");
        } else {
            Profile profile = Profile.getCurrentProfile();
            if (profile == null) {
                FacebookSignInBusiness.ins.onSignInFail(-1, "invalid profile");
            } else {
                FacebookSignInBusiness.ins.onSignInSuccess(profile);
            }
        }
    }

    public void onSignInSuccess(Profile profile) {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("id", "fb_" + profile.getId());
        jsonObject.put("name", profile.getName());
        String avatarUrl = null;
        if (null != profile.getProfilePictureUri(200, 200)) {
            avatarUrl = profile.getProfilePictureUri(200, 200).toString();
        }
        if (null == avatarUrl) {
            if (profile.getPictureUri() != null) {
                avatarUrl =  profile.getPictureUri().toString();
            }
        }

        jsonObject.put("avatarUrl", avatarUrl != null ? avatarUrl : "");
        String msg = jsonObject.toJSONString();

        Log.i(TAG, "onSignInSuccess, msg=" + msg);
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

        if(mCompleteListener != null) {
            mCompleteListener.onComplete(false, msg);
        }
    }

    private void OnTokenRefreshFailed(@Nullable FacebookException e) {
        Log.i(TAG, "token refresh failed");
        this.onSignInFail(-1, e.getMessage());
    }

    private void OnTokenRefreshed(@Nullable AccessToken accessToken) {
        Log.i(TAG, "token refreshed");
        Profile.fetchProfileForCurrentAccessToken();
        this.cancelTimer();
        if (null == this.timer) {
            this.timer = new Timer();
        }
        synchronized (this.timer) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    FacebookSignInBusiness.this.cancelTimer();
                    Profile profile = Profile.getCurrentProfile();
                    if (profile == null) {
                        FacebookSignInBusiness.ins.onSignInFail(-1, "invalid profile");
                    } else {
                        FacebookSignInBusiness.ins.onSignInSuccess(profile);
                    }
                }
            };
            this.timer.schedule(task, 2000);
        }
    }

    private void cancelTimer() {
        if (null != this.timer) {
            synchronized (this.timer) {
                this.timer.cancel();
            };
        }
    }
}
