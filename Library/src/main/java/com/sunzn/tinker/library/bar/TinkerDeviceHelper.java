package com.sunzn.tinker.library.bar;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

@SuppressLint("PrivateApi")
public class TinkerDeviceHelper {
    private final static String TAG = "QMUIDeviceHelper";
    private final static String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private final static String ZTEC2016 = "zte c2016";
    private static String sMiuiVersionName;

    static {
        Properties properties = new Properties();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/uild.prop 会报 permission denied
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                properties.load(fileInputStream);
            } catch (Exception e) {
                Log.e(TAG, "read file error : " + e);
            } finally {
                close(fileInputStream);
            }
        }

        Class<?> clzSystemProperties;
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = clzSystemProperties.getDeclaredMethod("get", String.class);
            // miui
            sMiuiVersionName = getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME);
        } catch (Exception e) {
            Log.e(TAG, "read SystemProperties error : " + e);
        }
    }

    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 判断是否是 MIUI 系统
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    static boolean isMIUI_V5() {
        return "v5".equals(sMiuiVersionName);
    }

    static boolean isMIUI_V6() {
        return "v6".equals(sMiuiVersionName);
    }

    static boolean isMIUI_V7() {
        return "v7".equals(sMiuiVersionName);
    }

    static boolean isMIUI_V8() {
        return "v8".equals(sMiuiVersionName);
    }

    static boolean isMIUI_V9() {
        return "v9".equals(sMiuiVersionName);
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 判断是否为 ZUK Z1 和 ZTK C2016。两台设备的系统虽然为 Android 6.0，但不支持状态栏 icon 颜色改
     * ║ 变，因此经常需要对它们进行额外判断。
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    static boolean isZTKC2016() {
        final String board = Build.MODEL;
        return board != null && board.toLowerCase().contains(ZTEC2016);
    }

    @Nullable
    private static String getLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke(null, key);
            } catch (Exception ignored) {
            }
        }
        if (name != null) name = name.toLowerCase();
        return name;
    }

}
