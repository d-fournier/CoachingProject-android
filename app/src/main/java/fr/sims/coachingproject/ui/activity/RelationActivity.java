package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.RelationLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.adapter.RelationPagerAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class RelationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CoachingRelation>, View.OnClickListener {

    private static final String EXTRA_COACHING_RELATION_ID = "fr.sims.coachingproject.extra.COACHING_RELATION_ID";

    RelationPagerAdapter mRelationPagerAdapter;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    NestedScrollView mInvitationLayout;
    TextView mRefusedInvitationTV;
    // Send message views
    Toolbar mSendMessageTB;
    Button mSendBtn;
    EditText mMessageET;


    CoachingRelation mRelation;
    UserProfile mPartner;
    boolean mIsCurrentUserCoach;
    private long mRelationId;


    public static void startActivity(Context ctx, long id) {
        ctx.startActivity(getIntent(ctx, id));
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
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // Get the transferred id
        Intent mIntent = getIntent();
        mRelationId = mIntent.getLongExtra(EXTRA_COACHING_RELATION_ID, 0);

        // Tabs Pattern
        mRelationPagerAdapter = new RelationPagerAdapter(getSupportFragmentManager(), mRelationId);
        mViewPager = (ViewPager) findViewById(R.id.messagePager);
        mViewPager.setAdapter(mRelationPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        // Invitation Layout
        mInvitationLayout = ((NestedScrollView) findViewById(R.id.invitationLayout));
        findViewById(R.id.coaching_invitation_accept).setOnClickListener(this);
        findViewById(R.id.coaching_invitation_refuse).setOnClickListener(this);

        mRefusedInvitationTV = ((TextView) findViewById(R.id.coaching_invitation_refused));
        findViewById(R.id.profile_layout).setOnClickListener(this);

        // Send Message View
        mSendMessageTB = (Toolbar) findViewById(R.id.message_send_layout);
        mSendBtn = (Button) findViewById(R.id.message_send);
        mSendBtn.setOnClickListener(this);
        mMessageET = (EditText) findViewById(R.id.message_content);

        getSupportLoaderManager().initLoader(Const.Loaders.RELATION_LOADER_ID, null, this);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        boolean res = super.onPrepareOptionsMenu(menu);
        if(mRelation != null && mRelation.mIsAccepted && mRelation.mActive) {
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
                new PutEndRelationTask().execute(false);
                MainActivity.startActivity(getBaseContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void bindRelationDetails() {
        ImageView picture = (ImageView) findViewById(R.id.imagePicture);
        TextView city = (TextView) findViewById(R.id.city);
        TextView name = (TextView) findViewById(R.id.name);
        TextView sport = (TextView) findViewById(R.id.sport);

        city.setText(mPartner.mCity);
        name.setText(mPartner.mDisplayName);
        sport.setText(mRelation.mSport.mName);
        Picasso.with(RelationActivity.this).load(mPartner.mPicture).into(picture);

        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(mPartner.mDisplayName);
    }


    private void bindRelationContent() {
        // TODO Use Fragments ?
        // Set everything to GONE and update only elements to display according to the relation state
        mTabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        mInvitationLayout.setVisibility(View.GONE);
        mRefusedInvitationTV.setVisibility(View.GONE);
        mSendMessageTB.setVisibility(View.GONE);
        invalidateOptionsMenu();

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
                mSendMessageTB.setVisibility(View.VISIBLE);
            } else {
                mRefusedInvitationTV.setVisibility(View.VISIBLE);
            }
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
                ProfileActivity.startActivity(this, mPartner.mIdDb, -1);
                break;
            case R.id.coaching_invitation_accept:
                new AnswerInvitationTask().execute(true);
                break;
            case R.id.coaching_invitation_refuse:
                new AnswerInvitationTask().execute(false);
                break;
            case R.id.message_send:
                sendMessage();
            default:

        }
    }

    private void sendMessage(){
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow( mMessageET.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String message = mMessageET.getText().toString();

        String body = "";

        try {
            JSONObject parent = new JSONObject();
            parent.put("content",message);
            parent.put("to_relation", ""+mRelationId);
            parent.put("is_pinned", false);

            body = parent.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendRequestTask().execute(body);

    }

    private class PutEndRelationTask extends AsyncTask<Boolean, Void, Boolean> {

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

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (params.length > 0) {

                String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION + mRelationId + "/";
                String token = SharedPrefUtil.getConnectedToken(getApplicationContext());
                String body = new EndRelation(false).toJson();

                NetworkUtil.Response res = NetworkUtil.patch(url, token, body);

                if (!res.getBody().isEmpty()) {
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

                if(!res.getBody().isEmpty()) {
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

    }


    private class SendRequestTask extends AsyncTask<String, Void, NetworkUtil.Response> {
        @Override
        protected NetworkUtil.Response doInBackground(String... params) {
            if (params.length > 0) {
                String body = params[0];
                String connectedToken = SharedPrefUtil.getConnectedToken(getApplicationContext());
                NetworkUtil.Response response = NetworkUtil.post("https://coachingproject.herokuapp.com/api/messages/", connectedToken, body);
                return response;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if(response != null) {
                mSendBtn.setEnabled(true);
                if(response.isSuccessful()) {
                    mMessageET.setText("");
                    NetworkService.startActionMessages(getApplicationContext(), mRelationId);
                } else {
                    Snackbar.make(mViewPager, R.string.no_connectivity, Snackbar.LENGTH_SHORT);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            mSendBtn.setEnabled(false);
        }

    }
}

