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

package com.sriky.materialreader.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sriky.materialreader.R;
import com.sriky.materialreader.adaptor.ArticleListAdaptor;
import com.sriky.materialreader.data.ArticleLoader;
import com.sriky.materialreader.databinding.FragmentArticleListBinding;
import com.sriky.materialreader.utils.MaterialReaderUtils;

/**
 * Fragment responsible for displaying the {@link android.support.v7.widget.RecyclerView} associated
 * to the articles.
 */

public class ArticleListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private FragmentArticleListBinding mFragmentArticleListBinding;
    private ArticleListAdaptor mArticleListAdaptor;

    public ArticleListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mFragmentArticleListBinding =
                FragmentArticleListBinding.inflate(inflater, container, false);

        mArticleListAdaptor = new ArticleListAdaptor(getContext(), null);
        mArticleListAdaptor.setHasStableIds(true);
        mFragmentArticleListBinding.recyclerView.setAdapter(mArticleListAdaptor);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mFragmentArticleListBinding.recyclerView.setLayoutManager(sglm);

        getLoaderManager().initLoader(MaterialReaderUtils.ARTICLE_LIST_LOADER, null, ArticleListFragment.this);
        return mFragmentArticleListBinding.getRoot();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case MaterialReaderUtils.ARTICLE_LIST_LOADER: {
                return ArticleLoader.newAllArticlesInstance(getContext());
            }

            default: {
                throw new RuntimeException("Unsupported loaderId: " + i);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mArticleListAdaptor.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArticleListAdaptor.swapCursor(null);
    }
}
