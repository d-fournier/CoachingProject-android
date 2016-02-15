package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.sims.coachingproject.R;


/**
 * Created by abarbosa on 10/02/2016.
 */
public class CoachingRelationsFragment extends GenericFragment {

    public static final String TABS_TITLE = "Coaching";

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
}
