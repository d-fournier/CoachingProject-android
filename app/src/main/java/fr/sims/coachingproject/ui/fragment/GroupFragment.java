package fr.sims.coachingproject.ui.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.activity.CreateGroupActivity;
import fr.sims.coachingproject.ui.activity.GroupActivity;
import fr.sims.coachingproject.ui.adapter.GroupAdapter;
import fr.sims.coachingproject.util.Const;


public class GroupFragment extends GenericFragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<Group>>, SwipeRefreshLayout.OnRefreshListener, GenericBroadcastReceiver.BroadcastReceiverListener{


    public static final String TABS_TITLE = "Groups";

    private GroupAdapter mGroupAdapter;
    private RecyclerView mGroupList;
    private SwipeRefreshLayout mRefreshLayout;
    private GenericBroadcastReceiver mBroadcastReceiver;

    public GroupFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupFragment.
     */
    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Const.Loaders.GROUP_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_creategroup,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_group :
                CreateGroupActivity.startActivity(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mGroupAdapter = new GroupAdapter();
        NetworkService.startActionUserGroups(getContext());
        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mGroupList = (RecyclerView) view.findViewById(R.id.group_list);
        mGroupList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupList.setAdapter(mGroupAdapter);


        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pull_refresh_group);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });

        // set onItemClick event
        mGroupAdapter.setOnItemClickListener(new GroupAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                GroupActivity.startActivity(getContext(), Long.parseLong(data));
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group;
    }


    @Override
    public Loader<List<Group>> onCreateLoader(int id, Bundle args) {
        return new GroupLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Group>> loader, List<Group> data) {
        if (data != null) {
            mGroupAdapter.clearData();
            mGroupAdapter.setData(data);

        } else {
            mGroupAdapter.setData(new ArrayList<Group>());
        }
    }



    @Override
    public void onLoaderReset(Loader<List<Group>> loader) {

    }

    @Override
    public void onRefresh() {
        NetworkService.startActionUserGroups(getContext());
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_USER_GROUPS) && mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        CreateGroupActivity.startActivity(getActivity());
    }

}
