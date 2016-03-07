package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.sims.coachingproject.R;

/**
 * Created by Zhenjie CEN on 2016/3/7.
 */
public class GroupMembersFragment extends GenericFragment {

    public static final String TABS_TITLE = "Members";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_group_members, container, false);

        TextView tv = (TextView) rootView.findViewById(R.id.tvFragFirst);
        tv.setText(getArguments().getString("msg"));
        return rootView;
    }

    public static GroupMembersFragment newInstance() {
        GroupMembersFragment fragment = new GroupMembersFragment();
        Bundle args = new Bundle();

        args.putString("msg", "members");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_members;
    }
}
