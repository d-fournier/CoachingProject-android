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
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.NetworkService;
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.CoachingLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.ui.activity.ProfileActivity;
import fr.sims.coachingproject.ui.adapter.CoachListAdapter;
import fr.sims.coachingproject.util.Const;


/**
 * Created by abarbosa on 10/02/2016.
 */
public class CoachingRelationsFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<List<CoachingRelation>>, RecyclerView.OnItemTouchListener, SwipeRefreshLayout.OnRefreshListener, GenericBroadcastReceiver.BroadcastReceiverListener {

    public static final String TABS_TITLE = "Coaching";


    private RecyclerView mCoachList;
    private SwipeRefreshLayout mRefreshLayout;

    GenericBroadcastReceiver mBroadcastReceiver;

    private CoachListAdapter mRecyclerAdapter;

    private ArrayList<UserProfile> listCr = new ArrayList<>();
    private ArrayList<UserProfile> listLr = new ArrayList<>();
    private ArrayList<UserProfile> Pending_listCr = new ArrayList<>();
    private ArrayList<UserProfile> Pending_listLr = new ArrayList<>();

    public static CoachingRelationsFragment newInstance() {
        CoachingRelationsFragment fragment = new CoachingRelationsFragment();
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
        mCoachList.setAdapter(mRecyclerAdapter);
        mCoachList.addOnItemTouchListener(this);

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
            listCr.clear();
            listLr.clear();
            Pending_listCr.clear();
            Pending_listLr.clear();

            for (CoachingRelation relation : data) {
                if(!relation.mIsPending)
                {
                    if (relation.mCoach.mIdDb != 1)
                        listCr.add(relation.mCoach);
                    else
                        listLr.add(relation.mTrainee);
                }
                else
                {
                    if (relation.mCoach.mIdDb != 1)
                        Pending_listCr.add(relation.mCoach);
                    else
                        Pending_listLr.add(relation.mTrainee);
                }
            }
            mRecyclerAdapter.setDataCr(listCr);
            mRecyclerAdapter.setDataLr(listLr);
            mRecyclerAdapter.setDataPendingCr(Pending_listCr);
            mRecyclerAdapter.setDataPendingLr(Pending_listLr);


            mRecyclerAdapter.setOnItemClickListener(new CoachListAdapter.OnItemClickListener() {
                 @Override
                 public void onItemClick(View view, int position) {
                     Intent i = new Intent(getContext(), ProfileActivity.class);
                     switch(mRecyclerAdapter.getItemViewType(position)){
                         case CoachListAdapter.LIST_COACH:
                             i.putExtra("id",listCr.get(position - 1).mIdDb);
                             break;
                         case CoachListAdapter.LIST_LEARNER:
                             position = position - (listCr.size() + 1);
                             i.putExtra("id",listLr.get(position - 1).mIdDb);
                             break;
                         case CoachListAdapter.LIST_PENDING_COACH:
                             position = position - (listCr.size() + listLr.size() + 2);
                             i.putExtra("id",Pending_listCr.get(position - 1).mIdDb);
                             break;
                         case CoachListAdapter.LIST_PENDING_LEARNER:
                             position = position - (mRecyclerAdapter.getItemCount()-2);
                             i.putExtra("id",Pending_listLr.get(position - 1).mIdDb);
                             break;
                     }
                     startActivity(i);
                 }

                 @Override
                 public void onItemLongClick(View view, int position) {
                     //mRecyclerAdapter.remove(position); //remove the item
                     //Intent i = new Intent(getContext(), ProfileActivity.class);
                     //Toast.makeText(getContext(), Long.toString(iiid), Toast.LENGTH_SHORT).show();
                     //i.putExtra("id",listCr.get(position - 1).mId);
                     //startActivity(i);
                     //startActivity(new Intent(getContext(), ProfileActivity.class));
                 }
            });
    }

    @Override
    public void onLoaderReset(Loader<List<CoachingRelation>> loader) {
        mRecyclerAdapter.clearData();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onRefresh() {
        NetworkService.startActionCoachingRelations(getContext());
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if(intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_COACHING_RELATIONS) && mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        }
    }
}
