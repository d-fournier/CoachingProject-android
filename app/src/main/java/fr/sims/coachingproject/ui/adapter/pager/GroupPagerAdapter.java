package fr.sims.coachingproject.ui.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.sims.coachingproject.ui.fragment.GroupMembersFragment;
import fr.sims.coachingproject.ui.fragment.MessageFragment;

/**
 * Created by Zhenjie CEN on 2016/3/7.
 */
public class GroupPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES_MEMBER = 3;
    private static final int NUM_PAGES_NOT_MEMBER = 1;

    private long mGroupIdDb;

    private boolean mIsMember;

    public GroupPagerAdapter(FragmentManager fm, long groupIdDb, boolean isMember) {
        super(fm);
        mGroupIdDb = groupIdDb;
        mIsMember = isMember;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        if (!mIsMember) {
            switch(position) {
                case 0:
                default:
                    fragment = GroupMembersFragment.newInstance(mGroupIdDb);
                    break;
            }
        } else {
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
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mIsMember ? NUM_PAGES_MEMBER : NUM_PAGES_NOT_MEMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        if (!mIsMember) {
            switch(position) {
                case 0:
                default:
                    title = GroupMembersFragment.MEMBERS_TITLE;
                    break;
            }
        } else {
            switch (position) {
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
        }
        return title;
    }
}



