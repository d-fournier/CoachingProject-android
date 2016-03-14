package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.net.ssl.HttpsURLConnection;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.UserLoader;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.ProfileSportListAdapter;
import fr.sims.coachingproject.ui.adapter.pager.ProfilePagerAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.ImageUtil;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

import static fr.sims.coachingproject.util.NetworkUtil.post;

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<UserProfile> {

    private static final String EXTRA_USER_PROFILE_ID = "fr.sims.coachingproject.extra.USER_PROFILE_ID";
    private static final String EXTRA_SPORT_ID = "fr.sims.coachingproject.extra.SPORT_ID";

    private long mId;
    private long mSportId;
    private String mConnectedToken;
    private long mConnectedUserId;
    private long mCoachUserId;

    private UserProfile mProfile;
    private String mRequest_Body;

    private ProfilePagerAdapter mProfilePagerAdapter;

    private View mMainLayout;

    private FloatingActionButton mSendRequestBtn;

    private ViewPager mViewPager;


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

        mConnectedUserId = SharedPrefUtil.getConnectedUserId(this);

        // Get the transferred id
        Intent mIntent = getIntent();
        mId = mIntent.getLongExtra(EXTRA_USER_PROFILE_ID, 0);
        mSportId = mIntent.getLongExtra(EXTRA_SPORT_ID, -1);

        // Bind
        mMainLayout = findViewById(R.id.profile_main_layout);

//        mSendRequestBtn = (FloatingActionButton) findViewById(R.id.profile_send_request);
//        mSendRequestBtn.setOnClickListener(this);

        getSupportLoaderManager().initLoader(Const.Loaders.USER_LOADER_ID, null, this);

        mProfilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), mId);
        mViewPager = (ViewPager) findViewById(R.id.profile_view_pager);
        mViewPager.setAdapter(mProfilePagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.profile_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(Const.Loaders.USER_LOADER_ID, null, this);
    }

    @Override
    public Loader<UserProfile> onCreateLoader(int id, Bundle args) {
        return new UserLoader(this, mId);
    }

    @Override
    public void onLoadFinished(Loader<UserProfile> loader, final UserProfile data) {
        mProfile = data;

        // Get components
        TextView nameTV = (TextView) findViewById(R.id.profile_name);
        TextView infoTV = (TextView) findViewById(R.id.profile_info);
        TextView isCoachTV = (TextView) findViewById(R.id.profile_isCoach);
        ImageView pictureIV = (ImageView) findViewById(R.id.profile_picture);


        // Set values
        String age = getResources().getQuantityString(R.plurals.user_age, mProfile.getAge(), mProfile.getAge());
        nameTV.setText(mProfile.mDisplayName);
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(mProfile.mDisplayName);
        infoTV.setText(getString(R.string.separator_strings, mProfile.mCity, age));
        ImageUtil.loadProfilePicture(this, data.mPicture, pictureIV);
        Picasso.with(ProfileActivity.this).load(data.mPicture).into(pictureIV);

        if (mProfile.mIsCoach) {
            isCoachTV.setText(getString(R.string.profile_accept_coaching, mProfile.mDisplayName));
//            mSendRequestBtn.setVisibility(View.VISIBLE);
//            fillCoachingRequestLayout();
        } else {
            isCoachTV.setText(getString(R.string.profile_not_accept_coaching, mProfile.mDisplayName));
//            mSendRequestBtn.setVisibility(View.GONE);
//            findViewById(R.id.profile_send_request_layout).setVisibility(View.GONE);
        }
    }

    /*
    // TODO Debug Will be remove
    private void fillCoachingRequestLayout(){
        Spinner spinner = (Spinner) findViewById(R.id.spinner_profile_sports);

        // fill sport list
        Map<Long,Sport> sports = new HashMap<>();
        for (SportLevel level : mProfile.mSportsList) {
            // TODO Main Thread + BDD
            Sport s = level.mSport;
            if (!mProfile.isCoachingUser(mConnectedUserId, s.mIdDb) && !sports.containsKey(s.mIdDb)) {
                sports.put(s.mIdDb, s);
            }
        }

        if (sports.isEmpty()) {
            findViewById(R.id.profile_send_request_layout).setVisibility(View.GONE);
            mSendRequestBtn.setVisibility(View.GONE);
        } else {
            List<Sport> sportslist = new ArrayList<>(sports.values());
            spinner.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, sportslist));
            if (mSportId != -1) {
                for (int i = 0; i < sportslist.size(); i++) {
                    if(sportslist.get(i).mIdDb == mSportId) {
                        spinner.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
    */

    @Override
    public void onLoaderReset(Loader<UserProfile> loader) {
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
                    parent.put("trainee", Long.toString(mConnectedUserId));
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

    class SendRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            NetworkUtil.Response response = post("https://coachingproject.herokuapp.com/api/relations/", mConnectedToken, mRequest_Body);
            return String.valueOf(response.getReturnCode());
        }

        @Override
        protected void onPostExecute(String response) {
            if (Integer.parseInt(response) == HttpsURLConnection.HTTP_CREATED) {
                Snackbar.make(mMainLayout, R.string.request_sent, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mMainLayout, R.string.request_error, Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
