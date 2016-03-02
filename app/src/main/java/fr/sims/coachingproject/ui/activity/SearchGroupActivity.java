package fr.sims.coachingproject.ui.activity;


import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupSearchLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.ui.adapter.SearchGroupListAdapter;

/**
 * Created by Anthony Barbosa on 16/02/2016.
 */

public class SearchGroupActivity extends AppCompatActivity implements SearchGroupListAdapter.OnItemClickListener {

    EditText mSearchInput;
    RecyclerView mRecycleView;
    Button mSearchButton;
    List<Group> mGroupList;
    SearchGroupListAdapter mSearchGroupListAdapter;
    Bundle mSearchArgs;
    ProgressBar mLoadingBar;
    TextView mEmptyCoachListText;
    GroupsLoaderCallbacks mGroupLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

        mLoadingBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        mSearchArgs = new Bundle();

        mGroupList = new ArrayList<>();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mSearchGroupListAdapter = new SearchGroupListAdapter();
        mSearchGroupListAdapter.setOnItemClickListener(this);
        mRecycleView.setAdapter(mSearchGroupListAdapter);


        mSearchInput = (EditText) findViewById(R.id.inputSearch);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mEmptyCoachListText = (TextView) findViewById(R.id.no_coach_text);


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          getLoaderManager().restartLoader(1, mSearchArgs, mGroupLoader);
            }
        });


        mGroupLoader = new GroupsLoaderCallbacks();
        getLoaderManager().initLoader(0, null, mGroupLoader);
    }

    @Override
    public void onItemClick(View view, int position) {
        // ProfileActivity.startActivity(this, mUserList.get(position).mIdDb);
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }

    class GroupsLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Group>> {


        @Override
        public Loader<List<Group>> onCreateLoader(int id, Bundle args) {
            mLoadingBar.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.GONE);
            mEmptyCoachListText.setVisibility(View.GONE);
            return new GroupSearchLoader(getApplicationContext(),mSearchArgs.getString("searchText",""),mSearchArgs.getLong("idSport",-1));
        }

        @Override
        public void onLoadFinished(Loader<List<Group>> loader, List<Group> data) {
            mGroupList = data;
            mSearchGroupListAdapter.setData(mGroupList);
            mLoadingBar.setVisibility(View.GONE);

            if (mGroupList.isEmpty()) {
                mRecycleView.setVisibility(View.GONE);
                mEmptyCoachListText.setVisibility(View.VISIBLE);
            } else {
                mRecycleView.setVisibility(View.VISIBLE);
                mEmptyCoachListText.setVisibility(View.GONE);
            }

        }

        @Override
        public void onLoaderReset(Loader<List<Group>> loader) {

        }


    }
}
