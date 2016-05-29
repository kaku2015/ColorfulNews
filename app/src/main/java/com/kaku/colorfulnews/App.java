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
package com.kaku.colorfulnews;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.kaku.colorfulnews.utils.LogUtil;
import com.socks.library.KLog;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * @author 咖枯
 * @version 1.0 2016/05/20
 */
public class App extends Application {

    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    private static Context sAppContext;

    public static Context getAppContext() {
        return sAppContext;
    }

    /**
     * release版本使用此方法
     */
    protected RefWatcher installLeakCanary() {
        return RefWatcher.DISABLED;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        KLog.init(BuildConfig.LOG_DEBUG);

/*        // 注册M权限回调
        Dexter.initialize(this);*/

        refWatcher = LeakCanary.install(this);
//        refWatcher = installLeakCanary();

        this.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtil.v("=========", activity + "  onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtil.v("=========", activity + "  onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtil.v("=========", activity + "  onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtil.v("=========", activity + "  onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtil.v("=========", activity + "  onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtil.v("=========", activity + "  onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtil.v("=========", activity + "  onActivityDestroyed");
            }
        });
    }

}
