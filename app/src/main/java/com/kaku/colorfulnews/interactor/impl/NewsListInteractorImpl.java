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
package com.kaku.colorfulnews.interactor.impl;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.bean.NewsSummary;
import com.kaku.colorfulnews.common.ApiConstants;
import com.kaku.colorfulnews.common.HostType;
import com.kaku.colorfulnews.interactor.NewsListInteractor;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.socks.library.KLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import http.RetrofitManager;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * @author 咖枯
 * @version 1.0 2016/5/19
 */
public class NewsListInteractorImpl implements NewsListInteractor<List<NewsSummary>> {

    private boolean mIsNetError;

    @Override
    public void loadNews(final RequestCallBack<List<NewsSummary>> listener, String type,
                         final String id, int startPage) {
        mIsNetError = false;
        // 对API调用了observeOn(MainThread)之后，线程会跑在主线程上，包括onComplete也是，
        // unsubscribe也在主线程，然后如果这时候调用call.cancel会导致NetworkOnMainThreadException
        // 加一句unsubscribeOn(io)
        RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO).getNewsListObservable(type, id, startPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .flatMap(new Func1<Map<String, List<NewsSummary>>, Observable<NewsSummary>>() {
                    @Override
                    public Observable<NewsSummary> call(Map<String, List<NewsSummary>> map) {
                        if (id.endsWith(ApiConstants.HOUSE_ID)) {
                            // 房产实际上针对地区的它的id与返回key不同
                            return Observable.from(map.get("北京"));
                        }
                        return Observable.from(map.get(id));
                    }
                })
                .map(new Func1<NewsSummary, NewsSummary>() {
                    @Override
                    public NewsSummary call(NewsSummary newsSummary) {
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
                                    .parse(newsSummary.getPtime());
                            String ptime = new SimpleDateFormat("MM-dd hh:mm", Locale.getDefault()).format(date);
                            newsSummary.setPtime(ptime);
                        } catch (ParseException e) {
                            KLog.e("转换新闻日期格式异常：" + e.toString());
                        }
                        return newsSummary;
                    }
                })
                .toSortedList(new Func2<NewsSummary, NewsSummary, Integer>() {
                    @Override
                    public Integer call(NewsSummary newsSummary, NewsSummary newsSummary2) {
                        return newsSummary2.getPtime().compareTo(newsSummary.getPtime());
                    }
                })
                .subscribe(new Subscriber<List<NewsSummary>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e.getLocalizedMessage() + "\n" + e.toString());
//                        checkNetState(listener);
//                        if (!mIsNetError) {
                        listener.onError(App.getAppContext().getString(R.string.load_error));
//                        }
                    }

                    @Override
                    public void onNext(List<NewsSummary> newsSummaries) {
//                        checkNetState(listener);
                        listener.success(newsSummaries);
                    }
                });

    }
/*
    private void checkNetState(RequestCallback<List<NewsSummary>> listener) {
        if (!NetUtil.isNetworkAvailable(App.getAppContext())) {
            mIsNetError = true;
            listener.onError(App.getAppContext().getString(R.string.internet_error));
        } else {
            mIsNetError = false;
        }
    }*/
}
