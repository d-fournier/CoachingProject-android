package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fr.sims.coachingproject.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "fr.sims.coachingproject.extra.URL";

    public static void startActivity(Context ctx, String url) {
        Intent intent = new Intent(ctx, ImagePreviewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);

        ImageView previewIV = (ImageView) findViewById(R.id.image_preview);

        Picasso.with(this).load(url).into(previewIV);

        PhotoViewAttacher attacher = new PhotoViewAttacher(previewIV);
    }
}
