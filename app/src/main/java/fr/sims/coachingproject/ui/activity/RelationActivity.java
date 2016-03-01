package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class RelationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CoachingRelation>, View.OnClickListener {

    private static final String EXTRA_COACHING_RELATION_ID = "fr.sims.coachingproject.extra.COACHING_RELATION_ID";

    RelationPagerAdapter mRelationPagerAdapter;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    ScrollView mInvitationLayout;

    CoachingRelation mRelation;
    private long mId;


    public static void startActivity(Context ctx, long id) {
        Intent intent = new Intent(ctx, RelationActivity.class);
        intent.putExtra(EXTRA_COACHING_RELATION_ID, id);
        ctx.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_relation);

        // Get the transferred id
        Intent mIntent = getIntent();
        mId = mIntent.getLongExtra(EXTRA_COACHING_RELATION_ID, 0);

        // Tabs Pattern
        mRelationPagerAdapter = new RelationPagerAdapter(getSupportFragmentManager(), mId);
        mViewPager = (ViewPager) findViewById(R.id.messagePager);
        mViewPager.setAdapter(mRelationPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        // Invitation Layout
        mInvitationLayout = ((ScrollView) findViewById(R.id.invitationLayout));

        findViewById(R.id.profile_layout).setOnClickListener(this);
        findViewById(R.id.coaching_invitation_accept).setOnClickListener(this);
        findViewById(R.id.coaching_invitation_refuse).setOnClickListener(this);
        findViewById(R.id.End_Relation_Button).setOnClickListener(this);


        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<CoachingRelation> onCreateLoader(int id, Bundle args) {
        return new RelationLoader(this, mId);
    }

    @Override
    public void onLoadFinished(Loader<CoachingRelation> loader, CoachingRelation data) {
        mRelation = data;

        ImageView picture = (ImageView) findViewById(R.id.imagePicture);
        TextView city = (TextView) findViewById(R.id.city);
        TextView name = (TextView) findViewById(R.id.name);
        TextView age = (TextView) findViewById(R.id.age);
        TextView sport = (TextView) findViewById(R.id.sport);

        UserProfile partner;
        boolean isCurrentUserCoach = (mRelation.mCoach.mIdDb == SharedPrefUtil.getConnectedUserId(this));
        if (isCurrentUserCoach) {
            partner = mRelation.mTrainee;
        } else {
            partner = mRelation.mCoach;
        }

        int userAge = partner.getAge();

        city.setText(partner.mCity);
        name.setText(partner.mDisplayName);
        age.setText(getResources().getQuantityString(R.plurals.user_age, userAge, userAge));
        sport.setText(mRelation.mSport.mName);
        Picasso.with(RelationActivity.this).load(partner.mPicture).into(picture);


        if (mRelation.mIsPending) {
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            findViewById(R.id.End_Relation_Button).setVisibility(View.GONE);
            mInvitationLayout.setVisibility(View.VISIBLE);


            ((TextView) findViewById(R.id.coaching_invitation_description)).setText(mRelation.mComment);

            if (isCurrentUserCoach) {
                ((TextView) findViewById(R.id.coaching_invitation_title)).setText(getString(R.string.coaching_invitation_coach_title, partner.mDisplayName));
                findViewById(R.id.coaching_invitation_buttons).setVisibility(View.VISIBLE);
            } else {
                ((TextView) findViewById(R.id.coaching_invitation_title)).setText(getString(R.string.coaching_invitation_trainee_title, partner.mDisplayName));
                findViewById(R.id.coaching_invitation_buttons).setVisibility(View.GONE);
            }

        } else {
            mTabLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mInvitationLayout.setVisibility(View.GONE);
            if (!isCurrentUserCoach)
                findViewById(R.id.End_Relation_Button).setVisibility(View.VISIBLE);
            else
                findViewById(R.id.End_Relation_Button).setVisibility(View.GONE);

        }
    }

    @Override
    public void onLoaderReset(Loader<CoachingRelation> loader) {

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.profile_layout:
                ProfileActivity.startActivity(this, mRelation.mIdDb);
                break;
            case R.id.coaching_invitation_accept:
                new AnswerInvitationTask().execute(true);
                break;
            case R.id.End_Relation_Button:
                new AnswerInvitationTask().execute(false);
                MainActivity.startActivity(getBaseContext());
            case R.id.coaching_invitation_refuse:
                new AnswerInvitationTask().execute(false);
                break;
            default:

        }
    }



    private class AnswerInvitationTask extends AsyncTask<Boolean, Void, Boolean> {

        private class Answer {
            @Expose
            @SerializedName("requestStatus")
            public Boolean mRequestStatus;

            public Answer(boolean requestStatus) {
                mRequestStatus = requestStatus;
            }

            public String toJson() {
                return new Gson().toJson(this);
            }
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (params.length > 0) {
                boolean isAccepted = params[0];

                String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION + mId + "/";
                String token = SharedPrefUtil.getConnectedToken(getApplicationContext());
                String body = new Answer(isAccepted).toJson();

                String res = NetworkUtil.patch(url, token, body);

                if (!res.isEmpty()) {
                    mRelation.mIsPending = false;
                    mRelation.mIsAccepted = isAccepted;
                    mRelation.save();
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
