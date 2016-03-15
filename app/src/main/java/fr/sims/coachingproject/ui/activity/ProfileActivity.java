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
import android.widget.Spinner;
import android.widget.TextView;

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
        UserProfile profile = data;

        // Get components
        TextView nameTV = (TextView) findViewById(R.id.profile_name);
        TextView infoTV = (TextView) findViewById(R.id.profile_info);
        TextView isCoachTV = (TextView) findViewById(R.id.profile_isCoach);
        ImageView pictureIV = (ImageView) findViewById(R.id.profile_picture);


        // Set values
        String age = getResources().getQuantityString(R.plurals.user_age, profile.getAge(), profile.getAge());
        nameTV.setText(profile.mDisplayName);
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(profile.mDisplayName);
        infoTV.setText(getString(R.string.separator_strings, profile.mCity, age));
        ImageUtil.loadProfilePicture(this, data.mPicture, pictureIV);

        if (profile.mIsCoach && mUserId != mConnectedUserId) {
            isCoachTV.setText(getString(R.string.profile_accept_coaching, profile.mDisplayName));
            fillCoachingRequestLayout(profile);
        } else {
            isCoachTV.setText(getString(R.string.profile_not_accept_coaching, profile.mDisplayName));
            mSendRequestBtn.hide();
        }
    }

    // TODO Debug Will be remove
    private void fillCoachingRequestLayout(UserProfile profile){
        // fill sport list
        Map<Long,Sport> sports = new HashMap<>();
        for (SportLevel level : profile.mSportsList) {
            // TODO Main Thread + BDD
            Sport s = level.mSport;
            if (!profile.isCoachingUser(mConnectedUserId, s.mIdDb) && !sports.containsKey(s.mIdDb)) {
                sports.put(s.mIdDb, s);
            }
        }

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

    /*
    private void displayCoachingRequest() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_send_request_coaching, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Send Button
        Button btn_confirm_send_request = (Button) popupView.findViewById(R.id.confirm_send_request);
        btn_confirm_send_request.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // Get the token of connected user
                mConnectedToken = getConnectedToken(ProfileActivity.this);
                // Get the trainee id
                mConnectedUserId = getConnectedUserId(ProfileActivity.this);
                // Get the coach id
                mCoachUserId = mProfile.mIdDb;
                // Get checked sport id
                Spinner lv = (Spinner) findViewById(R.id.spinner_profile_sports);
                Sport checked_sport = (Sport) lv.getSelectedItem();
                long check_sport_id = checked_sport.mIdDb;
                // Get request comment
                EditText et = (EditText) popupView.findViewById(R.id.request_comment);
                String request_comment = et.getText().toString();
                // Only keep the first 200 characters
                if (request_comment.length() > 200) {
                    request_comment = request_comment.substring(0, 200);
                }
                // Create request to post
                try {
                    JSONObject parent = new JSONObject();
                    parent.put("coach", Long.toString(mCoachUserId));
                    parent.put("sport", Long.toString(check_sport_id));
                    parent.put("comment", request_comment);
                    Log.d("output", parent.toString(2));
                    mRequest_Body = parent.toString(2);

                    new SendRequest().execute("");
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    popupWindow.dismiss();
                }
            }
        });

        // Cancel Button
        Button btn_cancel_send_request = (Button) popupView.findViewById(R.id.cancel_send_request);
        btn_cancel_send_request.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // No selected sport, no pop-up window
        Spinner lv = (Spinner) findViewById(R.id.spinner_profile_sports);
        if (lv.getSelectedItem() != null) {
            popupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
            // Set focus to popup window
            popupWindow.setFocusable(true);
            popupWindow.update();
        } else {
            Toast.makeText(getApplicationContext(),
                    "You should choose a sport first !", Toast.LENGTH_LONG).show();
        }
    }
    */
}
