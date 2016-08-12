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
package com.kaku.colorfulnews.mvp.ui.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.mvp.ui.activities.base.BaseActivity;
import com.kaku.colorfulnews.utils.SystemUiVisibilityUtil;
import com.kaku.colorfulnews.utils.MyUtils;
import com.kaku.colorfulnews.widget.PullBackLayout;
import com.socks.library.KLog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author 咖枯
 * @version 1.0 2016/8/11
 */
public class PhotoDetailActivity extends BaseActivity implements PullBackLayout.Callback {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.photo_iv)
    ImageView mPhotoIv;
    @BindView(R.id.pull_back_layout)
    PullBackLayout mPullBackLayout;
    @BindView(R.id.photo_touch_iv)
    PhotoView mPhotoTouchIv;

    private ColorDrawable mBackground;
    private boolean mIsToolBarHidden;
    private boolean mIsStatusBarHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPullBackLayout.setCallback(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo_detail;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initViews() {
        initToolbar();
        initImageView();
        initBackground();
    }

    private void initToolbar() {
        mToolbar.setTitle(getString(R.string.girl));
    }

    private void initImageView() {
        loadPhotoIv();
    }

    private void loadPhotoIv() {
        Glide.with(this)
                .load(getIntent().getStringExtra(Constants.PHOTO_DETAIL))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mSubscription = Observable.timer(500, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Long>() {
                                    @Override
                                    public void call(Long aLong) {
                                        loadPhotoTouchIv();
                                        setPhotoViewClickEvent();
                                    }
                                });
                        return false;
                    }
                })
                .into(mPhotoIv);
    }

    private void loadPhotoTouchIv() {
        Glide.with(this)
                .load(getIntent().getStringExtra(Constants.PHOTO_DETAIL))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_load_fail)
                .into(mPhotoTouchIv);
    }

    private void setPhotoViewClickEvent() {
        mPhotoTouchIv.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                KLog.d();
                hideOrShowToolbar();
                hideOrShowStatusBar();
            }

            @Override
            public void onOutsidePhotoTap() {
                KLog.d();
                hideOrShowToolbar();
                hideOrShowStatusBar();
            }
        });
    }

    protected void hideOrShowToolbar() {
        mToolbar.animate()
                .alpha(mIsToolBarHidden ? 1.0f : 0.0f)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mIsToolBarHidden = !mIsToolBarHidden;
    }

    private void hideOrShowStatusBar() {
        if (mIsStatusBarHidden) {
            SystemUiVisibilityUtil.enter(PhotoDetailActivity.this);
        } else {
            SystemUiVisibilityUtil.exit(PhotoDetailActivity.this);
        }
        mIsStatusBarHidden = !mIsStatusBarHidden;
    }

    @SuppressWarnings("deprecation")
    private void initBackground() {
        mBackground = new ColorDrawable(Color.BLACK);
        MyUtils.getRootView(this).setBackgroundDrawable(mBackground);
    }

    @Override
    public void initSupportActionBar() {
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onPullStart() {
        mIsToolBarHidden = false;
        hideOrShowToolbar();
        mIsStatusBarHidden = true;
        hideOrShowStatusBar();
    }

    @Override
    public void onPull(float progress) {
        KLog.d("progress: " + progress);
        progress = Math.min(1f, progress * 3f);
        KLog.d("alpha: " + (int) (0xff * (1f - progress)));
        mBackground.setAlpha((int) (0xff/*255*/ * (1f - progress)));
    }

    @Override
    public void onPullCancel() {
        mIsToolBarHidden = true;
        hideOrShowToolbar();
    }

    @Override
    public void onPullComplete() {
        supportFinishAfterTransition();
    }
}
