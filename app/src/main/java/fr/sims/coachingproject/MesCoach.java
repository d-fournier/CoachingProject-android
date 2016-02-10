package fr.sims.coachingproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by abarbosa on 10/02/2016.
 */
public class MesCoach extends Fragment {
    public final String ARG_OBJECT = "Mes Coach";


    public static MesCoach newInstance() {
        MesCoach fragment = new MesCoach();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_mescoach, container, false);
        Bundle args = getArguments();
        ((TextView) rootView.findViewById(R.id.text1)).setText(
                "test");
        return rootView;
    }
}
