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

package com.sriky.materialreader.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sriky.materialreader.R;
import com.sriky.materialreader.data.ArticleLoader;
import com.sriky.materialreader.event.Message;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import timber.log.Timber;

/**
 * Adaptor for the articles {@link android.support.v7.widget.RecyclerView} used in
 * {@link com.sriky.materialreader.ui.ArticleListFragment}
 */

public class ArticleListAdaptor extends RecyclerView.Adapter<ArticleListAdaptor.ViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
    private Context mContext;
    private Cursor mCursor;

    public ArticleListAdaptor(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view);
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Timber.e(ex.getMessage());
            Timber.i("passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            holder.articleAuthorView.setText(mCursor.getString(ArticleLoader.Query.AUTHOR));
            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                holder.articleDateView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + "<br/>"));
            } else {
                holder.articleDateView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate)
                                + "<br/>"));
            }
            //TODO: Add a11y support!
            Picasso.with(mContext)
                    .load(Uri.parse(mCursor.getString(ArticleLoader.Query.THUMB_URL)))
                    //.placeholder(R.drawable.ic_cake_loading)
                    //.error(R.drawable.ic_error_pink)
                    .into( holder.thumbnailView);

            //notify that data was loaded and pass the articleId so that ArticleActivity can
            //load the details fragment for tablets.
            if (position == 0) {
                EventBus.getDefault().post(new Message.ArticleDataLoaded(
                        mCursor.getLong(ArticleLoader.Query._ID)));
            }

            //set the tag which will be retrieved when the item is clicked.
            holder.itemView.setTag(mCursor.getLong(ArticleLoader.Query._ID));
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView articleAuthorView;
        public TextView articleDateView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.article_title);
            articleAuthorView = view.findViewById(R.id.article_author);
            articleDateView = view.findViewById(R.id.article_date);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            EventBus.getDefault().post(new Message.ArticleClicked((long) view.getTag()));
        }
    }
}
