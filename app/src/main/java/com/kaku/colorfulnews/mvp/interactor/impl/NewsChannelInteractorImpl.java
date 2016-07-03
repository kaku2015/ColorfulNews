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
package com.kaku.colorfulnews.mvp.interactor.impl;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.greendao.NewsChannelTable;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.mvp.interactor.NewsChannelInteractor;
import com.kaku.colorfulnews.repository.db.NewsChannelTableManager;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author 咖枯
 * @version 1.0 2016/6/30
 */
public class NewsChannelInteractorImpl implements NewsChannelInteractor<Map<Integer, List<NewsChannelTable>>> {

    @Inject
    public NewsChannelInteractorImpl() {
    }

    @Override
    public Subscription lodeNewsChannels(final RequestCallBack<Map<Integer, List<NewsChannelTable>>> callback) {
        return rx.Observable.create(new rx.Observable.OnSubscribe<Map<Integer, List<NewsChannelTable>>>() {
            @Override
            public void call(Subscriber<? super Map<Integer, List<NewsChannelTable>>> subscriber) {
                Map<Integer, List<NewsChannelTable>> newsChannelListMap = getNewsChannelData();
                subscriber.onNext(newsChannelListMap);
                subscriber.onCompleted();
            }

        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<Integer, List<NewsChannelTable>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(App.getAppContext().getString(R.string.db_error));
                    }

                    @Override
                    public void onNext(Map<Integer, List<NewsChannelTable>> newsChannelListMap) {
                        callback.success(newsChannelListMap);
                    }
                });
    }

    @Override
    public void swapDb(final int fromPosition, final int toPosition) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (NewsChannelInteractorImpl.this) {
                    KLog.d("fromPosition: " + fromPosition + "； toPosition: " + toPosition);
                    NewsChannelTable fromNewsChannel = NewsChannelTableManager.loadNewsChannel(fromPosition);
                    NewsChannelTable toNewsChannel = NewsChannelTableManager.loadNewsChannel(toPosition);

                    if (isAdjacent(fromPosition, toPosition)) {
                        swapAdjacentIndexAndUpdate(fromNewsChannel, toNewsChannel);
                    } else if (fromPosition - toPosition > 0) {
                        increaseOrReduceIndexAndUpdate(toPosition, fromPosition - 1, true);
                        changeFromChannelIndexAndUpdate(fromNewsChannel);
                    } else if (fromPosition - toPosition < 0) {
                        increaseOrReduceIndexAndUpdate(fromPosition + 1, toPosition, false);
                        changeFromChannelIndexAndUpdate(fromNewsChannel);
                    }
                }

            }

            private boolean isAdjacent(int fromChannelIndex, int toChannelIndex) {
                return Math.abs(fromChannelIndex - toChannelIndex) == 1;
            }

            private void swapAdjacentIndexAndUpdate(NewsChannelTable fromNewsChannel,
                                                    NewsChannelTable toNewsChannel) {
                fromNewsChannel.setNewsChannelIndex(toPosition);
                toNewsChannel.setNewsChannelIndex(fromPosition);

                NewsChannelTableManager.update(fromNewsChannel);
                NewsChannelTableManager.update(toNewsChannel);
            }

            private void increaseOrReduceIndexAndUpdate(int fromChannelIndex, int toChannelIndex, boolean isIncrease) {
                List<NewsChannelTable> newsChannels = NewsChannelTableManager
                        .LoadNewsChannelsWithin(fromChannelIndex, toChannelIndex);
                for (NewsChannelTable newsChannel : newsChannels) {
                    increaseOrReduceIndex(isIncrease, newsChannel);
                    NewsChannelTableManager.update(newsChannel);
                }
            }

            private void increaseOrReduceIndex(boolean isIncrease, NewsChannelTable newsChannel) {
                int targetIndex;
                if (isIncrease) {
                    targetIndex = newsChannel.getNewsChannelIndex() + 1;
                } else {
                    targetIndex = newsChannel.getNewsChannelIndex() - 1;
                }
                newsChannel.setNewsChannelIndex(targetIndex);
            }

            private void changeFromChannelIndexAndUpdate(NewsChannelTable fromNewsChannel) {
                fromNewsChannel.setNewsChannelIndex(toPosition);
                NewsChannelTableManager.update(fromNewsChannel);
            }

        }).start();
    }

    private Map<Integer, List<NewsChannelTable>> getNewsChannelData() {
        Map<Integer, List<NewsChannelTable>> newsChannelListMap = new HashMap<>();
        List<NewsChannelTable> channelTableListMine = NewsChannelTableManager.loadNewsChannelsMine();
        List<NewsChannelTable> channelTableListMore = NewsChannelTableManager.loadNewsChannelsMore();
        newsChannelListMap.put(Constants.NEWS_CHANNEL_MINE, channelTableListMine);
        newsChannelListMap.put(Constants.NEWS_CHANNEL_MORE, channelTableListMore);
        return newsChannelListMap;
    }
}
