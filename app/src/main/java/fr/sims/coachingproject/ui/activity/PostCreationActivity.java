package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.ui.view.MarkDownView;

public class PostCreationActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mContentET;
    MarkDownView mMdPreview;

    Button mRenderBt;

    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx, PostCreationActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);

        mContentET = (EditText) findViewById(R.id.post_content);
        mMdPreview = (MarkDownView) findViewById(R.id.post_preview);
        mRenderBt = (Button) findViewById(R.id.post_render);

        mRenderBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.post_render:
                mMdPreview.setMarkdown(mContentET.getText().toString());
                break;
        }
    }
}
