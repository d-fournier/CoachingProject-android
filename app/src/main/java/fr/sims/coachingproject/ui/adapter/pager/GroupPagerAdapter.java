package fr.sims.coachingproject.ui.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.sims.coachingproject.ui.activity.GroupActivity;
import fr.sims.coachingproject.ui.fragment.GroupMembersFragment;
import fr.sims.coachingproject.ui.fragment.MessageFragment;

/**
 * Created by Zhenjie CEN on 2016/3/7.
 */
public class GroupPagerAdapter extends FragmentPagerAdapter {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    private long mGroupIdDb;

    public GroupPagerAdapter(FragmentManager fm, long groupIdDb) {
        super(fm);
        mGroupIdDb = groupIdDb;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch(position) {
            case 1:
                fragment = MessageFragment.newGroupInstance(mGroupIdDb, true);
                break;
            case 2:
                fragment = GroupMembersFragment.newInstance(mGroupIdDb);
                break;
            case 0:
            default:
                fragment = MessageFragment.newGroupInstance(mGroupIdDb, false);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch(position) {
            case 1:
                title = MessageFragment.PINNED_TITLE;
                break;
            case 2:
                title = GroupMembersFragment.MEMBERS_TITLE;
                break;
            case 0:
            default:
                title = MessageFragment.MESSAGES_TITLE;
                break;
        }
        return title;
    }
}



