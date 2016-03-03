package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.ui.adapter.FirstLaunchPagerAdapter;

public class FirstLaunchActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private FirstLaunchPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private ImageButton mNextBtn;
    private ImageButton mPreviousBtn;
    private ImageView mBkg1IV;
    private ImageView mBkg2IV;
    TypedArray mBkgIds;


    public static void startActivity(Context ctx){
        Intent intent = new Intent(ctx,FirstLaunchActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        mSectionsPagerAdapter = new FirstLaunchPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.first_launch_title).length);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mNextBtn = (ImageButton) findViewById(R.id.first_launch_next);
        mPreviousBtn = (ImageButton) findViewById(R.id.first_launch_previous);
        mBkg1IV = ((ImageView) findViewById(R.id.first_launch_bkg1));
        mBkg2IV = ((ImageView) findViewById(R.id.first_launch_bkg2));

        updateBtn(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mBkgIds = getResources().obtainTypedArray(R.array.first_launch_image);

    }

    @Override
    protected void onStop() {
        mBkgIds.recycle();
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int id1 = mBkgIds.getResourceId(position, -1);
        int id2 = mBkgIds.getResourceId(position+1,-1);
        ImageView img1;
        ImageView img2;
        if(position % 2 == 0) {
            img1 = mBkg1IV;
            img2 = mBkg2IV;
        } else {
            img1 = mBkg2IV;
            img2 = mBkg1IV;
        }

        if(id1 != -1)
            img1.setImageResource(id1);
        if(id2 != -1)
            img2.setImageResource(id2);


        img1.setAlpha(1-positionOffset);
        img2.setAlpha(positionOffset);
    }

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
