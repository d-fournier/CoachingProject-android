package fr.sims.coachingproject.ui.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.ui.adapter.GroupPagerAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        Intent intent = getIntent();
        mGroupIdDb = intent.getLongExtra("groupIdDb",-1);

        mGroupName = (TextView) findViewById(R.id.group_name);
        mGroupDescription = (TextView) findViewById(R.id.group_description);
        mGroupCreationDate = (TextView) findViewById(R.id.group_creation_date);
        mGroupSport = (TextView) findViewById(R.id.group_sport);
        mGroupCity = (TextView) findViewById(R.id.group_city);

        mGroupLoader = new GroupLoaderCallbacks();
        getSupportLoaderManager().initLoader(0, null, mGroupLoader);


        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.groupMembersPager);
        mPagerAdapter = new GroupPagerAdapter(getSupportFragmentManager(), mGroupIdDb);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.view_group_tabs);
        mTabLayout.setupWithViewPager(mPager);

        mTabLayout.setVisibility(View.VISIBLE);
        mPager.setVisibility(View.VISIBLE);
    }

    public static void startActivity(Context ctx, long id) {
        Intent startIntent = new Intent(ctx, GroupActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.putExtra("groupIdDb", id);
        ctx.startActivity(startIntent);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    class GroupLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Group>> {

        @Override
        public Loader<List<Group>> onCreateLoader(int id, Bundle args) {
            return new GroupLoader(getApplicationContext(), mGroupIdDb);
        }

        @Override
        public void onLoadFinished(Loader<List<Group>> loader, List<Group> data) {
            try{
                Group myGroup = data.get(0);
                mGroupName.setText(myGroup.mName);
                mGroupDescription.setText(myGroup.mDescription);
                mGroupCreationDate.setText(myGroup.mCreationDate);
                mGroupSport.setText(myGroup.mSport.mName);
                mGroupCity.setText(myGroup.mCity);
            }catch(NullPointerException e){
                //TODO rajouter le catch de l'erreur
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Group>> loader) {

        }

    }

}
