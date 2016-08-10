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

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.LoadNewsType;
import com.kaku.colorfulnews.listener.OnItemClickListener;
import com.kaku.colorfulnews.mvp.entity.PhotoGirl;
import com.kaku.colorfulnews.mvp.presenter.impl.PhotoPresenterImpl;
import com.kaku.colorfulnews.mvp.ui.activities.base.BaseActivity;
import com.kaku.colorfulnews.mvp.ui.adapter.PhotoListAdapter;
import com.kaku.colorfulnews.mvp.view.PhotoView;
import com.kaku.colorfulnews.utils.NetUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author 咖枯
 * @version 1.0 2016/8/6
 */
public class PhotoActivity extends BaseActivity implements PhotoView, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.photo_rv)
    RecyclerView mPhotoRv;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty_view)
    TextView mEmptyView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Inject
    PhotoPresenterImpl mPhotoPresenter;
    @Inject
    PhotoListAdapter mPhotoListAdapter;

    private boolean mIsAllLoaded;

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initViews() {
        initSwipeRefreshLayout();
        initRecyclerView();
        setAdapterItemClickEvent();
        initPresenter();
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.gplus_colors));
    }

    private void initRecyclerView() {
        mPhotoRv.setHasFixedSize(true);
        mPhotoRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mPhotoRv.setItemAnimator(new DefaultItemAnimator());
        mPhotoRv.setAdapter(mPhotoListAdapter);
        setRvScrollEvent();
    }

    private void setRvScrollEvent() {
        mPhotoRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int[] lastVisibleItemPosition = ((StaggeredGridLayoutManager) layoutManager)
                        .findLastVisibleItemPositions(null);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                if (!mIsAllLoaded && visibleItemCount > 0 &&
                        (newState == RecyclerView.SCROLL_STATE_IDLE) &&
                        ((lastVisibleItemPosition[0] >= totalItemCount - 1) ||
                                (lastVisibleItemPosition[1] >= totalItemCount - 1))) {
                    mPhotoPresenter.loadMore();
                    mPhotoListAdapter.showFooter();
                    mPhotoRv.scrollToPosition(mPhotoListAdapter.getItemCount() - 1);
                }
            }

        });
    }

    private void setAdapterItemClickEvent() {
        mPhotoListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
    }

    private void initPresenter() {
        mPresenter = mPhotoPresenter;
        mPresenter.attachView(this);
        mPresenter.onCreate();
    }

    @Override
    public void initSupportActionBar() {
        setSupportActionBar(mToolbar);
    }

    @Override
    public void setPhotoList(List<PhotoGirl> photoGirls, @LoadNewsType.checker int loadType) {
        switch (loadType) {
            case LoadNewsType.TYPE_REFRESH_SUCCESS:
                mSwipeRefreshLayout.setRefreshing(false);
                mPhotoListAdapter.setList(photoGirls);
                mPhotoListAdapter.notifyDataSetChanged();
                checkIsEmpty(photoGirls);
                mIsAllLoaded = false;
                break;
            case LoadNewsType.TYPE_REFRESH_ERROR:
                mSwipeRefreshLayout.setRefreshing(false);
                checkIsEmpty(photoGirls);
                break;
            case LoadNewsType.TYPE_LOAD_MORE_SUCCESS:
                mPhotoListAdapter.hideFooter();
                if (photoGirls == null || photoGirls.size() == 0) {
                    mIsAllLoaded = true;
                    Snackbar.make(mPhotoRv, getString(R.string.no_more), Snackbar.LENGTH_SHORT).show();
                } else {
                    mPhotoListAdapter.addMore(photoGirls);
                }
                break;
            case LoadNewsType.TYPE_LOAD_MORE_ERROR:
                mPhotoListAdapter.hideFooter();
                break;
        }
    }

    private void checkIsEmpty(List<PhotoGirl> photoGirls) {
        if (photoGirls == null && mPhotoListAdapter.getList() == null) {
            mPhotoRv.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);

        } else {
            mPhotoRv.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
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
        if (NetUtil.isNetworkAvailable()) {
            Snackbar.make(mPhotoRv, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        mPhotoPresenter.refreshData();
    }

    @OnClick(R.id.fab)
    public void onClick() {
        mPhotoRv.getLayoutManager().scrollToPosition(0);
    }
}
