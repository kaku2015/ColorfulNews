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
package com.kaku.colorfulnews.mvp.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.greendao.NewsChannelTable;
import com.kaku.colorfulnews.mvp.ui.widget.ItemDragHelperCallback;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 咖枯
 * @version 1.0 2016/6/30
 */
public class NewsChannelAdapter extends RecyclerView.Adapter<NewsChannelAdapter.NewsChannelViewHolder> implements
        ItemDragHelperCallback.OnItemMoveListener {
    private static final int IS_CHANNEL_FIXED = 0;
    private static final int IS_CHANNEL_NO_FIXED = 1;

    private ItemDragHelperCallback mItemDragHelperCallback;

    public void setItemDragHelperCallback(ItemDragHelperCallback itemDragHelperCallback) {
        mItemDragHelperCallback = itemDragHelperCallback;
    }

    private List<NewsChannelTable> mNewsChannelTableList;

    public NewsChannelAdapter(List<NewsChannelTable> newsChannelTableList) {
        mNewsChannelTableList = newsChannelTableList;
    }

    @Override
    public NewsChannelViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_channel, parent, false);
        final NewsChannelViewHolder newsChannelViewHolder = new NewsChannelViewHolder(view);
        handleLongPress(newsChannelViewHolder);
        return newsChannelViewHolder;
    }

    private void handleLongPress(final NewsChannelViewHolder newsChannelViewHolder) {
        if (mItemDragHelperCallback != null) {
            newsChannelViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    NewsChannelTable newsChannel = mNewsChannelTableList.get(newsChannelViewHolder.getLayoutPosition());
                    boolean isChannelFixed = newsChannel.getNewsChannelFixed();
                    if (isChannelFixed) {
                        mItemDragHelperCallback.setLongPressEnabled(false);
                    } else {
                        mItemDragHelperCallback.setLongPressEnabled(true);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(NewsChannelViewHolder holder, int position) {
        final NewsChannelTable newsChannel = mNewsChannelTableList.get(position);
        String newsChannelName = newsChannel.getNewsChannelName();
        holder.mNewsChannelTv.setText(newsChannelName);

        if (newsChannel.getNewsChannelIndex() == 0) {
            holder.mNewsChannelTv.setTextColor(ContextCompat
                    .getColor(App.getAppContext(), R.color.alpha_40_black));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mNewsChannelTableList.get(position).getNewsChannelFixed()) {
            return IS_CHANNEL_FIXED;
        } else {
            return IS_CHANNEL_NO_FIXED;
        }
    }

    @Override
    public int getItemCount() {
        return mNewsChannelTableList.size();
    }

    @Override
    public boolean onItemMoved(int fromPosition, int toPosition) {
        if (isChannelFixed(fromPosition, toPosition)) {
            return false;
        }
        Collections.swap(mNewsChannelTableList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    private boolean isChannelFixed(int fromPosition, int toPosition) {
        return mNewsChannelTableList.get(fromPosition).getNewsChannelFixed() ||
                mNewsChannelTableList.get(toPosition).getNewsChannelFixed();
    }

    class NewsChannelViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_channel_tv)
        TextView mNewsChannelTv;

        public NewsChannelViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
