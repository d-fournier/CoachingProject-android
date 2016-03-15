package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

import static fr.sims.coachingproject.util.NetworkUtil.post;

public class SendRequestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EXTRA_COACH_ID = "fr.sims.coachingproject.extra.COACH_ID";
    private static final String EXTRA_SPORT_ID = "fr.sims.coachingproject.extra.SPORT_ID";
    private static final String EXTRA_SPORT_LIST = "fr.sims.coachingproject.extra.SPORT_LIST";

    private LinearLayout mMainLayout;
    private Spinner mSportSpinner;
    private EditText mRequestText;
    private Button mSendRequestBt;

    private long mCoachId;
    private long mSportId;
    private ArrayList<Sport> mSportsList;
    private ArrayAdapter<Sport> mAdapter;

    public static void startActivity(Context ctx, long coachId, long sportId, ArrayList<Sport> sportsList) {
        Intent intent = new Intent(ctx, SendRequestActivity.class);
        intent.putExtra(EXTRA_COACH_ID, coachId);
        intent.putExtra(EXTRA_SPORT_ID, sportId);
        intent.putParcelableArrayListExtra(EXTRA_SPORT_LIST, sportsList);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        Intent mIntent = getIntent();
        mCoachId = mIntent.getLongExtra(EXTRA_COACH_ID, -1);
        mSportId = mIntent.getLongExtra(EXTRA_SPORT_ID, -1);
        mSportsList = mIntent.getParcelableArrayListExtra(EXTRA_SPORT_LIST);

        mMainLayout = (LinearLayout) findViewById(R.id.request_main_layout);
        mRequestText = (EditText) findViewById(R.id.request_text);

        mSportSpinner = (Spinner) findViewById(R.id.request_spinner);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mSportsList);
        mSportSpinner.setAdapter(mAdapter);
        if (mSportId != -1) {
            for (int i = 0; i < mAdapter.getCount(); i++) {
                if(mAdapter.getItem(i).mIdDb == mSportId) {
                    mSportSpinner.setSelection(i);
                    break;
                }
            }
        }

        mSendRequestBt = (Button) findViewById(R.id.request_send);
        mSendRequestBt.setOnClickListener(this);
    }

    private void sendRequest(){
        try {
            JSONObject parent = new JSONObject();
            parent.put("coach", mCoachId);
            parent.put("sport", mSportsList.get(mSportSpinner.getSelectedItemPosition()).mIdDb);
            parent.put("comment", mRequestText.getText());
            String text = parent.toString();

            mSendRequestBt.setEnabled(false);
            new SendRequest().execute(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.request_send:
                sendRequest();
                break;
        }
    }

    class SendRequest extends AsyncTask<String, Void, NetworkUtil.Response> {
        @Override
        protected NetworkUtil.Response doInBackground(String... params) {
            if(params.length == 0)
                return null;

            String body = params[0];
            NetworkUtil.Response response = post("https://coachingproject.herokuapp.com/api/relations/", SharedPrefUtil.getConnectedToken(getApplicationContext()), body);
            // TODO Get and persist new Relation
            return response;
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if (response != null && response.isSuccessful()) {
                Snackbar.make(mMainLayout, R.string.request_sent, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mMainLayout, R.string.request_error, Snackbar.LENGTH_SHORT).show();
                mSendRequestBt.setEnabled(true);
            }
        }
    }
}
