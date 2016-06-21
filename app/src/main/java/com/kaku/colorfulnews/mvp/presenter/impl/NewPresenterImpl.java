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

import com.kaku.colorfulnews.greendao.NewsChannelTable;
import com.kaku.colorfulnews.mvp.interactor.NewsInteractor;
import com.kaku.colorfulnews.mvp.interactor.impl.NewsInteractorImpl;
import com.kaku.colorfulnews.mvp.presenter.NewsPresenter;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenterImpl;
import com.kaku.colorfulnews.mvp.view.NewsView;

import java.util.List;

/**
 * @author 咖枯
 * @version 1.0 2016/6/2
 */
public class NewPresenterImpl extends BasePresenterImpl<NewsView, List<NewsChannelTable>>
        implements NewsPresenter {

    private NewsInteractor<List<NewsChannelTable>> mNewsInteractor;

    public NewPresenterImpl(NewsView newsView) {
        mView = newsView;
        mNewsInteractor = new NewsInteractorImpl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSubscription = mNewsInteractor.lodeNewsChannels(this);
    }

    @Override
    public void success(List<NewsChannelTable> data) {
        super.success(data);
        mView.initViewPager(data);
    }
}
