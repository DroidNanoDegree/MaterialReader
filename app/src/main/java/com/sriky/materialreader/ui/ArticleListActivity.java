package com.sriky.materialreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.sriky.materialreader.R;
import com.sriky.materialreader.data.UpdaterService;
import com.sriky.materialreader.databinding.ActivityArticleListBinding;
import com.sriky.materialreader.event.Message;
import com.sriky.materialreader.utils.MaterialReaderUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity {
    private static String SELECTED_ARTICLE_ID_BUNDLE_KEY = "selected_article_id";
    private boolean mIsRefreshing = false;
    private boolean mTwoPane = false;
    private boolean mCanUpdateDetailsFragment;
    private long mPreviousSelectedArticleId = 0;
    private ActivityArticleListBinding mActivityArticleListBinding;
    private ArticleListFragment mArticleListFragment;
    private Snackbar mRefreshingSnackbar;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityArticleListBinding = DataBindingUtil.setContentView(ArticleListActivity.this,
                R.layout.activity_article_list);

        mTwoPane = getResources().getBoolean(R.bool.isTablet);
        if (savedInstanceState == null) {
            Timber.plant(new Timber.DebugTree());
            refresh();

            //add ArticleListFragment.
            mArticleListFragment = new ArticleListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.article_list_container, mArticleListFragment)
                    .commit();

            //flag to make sure details fragment isn't updated after configuration change.
            mCanUpdateDetailsFragment = true;
        } else if (mTwoPane && savedInstanceState.containsKey(SELECTED_ARTICLE_ID_BUNDLE_KEY)) {
            mPreviousSelectedArticleId = savedInstanceState.getLong(SELECTED_ARTICLE_ID_BUNDLE_KEY);
        }
    }

    public void refresh() {
        mRefreshingSnackbar = Snackbar.make(mActivityArticleListBinding.getRoot(),
                getString(R.string.fetching_data), Snackbar.LENGTH_LONG);
        mRefreshingSnackbar.show();
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
        EventBus.getDefault().register(ArticleListActivity.this);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mRefreshingReceiver);
        EventBus.getDefault().unregister(ArticleListActivity.this);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(SELECTED_ARTICLE_ID_BUNDLE_KEY, mPreviousSelectedArticleId);
        super.onSaveInstanceState(outState);
    }

    private void updateRefreshingUI() {
        if(mRefreshingSnackbar != null && !mIsRefreshing) {
            mRefreshingSnackbar.dismiss();
        }
        if(mArticleListFragment != null) {
            mArticleListFragment.updateSwipeRefresh(mIsRefreshing);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveArticleDataLoaded(Message.ArticleDataLoaded event) {
        if (mTwoPane && mCanUpdateDetailsFragment) {
            updateArticleDetailFragment(event.getArcticleId());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArcticleClicked(Message.ArticleClicked event) {
        if (mTwoPane) {
            updateArticleDetailFragment(event.getArcticleId());
        } else {
            Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.ARTICLE_ID_BUNDLE_KEY, event.getArcticleId());
            startActivity(intent);
        }
    }

    private void updateArticleDetailFragment(long articleId) {
        //don't replace ArticleDetailFragment when
        //there is a configuration change or a particular article is already displayed.
        Timber.d("mPreviousSelectedArticleId: %d, articleId: %d", mPreviousSelectedArticleId,
                articleId);
        if (mPreviousSelectedArticleId != articleId) {
            mPreviousSelectedArticleId = articleId;
            //add the fragment.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.article_detail_container,
                            MaterialReaderUtils.buildArticleDetailFragment(articleId))
                    .commit();
        }
    }
}
