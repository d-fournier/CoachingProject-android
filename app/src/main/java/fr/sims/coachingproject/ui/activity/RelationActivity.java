package fr.sims.coachingproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.sims.coachingproject.NetworkService;
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.RelationLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.RelationPagerAdapter;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class RelationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CoachingRelation>, View.OnClickListener{

    RelationPagerAdapter mRelationPagerAdapter;
    ViewPager mViewPager;
    CoachingRelation mRelation;
    private long mId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_relation);

        // Get the transferred id
        Intent mIntent = getIntent();
        mId = mIntent.getLongExtra("id", 0);

        // Tabs Pattern
        mRelationPagerAdapter = new RelationPagerAdapter(getSupportFragmentManager(), mId);
        mViewPager = (ViewPager) findViewById(R.id.messagePager);
        mViewPager.setAdapter(mRelationPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        View profileView=findViewById(R.id.profile_layout);
        profileView.setOnClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<CoachingRelation> onCreateLoader(int id, Bundle args) {
        return new RelationLoader(this, mId);
    }

    @Override
    public void onLoadFinished(Loader<CoachingRelation> loader, CoachingRelation data) {
        mRelation=data;
        ImageView picture = (ImageView) findViewById(R.id.imagePicture);
        TextView city = (TextView) findViewById(R.id.city);
        TextView name = (TextView) findViewById(R.id.name);
        TextView age = (TextView) findViewById(R.id.age);
        TextView sport = (TextView) findViewById(R.id.sport);

        UserProfile partner;

        if(mRelation.mTrainee.mIdDb== SharedPrefUtil.getConnectedUserId(this)){
            partner=mRelation.mCoach;
        }else{
            partner=mRelation.mTrainee;
        }

        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        int userAge=0;
        try {
            SimpleDateFormat sdf=new SimpleDateFormat( "yyyy-MM-dd");
            birthdate.setTime(sdf.parse(partner.mBirthdate));
            userAge = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)){
                userAge--;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        city.setText(partner.mCity);
        name.setText(partner.mDisplayName);
        age.setText(String.valueOf(userAge)+" ans");
        sport.setText(mRelation.mSport.mName);
        Picasso.with(RelationActivity.this).load(partner.mPicture).into(picture);

//        NetworkService.startActionMessages(this, mRelation.mIdDb);
    }

    @Override
    public void onLoaderReset(Loader<CoachingRelation> loader) {

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, ProfileActivity.class);
        UserProfile partner;

        if(mRelation.mTrainee.mIdDb== SharedPrefUtil.getConnectedUserId(this)){
            partner=mRelation.mCoach;
        }else{
            partner=mRelation.mTrainee;
        }
        i.putExtra("id", partner.mIdDb);
        startActivity(i);
    }
}
