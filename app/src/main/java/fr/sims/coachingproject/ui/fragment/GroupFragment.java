package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;

import fr.sims.coachingproject.R;


public class GroupFragment extends GenericFragment{

    public static final String TABS_TITLE = "Groups";

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
}
