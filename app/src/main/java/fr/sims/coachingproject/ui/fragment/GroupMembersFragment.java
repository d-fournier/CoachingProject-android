package fr.sims.coachingproject.ui.fragment;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.GroupMembersLoader;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.activity.ProfileActivity;
import fr.sims.coachingproject.ui.adapter.GroupMembersAdapter;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Zhenjie CEN on 2016/3/7.
 */
public class GroupMembersFragment extends GenericFragment implements GenericBroadcastReceiver.BroadcastReceiverListener, GroupMembersAdapter.OnUserClickListener {

    public static final String MEMBERS_TITLE = "Members";
    public static final String GROUP_ID = "groupId";

    GroupMembersLoaderCallbacks mGroupMembersLoader;
    GroupPendingMembersLoaderCallbacks mGroupPendingMembersLoader;
    GenericBroadcastReceiver mBroadcastReceiver;
    private GroupMembersAdapter mGroupMembersAdapter;
    private RecyclerView mGroupMembersList;
    private long mGroupId;

    public static GroupMembersFragment newInstance(long groupId) {
        GroupMembersFragment fragment = new GroupMembersFragment();
        Bundle args = new Bundle();
        args.putLong(GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGroupMembersLoader = new GroupMembersLoaderCallbacks();
        mGroupPendingMembersLoader = new GroupPendingMembersLoaderCallbacks();
        getLoaderManager().initLoader(Const.Loaders.GROUP_MEMBERS_LOADER_ID, null, mGroupMembersLoader);
        getLoaderManager().initLoader(Const.Loaders.GROUP_PENDING_MEMBERS_LOADER_ID, null, mGroupPendingMembersLoader);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void bindArguments(Bundle args) {
        super.bindArguments(args);
        mGroupId = args.getLong(GROUP_ID, -1);
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mGroupMembersAdapter = new GroupMembersAdapter(getActivity(), mGroupId);
        mGroupMembersList = (RecyclerView) view.findViewById(R.id.group_members_list);
        mGroupMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupMembersList.setAdapter(mGroupMembersAdapter);
        mGroupMembersAdapter.setOnUserClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_members;
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_ACCEPT_USER_GROUPS)) {
            getLoaderManager().restartLoader(Const.Loaders.GROUP_MEMBERS_LOADER_ID, null, mGroupMembersLoader);
            getLoaderManager().restartLoader(Const.Loaders.GROUP_PENDING_MEMBERS_LOADER_ID, null, mGroupPendingMembersLoader);
        }
    }

    @Override
    public void onUserClick(View view, long userDbId) {
        ProfileActivity.startActivity(getActivity(), userDbId);
    }


    public class GroupPendingMembersLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            return new GroupMembersLoader(getActivity(), mGroupId, true);
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            if (data != null) {
                mGroupMembersAdapter.setPendingMembers(data);
            } else {
                mGroupMembersAdapter.setPendingMembers(new ArrayList<UserProfile>());
            }
        }

        @Override
        public void onLoaderReset(Loader<List<UserProfile>> loader) {
        }
    }

    public class GroupMembersLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            return new GroupMembersLoader(getActivity(), mGroupId, false);
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            if (data != null) {
                mGroupMembersAdapter.setMembers(data);
            } else {
                mGroupMembersAdapter.setMembers(new ArrayList<UserProfile>());
            }
        }

        @Override
        public void onLoaderReset(Loader<List<UserProfile>> loader) {
        }
    }
}
