package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupMembersLoader;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.activity.ProfileActivity;
import fr.sims.coachingproject.ui.adapter.GroupMembersAdapter;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Zhenjie CEN on 2016/3/7.
 */
public class GroupMembersFragment extends GenericFragment {

    public static final String MEMBERS_TITLE = "Members";
    GroupMembersLoaderCallbacks mGroupMembersLoader;
    GroupPendingMembersLoaderCallbacks mGroupPendingMembersLoader;
    private GroupMembersAdapter mGroupMembersAdapter;
    private RecyclerView mGroupMembersList;
    private long mGroupId;

    public static GroupMembersFragment newInstance(long groupId) {
        GroupMembersFragment fragment = new GroupMembersFragment();
        Bundle args = new Bundle();
        fragment.mGroupId = groupId;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGroupMembersLoader = new GroupMembersLoaderCallbacks();
        mGroupPendingMembersLoader = new GroupPendingMembersLoaderCallbacks();
        getLoaderManager().initLoader(Const.Loaders.GROUP_MEMBERS_LOADER_ID, null, mGroupMembersLoader);
        getLoaderManager().initLoader(Const.Loaders.GROUP_PENDING_MEMBERS_LOADER_ID,null,mGroupPendingMembersLoader);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mGroupMembersAdapter = new GroupMembersAdapter(getContext());
        mGroupMembersList = (RecyclerView) view.findViewById(R.id.group_members_list);
        mGroupMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupMembersList.setAdapter(mGroupMembersAdapter);

        mGroupMembersAdapter.setOnItemClickListener(new GroupMembersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ProfileActivity.startActivity(getContext(), mGroupMembersAdapter.getMemberId(position), -1);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_members;
    }


    public class GroupPendingMembersLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {


        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            return new GroupMembersLoader(getContext(),mGroupId, true);
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            if (data != null) {
                mGroupMembersAdapter.clearPendingMembers();
                mGroupMembersAdapter.setPendingMembers(data);
            } else {
                mGroupMembersAdapter.setPendingMembers(new ArrayList<UserProfile>());
                //TODO Don't display this section if not admin
            }
        }

        @Override
        public void onLoaderReset(Loader<List<UserProfile>> loader) {

        }
    }


    public class GroupMembersLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            return new GroupMembersLoader(getContext(),mGroupId, false);
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            if (data != null) {
                mGroupMembersAdapter.clearMembers();
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
