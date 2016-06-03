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
package com.kaku.colorfulnews.presenter.impl;

import com.kaku.colorfulnews.bean.NewsSummary;
import com.kaku.colorfulnews.interactor.NewsListInteractor;
import com.kaku.colorfulnews.interactor.impl.NewsListInteractorImpl;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.presenter.NewsListPresenter;
import com.kaku.colorfulnews.view.NewsListView;

import java.util.List;

/**
 * @author 咖枯
 * @version 1.0 2016/5/19
 */
public class NewsListPresenterImpl implements NewsListPresenter, RequestCallBack<List<NewsSummary>> {

    private NewsListView mNewsListView;
    private NewsListInteractor<List<NewsSummary>> mNewsListInteractor;
    private String mNewsType;
    private String mNewsId;
    private int mStartPage;

    /**
     * 新闻页面首次加载完毕
     */
    private boolean misLoaded;

    public NewsListPresenterImpl(NewsListView newsListView, String newsType, String newsId) {
        mNewsListView = newsListView;
        mNewsListInteractor = new NewsListInteractorImpl();
        mNewsType = newsType;
        mNewsId = newsId;
    }

    @Override
    public void onCreate() {
        if (mNewsListView != null) {
            mNewsListInteractor.loadNews(this, mNewsType, mNewsId, mStartPage);
        }
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public void onDestroy() {
        mNewsListView = null;

    }

    @Override
    public void beforeRequest() {
        if (!misLoaded) {
            mNewsListView.showProgress();
        }
    }

    @Override
    public void success(List<NewsSummary> items) {
        misLoaded = true;
        if (mNewsListView != null) {
            mNewsListView.setItems(items);
            mNewsListView.hideProgress();
        }

    }

    @Override
    public void onError(String errorMsg) {
        mNewsListView.showErrorMsg(errorMsg);
    }

}
