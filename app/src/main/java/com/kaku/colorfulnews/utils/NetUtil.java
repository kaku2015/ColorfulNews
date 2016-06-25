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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.kaku.colorfulnews.App;

/**
 * @author 咖枯
 * @version 1.0 2016/5/26
 */
public class NetUtil {

    /**
     * 检查当前网络是否可用
     *
     * @param context context
     * @return 是否连接到网络
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {

                    return true;
                }
            }
        }
        return false;
    }

    public static void checkNetworkState(String msg) {
        if (!NetUtil.isNetworkAvailable(App.getAppContext())) {
            //TODO: 刚启动app Snackbar不起作用，延迟显示也不好使，这是why？
            Toast.makeText(App.getAppContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

}
