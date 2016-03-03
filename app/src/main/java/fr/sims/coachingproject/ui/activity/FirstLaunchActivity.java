package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.ui.adapter.FirstLaunchPagerAdapter;

public class FirstLaunchActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private FirstLaunchPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private ImageButton mNextBtn;
    private ImageButton mPreviousBtn;

    public static void startActivity(Context ctx){
        Intent intent = new Intent(ctx,FirstLaunchActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new FirstLaunchPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.first_launch_title).length);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mNextBtn = (ImageButton) findViewById(R.id.first_launch_next);
        mPreviousBtn = (ImageButton) findViewById(R.id.first_launch_previous);

        updateBtn(0);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        updateBtn(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {  }

    private void updateBtn(int position){
        int itemNumber = mSectionsPagerAdapter.getCount();

        if(position > 0) {
            mPreviousBtn.setVisibility(View.VISIBLE);
            mPreviousBtn.setOnClickListener(this);
        } else {
            mPreviousBtn.setVisibility(View.GONE);
            mPreviousBtn.setOnClickListener(null);
        }
        if(position < itemNumber-1) {
            mNextBtn.setVisibility(View.VISIBLE);
            mNextBtn.setOnClickListener(this);
        } else {
            mNextBtn.setVisibility(View.GONE);
            mNextBtn.setOnClickListener(null);
        }

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.first_launch_next:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1, true);
                break;
            case R.id.first_launch_previous:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1, true);
                break;
        }
    }
}
