package fr.sims.coachingproject.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.local.RelationLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.pager.RelationPagerAdapter;
import fr.sims.coachingproject.ui.fragment.MessageSendFragment;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.ImageUtil;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class RelationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CoachingRelation>, View.OnClickListener {

    private static final String EXTRA_COACHING_RELATION_ID = "fr.sims.coachingproject.extra.COACHING_RELATION_ID";

    private RelationPagerAdapter mRelationPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private NestedScrollView mInvitationLayout;
    private TextView mRefusedInvitationTV;

    private EditText mMessageET;

    private MessageSendFragment mSendMessFragment;

    private AlertDialog.Builder alertDialog;

    private CoachingRelation mRelation;
    private UserProfile mPartner;
    boolean mIsCurrentUserCoach;
    private long mRelationId;
    private ImageView mPictureIV;

    public static void startActivityWithAnimation(Activity activityCtx, long id, View pictureView){
        Intent intent = getIntent(activityCtx, id);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activityCtx, pictureView,
                            activityCtx.getResources().getString(R.string.transition_user_picture));
            activityCtx.startActivity(intent, options.toBundle());
        } else {
            activityCtx.startActivity(intent);
        }
    }

    public static Intent getIntent(Context ctx, long id) {
        Intent intent = new Intent(ctx, RelationActivity.class);
        intent.putExtra(EXTRA_COACHING_RELATION_ID, id);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);


        // Get the transferred id
        Intent mIntent = getIntent();
        mRelationId = mIntent.getLongExtra(EXTRA_COACHING_RELATION_ID, 0);

        // Tabs Pattern
        mRelationPagerAdapter = new RelationPagerAdapter(getFragmentManager(), mRelationId);
        mViewPager = (ViewPager) findViewById(R.id.messagePager);
        mViewPager.setAdapter(mRelationPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mPictureIV = (ImageView) findViewById(R.id.imagePicture);

        // Invitation Layout
        mInvitationLayout = ((NestedScrollView) findViewById(R.id.invitationLayout));
        findViewById(R.id.coaching_invitation_accept).setOnClickListener(this);
        findViewById(R.id.coaching_invitation_refuse).setOnClickListener(this);

        mRefusedInvitationTV = ((TextView) findViewById(R.id.coaching_invitation_refused));
        findViewById(R.id.profile_layout).setOnClickListener(this);

        // Manage send message Fragment
        String tag = MessageSendFragment.getRelationTag(mRelationId);
        FragmentManager fm = getFragmentManager();
        mSendMessFragment = (MessageSendFragment) fm.findFragmentByTag(tag);
        if (mSendMessFragment == null) {
            mSendMessFragment = MessageSendFragment.newRelationInstance(mRelationId);
        }
        fm.beginTransaction().replace(R.id.relation_send_message_fragment, mSendMessFragment, tag).hide(mSendMessFragment).commit();


        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        new PutEndRelationTask().execute(false);
                        MainActivity.startActivity(getBaseContext());
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton(R.string.button_confirm, dialogClickListener)
                .setNegativeButton(R.string.button_cancel, dialogClickListener);

        getLoaderManager().initLoader(Const.Loaders.RELATION_LOADER_ID, null, this);

        // Remove Notification pending content
        SharedPrefUtil.clearNotificationContent(this, Const.Notification.Id.RELATION + "_" + Const.Notification.Tag.RELATION + String.valueOf(mRelationId));
        SharedPrefUtil.clearNotificationContent(this, Const.Notification.Id.MESSAGE + "_" + Const.Notification.Tag.RELATION + String.valueOf(mRelationId));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean res = super.onPrepareOptionsMenu(menu);
        if (mRelation != null && mRelation.mIsAccepted && mRelation.mActive) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_relation, menu);
            return true;
        }
        return res;
    }


    @Override
    public Loader<CoachingRelation> onCreateLoader(int id, Bundle args) {
        return new RelationLoader(this, mRelationId);
    }

    @Override
    public void onLoadFinished(Loader<CoachingRelation> loader, CoachingRelation data) {
        mRelation = data;

        mIsCurrentUserCoach = (mRelation.mCoach.mIdDb == SharedPrefUtil.getConnectedUserId(this));
        if (mIsCurrentUserCoach) {
            mPartner = mRelation.mTrainee;
        } else {
            mPartner = mRelation.mCoach;
        }

        bindRelationDetails();
        bindRelationContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.relation_cancel:
                alertDialog.setMessage("Are you sure you want to remove " + mPartner.mDisplayName + " as your relation ?").show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void bindRelationDetails() {
        TextView city = (TextView) findViewById(R.id.city);
        TextView name = (TextView) findViewById(R.id.name);
        TextView sport = (TextView) findViewById(R.id.sport);

        city.setText(mPartner.mCity);
        name.setText(mPartner.mDisplayName);
        sport.setText(mRelation.mSport.mName);
        ImageUtil.loadProfilePicture(this, mPartner.mPicture, mPictureIV);

        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(mPartner.mDisplayName);
    }


    private void bindRelationContent() {
        // TODO Use Fragments ?
        // Set everything to GONE and update only elements to display according to the relation state
        mTabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        mInvitationLayout.setVisibility(View.GONE);
        mRefusedInvitationTV.setVisibility(View.GONE);
        invalidateOptionsMenu();
        boolean displaySendMessage = false;

        if (mRelation.mIsPending) {
            mInvitationLayout.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.coaching_invitation_description)).setText(mRelation.mComment);

            if (mIsCurrentUserCoach) {
                ((TextView) findViewById(R.id.coaching_invitation_title)).setText(getString(R.string.coaching_invitation_coach_title, mPartner.mDisplayName));
                findViewById(R.id.coaching_invitation_buttons).setVisibility(View.VISIBLE);
            } else {
                ((TextView) findViewById(R.id.coaching_invitation_title)).setText(getString(R.string.coaching_invitation_trainee_title, mPartner.mDisplayName));
                findViewById(R.id.coaching_invitation_buttons).setVisibility(View.GONE);
            }
        } else {
            if (mRelation.mIsAccepted) {
                mTabLayout.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
                displaySendMessage = true;
            } else {
                mRefusedInvitationTV.setVisibility(View.VISIBLE);
            }
        }

        if (displaySendMessage)
            mSendMessFragment.show();
        else
            mSendMessFragment.hide();


    }

    @Override
    public void onLoaderReset(Loader<CoachingRelation> loader) {
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.profile_layout:
                ProfileActivity.startActivityWithAnimation(this, mPartner.mIdDb, -1, mPictureIV);
                break;
            case R.id.coaching_invitation_accept:
                new AnswerInvitationTask().execute(true);
                break;
            case R.id.coaching_invitation_refuse:
                new AnswerInvitationTask().execute(false);
                break;

        }
    }

    private class PutEndRelationTask extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (params.length > 0) {

                String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION + mRelationId + "/";
                String token = SharedPrefUtil.getConnectedToken(getApplicationContext());
                String body = new EndRelation(false).toJson();

                NetworkUtil.Response res = NetworkUtil.patch(url, token, body);

                if (res.isSuccessful()) {
                    mRelation.mActive = false;
                    mRelation.save();
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        private class EndRelation {
            @Expose
            @SerializedName("active")
            public Boolean mActive;

            public EndRelation(boolean active) {
                mActive = active;
            }

            public String toJson() {
                return new Gson().toJson(this);
            }
        }
    }

    private class AnswerInvitationTask extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            findViewById(R.id.coaching_invitation_progress).setVisibility(View.VISIBLE);
            findViewById(R.id.coaching_invitation_buttons).setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (params.length > 0) {
                boolean isAccepted = params[0];
                String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION + mRelationId + Const.WebServer.SEPARATOR;

                String token = SharedPrefUtil.getConnectedToken(getApplicationContext());
                String body = new Answer(isAccepted).toJson();

                NetworkUtil.Response res = NetworkUtil.patch(url, token, body);

                if (res.isSuccessful()) {
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

        @Override
        protected void onPostExecute(Boolean isRequestSuccessful) {
            if (!isRequestSuccessful) {
                Snackbar.make(mInvitationLayout, "Network error", Snackbar.LENGTH_LONG).show();
            }
            findViewById(R.id.coaching_invitation_progress).setVisibility(View.GONE);
            bindRelationContent();

            super.onPostExecute(isRequestSuccessful);
        }

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

    }
}

