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

import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.mvp.ui.activities.base.BaseActivity;

import butterknife.BindView;

/**
 * @author 咖枯
 * @version 1.0 2016/8/21
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.msg_tv)
    TextView mMsgTv;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initViews() {
        mMsgTv.setAutoLinkMask(Linkify.ALL);
        mMsgTv.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
