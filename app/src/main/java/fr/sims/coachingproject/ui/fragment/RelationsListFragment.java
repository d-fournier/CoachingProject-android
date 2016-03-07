package fr.sims.coachingproject.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import java.util.List;

import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.CoachingLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.ui.activity.RelationActivity;
import fr.sims.coachingproject.ui.adapter.CoachListAdapter;
import fr.sims.coachingproject.util.Const;


/**
 * Created by abarbosa on 10/02/2016.
 */
public class RelationsListFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<List<CoachingRelation>>, SwipeRefreshLayout.OnRefreshListener, GenericBroadcastReceiver.BroadcastReceiverListener, CoachListAdapter.OnItemClickListener {

    public static final String TABS_TITLE = "Coaching";

    private RecyclerView mCoachList;
    private SwipeRefreshLayout mRefreshLayout;

    private CoachListAdapter mRecyclerAdapter;

    GenericBroadcastReceiver mBroadcastReceiver;

    public static RelationsListFragment newInstance() {
        RelationsListFragment fragment = new RelationsListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_coaching_relations;
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mCoachList = (RecyclerView) view.findViewById(R.id.coach_list);
        mCoachList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new CoachListAdapter(getContext());
        mRecyclerAdapter.setOnItemClickListener(this);
        mCoachList.setAdapter(mRecyclerAdapter);

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public Loader<List<CoachingRelation>> onCreateLoader(int id, Bundle args) {
        return new CoachingLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<CoachingRelation>> loader, List<CoachingRelation> data) {
        mRecyclerAdapter.setData(data);
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
    public void onItemClick(View view, int position) {
        long id = mRecyclerAdapter.getRelationId(position);
        Intent intent = RelationActivity.getIntent(getContext(), id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(),
                            Pair.create(view, getString(R.string.transition_relation_picture)),
                            Pair.create(view, getString(R.string.transition_relation_name))
                    );
            getActivity().startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    @Override
    public void onItemLongClick(View view, int position) {
    }

}
