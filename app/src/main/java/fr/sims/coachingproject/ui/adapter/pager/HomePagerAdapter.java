package fr.sims.coachingproject.ui.adapter.pager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import fr.sims.coachingproject.ui.fragment.RelationsListFragment;
import fr.sims.coachingproject.ui.fragment.GroupListFragment;

/**
 * Created by abarbosa on 10/02/2016.
 */



public class HomePagerAdapter extends FragmentPagerAdapter {

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        
        switch(i) {
            case 1:
                fragment = GroupListFragment.newInstance();
                break;
            case 0:
            default:
                fragment = RelationsListFragment.newInstance();
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
                title = GroupListFragment.TABS_TITLE;
                break;
            case 0:
            default:
                title = RelationsListFragment.TABS_TITLE;
                break;
        }
        return title;
    }
}

