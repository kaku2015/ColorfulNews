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

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.mvp.interactor.PhotoDetailInteractor;
import com.kaku.colorfulnews.utils.TransformUtils;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

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
                KLog.d(Thread.currentThread().getName());

                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.with(App.getAppContext())
                            .load(url)
                            .get();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
                if (bitmap == null) {
                    subscriber.onError(new Exception("下载图片失败"));
                }
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            }

        })
//                .observeOn(Schedulers.io())
                .flatMap(new Func1<Bitmap, Observable<Uri>>() {
                    @Override
                    public Observable<Uri> call(Bitmap bitmap) {
                        KLog.d(Thread.currentThread().getName());

                        return getUriObservable(bitmap, url);
                    }
                })
                .compose(TransformUtils.<Uri>defaultSchedulers())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e.toString());
                        listener.onError(App.getAppContext().getString(R.string.error_try_again));
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
        if (file == null) {
            return Observable.error(new NullPointerException("Save image file failed!"));
        }
        Uri uri = Uri.fromFile(file);
        // 通知图库更新 //Update the System --> MediaStore.Images.Media --> 更新ContentUri
        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        App.getAppContext().sendBroadcast(scannerIntent);
        return Observable.just(uri);
    }

    private File getImageFile(Bitmap bitmap, String url) {
        String fileName = "/colorful_news/photo/" + url.hashCode() + ".jpg";
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
            return null;
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
