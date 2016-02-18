package fr.sims.coachingproject.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Segolene on 18/02/2016.
 */
public class PinnedMessagesFragment extends GenericFragment {

    public static final String TABS_TITLE = "Favoris";

    @Override
    protected int getLayoutId() {
        return 0;
    }

    public static android.support.v4.app.Fragment newInstance() {
        PinnedMessagesFragment fragment = new PinnedMessagesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
}
