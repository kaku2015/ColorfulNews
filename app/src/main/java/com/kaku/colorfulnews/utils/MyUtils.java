/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.kaku.colorfulnews.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.common.Constants;
import com.socks.library.KLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author 咖枯
 * @version 1.0 2016/5/31
 */
public class MyUtils {

    public static boolean isNightMode() {
        SharedPreferences preferences = App.getAppContext().getSharedPreferences(
                Constants.SHARES_COLOURFUL_NEWS, Activity.MODE_PRIVATE);
        return preferences.getBoolean(Constants.NIGHT_THEME_MODE, false);
    }

    public static void saveTheme(boolean isNight) {
        SharedPreferences preferences = App.getAppContext().getSharedPreferences(
                Constants.SHARES_COLOURFUL_NEWS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.NIGHT_THEME_MODE, isNight);
        editor.apply();
    }

    public static SharedPreferences getSharedPreferences() {
        return App.getAppContext()
                .getSharedPreferences(Constants.SHARES_COLOURFUL_NEWS, Context.MODE_PRIVATE);
    }

    /**
     * from yyyy-MM-dd HH:mm:ss to MM-dd HH:mm
     */
    public static String formatDate(String before) {
        String after;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .parse(before);
            after = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            KLog.e("转换新闻日期格式异常：" + e.toString());
            return before;
        }
        return after;
    }
}
