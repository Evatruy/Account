package com.lixin.account.ucost.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Lixin on 2018/3/3
 */
public class ScreenUtils {
    private ScreenUtils() {
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static int dp2sp(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

}
