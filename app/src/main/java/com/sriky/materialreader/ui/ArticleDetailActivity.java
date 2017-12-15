package com.sriky.materialreader.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sriky.materialreader.R;
import com.sriky.materialreader.databinding.ActivityArticleDetailBinding;
import com.sriky.materialreader.utils.MaterialReaderUtils;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    public static final String ARTICLE_ID_BUNDLE_KEY = "article_id";

    private ActivityArticleDetailBinding mActivityArticleDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityArticleDetailBinding = DataBindingUtil.setContentView(ArticleDetailActivity.this,
                R.layout.activity_article_detail);

        //set toolbar as the actionbar.
        setSupportActionBar(mActivityArticleDetailBinding.detailToolbar);
        //enable the back-key button in the actionbar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent == null) {
            throw new RuntimeException("Cannot launch without intent!");
        }

        Bundle data = intent.getExtras();
        if (data == null || !data.containsKey(ARTICLE_ID_BUNDLE_KEY)) {
            throw new RuntimeException("Activity requires ArticleId to be set with intent bundle!");
        }

        //add the detail fragment.
        getSupportFragmentManager().beginTransaction()
                .add(R.id.article_detail_fragment_container,
                        MaterialReaderUtils.buildArticleDetailFragment(data.getLong(ARTICLE_ID_BUNDLE_KEY)))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
