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
package com.kaku.colorfulnews.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.bean.NewsSummary;
import com.kaku.colorfulnews.listener.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 咖枯
 * @version 1.0 2016/5/19
 */
public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

    private List<NewsSummary> mNewsSummaryList;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<NewsSummary> getNewsSummaryList() {
        return mNewsSummaryList;
    }

    public void setItems(List<NewsSummary> items) {
        this.mNewsSummaryList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String title = mNewsSummaryList.get(position).getLtitle();
        if (title == null) {
            title = mNewsSummaryList.get(position).getTitle();
        }
        String ptime = mNewsSummaryList.get(position).getPtime();
        String digest = mNewsSummaryList.get(position).getDigest();
        String imgSrc = mNewsSummaryList.get(position).getImgsrc();

        holder.mNewsSummaryTitleTv.setText(title);
        holder.mNewsSummaryPtimeTv.setText(ptime);
        holder.mNewsSummaryDigestTv.setText(digest);

        Glide.with(App.getAppContext()).load(imgSrc)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.mipmap.ic_loading)
                .error(R.mipmap.ic_load_fail)
                .into(holder.mNewsSummaryPhotoIv);


    }

    @Override
    public int getItemCount() {
        return mNewsSummaryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_summary_photo_iv)
        ImageView mNewsSummaryPhotoIv;
        @BindView(R.id.news_summary_title_tv)
        TextView mNewsSummaryTitleTv;
        @BindView(R.id.news_summary_Digest_tv)
        TextView mNewsSummaryDigestTv;
        @BindView(R.id.news_summary_ptime_tv)
        TextView mNewsSummaryPtimeTv;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
