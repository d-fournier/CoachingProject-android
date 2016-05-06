package fr.sims.coachingproject.ui.activity;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.GroupSearchLoader;
import fr.sims.coachingproject.loader.local.SportLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.ui.adapter.SearchGroupListAdapter;
import fr.sims.coachingproject.ui.adapter.CityAutoCompleteAdapter;
import fr.sims.coachingproject.util.Const;


/**
 * Created by Anthony Barbosa on 16/02/2016.
 */

public class SearchGroupActivity extends AppCompatActivity implements SearchGroupListAdapter.OnItemClickListener {

    EditText mSearchInput;

    RecyclerView mRecycleView;
    Button mSearchButton;
    Spinner mSportsSpinner;
    List<Group> mGroupList;
    List<Sport> mSportList;
    SearchGroupListAdapter mSearchGroupListAdapter;
    Bundle mSearchArgs;
    ProgressBar mLoadingBar;
    TextView mEmptyGroupListText;
    GroupsLoaderCallbacks mGroupLoader;
    SportLoaderCallbacks mSportLoader;
    ArrayAdapter<Sport> mSportsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

        final AutoCompleteTextView mAutoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        mAutoCompView.setAdapter(new CityAutoCompleteAdapter(this, R.layout.list_item_city));

        mLoadingBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        mSearchArgs = new Bundle();

        mGroupList = new ArrayList<>();
        mSportList = new ArrayList<>();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mSearchGroupListAdapter = new SearchGroupListAdapter(getApplicationContext());
        mSearchGroupListAdapter.setOnItemClickListener(this);
        mRecycleView.setAdapter(mSearchGroupListAdapter);

        mSportsSpinner = (Spinner) findViewById(R.id.spinner_sports);
        mSportsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mSportList);
        mSportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsSpinner.setAdapter(mSportsAdapter);


        mSearchInput = (EditText) findViewById(R.id.inputSearch);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mEmptyGroupListText = (TextView) findViewById(R.id.no_group_text);


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSearchArgs.putCharSequence("searchText", mSearchInput.getText().toString());
                    mSearchArgs.putLong("idSport", ((Sport) mSportsSpinner.getSelectedItem()).getmIdDb());
                    mSearchArgs.putCharSequence("searchPlace", mAutoCompView.getText().toString());
                    getLoaderManager().restartLoader(Const.Loaders.GROUP_LOADER_ID, mSearchArgs, mGroupLoader);
                }catch(NullPointerException e){

                }
            }
        });

        mSportLoader = new SportLoaderCallbacks();
        mGroupLoader = new GroupsLoaderCallbacks();
        getLoaderManager().initLoader(Const.Loaders.GROUP_LOADER_ID, mSearchArgs, mGroupLoader);
        getLoaderManager().initLoader(Const.Loaders.SPORT_LOADER_ID, mSearchArgs, mSportLoader);
    }

    public static void startActivity(Context ctx) {
        Intent startIntent = new Intent(ctx, SearchGroupActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(startIntent);
    }

    @Override
    public void onItemClick(View view, int position) {
        GroupActivity.startActivity(this, mGroupList.get(position).mIdDb);
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }


    class GroupsLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Group>> {


        @Override
        public Loader<List<Group>> onCreateLoader(int id, Bundle args) {
            mLoadingBar.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.GONE);
            mEmptyGroupListText.setVisibility(View.GONE);
            return new GroupSearchLoader(getApplicationContext(), mSearchArgs.getString("searchText", ""), mSearchArgs.getLong("idSport", -1), mSearchArgs.getString("searchPlace", ""));
        }

        @Override
        public void onLoadFinished(Loader<List<Group>> loader, List<Group> data) {
            if(data!=null){
                mGroupList = data;
                mSearchGroupListAdapter.setData(mGroupList);
                mLoadingBar.setVisibility(View.GONE);
                mSportsSpinner.setVisibility(View.VISIBLE);

                if (mGroupList.isEmpty()) {
                    mRecycleView.setVisibility(View.GONE);
                    mEmptyGroupListText.setVisibility(View.VISIBLE);
                } else {
                    mRecycleView.setVisibility(View.VISIBLE);
                    mEmptyGroupListText.setVisibility(View.GONE);
                }
            }else{
                Snackbar.make(mRecycleView,R.string.no_connectivity,Snackbar.LENGTH_LONG).show();
                mLoadingBar.setVisibility(View.GONE);
                mEmptyGroupListText.setVisibility(View.VISIBLE);
                mSportsSpinner.setVisibility(View.GONE);
            }


        }

        @Override
        public void onLoaderReset(Loader<List<Group>> loader) {
        }


    }

    class SportLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Sport>> {

        @Override
        public Loader<List<Sport>> onCreateLoader(int id, Bundle args) {
            return new SportLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Sport>> loader, List<Sport> data) {
            if(data!=null){
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
        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }


    }
}
