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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.mvp.ui.activities.base.BaseActivity;
import com.kaku.colorfulnews.utils.MyUtils;
import com.kaku.colorfulnews.widget.PullBackLayout;
import com.socks.library.KLog;

import butterknife.BindView;
import uk.co.senab.photoview.PhotoView;

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
//    PhotoViewAttacher mPhotoViewAttacher;

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
//        setupPhotoAttacher();
    }

    private void initToolbar() {
        mToolbar.setTitle("");
    }

    private void initImageView() {
        loadPhotoTouchIv();
        loadPhotoIv();
    }

    private void loadPhotoTouchIv() {
        Glide.with(this)
                .load(getIntent().getStringExtra(Constants.PHOTO_DETAIL))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.image_place_holder)
                .error(R.drawable.ic_load_fail)
                .into(mPhotoTouchIv);
    }

    private void loadPhotoIv() {
        Glide.with(this)
                .load(getIntent().getStringExtra(Constants.PHOTO_DETAIL))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mPhotoIv);
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

    //    private void setupPhotoAttacher() {
//        mPhotoViewAttacher = new PhotoViewAttacher(mPhotoIv);
//
//    }
    @Override
    public void onPullStart() {
        startAnimation(View.GONE, 0.9f, 0.5f);
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
        startAnimation(View.VISIBLE, 0.9f, 0.5f);
    }

    private void startAnimation(final int endState, float startValue, float endValue) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mToolbar, "alpha", startValue, endValue)
                .setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mToolbar.setAlpha(1.0f);
                mToolbar.setVisibility(endState);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    public void onPullComplete() {
        supportFinishAfterTransition();
    }
}
