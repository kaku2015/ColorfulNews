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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.annotation.BindValues;
import com.kaku.colorfulnews.di.component.ActivityComponent;
import com.kaku.colorfulnews.di.component.DaggerActivityComponent;
import com.kaku.colorfulnews.di.module.ActivityModule;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenter;
import com.kaku.colorfulnews.mvp.ui.activities.AboutActivity;
import com.kaku.colorfulnews.mvp.ui.activities.NewsActivity;
import com.kaku.colorfulnews.mvp.ui.activities.NewsDetailActivity;
import com.kaku.colorfulnews.mvp.ui.activities.PhotoActivity;
import com.kaku.colorfulnews.mvp.ui.activities.PhotoDetailActivity;
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
    protected ActivityComponent mActivityComponent;
    private boolean mIsChangeTheme;

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    private WindowManager mWindowManager = null;
    private View mNightView = null;
    private boolean mIsAddedView;
    protected T mPresenter;
    protected boolean mIsHasNavigationView;
    private DrawerLayout mDrawerLayout;
    private Class mClass;

    public abstract int getLayoutId();

    public abstract void initInjector();

    public abstract void initViews();

    protected Subscription mSubscription;
    protected NavigationView mBaseNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.i(getClass().getSimpleName());
        initAnnotation();
        NetUtil.isNetworkErrThenShowMsg();
        initActivityComponent();
        setStatusBarTranslucent();
        setNightOrDayMode();

        int layoutId = getLayoutId();
        setContentView(layoutId);
        initInjector();
        ButterKnife.bind(this);
        initToolBar();
        initViews();
        if (mIsHasNavigationView) {
            initDrawerLayout();
        }
        if (mPresenter != null) {
            mPresenter.onCreate();
        }

        initNightModeSwitch();
    }

    private void initAnnotation() {
        if (getClass().isAnnotationPresent(BindValues.class)) {
            BindValues annotation = getClass().getAnnotation(BindValues.class);
            mIsHasNavigationView = annotation.mIsHasNavigationView();
        }
    }

    private void initNightModeSwitch() {
        if (this instanceof NewsActivity || this instanceof PhotoActivity) {
            MenuItem menuNightMode = mBaseNavView.getMenu().findItem(R.id.nav_night_mode);
            SwitchCompat dayNightSwitch = (SwitchCompat) MenuItemCompat
                    .getActionView(menuNightMode);
            setCheckedState(dayNightSwitch);
            setCheckedEvent(dayNightSwitch);
        }
    }

    private void setCheckedState(SwitchCompat dayNightSwitch) {
        if (MyUtils.isNightMode()) {
            dayNightSwitch.setChecked(true);
        } else {
            dayNightSwitch.setChecked(false);
        }
    }

    private void setCheckedEvent(SwitchCompat dayNightSwitch) {
        dayNightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeToNight();
                    MyUtils.saveTheme(true);
                } else {
                    changeToDay();
                    MyUtils.saveTheme(false);
                }

                mIsChangeTheme = true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void initActivityComponent() {
        mActivityComponent = DaggerActivityComponent.builder()
                .applicationComponent(((App) getApplication()).getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }


    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initDrawerLayout() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (navView != null) {
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_news:
                            mClass = NewsActivity.class;
                            break;
                        case R.id.nav_photo:
                            mClass = PhotoActivity.class;
                            break;
                        case R.id.nav_video:
                            Toast.makeText(BaseActivity.this, "施工准备中...", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.nav_night_mode:
                            break;
                    }
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return false;
                }
            });
        }

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (mClass != null) {
                    Intent intent = new Intent(BaseActivity.this, mClass);
                    // 此标志用于启动一个Activity的时候，若栈中存在此Activity实例，则把它调到栈顶。不创建多一个
//                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    mClass = null;
                }

                if (mIsChangeTheme) {
                    mIsChangeTheme = false;
                    getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                    recreate();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mIsHasNavigationView && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                !(this instanceof NewsDetailActivity || this instanceof PhotoActivity
                        || this instanceof PhotoDetailActivity))
                || (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT
                && this instanceof NewsDetailActivity)) {
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (mIsHasNavigationView) {
            overridePendingTransition(0, 0);
        }
//        getWindow().getDecorView().invalidate();
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
            case R.id.action_about:
                if (mIsHasNavigationView) {
                    Intent intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsHasNavigationView) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
