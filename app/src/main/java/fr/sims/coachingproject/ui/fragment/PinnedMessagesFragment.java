package fr.sims.coachingproject.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;

import fr.sims.coachingproject.R;

/**
 * Created by Segolene on 18/02/2016.
 */
public class PinnedMessagesFragment extends GenericFragment {

    public static final String TABS_TITLE = "Favoris";

    public static android.support.v4.app.Fragment newInstance() {
        PinnedMessagesFragment fragment = new PinnedMessagesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pinned_messages;
    }
}
