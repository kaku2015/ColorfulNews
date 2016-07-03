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
package com.kaku.colorfulnews.mvp.presenter.impl;

import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.greendao.NewsChannelTable;
import com.kaku.colorfulnews.mvp.interactor.impl.NewsChannelInteractorImpl;
import com.kaku.colorfulnews.mvp.presenter.NewsChannelPresenter;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenterImpl;
import com.kaku.colorfulnews.mvp.view.NewsChannelView;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author 咖枯
 * @version 1.0 2016/6/30
 */
public class NewsChannelPresenterImpl extends BasePresenterImpl<NewsChannelView,
        Map<Integer, List<NewsChannelTable>>> implements NewsChannelPresenter {

    private NewsChannelInteractorImpl mNewsChannelInteractor;

    @Inject
    public NewsChannelPresenterImpl(NewsChannelInteractorImpl newsChannelInteractor) {
        mNewsChannelInteractor = newsChannelInteractor;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNewsChannelInteractor.lodeNewsChannels(this);
    }

    @Override
    public void success(Map<Integer, List<NewsChannelTable>> data) {
        super.success(data);
        mView.initRecyclerViews(data.get(Constants.NEWS_CHANNEL_MINE), data.get(Constants.NEWS_CHANNEL_MORE));
    }

    @Override
    public void onItemSwap(int fromPosition, int toPosition) {
            mNewsChannelInteractor.swapDb(fromPosition,toPosition);
    }
}
