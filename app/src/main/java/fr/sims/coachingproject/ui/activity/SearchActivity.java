package fr.sims.coachingproject.ui.activity;


import android.app.Activity;


import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.CoachLoader;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.SearchListAdapter;

/**
 * Created by Anthony Barbosa on 16/02/2016.
 */

public class SearchActivity extends Activity implements LoaderManager.LoaderCallbacks<List<UserProfile>> {


    EditText inputSearch;
    RecyclerView mRecycleView;
    List<UserProfile> mlist;
    SearchListAdapter mAdapter;
    Bundle searchArgs;

    ArrayList<HashMap<String, String>> productList;


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

        ArrayList<UserProfile> ListCoach = new ArrayList<>();

        mlist = new ArrayList<>();//UserProfile.getAllCoachProfile();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchListAdapter();
        mRecycleView.setAdapter(mAdapter);


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

    @Override
    public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
        return new CoachLoader(this, searchArgs.getString("searchText",""));
    }

    @Override
    public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
        mlist = data;
        mAdapter.setData(mlist);
    }

    @Override
    public void onLoaderReset(Loader<List<UserProfile>> loader) {

    }
}
