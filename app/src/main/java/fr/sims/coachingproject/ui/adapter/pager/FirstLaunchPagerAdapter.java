package fr.sims.coachingproject.ui.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.sims.coachingproject.ui.fragment.FirstLaunchFragment;
import fr.sims.coachingproject.ui.fragment.LoginFragment;

/**
 * Created by Donovan on 02/03/2016.
 */
public class FirstLaunchPagerAdapter extends FragmentPagerAdapter {

        private int mItemNumber;

        public FirstLaunchPagerAdapter(FragmentManager fm, int itemNumber) {
            super(fm);
            mItemNumber = itemNumber;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==3){
                return LoginFragment.newInstance(true);
            }
            return FirstLaunchFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return mItemNumber;
        }

        // TODO Find name
        @Override
        public CharSequence getPageTitle(int position) {
            return FirstLaunchFragment.TABS_TITLE;
        }
}
