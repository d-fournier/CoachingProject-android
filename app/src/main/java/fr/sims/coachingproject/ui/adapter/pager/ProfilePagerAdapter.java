package fr.sims.coachingproject.ui.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
                fragment = ProfileAboutFragment.newInstance(mUserId);
                break;
            case 0:
            default:
                fragment = BlogListFragment.newInstance(mUserId);
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
                title = ProfileAboutFragment.TITLE;
                break;
            case 0:
            default:
                title = BlogListFragment.TITLE;
                break;
        }
        return title;
    }

}
