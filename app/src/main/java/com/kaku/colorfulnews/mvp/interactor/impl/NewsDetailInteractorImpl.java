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
package com.kaku.colorfulnews.mvp.interactor.impl;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.bean.NewsDetail;
import com.kaku.colorfulnews.common.HostType;
import com.kaku.colorfulnews.http.RetrofitManager;
import com.kaku.colorfulnews.mvp.interactor.NewsDetailInteractor;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.socks.library.KLog;

import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author 咖枯
 * @version 1.0 2016/6/4
 */
public class NewsDetailInteractorImpl implements NewsDetailInteractor<NewsDetail> {

    @Override
    public Subscription loadNewsDetail(final RequestCallBack<NewsDetail> callBack, final String postId) {
        return RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO).getNewsDetailObservable(postId)
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Map<String, NewsDetail>, NewsDetail>() {
                    @Override
                    public NewsDetail call(Map<String, NewsDetail> map) {
                        NewsDetail newsDetail = map.get(postId);
                        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
                        if (imgSrcs != null && imgSrcs.size() >= 2 && App.isHavePhoto()) {
                            String newsBody = newsDetail.getBody();
                            for (int i = 1; i < imgSrcs.size(); i++) {
                                String oldChars = "<!--IMG#" + i + "-->";
                                String newChars = "<img src=\"" + imgSrcs.get(i).getSrc() + "\" />";
                                newsBody = newsBody.replace(oldChars, newChars);

                            }
                            newsDetail.setBody(newsBody);
                        }
                        return newsDetail;
                    }
                })
                .subscribe(new Observer<NewsDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e.toString());
                        callBack.onError(App.getAppContext().getString(R.string.load_error));
                    }

                    @Override
                    public void onNext(NewsDetail newsDetail) {
                        callBack.success(newsDetail);
                    }
                });
    }
}
