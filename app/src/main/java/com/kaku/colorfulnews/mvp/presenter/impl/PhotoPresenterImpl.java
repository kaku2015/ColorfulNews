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

import com.kaku.colorfulnews.common.LoadNewsType;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.mvp.entity.PhotoGirl;
import com.kaku.colorfulnews.mvp.interactor.impl.PhotoInteractorImpl;
import com.kaku.colorfulnews.mvp.presenter.PhotoPresenter;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenterImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @author 咖枯
 * @version 1.0 2016/8/6
 */
public class PhotoPresenterImpl extends BasePresenterImpl<com.kaku.colorfulnews.mvp.view.PhotoView, List<PhotoGirl>>
        implements PhotoPresenter, RequestCallBack<List<PhotoGirl>> {
    private PhotoInteractorImpl mPhotoInteractor;
    private static int SIZE = 20;
    private int mStartPage = 1;
    private boolean misFirstLoad;
    private boolean mIsRefresh = true;

    @Inject
    public PhotoPresenterImpl(PhotoInteractorImpl photoInteractor) {
        mPhotoInteractor = photoInteractor;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadPhotoData();
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
            mView.setPhotoList(null, loadType);
        }
    }

    @Override
    public void success(List<PhotoGirl> items) {
        super.success(items);
        misFirstLoad = true;
        if (items != null) {
            mStartPage += 1;
        }

        int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_SUCCESS : LoadNewsType.TYPE_LOAD_MORE_SUCCESS;
        if (mView != null) {
            mView.setPhotoList(items, loadType);
            mView.hideProgress();
        }
    }

    @Override
    public void refreshData() {
        mStartPage = 1;
        mIsRefresh = true;
        loadPhotoData();
    }

    @Override
    public void loadMore() {
        mIsRefresh = false;
        loadPhotoData();
    }

    private void loadPhotoData() {
        mPhotoInteractor.loadPhotos(this, SIZE, mStartPage);
    }
}
