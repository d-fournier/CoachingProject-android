package fr.sims.coachingproject.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupLoader;
import fr.sims.coachingproject.loader.GroupMembersLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.activity.GroupActivity;
import fr.sims.coachingproject.ui.adapter.GroupAdapter;
import fr.sims.coachingproject.ui.adapter.GroupMembersAdapter;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Zhenjie CEN on 2016/3/7.
 */
public class GroupMembersFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<List<UserProfile>>, SwipeRefreshLayout.OnRefreshListener, GenericBroadcastReceiver.BroadcastReceiverListener{

    public static final String TABS_TITLE = "Members";

    private RecyclerView mRecyclerView;
    private GroupMembersAdapter mGroupMembersAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private RecyclerView mGroupMembersList;
    private SwipeRefreshLayout mRefreshLayout;

    public static GroupMembersFragment newInstance() {
        GroupMembersFragment fragment = new GroupMembersFragment();
        Bundle args = new Bundle();
        //args.putString("msg", "members");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Const.Loaders.GROUP_MEMBERS_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupMembersAdapter = new GroupMembersAdapter();
        NetworkService.startActionUserGroups(getContext());
        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_group_members, container, false);

        //TextView tv = (TextView) rootView.findViewById(R.id.tvFragFirst);
        //tv.setText(getArguments().getString("msg"));
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.group_members_list);

        return rootView;
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mGroupMembersList = (RecyclerView) view.findViewById(R.id.group_list);
        mGroupMembersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupMembersList.setAdapter(mGroupMembersAdapter);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pull_refresh_group);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });

        // set onItemClick event
        mGroupMembersAdapter.setOnItemClickListener(new GroupAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                //Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(getActivity(), GroupActivity.class);
                myIntent.putExtra("groupIdDb", data);
                getActivity().startActivity(myIntent);
            }
        });

        NetworkService.startActionGroups(getContext());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_members;
    }

    @Override
    public Loader<List<UserProfile>> onCreateLoader(int id, Bundle args) {
        return new GroupMembersLoader(getContext(),-1);
    }

    @Override
    public void onLoadFinished(Loader<List<UserProfile>> loader, List<UserProfile> data) {
        if(data != null) {
            mGroupMembersAdapter.clearData();
            mGroupMembersAdapter.setData(data);
        }else{
            mGroupMembersAdapter.setData(new ArrayList<Group>());
            //Here is an error
        }
    }

    @Override
    public void onLoaderReset(Loader<List<UserProfile>> loader) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onBroadcastReceive(Intent intent) {

    }
}
