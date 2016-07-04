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
package com.kaku.colorfulnews.mvp.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.mvp.entity.NewsSummary;
import com.kaku.colorfulnews.listener.OnItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 咖枯
 * @version 1.0 2016/5/19
 */
public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_FOOTER = 1;
    private boolean mIsShowFooter;
    private List<NewsSummary> mNewsSummaryList;
    private OnItemClickListener mOnItemClickListener;
    private int mLastPosition = -1;

    @Inject
    public NewsListAdapter() {
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<NewsSummary> getNewsSummaryList() {
        return mNewsSummaryList;
    }

    public void setItems(List<NewsSummary> items) {
        mNewsSummaryList = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_footer, parent, false);
            return new FooterViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            final ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            setItemOnClick(itemViewHolder);
            return itemViewHolder;
        }
        throw new RuntimeException("there is no type that matches the type " +
                viewType + " + make sure your using types correctly");
    }

    private void setItemOnClick(final RecyclerView.ViewHolder holder) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsShowFooter && (getItemCount() - 1) == position) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        setItemValues(holder, position);
        setItemAppearAnimation(holder, position);
    }

    private void setItemValues(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            String title = mNewsSummaryList.get(position).getLtitle();
            if (title == null) {
                title = mNewsSummaryList.get(position).getTitle();
            }
            String ptime = mNewsSummaryList.get(position).getPtime();
            String digest = mNewsSummaryList.get(position).getDigest();
            String imgSrc = mNewsSummaryList.get(position).getImgsrc();

            ((ItemViewHolder) holder).mNewsSummaryTitleTv.setText(title);
            ((ItemViewHolder) holder).mNewsSummaryPtimeTv.setText(ptime);
            ((ItemViewHolder) holder).mNewsSummaryDigestTv.setText(digest);

            Glide.with(App.getAppContext()).load(imgSrc).asBitmap() // gif格式有时会导致整体图片不显示，貌似有冲突
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.image_place_holder)
                    .error(R.drawable.ic_load_fail)
                    .into(((ItemViewHolder) holder).mNewsSummaryPhotoIv);
        }
    }

    private void setItemAppearAnimation(RecyclerView.ViewHolder holder, int position) {
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_bottom_in);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.itemView.getAnimation() != null && holder.itemView
                .getAnimation().hasStarted()) {
            holder.itemView.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        int itemSize = mNewsSummaryList.size();
        if (mIsShowFooter) {
            itemSize += 1;
        }
        return itemSize;
    }

    public void addMore(List<NewsSummary> data) {
        int startPosition = mNewsSummaryList.size();
        mNewsSummaryList.addAll(data);
        notifyItemRangeInserted(startPosition, mNewsSummaryList.size());
    }

    public void showFooter() {
        mIsShowFooter = true;
        notifyItemInserted(getItemCount());
    }

    public void hideFooter() {
        mIsShowFooter = false;
        notifyItemRemoved(getItemCount());
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_summary_photo_iv)
        ImageView mNewsSummaryPhotoIv;
        @BindView(R.id.news_summary_title_tv)
        TextView mNewsSummaryTitleTv;
        @BindView(R.id.news_summary_Digest_tv)
        TextView mNewsSummaryDigestTv;
        @BindView(R.id.news_summary_ptime_tv)
        TextView mNewsSummaryPtimeTv;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }


}
