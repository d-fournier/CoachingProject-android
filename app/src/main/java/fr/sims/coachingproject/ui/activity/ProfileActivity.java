package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.local.UserLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.pager.ProfilePagerAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.ImageUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<UserProfile>, View.OnClickListener {

    private static final String EXTRA_USER_PROFILE_ID = "fr.sims.coachingproject.extra.USER_PROFILE_ID";
    private static final String EXTRA_SPORT_ID = "fr.sims.coachingproject.extra.SPORT_ID";

    private long mUserId;
    private long mConnectedUserId;
    private long mSportId;
    private ArrayList<Sport> mSportsList;

    private FloatingActionButton mSendRequestBtn;


    /**
     * Start activity
     *
     * @param ctx
     * @param id
     * @param idSport id of sport to be selected, -1 if no preference
     */
    public static void startActivity(Context ctx, long id, long idSport) {
        Intent intent = new Intent(ctx, ProfileActivity.class);
        intent.putExtra(EXTRA_USER_PROFILE_ID, id);
        intent.putExtra(EXTRA_SPORT_ID, idSport);
        ctx.startActivity(intent);
    }

    public static void startActivity(Context ctx, long id) {
        Intent intent = new Intent(ctx, ProfileActivity.class);
        intent.putExtra(EXTRA_USER_PROFILE_ID, id);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the transferred id
        Intent mIntent = getIntent();
        mUserId = mIntent.getLongExtra(EXTRA_USER_PROFILE_ID, 0);
        mSportId = mIntent.getLongExtra(EXTRA_SPORT_ID, -1);

        mSportsList = new ArrayList<>();
        mConnectedUserId = SharedPrefUtil.getConnectedUserId(this);

        mSendRequestBtn = (FloatingActionButton) findViewById(R.id.profile_send_request);
        mSendRequestBtn.setOnClickListener(this);

        getSupportLoaderManager().initLoader(Const.Loaders.USER_LOADER_ID, null, this);

        ProfilePagerAdapter profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), mUserId);
        ViewPager viewPager = (ViewPager) findViewById(R.id.profile_view_pager);
        viewPager.setAdapter(profilePagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.profile_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(Const.Loaders.USER_LOADER_ID, null, this);
    }

    @Override
    public Loader<UserProfile> onCreateLoader(int id, Bundle args) {
        return new UserLoader(this, mUserId);
    }

    @Override
    public void onLoadFinished(Loader<UserProfile> loader, final UserProfile data) {

        // Get components
        TextView nameTV = (TextView) findViewById(R.id.profile_name);
        TextView infoTV = (TextView) findViewById(R.id.profile_info);
        TextView isCoachTV = (TextView) findViewById(R.id.profile_isCoach);
        ImageView pictureIV = (ImageView) findViewById(R.id.profile_picture);

        if (data != null) {
            // Set values
            String age = getResources().getQuantityString(R.plurals.user_age, data.getAge(), data.getAge());
            nameTV.setText(data.mDisplayName);
            ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(data.mDisplayName);
            infoTV.setText(getString(R.string.separator_strings, data.mCity, age));
            ImageUtil.loadProfilePicture(this, data.mPicture, pictureIV);

            if (data.mIsCoach && mUserId != mConnectedUserId) {
                isCoachTV.setText(getString(R.string.profile_accept_coaching, data.mDisplayName));
                fillCoachingRequestLayout(data);
            } else {
                isCoachTV.setText(getString(R.string.profile_not_accept_coaching, data.mDisplayName));
                mSendRequestBtn.hide();
            }
        } else {
            //Todo ERROR
            Toast.makeText(getApplicationContext(), "User unknown", Toast.LENGTH_LONG).show();
        }
    }

    private void fillCoachingRequestLayout(UserProfile profile) {
        // fill sport list
        Map<Long, Sport> sports = new HashMap<>();
        for (SportLevel level : profile.mSportsList) {
            // TODO Main Thread + BDD
            Sport s = level.mSport;
            if (!profile.isCoachingUser(mConnectedUserId, s.mIdDb) && !sports.containsKey(s.mIdDb)) {
                sports.put(s.mIdDb, s);
            }
        }

        mSportsList.clear();
        mSportsList.addAll(sports.values());
        if (sports.isEmpty())
            mSendRequestBtn.hide();
        else
            mSendRequestBtn.show();
    }

    @Override
    public void onLoaderReset(Loader<UserProfile> loader) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.profile_send_request:
                SendRequestActivity.startActivity(this, mUserId, mSportId, mSportsList);
                break;
        }
    }
}
