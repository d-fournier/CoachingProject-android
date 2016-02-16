package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import fr.sims.coachingproject.ui.adapter.CoachListAdapter;


/**
 * Created by abarbosa on 10/02/2016.
 */
public class CoachingRelationsFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<List<CoachingRelation>>, RecyclerView.OnItemTouchListener {

    public static final String TABS_TITLE = "Coaching";


    private RecyclerView mCoachList;
    CoachListAdapter mRecyclerAdapter;
   // private int mDataSetTypes[]= {HEADER, COACH};

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
        setupRecyclerView(view);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        NetworkService.startActionCoachingRelations(getContext());
        getLoaderManager().restartLoader(0, null, this);
    }

    private void setupRecyclerView(View view) {
        mCoachList = (RecyclerView) view.findViewById(R.id.coach_list);
        mCoachList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new CoachListAdapter();
        mCoachList.setAdapter(mRecyclerAdapter);
        mCoachList.addOnItemTouchListener(this);
    }

    @Override
    public Loader<List<CoachingRelation>> onCreateLoader(int id, Bundle args) {
        return (new CoachingLoader(getContext()));
    }

    @Override
    public void onLoadFinished(Loader<List<CoachingRelation>> loader, List<CoachingRelation> data) {
        ArrayList<UserProfile> listCr = new ArrayList<>();
        ArrayList<UserProfile> listLr = new ArrayList<>();
        ArrayList<UserProfile> Pending_listCr = new ArrayList<>();
        ArrayList<UserProfile> Pending_listLr = new ArrayList<>();

            for (CoachingRelation relation : data) {
                if(relation.mIsPending== true)
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
}
