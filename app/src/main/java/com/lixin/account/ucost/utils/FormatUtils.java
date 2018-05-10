package com.lixin.account.ucost.utils;

import java.text.DecimalFormat;

/**
 * Created by Lixin on 2018/3/2
 * 格式化数字工具类
 */
public class FormatUtils {

    private FormatUtils() {
    }

    public static float formatFloat(String pattern, float value) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return Float.parseFloat(decimalFormat.format(value));
    }
}
