package fr.sims.coachingproject.ui.activity;


import android.app.LoaderManager;
import android.content.Loader;
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

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.CoachLoader;
import fr.sims.coachingproject.loader.LevelLoader;
import fr.sims.coachingproject.loader.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.SearchListAdapter;

/**
 * Created by Anthony Barbosa on 16/02/2016.
 */

public class SearchActivity extends AppCompatActivity implements SearchListAdapter.OnItemClickListener {

    private final static String ID_SPORT = "idSport";
    private final static String ID_LEVEL = "idLevel";
    private final static String SEARCH_TEXT = "searchText";

    Snackbar mSnackbar;
    EditText mSearchInput;
    RecyclerView mRecycleView;
    Button mSearchButton;
    List<UserProfile> mUserList;
    List<Sport> mSportList;
    List<SportLevel> mLevelList;
    SearchListAdapter mSearchListAdapter;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mLoadingBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        mSearchArgs = new Bundle();

        mUserList = new ArrayList<>();
        mSportList = new ArrayList<>();
        mLevelList = new ArrayList<>();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mSearchListAdapter = new SearchListAdapter(getApplicationContext());
        mSearchListAdapter.setOnItemClickListener(this);
        mRecycleView.setAdapter(mSearchListAdapter);

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
                    getLoaderManager().restartLoader(2, mSearchArgs, mLevelLoader);
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
                getLoaderManager().restartLoader(1, mSearchArgs, mCoachLoader);
                if(mSportList.isEmpty()){
                    getLoaderManager().restartLoader(0, mSearchArgs, mSportLoader);
                }
            }
        });

        mSportLoader = new SportLoaderCallbacks();
        mCoachLoader = new CoachLoaderCallbacks();
        mLevelLoader = new LevelsLoaderCallbacks();
        getLoaderManager().initLoader(0, mSearchArgs, mSportLoader);
        getLoaderManager().initLoader(2, mSearchArgs, mLevelLoader);
        getLoaderManager().initLoader(1, mSearchArgs, mCoachLoader);
    }

    @Override
    public void onItemClick(View view, int position) {
        ProfileActivity.startActivity(this, mUserList.get(position).mIdDb);
    }

    @Override
    public void onItemLongClick(View view, int position) {
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
                mSnackbar = Snackbar.make(mRecycleView, R.string.no_connectivity, Snackbar.LENGTH_LONG);
                mSportsSpinner.setVisibility(View.GONE);
                mLevelsSpinner.setVisibility(View.GONE);
                mSnackbar.show();
                mUserList.clear();
            } else {
                //We clear and add the new data
                mUserList.clear();
                mUserList.addAll(data);
            }

            mSearchListAdapter.setData(mUserList);
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

}
