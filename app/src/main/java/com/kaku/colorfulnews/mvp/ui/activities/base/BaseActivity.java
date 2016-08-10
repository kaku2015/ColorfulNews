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
package com.kaku.colorfulnews.mvp.ui.activities.base;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.di.component.ActivityComponent;
import com.kaku.colorfulnews.di.component.DaggerActivityComponent;
import com.kaku.colorfulnews.di.module.ActivityModule;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenter;
import com.kaku.colorfulnews.mvp.ui.activities.NewsDetailActivity;
import com.kaku.colorfulnews.mvp.ui.activities.PhotoActivity;
import com.kaku.colorfulnews.utils.MyUtils;
import com.kaku.colorfulnews.utils.NetUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.socks.library.KLog;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import rx.Subscription;

/**
 * @author 咖枯
 * @version 1.0 2016/5/19
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {
    /**
     * Log tag ：BaseActivity
     */
    protected ActivityComponent mActivityComponent;

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    private static final String LOG_TAG = "BaseActivity";
    private WindowManager mWindowManager = null;
    private View mNightView = null;
    private boolean mIsAddedView;
    protected T mPresenter;

    public abstract int getLayoutId();

    public abstract void initInjector();

    public abstract void initViews();

    public abstract void initSupportActionBar();

    protected Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.i(LOG_TAG, getClass().getSimpleName());
        NetUtil.isNetworkErrThenShowMsg();
        mActivityComponent = DaggerActivityComponent.builder()
                .applicationComponent(((App) getApplication()).getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
        setStatusBarTranslucent();
        setNightOrDayMode();

        int layoutId = getLayoutId();
        setContentView(layoutId);
        initInjector();
        ButterKnife.bind(this);
        initSupportActionBar();
        initViews();
        if (mPresenter != null) {
            mPresenter.onCreate();
        }
    }

    private void setNightOrDayMode() {
        if (MyUtils.isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            initNightView();
            mNightView.setBackgroundResource(R.color.night_mask);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // TODO:适配4.4
    protected void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                !((this instanceof NewsDetailActivity) || this instanceof PhotoActivity)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }
    }

    public void changeToDay() {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mNightView.setBackgroundResource(android.R.color.transparent);
    }

    public void changeToNight() {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        initNightView();
        mNightView.setBackgroundResource(R.color.night_mask);
    }

    private void initNightView() {
        if (mIsAddedView) {
            return;
        }
        // 增加夜间模式蒙板
        WindowManager.LayoutParams nightViewParam = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mNightView = new View(this);
        mWindowManager.addView(mNightView, nightViewParam);
        mIsAddedView = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(this);
        refWatcher.watch(this);

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        removeNightModeMask();
        MyUtils.cancelSubscription(mSubscription);
        MyUtils.fixInputMethodManagerLeak(this);
    }

    private void removeNightModeMask() {
        if (mIsAddedView) {
            // 移除夜间模式蒙板
            mWindowManager.removeViewImmediate(mNightView);
            mWindowManager = null;
            mNightView = null;
        }
    }
}
