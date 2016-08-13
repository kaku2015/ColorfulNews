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

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.mvp.interactor.PhotoDetailInteractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author 咖枯
 * @version 1.0 2016/8/12
 */
public class PhotoDetailInteractorImpl implements PhotoDetailInteractor<Uri> {

    @Inject
    public PhotoDetailInteractorImpl() {
    }

    @Override
    public Subscription saveImageAndGetImageUri(final RequestCallBack<Uri> listener, final String url) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {
                Glide.with(App.getAppContext())
                        .load(url)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                subscriber.onNext(bitmap);
                                subscriber.onCompleted();
                            }
                        });
            }

        })
                .subscribeOn(AndroidSchedulers.mainThread()) // glide 需要在主线程中运行
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Bitmap, Observable<Uri>>() {
                    @Override
                    public Observable<Uri> call(Bitmap bitmap) {
                        return getUriObservable(bitmap, url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError(App.getAppContext().getString(R.string.share_error));
                    }

                    @Override
                    public void onNext(Uri uri) {
                        listener.success(uri);
                    }
                });
    }

    @NonNull
    private Observable<Uri> getUriObservable(Bitmap bitmap, String url) {
        File file = getImageFile(bitmap, url);
        Uri uri = Uri.fromFile(file);
        // 通知图库更新
/*        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        App.getAppContext().sendBroadcast(scannerIntent);*/
        return Observable.just(uri);
    }

    @NonNull
    private File getImageFile(Bitmap bitmap, String url) {
        String fileName = "/photo/" + url.hashCode() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory(), fileName); // getFilesDir()等只能在程序内部访问不能用作分享路径
        if (!file.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
