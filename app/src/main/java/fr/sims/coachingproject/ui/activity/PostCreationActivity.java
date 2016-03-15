package fr.sims.coachingproject.ui.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.ui.fragment.BlogPreviewDialogFragment;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class PostCreationActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<Sport>> {

    private EditText mContentET;
    private EditText mTitleET;
    private EditText mShortDescription;
    private Button mCreateBt;
    private Spinner mSportsSpinner;

    List<Sport> mSportList;
    private ArrayAdapter<Sport> mSportsAdapter;


    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx, PostCreationActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);

        ActionBar ab = getSupportActionBar();
        if(ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        mTitleET = (EditText) findViewById(R.id.post_title);
        mShortDescription = (EditText) findViewById(R.id.post_description);
        mContentET = (EditText) findViewById(R.id.post_content);
        mCreateBt = (Button) findViewById(R.id.post_create);
        mCreateBt.setOnClickListener(this);

        mSportsSpinner = (Spinner) findViewById(R.id.post_spinner_sports);
        mSportList = new ArrayList<>();
        mSportsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mSportList);
        mSportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsSpinner.setAdapter(mSportsAdapter);

        getLoaderManager().initLoader(Const.Loaders.SPORT_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_post_creation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.post_preview:
                BlogPreviewDialogFragment.newInstance(mContentET.getText().toString()).show(getSupportFragmentManager(), BlogPreviewDialogFragment.TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.post_create:
                createPost();
                break;
            default:
                break;
        }
    }

    private void createPost() {
        mCreateBt.setEnabled(false);
        mTitleET.setEnabled(false);
        mShortDescription.setEnabled(false);
        mContentET.setEnabled(false);

        JSONObject json = new JSONObject();
        try {
            json.put("title", mTitleET.getText());
            json.put("description", mShortDescription.getText());
            json.put("content", mContentET.getText());
            json.put("sport", ((Sport) mSportsSpinner.getSelectedItem()).mIdDb);
        } catch (Exception e) { }
        String body = json.toString();

        new CreateBlogPostTask().execute(body);
    }

    @Override
    public Loader<List<Sport>> onCreateLoader(int id, Bundle args) {
        return new SportLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Sport>> loader, List<Sport> data) {
        mSportsAdapter.clear();
        if (data != null) {
            mSportsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Sport>> loader) {

    }

    private class CreateBlogPostTask extends AsyncTask<String, Void, NetworkUtil.Response>{
        @Override
        protected NetworkUtil.Response doInBackground(String... params) {
            if(params.length == 0)
                return null;
            String body = params[0];
            String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.BLOG;
            NetworkUtil.Response res = NetworkUtil.post(url,
                    SharedPrefUtil.getConnectedToken(getApplicationContext()), body);

            return res;
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if(response != null && response.isSuccessful()) {
                finish();
            } else {
                mCreateBt.setEnabled(true);
                mTitleET.setEnabled(true);
                mShortDescription.setEnabled(true);
                mContentET.setEnabled(true);
            }
        }
    }
}
