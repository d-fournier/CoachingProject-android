package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.ui.adapter.GroupAdapter;


public class GroupFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Group>>{

    public static final String TABS_TITLE = "Groups";

    private GroupAdapter mGroupAdapter;

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
        mGroupAdapter = new GroupAdapter(getContext(), R.layout.list_item_group);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        super.setListAdapter(adapter);
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
            this.setListAdapter(mGroupAdapter);
        }else{
            mGroupAdapter.setData(new ArrayList<Group>());
            this.setListAdapter(mGroupAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Group>> loader) {

    }

}
