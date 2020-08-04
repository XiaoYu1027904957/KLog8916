package com.senter.klog;

/**
 * Created by zbmobi on 2016/10/27.
 */

public interface CallBack {
    void onLogPrint(int type, String tag, String log);
}