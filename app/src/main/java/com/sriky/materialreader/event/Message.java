/*
 * Copyright (C) 2017 Srikanth Basappa
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
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.sriky.materialreader.event;

/**
 * Class containing all the events used by {@link org.greenrobot.eventbus.EventBus}
 */

public final class Message {

    /**
     * Event to notify when {@link android.support.v7.widget.RecyclerView} data is loaded
     * for the {@link com.sriky.materialreader.ui.ArticleListFragment}
     */
    public static class ArticleDataLoaded {
        private long mArticleId;

        public ArticleDataLoaded(long id) {
            mArticleId = id;
        }

        public long getArcticleId() {
            return mArticleId;
        }

    }

    /**
     * Event to notify when {@link android.support.v7.widget.RecyclerView} item from
     * the {@link com.sriky.materialreader.ui.ArticleListFragment} is clicked.
     */
    public static class ArticleClicked {
        private long mArticleId;

        public ArticleClicked(long id) {
            mArticleId = id;
        }

        public long getArcticleId() {
            return mArticleId;
        }

    }
}
