package fr.sims.coachingproject.ui.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.ui.adapter.MessageAdapter;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRA_CREATE_GROUP = "fr.sims.coachingproject.extra.CREATE_GROUP";

    Spinner mSportsSpinner;
    ArrayAdapter<Sport> mSportsAdapter;
    List<Sport> mSportList;
    SportLoaderCallbacks mSportLoader;

    private Button mSendBtn;
    private EditText  mNameET;
    private EditText  mDescriptionET;
    private CheckBox mPrivateCB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mSportList = new ArrayList<>();
        mSportsSpinner = (Spinner) findViewById(R.id.spinner_sports);
        mSportsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mSportList);
        mSportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsSpinner.setAdapter(mSportsAdapter);

        mSportLoader= new SportLoaderCallbacks();
        mSendBtn = (Button) findViewById(R.id.ok_button);
        mSendBtn.setOnClickListener(this);

        mNameET = (EditText) findViewById(R.id.edit_Name);
        mDescriptionET = (EditText) findViewById(R.id.edit_Description);
        mPrivateCB =(CheckBox) findViewById(R.id.checkBox);

        getLoaderManager().initLoader(0, null, mSportLoader);
    }



    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx, CreateGroupActivity.class);
        ctx.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        long  ID_sport = ((Sport)mSportsSpinner.getSelectedItem()).getmIdDb();
        String mName = mNameET.getText().toString();
        String mDescription =  mDescriptionET.getText().toString();
        Boolean mPrivate = mPrivateCB.isChecked();
        String body = "";

        try {
            JSONObject parent = new JSONObject();
            parent.put("sport",ID_sport);
            parent.put("name", ""+mName);
            parent.put("description",""+mDescription);
            parent.put("private",""+mPrivate);

            body = parent.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendRequestTask().execute(body);
    }
    private class SendRequestTask extends AsyncTask<String, Void, NetworkUtil.Response> {
        @Override
        protected NetworkUtil.Response doInBackground(String... params) {
            if (params.length > 0) {
                String body = params[0];
                String connectedToken = SharedPrefUtil.getConnectedToken(getApplicationContext());
                NetworkUtil.Response response = NetworkUtil.post("https://coachingproject.herokuapp.com/api/groups/", connectedToken, body);
                return response;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if(response != null) {
                mSendBtn.setEnabled(true);
                if(response.isSuccessful()) {
                    mNameET.setText("");
                    mDescriptionET.setText("");
//                    NetworkService.startActionMessages(getContext(), mRelationId);
                } else {
//                    Snackbar.make(getListView(), R.string.no_connectivity, Snackbar.LENGTH_SHORT);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            mSendBtn.setEnabled(false);
        }

    }


    class SportLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Sport>> {

        @Override
        public Loader<List<Sport>> onCreateLoader(int id, Bundle args) {
            return new SportLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Sport>> loader, List<Sport> data) {
            mSportList = data;
            mSportsAdapter.clear();

            //Add all sports got from server
            mSportsAdapter.addAll(mSportList);


        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }


    }

}
