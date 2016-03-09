package fr.sims.coachingproject.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.sims.coachingproject.ui.fragment.RelationsListFragment;
import fr.sims.coachingproject.ui.fragment.GroupFragment;
import fr.sims.coachingproject.ui.fragment.QuestionsAnswersFragment;

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
                fragment = QuestionsAnswersFragment.newInstance();
                break;
            case 2:
                fragment = GroupFragment.newInstance();
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
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch(position) {
            case 1:
                title = QuestionsAnswersFragment.TABS_TITLE;
                break;
            case 2:
                title = GroupFragment.TABS_TITLE;
                break;
            case 0:
            default:
                title = RelationsListFragment.TABS_TITLE;
                break;
        }
        return title;
    }
}

