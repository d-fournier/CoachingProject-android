package fr.sims.coachingproject.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.BlogPostLoader;
import fr.sims.coachingproject.model.BlogPost;
import fr.sims.coachingproject.ui.view.MarkDownView;
import fr.sims.coachingproject.util.Const;

public class PostReadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<BlogPost> {
    private static final String EXTRA_POST_ID = "fr.sims.coachingproject.extra.POST_ID";

    private long mPostId;
    private CollapsingToolbarLayout mCollapsToolbar;
    private ImageView mPictureIV;
    private MarkDownView mMarkDownView;

    public static void startActivityWithAnimation(Activity activityCtx, long id, View picture) {
        Intent intent = new Intent(activityCtx, PostReadActivity.class);
        intent.putExtra(EXTRA_POST_ID, id);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activityCtx, picture,
                            activityCtx.getResources().getString(R.string.transition_post_picture));
            activityCtx.startActivity(intent, options.toBundle());
        } else {
            activityCtx.startActivity(intent);
        }

    }

    public static void startActivity(Context ctx, long id) {
        Intent intent = new Intent(ctx, PostReadActivity.class);
        intent.putExtra(EXTRA_POST_ID, id);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_read);

        Intent mIntent = getIntent();
        mPostId= mIntent.getLongExtra(EXTRA_POST_ID, -1);

        mMarkDownView = (MarkDownView) findViewById(R.id.post_content);
        mCollapsToolbar= (CollapsingToolbarLayout) findViewById(R.id.post_collapsing_toolbar);
        mPictureIV = (ImageView) findViewById(R.id.post_picture);

        getSupportLoaderManager().initLoader(Const.Loaders.BLOG_POST_LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<BlogPost> onCreateLoader(int id, Bundle args) {
        return new BlogPostLoader(this, mPostId);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<BlogPost> loader, BlogPost data) {
        if(data == null)
            return;
        mMarkDownView.setMarkdown(data.mContent);
        Picasso.with(this).load(data.mPicture).into(mPictureIV);
        mCollapsToolbar.setTitle(data.mTitle);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<BlogPost> loader) { }
}
