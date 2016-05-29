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

import com.kaku.colorfulnews.bean.NewsSummary;
import com.kaku.colorfulnews.common.ApiConstants;
import com.kaku.colorfulnews.common.HostType;
import com.kaku.colorfulnews.interactor.NewsInteractor;

import java.util.List;
import java.util.Map;

import http.RetrofitManager;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * @author 咖枯
 * @version 1.0 2016/5/19
 */
public class NewsInteractorImpl implements NewsInteractor<List<NewsSummary>> {

    private String type = ApiConstants.HEADLINE_TYPE;
    private String id = ApiConstants.HEADLINE_ID;
    private int startPage = 0;

    @Override
    public void loadNews(final OnFinishedListener listener) {
        RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO).getNewsListObservable(type, id, startPage)
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

                    }

                    @Override
                    public void onNext(List<NewsSummary> newsSummaries) {
                        listener.onFinished(newsSummaries);
                    }
                });

/*                        Observable.from(createArrayList())
*//*                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        String[] str = new String[]{s.split(" ")[0], s.split(" ")[1]};
                        return Observable.from(str);
                    }
                })*//*
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !s.contains("12");
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s + "_rxjava";
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        listener.onFinished(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        list.add(s);
                    }
                });*/

    }
}
