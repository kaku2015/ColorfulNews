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
package com.kaku.colorfulnews.ui.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.bean.NewsDetail;
import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.component.DaggerNewsDetailComponent;
import com.kaku.colorfulnews.module.NewsDetailModule;
import com.kaku.colorfulnews.presenter.NewsDetailPresenter;
import com.kaku.colorfulnews.ui.activities.base.BaseActivity;
import com.kaku.colorfulnews.utils.MyUtils;
import com.kaku.colorfulnews.view.NewsDetailView;
import com.kaku.colorfulnews.widget.URLImageGetter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 咖枯
 * @version 1.0 2016/6/5
 */
public class NewsDetailActivity extends BaseActivity implements NewsDetailView {
    @BindView(R.id.news_detail_photo_iv)
    ImageView mNewsDetailPhotoIv;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    /*    @BindView(R.id.news_detail_title_tv)
        TextView mNewsDetailTitleTv;*/
    @BindView(R.id.news_detail_from_tv)
    TextView mNewsDetailFromTv;
    @BindView(R.id.news_detail_body_tv)
    TextView mNewsDetailBodyTv;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Inject
    NewsDetailPresenter mNewsDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        init();
        setSupportActionBar(mToolbar);
    }

    private void init() {
        ButterKnife.bind(this);

        String postId = getIntent().getStringExtra(Constants.NEWS_POST_ID);
        DaggerNewsDetailComponent.builder()
                .newsDetailModule(new NewsDetailModule(this, postId))
                .build().Inject(this);

        mPresenter = mNewsDetailPresenter;
        mPresenter.onCreate();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setNewsDetail(NewsDetail newsDetail) {
        String newsTitle = newsDetail.getTitle();
        String newsSource = newsDetail.getSource();

        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
        String imgSrc;
        if (imgSrcs != null && imgSrcs.size() > 0) {
            imgSrc = imgSrcs.get(0).getSrc();
        } else {
            imgSrc = getIntent().getStringExtra(Constants.NEWS_IMG_RES);
        }

        String newsTime = MyUtils.formatDate(newsDetail.getPtime());
        String newsBody = newsDetail.getBody();

//        mNewsDetailTitleTv.setText(newsTitle);
        mNewsDetailFromTv.setText(getString(R.string.news_from, newsSource, newsTime));

        Glide.with(this).load(imgSrc).asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
//                .placeholder(R.mipmap.ic_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.mipmap.ic_load_fail)
                .into(mNewsDetailPhotoIv);

        if (mNewsDetailBodyTv != null) {
            if (App.isHavePhoto() && newsDetail.getImg().size() >= 2) {
///               mNewsDetailBodyTv.setMovementMethod(LinkMovementMethod.getInstance());//加这句才能让里面的超链接生效,实测经常卡机崩溃
                int total = newsDetail.getImg().size();
                URLImageGetter urlImageGetter = new URLImageGetter(mNewsDetailBodyTv, newsBody, total);
                mNewsDetailBodyTv.setText(Html.fromHtml(newsBody, urlImageGetter, null));
            } else {
                mNewsDetailBodyTv.setText(Html.fromHtml(newsBody));
            }
        }

        mToolbarLayout.setTitle(newsTitle);
        mToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_white));
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void showErrorMsg(String message) {
        mProgressBar.setVisibility(View.GONE);
        Snackbar.make(mAppBar, message, Snackbar.LENGTH_LONG).show();
    }
}
