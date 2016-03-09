package fr.sims.coachingproject.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.sims.coachingproject.ui.fragment.GroupMembersFragment;

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
            case 0:
                fragment = GroupMembersFragment.newInstance(mGroupIdDb);
                break;
            case 1:
            case 2:
            default:
                fragment = GroupMembersFragment.newInstance(mGroupIdDb);
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
            case 0:
            case 1:
            case 2:
            default:
                title = GroupMembersFragment.TABS_TITLE;
                break;
        }
        return title;
    }
}



