package fr.sims.coachingproject.ui.activity;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.CoachLoader;
import fr.sims.coachingproject.loader.network.LevelLoader;
import fr.sims.coachingproject.loader.network.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.UserProfileAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Anthony Barbosa on 16/02/2016.
 */

public class SearchActivity extends AppCompatActivity implements UserProfileAdapter.OnItemClickListener {

    private final static String ID_SPORT = "idSport";
    private final static String ID_LEVEL = "idLevel";
    private final static String SEARCH_TEXT = "searchText";

    EditText mSearchInput;
    RecyclerView mRecycleView;
    Button mSearchButton;
    List<UserProfile> mUserList;
    List<Sport> mSportList;
    List<SportLevel> mLevelList;
    UserProfileAdapter mUserProfileAdapter;
    Bundle mSearchArgs;
    Spinner mSportsSpinner;
    ProgressBar mLoadingBar;
    Spinner mLevelsSpinner;
    TextView mEmptyCoachListText;
    ArrayAdapter<Sport> mSportsAdapter;
    ArrayAdapter<SportLevel> mLevelsAdapter;
    SportLoaderCallbacks mSportLoader;
    CoachLoaderCallbacks mCoachLoader;
    LevelsLoaderCallbacks mLevelLoader;
    private SearchActivity mActivity;

    private boolean inviteInGroup;
    private long mInviteGroupIdDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mActivity = this;
        mLoadingBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        mSearchArgs = new Bundle();
        inviteInGroup = getIntent().getBooleanExtra("invite",false);
        mInviteGroupIdDb = getIntent().getLongExtra("groupId",-1);

        mUserList = new ArrayList<>();
        mSportList = new ArrayList<>();
        mLevelList = new ArrayList<>();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mUserProfileAdapter = new UserProfileAdapter(getApplicationContext());
        mUserProfileAdapter.setOnItemClickListener(this);
        mRecycleView.setAdapter(mUserProfileAdapter);

        mSportsSpinner = (Spinner) findViewById(R.id.spinner_sports);
        mSportsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mSportList);
        mSportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsSpinner.setAdapter(mSportsAdapter);

        mLevelsSpinner = (Spinner) findViewById(R.id.spinner_levels);
        mLevelsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mLevelList);
        mLevelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLevelsSpinner.setAdapter(mLevelsAdapter);

        mSearchInput = (EditText) findViewById(R.id.inputSearch);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mEmptyCoachListText = (TextView) findViewById(R.id.no_coach_text);

        mSportsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //We get the Sport selected
                long sportID = ((Sport) mSportsSpinner.getSelectedItem()).getmIdDb();

                //If it's not "All sports", we put the levels in the level spinner, and we displays it
                mLevelsAdapter.clear();
                mLevelsSpinner.setVisibility(View.GONE);
                if (sportID != -1) {
                    mSearchArgs.putLong(ID_SPORT, sportID);
                    getLoaderManager().restartLoader(Const.Loaders.LEVEL_LOADER_ID, mSearchArgs, mLevelLoader);
                } else {
                    mSearchArgs.remove(ID_LEVEL);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchArgs.putCharSequence(SEARCH_TEXT, mSearchInput.getText().toString());
                try {
                    mSearchArgs.putLong(ID_SPORT, ((Sport) mSportsSpinner.getSelectedItem()).getmIdDb());
                } catch (NullPointerException e) {
                    mSearchArgs.putLong(ID_SPORT, -1);
                }
                try {
                    mSearchArgs.putLong(ID_LEVEL, ((SportLevel) mLevelsSpinner.getSelectedItem()).getmIdDb());
                } catch (NullPointerException e) {
                    mSearchArgs.putLong(ID_LEVEL, -1);
                }
                getLoaderManager().restartLoader(Const.Loaders.COACH_LOADER_ID, mSearchArgs, mCoachLoader);
                if(mSportList.isEmpty()){
                    getLoaderManager().restartLoader(Const.Loaders.SPORT_LOADER_ID, mSearchArgs, mSportLoader);
                }
            }
        });

        mSportLoader = new SportLoaderCallbacks();
        mCoachLoader = new CoachLoaderCallbacks();
        mLevelLoader = new LevelsLoaderCallbacks();
        getLoaderManager().initLoader(Const.Loaders.SPORT_LOADER_ID, mSearchArgs, mSportLoader);
        getLoaderManager().initLoader(Const.Loaders.LEVEL_LOADER_ID, mSearchArgs, mLevelLoader);
        getLoaderManager().initLoader(Const.Loaders.COACH_LOADER_ID, mSearchArgs, mCoachLoader);
    }

    public static void startActivity(Context ctx, boolean invite, long groupId) {
        Intent startIntent = new Intent(ctx, SearchActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.putExtra("invite", invite);
        startIntent.putExtra("groupId",groupId);
        ctx.startActivity(startIntent);
    }

    @Override
    public void onItemClick(View view, int position) {
        ProfileActivity.startActivity(this, mUserList.get(position).mIdDb,mSportsSpinner.getSelectedItemId());
    }

    @Override
    public void onItemLongClick(View view, int position) {
        if(inviteInGroup){
            new InviteTask().execute(mUserList.get(position).mIdDb);
        }
    }

    class CoachLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            /*
            When we load data from the server
            We display the progressbar, we hide the list and the text for empty list
             */
            mLoadingBar.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.GONE);
            mEmptyCoachListText.setVisibility(View.GONE);
            return new CoachLoader(getApplicationContext(), mSearchArgs.getString(SEARCH_TEXT, ""), mSearchArgs.getLong(ID_SPORT, -1), mSearchArgs.getLong(ID_LEVEL, -1));
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            /*
            If data is Null, it's because we got no response => We have no network connection
             */
            if (data == null) {
                /*
                We display the snackbar with "No network" message
                We hide the spinners for sports and levels
                We clear the list of users so that it shows nothing
                 */
                Snackbar.make(mRecycleView, R.string.no_connectivity, Snackbar.LENGTH_LONG).show();
                mSportsSpinner.setVisibility(View.GONE);
                mLevelsSpinner.setVisibility(View.GONE);
                mUserList.clear();
            } else {
                //We clear and add the new data
                mUserList.clear();
                mUserList.addAll(data);
            }

            mUserProfileAdapter.setData(mUserList);
            mLoadingBar.setVisibility(View.GONE);

            if (mUserList.isEmpty()) {
                //If the userList is empty, we display the message "No Coach found"
                mRecycleView.setVisibility(View.GONE);
                mEmptyCoachListText.setVisibility(View.VISIBLE);
            } else {
                mRecycleView.setVisibility(View.VISIBLE);
                mEmptyCoachListText.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<UserProfile>> loader) {
        }

    }

    class SportLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Sport>> {

        @Override
        public Loader<List<Sport>> onCreateLoader(int id, Bundle args) {
            return new SportLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Sport>> loader, List<Sport> data) {
            mSportsAdapter.clear();
            if (data != null) {
                mSportsAdapter.addAll(data);

                //Create fake sport for "All sports" with ID -1
                Sport allSports = new Sport();
                allSports.mName = getString(R.string.all_sports);
                allSports.mIdDb = -1;

                mSportsAdapter.insert(allSports, 0);

                mSportsSpinner.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }


    }

    class LevelsLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<SportLevel>> {

        @Override
        public Loader<List<SportLevel>> onCreateLoader(int id, Bundle args) {
            return new LevelLoader(getApplicationContext(), mSearchArgs.getLong(ID_SPORT, -1));
        }

        @Override
        public void onLoadFinished(Loader<List<SportLevel>> loader, List<SportLevel> data) {
            mLevelsAdapter.clear();
            if (data != null && !data.isEmpty()) {
                mLevelsAdapter.addAll(data);

                //Create fake sport for "All levels" with ID -1
                SportLevel allLevel = new SportLevel();
                allLevel.mTitle = getString(R.string.all_levels);
                allLevel.mIdDb = -1;

                mLevelsAdapter.insert(allLevel, 0);

                mLevelsSpinner.setVisibility(View.VISIBLE);
                mLevelsSpinner.setSelection(0);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<SportLevel>> loader) {

        }
    }

    private class InviteTask extends AsyncTask<Long, Void, NetworkUtil.Response> {
        @Override
        protected NetworkUtil.Response doInBackground(Long... params) {
            JSONObject json = new JSONObject();
            JSONArray idArray= new JSONArray();
            for (long userId : params) {
                idArray.put(userId);
            }
            try {
                json.put("users",idArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String connectedToken = SharedPrefUtil.getConnectedToken(getApplicationContext());
            return NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mInviteGroupIdDb + Const.WebServer.SEPARATOR
                    + Const.WebServer.INVITE+Const.WebServer.SEPARATOR, connectedToken, json.toString());
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if (response.isSuccessful()) {
                mActivity.finish();
            } else {
                Snackbar.make(mRecycleView, response.getBody().replace("\"",""), Snackbar.LENGTH_LONG).show();
                //TODO voir quoi afficher, là je mets juste le body de la réponse
            }
        }

        @Override
        protected void onPreExecute() {

        }

    }

}
