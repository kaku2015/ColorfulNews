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
package com.kaku.colorfulnews.mvp.presenter.impl;

import com.kaku.colorfulnews.bean.NewsSummary;
import com.kaku.colorfulnews.mvp.interactor.NewsListInteractor;
import com.kaku.colorfulnews.mvp.interactor.impl.NewsListInteractorImpl;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.mvp.presenter.NewsListPresenter;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenterImpl;
import com.kaku.colorfulnews.mvp.view.NewsListView;

import java.util.List;

/**
 * @author 咖枯
 * @version 1.0 2016/5/19
 */
public class NewsListPresenterImpl extends BasePresenterImpl<NewsListView, List<NewsSummary>>
        implements NewsListPresenter, RequestCallBack<List<NewsSummary>> {

    private NewsListInteractor<List<NewsSummary>> mNewsListInteractor;
    private String mNewsType;
    private String mNewsId;
    private int mStartPage;

    /**
     * 新闻页面首次加载完毕
     */
    private boolean misLoaded;

    public NewsListPresenterImpl(NewsListView newsListView, String newsType, String newsId) {
        mView = newsListView;
        mNewsListInteractor = new NewsListInteractorImpl();
        mNewsType = newsType;
        mNewsId = newsId;
    }

    @Override
    public void onCreate() {
        if (mView != null) {
            mSubscription = mNewsListInteractor.loadNews(this, mNewsType, mNewsId, mStartPage);
        }
    }

    @Override
    public void beforeRequest() {
        if (!misLoaded) {
            mView.showProgress();
        }
    }

    @Override
    public void success(List<NewsSummary> items) {
        misLoaded = true;
        if (mView != null) {
            mView.setNewsList(items);
            mView.hideProgress();
        }

    }

}
