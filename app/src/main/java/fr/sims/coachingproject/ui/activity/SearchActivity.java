package fr.sims.coachingproject.ui.activity;


import android.app.Activity;



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
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.SearchListAdapter;

/**
 * Created by Anthony Barbosa on 16/02/2016.
 */

public class SearchActivity extends Activity {//implements LoaderManager.LoaderCallbacks<List<UserProfile>> {


    EditText inputSearch;
    RecyclerView mRecycleView;
    List<UserProfile> mlist;
    SearchListAdapter mAdapter;

    ArrayList<HashMap<String,String>> productList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ArrayList<UserProfile> ListCoach = new ArrayList<>();

        mlist = UserProfile.getAllCoachProfile();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchListAdapter();
        mRecycleView.setAdapter(mAdapter);

        for(UserProfile user : mlist)
            ListCoach.add(user);

        mAdapter.setData(ListCoach);

        inputSearch = (EditText) findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                SearchActivity.this.mAdapter.setFilter(cs);

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
}
