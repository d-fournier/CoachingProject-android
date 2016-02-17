package fr.sims.coachingproject.ui.activity;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
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

public class SearchActivity extends Activity implements LoaderManager.LoaderCallbacks<List<UserProfile>> {


    EditText inputSearch;
    RecyclerView mRecycleView;
    List<UserProfile> mUserList;
    List<Sport> mSportList;
    SearchListAdapter mAdapter;
    Bundle searchArgs;
    Spinner mSportsSpinner;


    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(0, searchArgs, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchArgs = new Bundle();
        getLoaderManager().initLoader(0, searchArgs, this);

        mUserList = new ArrayList<>();//UserProfile.getAllCoachProfile();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchListAdapter();
        mRecycleView.setAdapter(mAdapter);

        mSportsSpinner = (Spinner) findViewById(R.id.spinner_sports);


        inputSearch = (EditText) findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                searchArgs.putCharSequence("searchText", inputSearch.getText().toString());
                getLoaderManager().restartLoader(0, searchArgs, SearchActivity.this);

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    class CoachLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            return new CoachLoader(getApplicationContext(), searchArgs.getString("searchText", ""));
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            mUserList = data;
            mAdapter.setData(mUserList);
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
            //Regarder ce qu'il faut mettre en 3eme arg
            mSportsSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,mSportList));
        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }

    }


}
