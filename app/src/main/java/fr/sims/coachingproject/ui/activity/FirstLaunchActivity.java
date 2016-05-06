package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.ui.adapter.pager.FirstLaunchPagerAdapter;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class FirstLaunchActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private FirstLaunchPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private ImageView mBkg1IV;
    private ImageView mBkg2IV;
    private TypedArray mBkgIds;
    private Button mContinueBtn;

    private int mPreviousPosition = -1;


    public static void startActivity(Context ctx){
        Intent intent = new Intent(ctx,FirstLaunchActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        mSectionsPagerAdapter = new FirstLaunchPagerAdapter(getFragmentManager(), getResources().getStringArray(R.array.first_launch_title).length);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mBkg1IV = ((ImageView) findViewById(R.id.first_launch_bkg1));
        mBkg2IV = ((ImageView) findViewById(R.id.first_launch_bkg2));

        mContinueBtn = (Button) findViewById(R.id.first_launch_continue);
        mContinueBtn.setOnClickListener(this);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.first_launch_indicator);
        indicator.setViewPager(mViewPager);

        setupBtn(0);
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
        ImageView img1;
        ImageView img2;

        if(position % 2 == 0) {
            img1 = mBkg1IV;
            img2 = mBkg2IV;
        } else {
            img1 = mBkg2IV;
            img2 = mBkg1IV;
        }

        if(mPreviousPosition != position) {
            int id1 = mBkgIds.getResourceId(position, -1);
            int id2 = mBkgIds.getResourceId(position+1,-1);

            if(id1 != -1)
                img1.setImageResource(id1);
            if(id2 != -1)
                img2.setImageResource(id2);
            mPreviousPosition = position;
        }


        img1.setAlpha(1 - positionOffset);
        img2.setAlpha(positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        setupBtn(position);
    }

    private void setupBtn(int position){
        if(position == mSectionsPagerAdapter.getCount()-1) {
            mContinueBtn.setVisibility(View.VISIBLE);
        } else {
            mContinueBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onClick(View v) {
        SharedPrefUtil.putIsFirstLaunch(this, false);
        MainActivity.startActivity(this);
        finish();
    }
}
