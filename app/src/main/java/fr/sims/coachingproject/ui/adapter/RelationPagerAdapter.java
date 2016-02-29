package fr.sims.coachingproject.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.sims.coachingproject.ui.fragment.PinnedMessagesFragment;
import fr.sims.coachingproject.ui.fragment.RelationChatFragment;

/**
 * Created by Segolene on 18/02/2016.
 */
public class RelationPagerAdapter extends FragmentPagerAdapter {

    private long mRelationId;

    public RelationPagerAdapter(FragmentManager fm, long relationId) {
        super(fm);
        mRelationId=relationId;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch(i) {
            case 1:
                fragment = PinnedMessagesFragment.newInstance();
                break;
            case 0:
            default:
                fragment = RelationChatFragment.newInstance(mRelationId);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch(position) {
            case 1:
                title = PinnedMessagesFragment.TABS_TITLE;
                break;
            case 0:
            default:
                title = RelationChatFragment.TABS_TITLE;
                break;
        }
        return title;
    }
}
