package fr.sims.coachingproject.ui.activity;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

public class SearchActivity extends Activity {

    EditText mSearchInput;
    RecyclerView mRecycleView;
    Button mSearchButton;
    List<UserProfile> mUserList;
    List<Sport> mSportList;
    List<SportLevel> mLevelList;
    SearchListAdapter mAdapter;
    Bundle mSearchArgs;
    Spinner mSportsSpinner;
    Spinner mLevelsSpinner;
    ArrayAdapter<Sport> mSportsAdapter;
    ArrayAdapter<SportLevel> mLevelsAdapter;
    SportLoaderCallbacks mSportLoader;
    CoachLoaderCallbacks mCoachLoader;
    LevelsLoaderCallbacks mLevelLoader;
    private long mIdSport;


    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(0, null, mSportLoader);
        getLoaderManager().restartLoader(1, mSearchArgs, mCoachLoader);
        getLoaderManager().restartLoader(2, null, mLevelLoader);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchArgs = new Bundle();
        mSportLoader = new SportLoaderCallbacks();
        mCoachLoader = new CoachLoaderCallbacks();
        mLevelLoader = new LevelsLoaderCallbacks();
        getLoaderManager().initLoader(0, mSearchArgs, mSportLoader);
        getLoaderManager().initLoader(1, mSearchArgs, mCoachLoader);
        getLoaderManager().initLoader(2, mSearchArgs, mLevelLoader);

        mUserList = new ArrayList<>();
        mSportList = new ArrayList<>();
        mLevelList = new ArrayList<>();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchListAdapter();
        mRecycleView.setAdapter(mAdapter);

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

        mSportsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSearchArgs.putLong("idSport", ((Sport) mSportsSpinner.getSelectedItem()).getmIdDb());
                getLoaderManager().restartLoader(2, mSearchArgs, mLevelLoader);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchArgs.putCharSequence("searchText", mSearchInput.getText().toString());
                mSearchArgs.putLong("idSport", ((Sport) mSportsSpinner.getSelectedItem()).getmIdDb());
                mSearchArgs.putLong("idLevel", ((SportLevel) mLevelsSpinner.getSelectedItem()).getmIdDb());
                getLoaderManager().restartLoader(1, mSearchArgs, mCoachLoader);
            }
        });

    }


    class CoachLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            return new CoachLoader(getApplicationContext(), mSearchArgs.getString("searchText", ""), mSearchArgs.getLong("idSport", -1), mSearchArgs.getLong("idLevel", -1));
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            mUserList = data;
            mAdapter.setData(mUserList);
            mAdapter.setOnItemClickListener(new SearchListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    i.putExtra("id", mUserList.get(position).mIdDb);
                    startActivity(i);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
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
            mSportList = data;
            mSportsAdapter.clear();
            mSportsAdapter.addAll(mSportList);
        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }


    }

    class LevelsLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<SportLevel>> {

        @Override
        public Loader<List<SportLevel>> onCreateLoader(int id, Bundle args) {
            return new LevelLoader(getApplicationContext(), mSearchArgs.getLong("idSport", -1));
        }

        @Override
        public void onLoadFinished(Loader<List<SportLevel>> loader, List<SportLevel> data) {
            mLevelList = data;
            mLevelsAdapter.clear();

            //Create fake sport for "All levels" with ID -1
            SportLevel allLevel = new SportLevel();
            allLevel.mTitle = getString(R.string.all_levels);
            allLevel.mIdDb = -1;
            mLevelsAdapter.add(allLevel);

            //Add all sports got from server
            mLevelsAdapter.addAll(mLevelList);

        }

        @Override
        public void onLoaderReset(Loader<List<SportLevel>> loader) {

        }


    }


}
