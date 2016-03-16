package fr.sims.coachingproject.ui.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.local.RelationListLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.activity.RelationActivity;
import fr.sims.coachingproject.ui.adapter.RelationsListAdapter;
import fr.sims.coachingproject.util.Const;


/**
 * Created by abarbosa on 10/02/2016.
 */
public class RelationsListFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<List<CoachingRelation>>, SwipeRefreshLayout.OnRefreshListener, GenericBroadcastReceiver.BroadcastReceiverListener, RelationsListAdapter.OnRelationClickListener {

    public static final String TABS_TITLE = "Coaching";

    private View mEmptyView;
    private RecyclerView mRelationList;
    private SwipeRefreshLayout mRefreshLayout;

    private RelationsListAdapter mRecyclerAdapter;

    GenericBroadcastReceiver mBroadcastReceiver;

    public static RelationsListFragment newInstance() {
        RelationsListFragment fragment = new RelationsListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_relations_list;
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);

        mEmptyView = view.findViewById(R.id.relations_list_empty);

        mRelationList = (RecyclerView) view.findViewById(R.id.relations_list);
        mRelationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new RelationsListAdapter(getContext());
        mRecyclerAdapter.setOnRelationClickListener(this);
        mRelationList.setAdapter(mRecyclerAdapter);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pull_refresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        NetworkService.startActionCoachingRelations(getContext());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Const.Loaders.RELATION_LIST_LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(Const.Loaders.RELATION_LIST_LOADER_ID, null, this);
    }


    @Override
    public Loader<List<CoachingRelation>> onCreateLoader(int id, Bundle args) {
        return new RelationListLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<CoachingRelation>> loader, List<CoachingRelation> data) {
        mRecyclerAdapter.setData(data);
        if(mRecyclerAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRelationList.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRelationList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CoachingRelation>> loader) {
        mRecyclerAdapter.clearData();
    }

    @Override
    public void onRefresh() {
        NetworkService.startActionCoachingRelations(getContext());
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_COACHING_RELATIONS) && mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRelationClick(View view, long relationIdDb) {
        Intent intent = RelationActivity.getIntent(getContext(), relationIdDb);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(getActivity(),view.findViewById(R.id.user_picture),
                            getString(R.string.transition_user_picture));
            getActivity().startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }
}
