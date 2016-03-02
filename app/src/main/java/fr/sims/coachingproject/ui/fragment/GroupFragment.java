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
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.NetworkService;
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.ui.adapter.GroupAdapter;
import fr.sims.coachingproject.util.Const;


public class GroupFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<List<Group>>, SwipeRefreshLayout.OnRefreshListener, GenericBroadcastReceiver.BroadcastReceiverListener{

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));

    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mGroupList = (RecyclerView) view.findViewById(R.id.group_list);
        mGroupList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupAdapter = new GroupAdapter();
        mGroupList.setAdapter(mGroupAdapter);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pull_refresh_group);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        getLoaderManager().initLoader(0, null, this);
        NetworkService.startActionGroups(getContext());
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
        if(data != null) {
            mGroupAdapter.clearData();
            mGroupAdapter.setData(data);
        }else{
            mGroupAdapter.setData(new ArrayList<Group>());
            //Here is an error
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Group>> loader) {

    }

    @Override
    public void onRefresh() {
        NetworkService.startActionGroups(getContext());
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_GROUPS) && mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        }
    }
}
