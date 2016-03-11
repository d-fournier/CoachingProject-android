package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupLoader;
import fr.sims.coachingproject.loader.SingleGroupLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.ui.adapter.GroupPagerAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;


/**
 * Created by Zhenjie CEN on 2016/3/6.
 */
public class GroupActivity extends AppCompatActivity {

    private TextView mGroupName;
    private TextView mGroupDescription;
    private TextView mGroupCreationDate;
    private TextView mGroupSport;
    private TextView mGroupCity;

    private long mGroupIdDb;

    private GroupLoaderCallbacks mGroupLoader;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;

    public static void startActivity(Context ctx, long id) {
        Intent startIntent = new Intent(ctx, GroupActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.putExtra("groupIdDb", id);
        ctx.startActivity(startIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        mGroupIdDb = intent.getLongExtra("groupIdDb", -1);

        mGroupName = (TextView) findViewById(R.id.group_name);
        mGroupDescription = (TextView) findViewById(R.id.group_description);
        mGroupCreationDate = (TextView) findViewById(R.id.group_creation_date);
        mGroupSport = (TextView) findViewById(R.id.group_sport);
        mGroupCity = (TextView) findViewById(R.id.group_city);

        mGroupLoader = new GroupLoaderCallbacks();
        getSupportLoaderManager().initLoader(Const.Loaders.GROUP_LOADER_ID, null, mGroupLoader);


        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.groupMembersPager);
        mPagerAdapter = new GroupPagerAdapter(getSupportFragmentManager(), mGroupIdDb);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.view_group_tabs);
        mTabLayout.setupWithViewPager(mPager);

        mTabLayout.setVisibility(View.VISIBLE);
        mPager.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.join_group:
                new SendJoinTask().execute();
                return true;
            case R.id.invite_group:
                SearchActivity.startActivity(getApplicationContext(),true,mGroupIdDb);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class GroupLoaderCallbacks implements LoaderManager.LoaderCallbacks<Group> {

        @Override
        public Loader<Group> onCreateLoader(int id, Bundle args) {
            return new SingleGroupLoader(getApplicationContext(), mGroupIdDb);
        }

        @Override
        public void onLoadFinished(Loader<Group> loader, Group data) {
            try {
                mGroupName.setText(data.mName);
                mGroupDescription.setText(data.mDescription);
                mGroupCreationDate.setText(getString(R.string.created_on, data.mCreationDate));
                mGroupSport.setText(data.mSport.mName);
                mGroupCity.setText(data.mCity);
            } catch (NullPointerException e) {
                //TODO rajouter le catch de l'erreur
            }
        }

        @Override
        public void onLoaderReset(Loader<Group> loader) {

        }

    }

    private class SendJoinTask extends AsyncTask<Void, Void, NetworkUtil.Response> {

        @Override
        protected NetworkUtil.Response doInBackground(Void... params) {
            return NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mGroupIdDb + Const.WebServer.SEPARATOR
                            + Const.WebServer.JOIN+Const.WebServer.SEPARATOR,
                    SharedPrefUtil.getConnectedToken(getApplicationContext()), "");
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            Snackbar.make(mGroupName,response.getBody().replace("\"",""),Snackbar.LENGTH_LONG).show();
        }
    }


}


