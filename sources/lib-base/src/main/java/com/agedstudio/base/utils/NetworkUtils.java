package com.agedstudio.base.utils;

import com.blankj.utilcode.util.Utils;

public  class NetworkUtils {

    public interface OnNetworkListener {
        void OnAccept(Boolean bool);
    }

    /**
     * 判读网络是否连接
     * @return
     */
    public static void isAvailableAsync(final OnNetworkListener listener) {
        com.blankj.utilcode.util.NetworkUtils.isAvailableByDnsAsync("www.google.com", new Utils.Consumer<Boolean>() {
            @Override
            public void accept(Boolean bool) {
                if (!bool) {
                    com.blankj.utilcode.util.NetworkUtils.isAvailableByDnsAsync("www.facebook.com", new Utils.Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            if (null != listener) {
                                listener.OnAccept(aBoolean);
                            }
                        }
                    });
                } else {
                    if (null != listener) {
                        listener.OnAccept(bool);
                    }
                }
            }
        });
    }
    public static void isAvailableAsync(final String domain, final OnNetworkListener listener) {
        com.blankj.utilcode.util.NetworkUtils.isAvailableByDnsAsync(domain, new Utils.Consumer<Boolean>() {
            @Override
            public void accept(Boolean bool) {
                if (null != listener) {
                    listener.OnAccept(bool);
                }
            }
        });
    }
}