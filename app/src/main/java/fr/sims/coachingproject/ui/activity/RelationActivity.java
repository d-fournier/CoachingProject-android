package fr.sims.coachingproject.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.CoachingLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.ui.adapter.RelationPagerAdapter;

public class RelationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<CoachingRelation>>{

    RelationPagerAdapter mRelationPagerAdapter;
    ViewPager mViewPager;
    Bundle mRelationArgs;
    List<CoachingRelation> mListRelations;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_relation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Tabs Pattern
        mRelationPagerAdapter = new RelationPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mRelationPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mRelationArgs = new Bundle();
        getLoaderManager().initLoader(0, mRelationArgs, (android.app.LoaderManager.LoaderCallbacks<Object>) this);

        ArrayList<CoachingRelation> mListRelations = new ArrayList<>();

    }

    @Override
    public Loader<List<CoachingRelation>> onCreateLoader(int id, Bundle args) {
        return new CoachingLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<CoachingRelation>> loader, List<CoachingRelation> data) {
        mListRelations=data;
        //SET DATA TO ADAPTER
    }

    @Override
    public void onLoaderReset(Loader<List<CoachingRelation>> loader) {

    }
}
