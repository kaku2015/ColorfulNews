/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com>
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
package com.kaku.colorfulnews.mvp.ui.activities;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.mvp.ui.activities.base.BaseActivity;

import butterknife.BindView;

/**
 * @author 咖枯
 * @version 1.0 2016/7/28
 */
public class NewsBrowserActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_news_browser;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initViews() {
        mToolbar.setTitle(getIntent().getStringExtra(Constants.NEWS_TITLE));
        initWebView();
    }

    private void initWebView() {
        setWebViewSettings();
        setWebView();
    }

    private void setWebViewSettings() {
        WebSettings webSettings = mWebView.getSettings();
        // 打开页面时， 自适应屏幕
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        // 便页面支持缩放
        webSettings.setJavaScriptEnabled(true); //支持js
        webSettings.setSupportZoom(true); //支持缩放
//        webSettings.setBuiltInZoomControls(true); // 放大和缩小的按钮，容易引发异常 http://blog.csdn.net/dreamer0924/article/details/34082687

        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    private void setWebView() {
        mWebView.loadUrl(getIntent().getStringExtra(Constants.NEWS_LINK));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null) view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }
        });
    }

    @Override
    public void initSupportActionBar() {
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
    }
}
