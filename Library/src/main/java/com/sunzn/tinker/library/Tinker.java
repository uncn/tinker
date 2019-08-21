package com.sunzn.tinker.library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import com.sunzn.tinker.library.bar.TinkerBarHelper;
import com.sunzn.tinker.library.utils.BarUtils;

/**
 * Created by sunzn on 2017/11/20.
 */

public class Tinker {

    private static final int INIT_COLOR = 0x99000000;

    private View mTinkerView;

    public Tinker(Activity activity) {
        Window window = activity.getWindow();
        suitBar(activity, window);
    }

    public void setTinkerAlpha(float alpha) {
        if (mTinkerView != null) {
            mTinkerView.setAlpha(alpha);
        }
    }

    public void setTinkerColor(int color) {
        if (mTinkerView != null) {
            mTinkerView.setBackgroundColor(color);
        }
    }

    public void setTinkerColor(String color) {
        if (mTinkerView != null) {
            int i = Color.parseColor(color);
            mTinkerView.setBackgroundColor(i);
        }
    }

    public void setTinkerResource(int res) {
        if (mTinkerView != null) {
            mTinkerView.setBackgroundResource(res);
        }
    }

    private void suitBar(Activity context, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            suitBar21(context, window);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            suitBar19(context, window);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void suitBar21(Activity context, Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewGroup contentView = context.findViewById(Window.ID_ANDROID_CONTENT);
        mTinkerView = new View(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, BarUtils.getHeight(context));
        mTinkerView.setLayoutParams(params);
        mTinkerView.setBackgroundColor(INIT_COLOR);
        contentView.addView(mTinkerView, 0);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void suitBar19(Activity context, Window window) {
        setTranslucentStatus(window, true);
        ViewGroup contentView = context.findViewById(Window.ID_ANDROID_CONTENT);
        mTinkerView = new View(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, BarUtils.getHeight(context));
        params.gravity = Gravity.TOP;
        mTinkerView.setLayoutParams(params);
        mTinkerView.setBackgroundColor(INIT_COLOR);
        contentView.addView(mTinkerView, 0);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(Window window, boolean on) {
        WindowManager.LayoutParams winParams = window.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        window.setAttributes(winParams);
    }

    public static void setBarDarkMode(Activity activity) {
        TinkerBarHelper.setStatusBarDarkMode(activity);
    }

    public static void setBarLightMode(Activity activity) {
        TinkerBarHelper.setStatusBarLightMode(activity);
    }

}
