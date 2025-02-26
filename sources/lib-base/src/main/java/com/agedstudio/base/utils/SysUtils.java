package com.agedstudio.base.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class SysUtils {

    private static final String TAG = SysUtils.class.getSimpleName();

    public static boolean isDebugApp() {
        Context context = CommonUtil.getApplicationContext();
        try {
            ApplicationInfo info = context.getApplicationInfo();
            if ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                return true;
            }
        } catch (Exception exception) {
            return false;
        }
        return false;
    }

    public static String getVersionName() {
        Context context = CommonUtil.getApplicationContext();
        String versionName = "1.0.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode() {
        Context context = CommonUtil.getApplicationContext();
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getSHA1() {
        Context context = CommonUtil.getApplicationContext();
        try {
            String packgaeName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packgaeName, PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备型号
     * @return
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            return model;
        }
        return "";
    }

    /**
     * 获取设备厂商
     * @return
     */
    public static String getBrand() {
        String brand = Build.BRAND;
        if (brand != null) {
            return brand;
        }
        return "";
    }

    public static void vibrate(int milliseconds) {
        LogUtil.i(TAG, "vibrate");
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Context context = CommonUtil.getApplicationContext();
                    Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(milliseconds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void stopVibrate() {
        LogUtil.i(TAG, "stopVibrate");
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Context context = CommonUtil.getApplicationContext();
                    Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String getChannelName() {
        Context context = CommonUtil.getApplicationContext();
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo == null) {
                return "";
            }
            return applicationInfo.metaData.getString("AGED_STUDIO_CHANNEL", "");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "";
    }

    public static String getExternalFilesDir() {
        return CommonUtil.getApplicationContext().getExternalFilesDir("").getAbsolutePath() + File.separator;
    }

    public static String getExternalCacheDir() {
        return CommonUtil.getApplicationContext().getExternalCacheDir().getAbsolutePath() + File.separator;
    }

    public static String getCacheDir() {
        return CommonUtil.getApplicationContext().getCacheDir().getAbsolutePath() + File.separator;
    }

    /**
     * 设置屏幕的亮度
     * @param birghtness 0～255
     */
    public static void setScreenBrightness(final int birghtness) {
        Activity context = (Activity) CommonUtil.getActivityContext();
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Window window = context.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.screenBrightness = birghtness / 255.0f;
                window.setAttributes(lp);
            }
        });
    }

    /**
     * 获取屏幕的亮度
     * @returns 0～255
     */
    public static int getScreenBrightness() {
        Activity context = (Activity)CommonUtil.getActivityContext();
        Window window = context.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (lp.screenBrightness < 0) {
            int systemBrightness = 0;
            try {
                systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return Math.min(255, Math.max(0, systemBrightness));
        }
        return Math.round(lp.screenBrightness * 255);
    }

    public static void end() {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        });
    }

    public static void setKeepScreenOn(boolean value) {
        CommonUtil.setKeepScreenOn(value);
    }

    public static int getWindowWidth(){
        Activity activity = (Activity) CommonUtil.getActivityContext();
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float widthPixels = displayMetrics.widthPixels;
        float desity = displayMetrics.density;

        return (int)(widthPixels / desity);
    }
}
