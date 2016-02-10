package fr.sims.coachingproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by abarbosa on 10/02/2016.
 */



public class SwipeView extends FragmentPagerAdapter {
    public SwipeView(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch(i) {
            case 1:
                fragment = QR.newInstance();
                break;
            case 2:
                fragment = GroupFragment.newInstance();
                break;
            case 0:
            default:
                fragment = MesCoach.newInstance();
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
        return "OBJECT " + (position + 1);
    }
}

