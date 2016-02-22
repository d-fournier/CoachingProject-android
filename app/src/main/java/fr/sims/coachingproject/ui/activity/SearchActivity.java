package fr.sims.coachingproject.ui.activity;


import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.CoachLoader;
import fr.sims.coachingproject.loader.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.SearchListAdapter;

/**
 * Created by Anthony Barbosa on 16/02/2016.
 */

public class SearchActivity extends AppCompatActivity {

    EditText mSearchInput;
    RecyclerView mRecycleView;
    Button mSearchButton;
    List<UserProfile> mUserList;
    List<Sport> mSportList;
    SearchListAdapter mAdapter;
    Bundle mSearchArgs;
    Spinner mSportsSpinner;
    ProgressBar mLoadingBar;
    ArrayAdapter<Sport> mSportsAdapter;
    SportLoaderCallbacks mSportLoader;
    CoachLoaderCallbacks mCoachLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mLoadingBar = (ProgressBar)findViewById(R.id.loading_progress_bar);

        mSearchArgs = new Bundle();

        mUserList = new ArrayList<>();
        mSportList = new ArrayList<>();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchListAdapter();
        mRecycleView.setAdapter(mAdapter);

        mSportsSpinner = (Spinner) findViewById(R.id.spinner_sports);
        mSportsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mSportList);
        mSportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsSpinner.setAdapter(mSportsAdapter);


        mSearchInput = (EditText) findViewById(R.id.inputSearch);

        mSearchButton = (Button) findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchArgs.putCharSequence("searchText", mSearchInput.getText().toString());
                mSearchArgs.putLong("idSport", ((Sport) mSportsSpinner.getSelectedItem()).getmIdDb());
                getLoaderManager().restartLoader(1, mSearchArgs, mCoachLoader);
            }
        });

        mSportLoader = new SportLoaderCallbacks();
        mCoachLoader = new CoachLoaderCallbacks();
        getLoaderManager().initLoader(0, mSearchArgs, mSportLoader);
        getLoaderManager().initLoader(1, mSearchArgs, mCoachLoader);

    }


    class CoachLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            mLoadingBar.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.GONE);
            return new CoachLoader(getApplicationContext(), mSearchArgs.getString("searchText", ""),mSearchArgs.getLong("idSport", -1));
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            mUserList = data;
            mAdapter.setData(mUserList);
            mLoadingBar.setVisibility(View.GONE);
            mRecycleView.setVisibility(View.VISIBLE);
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

            //Create fake sport for "All sports" with ID -1
            Sport allSports = new Sport();
            allSports.mName = getString(R.string.all_sports);
            allSports.mIdDb = -1;
            mSportsAdapter.add(allSports);

            //Add all sports got from server
            mSportsAdapter.addAll(mSportList);


        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }

    }


}
