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

import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.local.GroupLoader;
import fr.sims.coachingproject.loader.network.InvitationLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.activity.CreateGroupActivity;
import fr.sims.coachingproject.ui.activity.GroupActivity;
import fr.sims.coachingproject.ui.adapter.GroupListAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.SharedPrefUtil;


public class GroupListFragment extends GenericFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, GenericBroadcastReceiver.BroadcastReceiverListener, GroupListAdapter.OnGroupClickListener {

    private long mCurrentUserId;

    public static final String TABS_TITLE = "Groups";

    private GroupListAdapter mGroupAdapter;
    private RecyclerView mGroupList;
    private SwipeRefreshLayout mRefreshLayout;
    private View mEmptyView;

    private GenericBroadcastReceiver mBroadcastReceiver;

    private GroupLoaderCallbacks mGroupLoader;
    private InvitationLoaderCallbacks mInvitationLoader;

    public GroupListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupFragment.
     */
    public static GroupListFragment newInstance() {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGroupLoader = new GroupLoaderCallbacks();
        mInvitationLoader = new InvitationLoaderCallbacks();
        getLoaderManager().initLoader(Const.Loaders.GROUP_LOADER_ID, null, mGroupLoader);
        getLoaderManager().initLoader(Const.Loaders.INVITATION_LOADER_ID, null, mInvitationLoader);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mCurrentUserId = SharedPrefUtil.getConnectedUserId(getContext());
        inflater.inflate(R.menu.activity_creategroup, menu);
        super.onCreateOptionsMenu(menu, inflater);
        boolean mVisible = (mCurrentUserId != -1);
        menu.setGroupVisible(0, mVisible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_group:
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
        mGroupAdapter = new GroupListAdapter(getContext());
        NetworkService.startActionUserGroups(getContext());
        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mGroupList = (RecyclerView) view.findViewById(R.id.group_list);
        mGroupList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupList.setAdapter(mGroupAdapter);

        mEmptyView = view.findViewById(R.id.group_list_empty);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pull_refresh_group);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });

        // set onItemClick event
        mGroupAdapter.setOnGroupClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_list;
    }

    @Override
    public void onRefresh() {
        NetworkService.startActionUserGroups(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkService.startActionUserGroups(getContext());
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_USER_GROUPS) && mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        }
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_INVITATION_USER_GROUPS))
        {
            mRefreshLayout.setRefreshing(true);
            NetworkService.startActionUserGroups(getContext());
            getLoaderManager().restartLoader(Const.Loaders.INVITATION_LOADER_ID, null, mInvitationLoader);
        }
    }

    @Override
    public void onClick(View v) {
        CreateGroupActivity.startActivity(getActivity());
    }

    @Override
    public void onGroupClick(View view, long groupDbId) {
        GroupActivity.startActivity(getContext(), groupDbId);
    }

    private void updateEmptyList(){
        if(mGroupAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mGroupList.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mGroupList.setVisibility(View.VISIBLE);
        }
    }

    public class GroupLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Group>> {
        @Override
        public Loader<List<Group>> onCreateLoader(int id, Bundle args) {
            return new GroupLoader(getContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Group>> loader, List<Group> data) {
            if (data != null) {
                mGroupAdapter.setGroups(data);
            } else {
                mGroupAdapter.clearGroups();
            }
            updateEmptyList();
        }

        @Override
        public void onLoaderReset(Loader<List<Group>> loader) { }
    }

    public class InvitationLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Group>> {
        @Override
        public Loader<List<Group>> onCreateLoader(int id, Bundle args) {
            return new InvitationLoader(getContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Group>> loader, List<Group> data) {
            if (data != null) {
                mGroupAdapter.setInvitations(data);
            } else {
                mGroupAdapter.clearInvitations();
            }
            updateEmptyList();
        }

        @Override
        public void onLoaderReset(Loader<List<Group>> loader) { }
    }


}
