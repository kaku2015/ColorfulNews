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
package com.kaku.colorfulnews.repository.db;

import com.kaku.colorfulnews.App;
import com.kaku.colorfulnews.R;
import com.kaku.colorfulnews.common.ApiConstants;
import com.kaku.colorfulnews.common.Constants;
import com.kaku.colorfulnews.greendao.NewsChannelTable;
import com.kaku.colorfulnews.greendao.NewsChannelTableDao;
import com.kaku.colorfulnews.utils.MyUtils;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * @author 咖枯
 * @version 1.0 2016/6/2
 */
public class NewsChannelTableManager {

    /**
     * 首次打开程序初始化db
     */
    public static void initDB() {
        if (!MyUtils.getSharedPreferences().getBoolean(Constants.INIT_DB, false)) {
            NewsChannelTableDao dao = App.getNewsChannelTableDao();
            List<String> channelName = Arrays.asList(App.getAppContext().getResources()
                    .getStringArray(R.array.news_channel_name));
            List<String> channelId = Arrays.asList(App.getAppContext().getResources()
                    .getStringArray(R.array.news_channel_id));
            for (int i = 0; i < channelName.size(); i++) {
                NewsChannelTable entity = new NewsChannelTable(channelName.get(i), channelId.get(i)
                        , ApiConstants.getType(channelId.get(i)), i <= 10, i, i == 0);
                dao.insert(entity);
            }
            MyUtils.getSharedPreferences().edit().putBoolean(Constants.INIT_DB, true).apply();
        }
    }

    public static List<NewsChannelTable> loadNewsChannelsMine() {
        Query<NewsChannelTable> newsChannelTableQuery = App.getNewsChannelTableDao().queryBuilder()
                .where(NewsChannelTableDao.Properties.NewsChannelSelect.eq(true))
                .orderAsc(NewsChannelTableDao.Properties.NewsChannelIndex).build();
        return newsChannelTableQuery.list();
    }

    public static List<NewsChannelTable> loadNewsChannelsMore() {
        Query<NewsChannelTable> newsChannelTableQuery = App.getNewsChannelTableDao().queryBuilder()
                .where(NewsChannelTableDao.Properties.NewsChannelSelect.eq(false))
                .orderAsc(NewsChannelTableDao.Properties.NewsChannelIndex).build();
        return newsChannelTableQuery.list();
    }

    public static NewsChannelTable loadNewsChannel(int position) {
        return App.getNewsChannelTableDao().queryBuilder()
                .where(NewsChannelTableDao.Properties.NewsChannelIndex.eq(position)).build().unique();
    }

    public static void update(NewsChannelTable newsChannelTable) {
        App.getNewsChannelTableDao().update(newsChannelTable);
    }

    public static List<NewsChannelTable> LoadNewsChannelsWithin(int from, int to) {
        Query<NewsChannelTable> newsChannelTableQuery = App.getNewsChannelTableDao().queryBuilder()
                .where(NewsChannelTableDao.Properties.NewsChannelIndex
                        .between(from, to)).build();
        return newsChannelTableQuery.list();

    }
}
