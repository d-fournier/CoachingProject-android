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
public class QR extends Fragment{

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_qr, container, false);
            ((TextView) rootView.findViewById(android.R.id.text2)).setText(
                    "test2");
            return rootView;
        }

}
