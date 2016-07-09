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
package com.kaku.colorfulnews.di.component;

import android.app.Activity;
import android.content.Context;

import com.kaku.colorfulnews.di.module.ActivityModule;
import com.kaku.colorfulnews.di.scope.ContextLife;
import com.kaku.colorfulnews.di.scope.PerActivity;
import com.kaku.colorfulnews.mvp.ui.activities.NewsActivity;
import com.kaku.colorfulnews.mvp.ui.activities.NewsChannelActivity;
import com.kaku.colorfulnews.mvp.ui.activities.NewsDetailActivity;
import com.kaku.colorfulnews.mvp.ui.activities.PhotoDetailActivity;

import dagger.Component;

/**
 * @author 咖枯
 * @version 1.0 2016/6/23
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    @ContextLife("Activity")
    Context getActivityContext();

    @ContextLife("Application")
    Context getApplicationContext();

    Activity getActivity();

    void inject(NewsActivity newsActivity);

    void inject(NewsDetailActivity newsDetailActivity);

    void inject(NewsChannelActivity newsChannelActivity);

    void inject(PhotoDetailActivity photoDetailActivity);
}
