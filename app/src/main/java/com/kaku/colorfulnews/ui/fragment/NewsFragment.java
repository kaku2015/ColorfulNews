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
package com.kaku.colorfulnews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.bean.NewsSummary;
import com.kaku.colorfulnews.component.DaggerNewsComponent;
import com.kaku.colorfulnews.module.NewsModule;
import com.kaku.colorfulnews.presenter.NewsPresenter;
import com.kaku.colorfulnews.ui.adapter.NewsRecyclerViewAdapter;
import com.kaku.colorfulnews.ui.fragment.base.BaseFragment;
import com.kaku.colorfulnews.view.NewsView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 咖枯
 * @version 1.0 2016/5/18
 */
public class NewsFragment extends BaseFragment implements NewsView {
    @BindView(R.id.news_rv)
    RecyclerView mNewsRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Inject
    NewsRecyclerViewAdapter mNewsRecyclerViewAdapter;
    @Inject
    NewsPresenter mNewsPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        mNewsRV.setHasFixedSize(true);
        mNewsRV.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));

        DaggerNewsComponent.builder()
                .newsModule(new NewsModule(this))
                .build()
                .inject(this);
        mNewsPresenter.onCreateView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void setItems(List<NewsSummary> items) {
        mNewsRecyclerViewAdapter.setItems(items);
        mNewsRV.setAdapter(mNewsRecyclerViewAdapter);
    }

    @Override
    public void showMessage(String message) {
    }

    @Override
    public void onDestroyView() {
        mNewsPresenter.onDestroy();
        super.onDestroyView();
    }
}
