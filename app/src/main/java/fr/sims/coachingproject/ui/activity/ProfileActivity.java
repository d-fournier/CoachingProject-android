package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import fr.sims.coachingproject.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


import fr.sims.coachingproject.loader.UserLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.ProfileSportListAdapter;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

import static fr.sims.coachingproject.util.NetworkUtil.post;
import static fr.sims.coachingproject.util.SharedPrefUtil.getConnectedToken;
import static fr.sims.coachingproject.util.SharedPrefUtil.getConnectedUserId;

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<UserProfile>, View.OnClickListener {

    private static final String EXTRA_USER_PROFILE_ID = "fr.sims.coachingproject.extra.USER_PROFILE_ID";
    private static final String EXTRA_SPORT_ID="fr.sims.coachingproject.extra.SPORT_ID";

    private long mId;
    private long mSportId;
    private String mConnectedToken;
    private long mConnectedUserId;
    private long mCoachUserId;
    private UserProfile mData;
    private String mRequest_Body;

    private View mMainLayout;

    private ProfileSportListAdapter mProfileAdapter;

    /**
     * Start activity
     * @param ctx
     * @param id
     * @param idSport id of sport to be selected, -1 if no preference
     */
    public static void startActivity(Context ctx, long id, long idSport){
        Intent intent = new Intent(ctx,ProfileActivity.class);
        intent.putExtra(EXTRA_USER_PROFILE_ID, id);
        intent.putExtra(EXTRA_SPORT_ID, idSport);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectedUserId= SharedPrefUtil.getConnectedUserId(this);
        setContentView(R.layout.activity_profile);
        Button btn_send_request = (Button) findViewById(R.id.send_request);

        // Get the transferred id
        Intent mIntent = getIntent();
        mId = mIntent.getLongExtra(EXTRA_USER_PROFILE_ID, 0);
        mSportId = mIntent.getLongExtra(EXTRA_SPORT_ID, -1);

        mMainLayout = findViewById(R.id.profile_layout);

        btn_send_request.setOnClickListener(this);
        mProfileAdapter=new ProfileSportListAdapter(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<UserProfile> onCreateLoader(int id, Bundle args) {
        return new UserLoader(this, mId);
    }


    // TODO Crash sometimes Data == null
    @Override
    public void onLoadFinished(Loader<UserProfile> loader, final UserProfile data) {
        mData = data;
        // Get components
        TextView tv_Name = (TextView) findViewById(R.id.textName);
        TextView tv_Birthday = (TextView) findViewById(R.id.textBirthday);
        TextView tv_City = (TextView) findViewById(R.id.textCity);
        TextView tv_IsCoach = (TextView) findViewById(R.id.textIsCoach);
        TextView tv_Description = (TextView) findViewById(R.id.textDescription);
        ImageView iv_Picture = (ImageView) findViewById(R.id.imagePicture);
        ListView lv_sport = (ListView) findViewById(R.id.listView1);
        Spinner spinner=(Spinner) findViewById(R.id.spinner_profile_sports);

        // Set values
        //tv_Id.setText("UserID: " + Long.toString(data.mId));
        tv_Name.setText(data.mDisplayName);
        tv_Birthday.setText(getResources().getQuantityString(R.plurals.user_age,data.getAge(), data.getAge()));
        tv_City.setText(data.mCity);
        tv_IsCoach.setText(data.mIsCoach ? "Can coach" : "Cannot coach");
        tv_Description.setText(data.mDescription);
        Picasso.with(ProfileActivity.this).load(data.mPicture).into(iv_Picture);

        // fill sport list
        List<Sport> sportList = new ArrayList<>();
        for (SportLevel level :  data.mSportsList)
        {
            if(!mData.isCoachingUser(mConnectedUserId, level.mSport.mIdDb )){
                sportList.add(level.mSport);
            }
        }


        if(sportList.isEmpty()){
            Button btn_send_request = (Button) findViewById(R.id.send_request);
            LinearLayout sentence_send_request= (LinearLayout) findViewById(R.id.send_coaching_request_sentence);
            btn_send_request.setVisibility(View.GONE);
            sentence_send_request.setVisibility(View.GONE);
        }else{
            spinner.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, sportList));
            if(mSportId!=-1){
                spinner.setSelection(sportList.indexOf(Sport.getSportById(mSportId)));
            }
        }








        mProfileAdapter.setData(data.mSportsList);

        lv_sport.setAdapter(mProfileAdapter);

    }

    @Override
    public void onLoaderReset(Loader<UserProfile> loader) {

    }

    @Override
    public void onClick(View v) {
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
                mCoachUserId = mData.mIdDb;
                // Get checked sport id
                Spinner lv = (Spinner) findViewById(R.id.spinner_profile_sports);
                Sport checked_sport = (Sport)lv.getSelectedItem();
                long check_sport_id = checked_sport.mIdDb;
                // Get request comment
                EditText et = (EditText) popupView.findViewById(R.id.request_comment);
                String request_comment = et.getText().toString();
                // Only keep the first 200 characters
                if (request_comment.length() > 200) {
                    request_comment = request_comment.substring(0,200);
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
                    class SendRequest extends AsyncTask<String, Void, String> {
                        @Override
                        protected String doInBackground(String... params) {
                            NetworkUtil.Response response = post("https://coachingproject.herokuapp.com/api/relations/", mConnectedToken, mRequest_Body);
                            return String.valueOf(response.getReturnCode());
                        }

                        @Override
                        protected void onPostExecute(String response) {
                            if (Integer.parseInt(response)== HttpsURLConnection.HTTP_CREATED){
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
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            // Set focus to popup window
            popupWindow.setFocusable(true);
            popupWindow.update();
        } else {
            Toast.makeText(getApplicationContext(),
                    "You should choose a sport first !", Toast.LENGTH_LONG).show();
        }

    }
}
