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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.UserLoader;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.NetworkUtil;

import static fr.sims.coachingproject.util.NetworkUtil.post;
import static fr.sims.coachingproject.util.SharedPrefUtil.getConnectedToken;
import static fr.sims.coachingproject.util.SharedPrefUtil.getConnectedUserId;

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<UserProfile>, View.OnClickListener {

    private static final String EXTRA_USER_PROFILE_ID = "fr.sims.coachingproject.extra.USER_PROFILE_ID";

    private long mId;
    private String mConnectedToken;
    private long mConnectedUserId;
    private long mCoachUserId;
    private UserProfile mData;
    private String mRequest_Body;

    private View mMainLayout;

    public static void startActivity(Context ctx, long id){
        Intent intent = new Intent(ctx,ProfileActivity.class);
        intent.putExtra(EXTRA_USER_PROFILE_ID, id);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageButton btn_send_request = (ImageButton) findViewById(R.id.send_request);
        // Get the transferred id
        Intent mIntent = getIntent();
        mId = mIntent.getLongExtra(EXTRA_USER_PROFILE_ID, 0);

        mMainLayout = findViewById(R.id.profile_layout);

        btn_send_request.setOnClickListener(this);


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
    public void onLoadFinished(Loader<UserProfile> loader, UserProfile data) {
        mData = data;
        // Get components
        TextView tv_Name = (TextView) findViewById(R.id.textName);
        TextView tv_Birthday = (TextView) findViewById(R.id.textBirthday);
        TextView tv_City = (TextView) findViewById(R.id.textCity);
        TextView tv_IsCoach = (TextView) findViewById(R.id.textIsCoach);
        TextView tv_Mail = (TextView) findViewById(R.id.textMail);
        ImageView iv_Picture = (ImageView) findViewById(R.id.imagePicture);
        ListView lv_sport = (ListView) findViewById(R.id.listView1);

        // Set values
        //tv_Id.setText("UserID: " + Long.toString(data.mId));
        tv_Name.setText("Name: " + data.mDisplayName);
        tv_Birthday.setText("Birthday: " + data.mBirthdate);
        tv_City.setText("City:" + data.mCity);
        tv_IsCoach.setText("Is Coach:" + Boolean.toString(data.mIsCoach));
        tv_Mail.setText("Email: " + data.mDisplayName);
        Picasso.with(ProfileActivity.this).load(data.mPicture).into(iv_Picture);

        // fill sport list
        String[] SportsNameList = new String[data.mSportsList.length];
        for (int i = 0; i < data.mSportsList.length; i++)
        {
            SportsNameList[i] = data.mSportsList[i].mSport.mName + "\t\tRank: " + data.mSportsList[i].mRank;
        }
        lv_sport.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, SportsNameList));
        lv_sport.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
                ListView lv = (ListView) findViewById(R.id.listView1);
                int checked_sport = lv.getCheckedItemPosition();
                long check_sport_id = mData.mSportsList[checked_sport].mSport.mIdDb;
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
                            return response.getBody();
                        }

                        @Override
                        protected void onPostExecute(String response) {
                            if (response.length() != 0){
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
        ListView lv = (ListView) findViewById(R.id.listView1);
        if (lv.getCheckedItemPosition() != -1) {
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
