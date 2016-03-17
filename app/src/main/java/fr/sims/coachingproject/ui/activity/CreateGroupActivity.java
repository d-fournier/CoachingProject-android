package fr.sims.coachingproject.ui.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.SportLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.ui.adapter.CityAutoCompleteAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner mSportsSpinner;
    ArrayAdapter<Sport> mSportsAdapter;
    List<Sport> mSportList;
    SportLoaderCallbacks mSportLoader;
    AutoCompleteTextView mCityAT;
    private Button mSendBtn;
    private EditText mNameET;
    private EditText mDescriptionET;
    private CheckBox mPrivateCB;

    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx, CreateGroupActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mCityAT = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        mCityAT.setAdapter(new CityAutoCompleteAdapter(this, R.layout.list_item_city));

        mSportList = new ArrayList<>();
        mSportsSpinner = (Spinner) findViewById(R.id.spinner_sports);
        mSportsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mSportList);
        mSportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsSpinner.setAdapter(mSportsAdapter);

        mSportLoader = new SportLoaderCallbacks();
        mSendBtn = (Button) findViewById(R.id.ok_button);
        mSendBtn.setOnClickListener(this);

        mNameET = (EditText) findViewById(R.id.edit_Name);
        mDescriptionET = (EditText) findViewById(R.id.edit_Description);
        mPrivateCB = (CheckBox) findViewById(R.id.checkBox);

        getLoaderManager().initLoader(Const.Loaders.SPORT_LOADER_ID, null, mSportLoader);
    }

    @Override
    public void onClick(View v) {
        long ID_sport = ((Sport) mSportsSpinner.getSelectedItem()).getmIdDb();
        String mName = mNameET.getText().toString();
        String mDescription = mDescriptionET.getText().toString();
        String mCity = mCityAT.getText().toString();
        Boolean mPrivate = mPrivateCB.isChecked();
        JSONObject json = new JSONObject();
        if (mName.length() == 0) {
            Snackbar.make(mSendBtn, "Please enter a name", Snackbar.LENGTH_LONG).show();
        } else if (mDescription.length() == 0) {
            Snackbar.make(mSendBtn, "Don't forget the description", Snackbar.LENGTH_LONG).show();
        } else if (mCity.length() == 0) {
            Snackbar.make(mSendBtn, "Please choose the city", Snackbar.LENGTH_LONG).show();
        } else {
            try {
                json.put("sport", ID_sport);
                json.put("name", "" + mName);
                json.put("description", "" + mDescription);
                json.put("city", "" + mCity);
                json.put("private", "" + mPrivate);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendRequestTask().execute(json.toString());
        }
    
    }

    private class SendRequestTask extends AsyncTask<String, Void, NetworkUtil.Response> {
        @Override
        protected NetworkUtil.Response doInBackground(String... params) {
            String body = params[0];
            String connectedToken = SharedPrefUtil.getConnectedToken(getApplicationContext());
            NetworkUtil.Response creation_response =  NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS, connectedToken, body);
            NetworkUtil.Response group_response = new NetworkUtil.Response("", NetworkUtil.Response.UNKNOWN_ERROR);
            if (creation_response.isSuccessful()) {
                JSONObject json = null;
                try {
                    json = new JSONObject(creation_response.getBody());
                    group_response =  NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS+json.getInt("id"), connectedToken, body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return group_response;
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if (response.isSuccessful()) {
                Group g = Group.parseItem(response.getBody());
                GroupActivity.startActivity(getApplicationContext(),g.mIdDb);
                CreateGroupActivity.this.finish();
            } else {
                Snackbar.make(mSendBtn, response.getBody(), Snackbar.LENGTH_LONG).show();
                //TODO voir quoi afficher, là je mets juste le body de la réponse
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
