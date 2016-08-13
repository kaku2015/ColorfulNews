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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.mvp.interactor.impl.PhotoDetailInteractorImpl;
import com.kaku.colorfulnews.mvp.presenter.PhotoDetailPresenter;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenterImpl;
import com.kaku.colorfulnews.mvp.view.PhotoDetailView;

import javax.inject.Inject;

/**
 * @author 咖枯
 * @version 1.0 2016/8/12
 */
public class PhotoDetailPresenterImpl extends BasePresenterImpl<PhotoDetailView, Uri> implements
        PhotoDetailPresenter, RequestCallBack<Uri> {

    private PhotoDetailInteractorImpl mPhotoDetailInteractor;
    private Activity mActivity;

    @Inject
    public PhotoDetailPresenterImpl(PhotoDetailInteractorImpl photoDetailInteractor, Activity activity) {
        mPhotoDetailInteractor = photoDetailInteractor;
        mActivity = activity;
    }

    @Override
    public void shareUri(String imageUrl) {
        mPhotoDetailInteractor.saveImageAndGetImageUri(this, imageUrl);
    }

    @Override
    public void success(Uri uri) {
        super.success(uri);
        share(uri);
    }

    private void share(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        mActivity.startActivity(intent);
    }
}
