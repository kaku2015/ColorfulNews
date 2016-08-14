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
package com.kaku.colorfulnews.mvp.presenter.impl;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.PhotoRequestType;
import com.kaku.colorfulnews.listener.RequestCallBack;
import com.kaku.colorfulnews.mvp.interactor.impl.PhotoDetailInteractorImpl;
import com.kaku.colorfulnews.mvp.presenter.PhotoDetailPresenter;
import com.kaku.colorfulnews.mvp.presenter.base.BasePresenterImpl;
import com.kaku.colorfulnews.mvp.view.PhotoDetailView;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

/**
 * @author 咖枯
 * @version 1.0 2016/8/12
 */
public class PhotoDetailPresenterImpl extends BasePresenterImpl<PhotoDetailView, Uri> implements
        PhotoDetailPresenter, RequestCallBack<Uri> {

    private PhotoDetailInteractorImpl mPhotoDetailInteractor;
    private Activity mActivity;
    private int mRequestType = -1;

    @Inject
    public PhotoDetailPresenterImpl(PhotoDetailInteractorImpl photoDetailInteractor, Activity activity) {
        mPhotoDetailInteractor = photoDetailInteractor;
        mActivity = activity;
    }

    @Override
    public void success(Uri imageUri) {
        super.success(imageUri);
        switch (mRequestType) {
            case PhotoRequestType.TYPE_SHARE:
                share(imageUri);
                break;
            case PhotoRequestType.TYPE_SAVE:
                showSavePathMsg(imageUri);
                break;
            case PhotoRequestType.TYPE_SET_WALLPAPER:
                setWallpaper(imageUri);
                break;
        }
    }

    private void share(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        mActivity.startActivity(Intent.createChooser(intent, App.getAppContext().getString(R.string.share)));
    }

    private void showSavePathMsg(Uri imageUri) {
        mView.showMsg(mActivity.getString(R.string.picture_already_save_to, imageUri.getPath()));
    }

    private void setWallpaper(Uri imageUri) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(mActivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File wallpaperFile = new File(imageUri.getPath());
            Uri contentURI = getImageContentUri(mActivity, wallpaperFile.getAbsolutePath());
//                    Uri uri1 = getImageContentUri(mActivity, imageUri.getPath());
            mActivity.startActivity(wallpaperManager.getCropAndSetWallpaperIntent(contentURI));
        } else {
            try {
                wallpaperManager.setStream(mActivity.getContentResolver().openInputStream(imageUri));
                mView.showMsg(App.getAppContext().getString(R.string.set_wallpaper_success));
            } catch (IOException e) {
                KLog.e(e.toString());
                mView.showMsg(e.getMessage());
            }
        }
    }

    // http://stackoverflow.com/questions/23207604/get-a-content-uri-from-a-file-uri
    public Uri getImageContentUri(Context context, String absPath) {
        KLog.d("getImageContentUri: " + absPath);
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }

    @Override
    public void handlePicture(String imageUrl, @PhotoRequestType.PhotoRequestTypeChecker int type) {
        mRequestType = type;
        mPhotoDetailInteractor.saveImageAndGetImageUri(this, imageUrl);
    }
}
