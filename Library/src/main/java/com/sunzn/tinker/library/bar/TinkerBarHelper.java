package com.sunzn.tinker.library.bar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TinkerBarHelper {

    private final static int STATUS_BAR_TYPE_DEFAULT = 0;
    private final static int STATUS_BAR_TYPE_MIUI = 1;
    private final static int STATUS_BAR_TYPE_FLYME = 2;
    private final static int STATUS_BAR_TYPE_ANDROID6 = 3;

    @StatusBarType
    private static int mStatusBarType = STATUS_BAR_TYPE_DEFAULT;

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 名称：设置状态栏黑色字体图标
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 描述：支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 参数：activity 需要被处理的 Activity
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 返回：boolean 是否成功
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    public static boolean setStatusBarLightMode(Activity activity) {
        if (activity == null) return false;
        if (TinkerDeviceHelper.isZTKC2016()) {
            return false;
        }
        if (mStatusBarType != STATUS_BAR_TYPE_DEFAULT) {
            return setStatusBarLightMode(activity, mStatusBarType);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isMIUICustomStatusBarLightModeImpl() && MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                mStatusBarType = STATUS_BAR_TYPE_MIUI;
                return true;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                mStatusBarType = STATUS_BAR_TYPE_FLYME;
                return true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Android6SetStatusBarLightMode(activity.getWindow(), true);
                mStatusBarType = STATUS_BAR_TYPE_ANDROID6;
                return true;
            }
        }
        return false;
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 名称：已知系统类型时，设置状态栏黑色字体图标
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 描述：支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 参数：activity 需要被处理的 Activity
     * ║ 参数：type     StatusBar 类型，对应不同的系统
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 返回：boolean 是否成功
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    private static boolean setStatusBarLightMode(Activity activity, @StatusBarType int type) {
        if (type == STATUS_BAR_TYPE_MIUI) {
            return MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == STATUS_BAR_TYPE_FLYME) {
            return FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == STATUS_BAR_TYPE_ANDROID6) {
            return Android6SetStatusBarLightMode(activity.getWindow(), true);
        }
        return false;
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 名称：设置状态栏白色字体图标
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 描述：支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    public static boolean setStatusBarDarkMode(Activity activity) {
        if (activity == null) return false;
        if (mStatusBarType == STATUS_BAR_TYPE_DEFAULT) {
            return true; // 默认状态，不需要处理
        }

        if (mStatusBarType == STATUS_BAR_TYPE_MIUI) {
            return MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (mStatusBarType == STATUS_BAR_TYPE_FLYME) {
            return FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (mStatusBarType == STATUS_BAR_TYPE_ANDROID6) {
            return Android6SetStatusBarLightMode(activity.getWindow(), false);
        }
        return true;
    }

    @TargetApi(23)
    private static int changeStatusBarModeRetainFlag(Window window, int out) {
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        return out;
    }

    private static int retainSystemUiFlag(Window window, int out, int type) {
        int now = window.getDecorView().getSystemUiVisibility();
        if ((now & type) == type) {
            out |= type;
        }
        return out;
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 名称：设置状态栏字体图标为深色，Android 6
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 参数：window 需要设置的窗口
     * ║ 参数：light  是否把状态栏字体及图标颜色设置为深色
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 返回：boolean 成功执行返回 true
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    @TargetApi(23)
    private static boolean Android6SetStatusBarLightMode(Window window, boolean light) {
        View decorView = window.getDecorView();
        int systemUi = light ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        systemUi = changeStatusBarModeRetainFlag(window, systemUi);
        decorView.setSystemUiVisibility(systemUi);
        if (TinkerDeviceHelper.isMIUI_V9()) {
            // MIUI 9 低于 6.0 版本依旧只能回退到以前的方案
            // https://github.com/Tencent/QMUI_Android/issues/160
            MIUISetStatusBarLightMode(window, light);
        }
        return true;
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 名称：设置状态栏字体图标为深色，需要 MIUI V6 以上
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 参数：window 需要设置的窗口
     * ║ 参数：light  是否把状态栏字体及图标颜色设置为深色
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 返回：boolean 成功执行返回 true
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    @SuppressWarnings({"unchecked", "PrivateApi"})
    private static boolean MIUISetStatusBarLightMode(Window window, boolean light) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (light) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag); //状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);     //清除黑色字体
                }
                result = true;
            } catch (Exception ignored) {

            }
        }
        return result;
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9 && Android 6 之后用回 Android 原生实现
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    private static boolean isMIUICustomStatusBarLightModeImpl() {
        if (TinkerDeviceHelper.isMIUI_V9() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return TinkerDeviceHelper.isMIUI_V5() || TinkerDeviceHelper.isMIUI_V6() || TinkerDeviceHelper.isMIUI_V7() || TinkerDeviceHelper.isMIUI_V8();
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════════════
     * ║ 名称：设置状态栏图标为深色和魅族特定的文字风格，可以用来判断是否为 Flyme 用户
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 参数：window 需要设置的窗口
     * ║ 参数：light  是否把状态栏字体及图标颜色设置为深色
     * ╟────────────────────────────────────────────────────────────────────────────────────────────
     * ║ 返回：boolean 成功执行返回 true
     * ╚════════════════════════════════════════════════════════════════════════════════════════════
     */
    private static boolean FlymeSetStatusBarLightMode(Window window, boolean light) {
        boolean result = false;
        if (window != null) {
            // flyme 在 6.2.0.0A 支持了 Android 官方的实现方案，旧的方案失效
            Android6SetStatusBarLightMode(window, light);

            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (light) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception ignored) {

            }
        }
        return result;
    }

    @IntDef({STATUS_BAR_TYPE_DEFAULT, STATUS_BAR_TYPE_MIUI, STATUS_BAR_TYPE_FLYME, STATUS_BAR_TYPE_ANDROID6})
    @Retention(RetentionPolicy.SOURCE)
    private @interface StatusBarType {

    }

}
