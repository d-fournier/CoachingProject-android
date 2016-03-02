package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by dfour on 11/02/2016.
 */
public abstract class GenericFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            bindArguments(args);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayoutId(), container, false);
        bindView(view);
        return view;
    }


    protected abstract int getLayoutId();

    protected void bindArguments(Bundle args) {}

    protected void bindView(View view) {}
}
