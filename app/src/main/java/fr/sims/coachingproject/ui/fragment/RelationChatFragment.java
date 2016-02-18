package fr.sims.coachingproject.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Segolene on 18/02/2016.
 */
public class RelationChatFragment extends GenericFragment {

    public static final String TABS_TITLE = "Messages";

    @Override
    protected int getLayoutId() {
        return 0;
    }

    public static android.support.v4.app.Fragment newInstance() {
        RelationChatFragment fragment = new RelationChatFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
}
