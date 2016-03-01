package fr.sims.coachingproject.ui.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.ui.adapter.GroupAdapter;


public class GroupFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<List<Group>>{

    public static final String TABS_TITLE = "Groups";

    private RecyclerView mGroupList;
    private GroupAdapter mGroupAdapter;

    public GroupFragment() {
        // Required empty public constructor
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
    protected int getLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mGroupList = (RecyclerView) view.findViewById(R.id.group_list);
        mGroupList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupAdapter = new GroupAdapter(getContext());
        mGroupList.setAdapter(mGroupAdapter);
    }

    @Override
    public Loader<List<Group>> onCreateLoader(int id, Bundle args) {
        return new GroupLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Group>> loader, List<Group> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<Group>> loader) {

    }
}
