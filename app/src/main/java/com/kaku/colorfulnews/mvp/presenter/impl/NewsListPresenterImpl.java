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

import com.kaku.colorfulnews.mvp.entity.NewsSummary;
import com.kaku.colorfulnews.common.LoadNewsType;
import com.kaku.colorfulnews.mvp.interactor.NewsListInteractor;
import com.kaku.colorfulnews.mvp.interactor.impl.NewsListInteractorImpl;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.mvp.presenter.NewsListPresenter;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenterImpl;
import com.kaku.colorfulnews.mvp.view.NewsListView;

import java.util.List;

import javax.inject.Inject;

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
    private boolean misFirstLoad;
    private boolean mIsRefresh = true;

    @Inject
    public NewsListPresenterImpl(NewsListInteractorImpl newsListInteractor) {
        mNewsListInteractor = newsListInteractor;
    }

    @Override
    public void onCreate() {
        if (mView != null) {
            loadNewsData();
        }
    }

    @Override
    public void beforeRequest() {
        if (!misFirstLoad) {
            mView.showProgress();
        }
    }

    @Override
    public void onError(String errorMsg) {
        super.onError(errorMsg);
        if (mView != null) {
            int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_ERROR : LoadNewsType.TYPE_LOAD_MORE_ERROR;
            mView.setNewsList(null, loadType);
        }
    }

    @Override
    public void success(List<NewsSummary> items) {
        misFirstLoad = true;
        if (items != null) {
            mStartPage += 20;
        }

        int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_SUCCESS : LoadNewsType.TYPE_LOAD_MORE_SUCCESS;
        if (mView != null) {
            mView.setNewsList(items, loadType);
            mView.hideProgress();
        }

    }

    @Override
    public void setNewsTypeAndId(String newsType, String newsId) {
        mNewsType = newsType;
        mNewsId = newsId;
    }

    @Override
    public void refreshData() {
        mStartPage = 0;
        mIsRefresh = true;
        loadNewsData();
    }

    @Override
    public void loadMore() {
        mIsRefresh = false;
        loadNewsData();
    }

    private void loadNewsData() {
        mSubscription = mNewsListInteractor.loadNews(this, mNewsType, mNewsId, mStartPage);
    }
}
