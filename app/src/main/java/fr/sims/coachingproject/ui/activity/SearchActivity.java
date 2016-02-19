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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class SearchActivity extends Activity {

    EditText mSearchInput;
    RecyclerView mRecycleView;
    Button mSearchButton;
    List<UserProfile> mUserList;
    List<Sport> mSportList;
    SearchListAdapter mAdapter;
    Bundle mSearchArgs;
    Spinner mSportsSpinner;
    ArrayAdapter<Sport> mSportsAdapter;
    SportLoaderCallbacks mSportLoader;
    CoachLoaderCallbacks mCoachLoader;


    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(0, null, mSportLoader);
        getLoaderManager().restartLoader(1, mSearchArgs, mCoachLoader);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchArgs = new Bundle();
        mSportLoader = new SportLoaderCallbacks();
        mCoachLoader = new CoachLoaderCallbacks();
        getLoaderManager().initLoader(0, mSearchArgs, mSportLoader);
        getLoaderManager().initLoader(1, mSearchArgs, mCoachLoader);

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

    }


    class CoachLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            return new CoachLoader(getApplicationContext(), mSearchArgs.getString("searchText", ""), mSearchArgs.getLong("idSport", -1));
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

            mAdapter.setOnItemClickListener(new SearchListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    i.putExtra("id", mSportList.get(position).mIdDb);
                    startActivity(i);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }


    }


}
