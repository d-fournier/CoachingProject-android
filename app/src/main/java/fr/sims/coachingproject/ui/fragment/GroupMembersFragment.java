package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupMembersLoader;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.activity.ProfileActivity;
import fr.sims.coachingproject.ui.adapter.UserProfileAdapter;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Zhenjie CEN on 2016/3/7.
 */
public class GroupMembersFragment extends GenericFragment {

    public static final String TABS_TITLE = "Members";
    GroupMembersLoaderCallbacks mGroupLoader;
    private UserProfileAdapter mGroupMembersAdapter;
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
        mGroupLoader = new GroupMembersLoaderCallbacks();
        getLoaderManager().initLoader(Const.Loaders.GROUP_MEMBERS_LOADER_ID, null, mGroupLoader);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mGroupMembersAdapter = new UserProfileAdapter(getContext());
        mGroupMembersList = (RecyclerView) view.findViewById(R.id.group_members_list);
        mGroupMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupMembersList.setAdapter(mGroupMembersAdapter);

        mGroupMembersAdapter.setOnItemClickListener(new UserProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ProfileActivity.startActivity(getContext(), mGroupMembersAdapter.getItemId(position),-1);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_members;
    }


    public class GroupMembersLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<UserProfile>> {

        @Override
        public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
            return new GroupMembersLoader(getContext(),mGroupId);
        }

        @Override
        public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
            if (data != null) {
                mGroupMembersAdapter.clearData();
                mGroupMembersAdapter.setData(data);
            } else {
                mGroupMembersAdapter.setData(new ArrayList<UserProfile>());
                //Here is an error
            }
        }

        @Override
        public void onLoaderReset(Loader<List<UserProfile>> loader) {

        }
    }
}
