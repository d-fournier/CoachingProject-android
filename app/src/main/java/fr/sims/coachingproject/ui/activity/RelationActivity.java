package fr.sims.coachingproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.CoachingLoader;
import fr.sims.coachingproject.loader.RelationLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.ui.adapter.RelationPagerAdapter;

public class RelationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CoachingRelation>{

    RelationPagerAdapter mRelationPagerAdapter;
    ViewPager mViewPager;
    Bundle mRelationArgs;
    CoachingRelation mRelation;
    private long mId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_relation);

        // Get the transferred id
        Intent mIntent = getIntent();
        mId = mIntent.getLongExtra("id", 0);

     //   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        // Tabs Pattern
        mRelationPagerAdapter = new RelationPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.messagePager);
        mViewPager.setAdapter(mRelationPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mRelationArgs = new Bundle();
    //    getSupportLoaderManager().initLoader(0, null, this);

        TextView city = (TextView) findViewById(R.id.city);
        TextView name = (TextView) findViewById(R.id.name);
        TextView age = (TextView) findViewById(R.id.age);
        TextView sport = (TextView) findViewById(R.id.sport);

        city.setText("Lyon");
        name.setText("Machin Truc");
        age.setText("13/01/1993");
        sport.setText("Badminton");


    }

    @Override
    public Loader<CoachingRelation> onCreateLoader(int id, Bundle args) {
        return new RelationLoader(this, mId);
    }

    @Override
    public void onLoadFinished(Loader<CoachingRelation> loader, CoachingRelation data) {
        mRelation=data;
    //    ImageView picture = (ImageView) findViewById(R.id.image);
      /*  TextView city = (TextView) findViewById(R.id.city);
        TextView name = (TextView) findViewById(R.id.name);
        TextView age = (TextView) findViewById(R.id.age);
        TextView sport = (TextView) findViewById(R.id.sport);

        city.setText(mRelation.mCoach.mCity);
        name.setText(mRelation.mCoach.mDisplayName);
        age.setText(mRelation.mCoach.mBirthdate);
        sport.setText(mRelation.mCoach.mSportsList[0].mTitle);*/
    }

    @Override
    public void onLoaderReset(Loader<CoachingRelation> loader) {

    }
}
