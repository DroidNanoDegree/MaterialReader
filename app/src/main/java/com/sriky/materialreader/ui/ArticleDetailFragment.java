package com.sriky.materialreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.sriky.materialreader.R;
import com.sriky.materialreader.data.ArticleLoader;
import com.sriky.materialreader.databinding.FragmentArticleDetailBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import timber.log.Timber;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARG_ITEM_ID = "item_id";
    private Cursor mCursor;
    private long mItemId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    private FragmentArticleDetailBinding mFragmentArticleDetailBinding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentArticleDetailBinding =
                FragmentArticleDetailBinding.inflate(inflater, container, false);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        getLoaderManager().initLoader(0, null, this);

        mFragmentArticleDetailBinding.shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        return mFragmentArticleDetailBinding.getRoot();
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

    private void bindViews() {
        if (mFragmentArticleDetailBinding.getRoot() == null) {
            return;
        }

        if (mCursor != null) {
            View rootView = mFragmentArticleDetailBinding.getRoot();
            rootView.setAlpha(0);
            rootView.setVisibility(View.VISIBLE);
            rootView.animate().alpha(1);

            setArticleBodyContent();

            String title = mCursor.getString(ArticleLoader.Query.TITLE);
            if (getActivity() instanceof ArticleDetailActivity) {
                ArticleDetailActivity parent = (ArticleDetailActivity) getActivity();
                parent.setAppBarTitle(title);
            } else {
                mFragmentArticleDetailBinding.articleTitle.setText(title);
            }

            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                mFragmentArticleDetailBinding.articleDate.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()));

            } else {
                // If date is before 1902, just show the string
                mFragmentArticleDetailBinding.articleDate.setText(Html.fromHtml(
                        outputFormat.format(publishedDate)));

            }

            mFragmentArticleDetailBinding.articleAuthor.setText(mCursor.getString(ArticleLoader.Query.AUTHOR));

            //TODO: Add a11y support!
            Picasso.with(getContext())
                    .load(Uri.parse(mCursor.getString(ArticleLoader.Query.THUMB_URL)))
                    //.placeholder(R.drawable.ic_cake_loading)
                    //.error(R.drawable.ic_error_pink)
                    .into(mFragmentArticleDetailBinding.photo);
        } else {
            mFragmentArticleDetailBinding.getRoot().setVisibility(View.GONE);
        }
    }

    private void setArticleBodyContent() {
        new AsyncTask<Void, Void, String>() {
            Snackbar mSnackbar;
            @Override
            protected void onPreExecute() {
                 mSnackbar = Snackbar
                        .make(mFragmentArticleDetailBinding.getRoot(),
                                getContext().getResources().getString(R.string.loading_data), Snackbar.LENGTH_LONG);

                mSnackbar.show();
                mFragmentArticleDetailBinding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... voids) {
                return Html.fromHtml(
                        mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")).toString();
            }

            @Override
            protected void onPostExecute(String s) {
                if(mSnackbar != null) {
                    mSnackbar.dismiss();
                }
                mFragmentArticleDetailBinding.progressBar.setVisibility(View.INVISIBLE);
                if (TextUtils.isEmpty(s)) {
                    Timber.e("Error parsing HTML text!");
                    return;
                }
                mFragmentArticleDetailBinding.articleBody.setText(s);
                mFragmentArticleDetailBinding.shareFab.setVisibility(View.VISIBLE);
            }
        }.execute();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Timber.e("Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }
}
