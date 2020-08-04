package com.senter.klog;

import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaokaiqiang on 15/11/18.
 */
public class Util {

    public static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || line.equals("\n") || line.equals("\t") || TextUtils.isEmpty(line.trim());
    }

    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "");
        } else {
            Log.d(tag, "");
        }
    }

    public static String getNowTimeByFormat(String format) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(now);
    }
}
