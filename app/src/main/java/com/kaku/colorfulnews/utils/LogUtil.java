/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 */
package com.kaku.colorfulnews.utils;

import android.util.Log;

import com.kaku.colorfulnews.BuildConfig;

/**
 * Log输出
 *
 * @author 咖枯
 * @version 1.0 2015/05
 */
public class LogUtil {

    public static void v(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            Log.e(tag, msg);
        }
    }

}
