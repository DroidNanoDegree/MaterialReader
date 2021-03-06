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

package com.sriky.materialreader.utils;

import android.os.Bundle;

import com.sriky.materialreader.ui.ArticleDetailFragment;

/**
 * Utility Class for MaterialReader.
 */

public final class MaterialReaderUtils {

    public static final int ARTICLE_LIST_LOADER = 1;

    public static ArticleDetailFragment buildArticleDetailFragment(long articleId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ArticleDetailFragment.ARG_ITEM_ID, articleId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }
}
