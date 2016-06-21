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
package com.kaku.colorfulnews.mvp.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.inject.component.DaggerNewsComponent;
import com.kaku.colorfulnews.greendao.NewsChannelTable;
import com.kaku.colorfulnews.inject.module.NewsModule;
import com.kaku.colorfulnews.mvp.presenter.NewsPresenter;
import com.kaku.colorfulnews.mvp.ui.activities.base.BaseActivity;
import com.kaku.colorfulnews.mvp.ui.fragment.NewsListFragment;
import com.kaku.colorfulnews.utils.MyUtils;
import com.kaku.colorfulnews.mvp.view.NewsView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewsView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Inject
    NewsPresenter mNewsPresenter;

    private List<Fragment> mNewsFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setStatusBarTranslucent();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);

        DaggerNewsComponent.builder()
                .newsModule(new NewsModule(this))
                .build().inject(this);

        mPresenter = mNewsPresenter;
        mPresenter.onCreate();
    }

    @OnClick(R.id.fab)
    public void onClick() {
/*        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
        if (MyUtils.isNightMode()) {
            changeToDay();
            MyUtils.saveTheme(false);
        } else {
            changeToNight();
            MyUtils.saveTheme(true);
        }
        recreate();
    }

    @Override
    public void initViewPager(List<NewsChannelTable> newsChannels) {
        final List<String> channelNames = new ArrayList<>();
        if (newsChannels != null) {
            for (NewsChannelTable newsChannel : newsChannels) {
                NewsListFragment newsListFragment = createListFragments(newsChannel.getNewsChannelId(),
                        newsChannel.getNewsChannelType(), newsChannel.getNewsChannelIndex());
                mNewsFragmentList.add(newsListFragment);
                channelNames.add(newsChannel.getNewsChannelName());

            }

            //设置TabLayout的模式
            mTabs.setTabMode(TabLayout.MODE_FIXED);
            //为TabLayout添加tab名称
//        mTabs.addTab(mTabs.newTab().setText("要闻"));
//        mTabs.addTab(mTabs.newTab().setText("科技"));
//        mTabs.addTab(mTabs.newTab().setText("娱乐"));

            NewsFragmentPagerAdapter adapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(), channelNames);
            mViewPager.setAdapter(adapter);
            mTabs.setupWithViewPager(mViewPager);
//        mTabs.setTabsFromPagerAdapter(adapter);
        }
    }

    private NewsListFragment createListFragments(String newsId, String newsType, int channelPosition) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.NEWS_ID, newsId);
        bundle.putString(Constants.NEWS_TYPE, newsType);
        bundle.putInt(Constants.CHANNEL_POSITION, channelPosition);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showErrorMsg(String message) {
        Snackbar.make(mFab, message, Snackbar.LENGTH_SHORT).show();
    }

    private class NewsFragmentPagerAdapter extends FragmentPagerAdapter {

        private final List<String> mTitles;

        public NewsFragmentPagerAdapter(FragmentManager fm, List<String> titles) {
            super(fm);
            mTitles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mNewsFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mNewsFragmentList.size();
        }

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
