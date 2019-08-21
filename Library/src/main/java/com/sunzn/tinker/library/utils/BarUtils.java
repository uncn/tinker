package com.sunzn.tinker.library.utils;

import android.content.Context;

/**
 * Created by sunzn on 2017/11/20.
 */

public class BarUtils {

    public static int getHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
