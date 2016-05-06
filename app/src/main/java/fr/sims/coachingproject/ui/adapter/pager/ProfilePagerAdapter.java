package fr.sims.coachingproject.ui.adapter.pager;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import fr.sims.coachingproject.ui.fragment.BlogListFragment;
import fr.sims.coachingproject.ui.fragment.ProfileAboutFragment;

/**
 * Created by dfour on 14/03/2016.
 */
public class ProfilePagerAdapter  extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;

    private long mUserId;

    public ProfilePagerAdapter(FragmentManager fm, long userId) {
        super(fm);
        mUserId = userId;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position){
            case 1:
                fragment = BlogListFragment.newInstance(mUserId);
                break;
            case 0:
            default:
                fragment = ProfileAboutFragment.newInstance(mUserId);
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
                title = BlogListFragment.TITLE;
                break;
            case 0:
            default:
                title = ProfileAboutFragment.TITLE;
                break;
        }
        return title;
    }

}
