package com.kaku.colorfulnews.mvp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.mvp.ui.fragment.base.BaseFragment;

import butterknife.BindView;
import uk.co.senab.photoview.PhotoView;

public class PhotoDetailFragment extends BaseFragment {

    @BindView(R.id.photo_view)
    PhotoView mPhotoView;

    private String mImgSrc;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImgSrc = getArguments().getString(Constants.PHOTO_DETAIL_IMGSRC);
        }
    }

    @Override
    public void initInjector() {
    }

    @Override
    public void initViews(View view) {
        Glide.with(App.getAppContext()).load(mImgSrc).asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.image_place_holder)
                .error(R.drawable.ic_load_fail)
                .into(mPhotoView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_photo_detail;
    }

}
