package com.lixin.account.ucost.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @author Lixin on 2018.3.5
 */
public class AppUtils {
    private AppUtils() {
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }
}
