package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.UserLoader;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.HomePagerAdapter;
import fr.sims.coachingproject.util.Const;

import static fr.sims.coachingproject.service.NetworkService.startActionCoachingRelations;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<UserProfile>, View.OnClickListener {

    HomePagerAdapter mHomePagerAdapter;
    ViewPager mViewPager;
    View mDrawerHeader;
    Intent mIntent;


    public static void startActivity(Context ctx) {
        Intent startIntent = new Intent(ctx, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(startIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer Pattern
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == 0) {
                    // drawer closed (update the coaching list)
                    startActionCoachingRelations(getApplicationContext());
                } else if (slideOffset != 0) {
                    // started opening
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Drawer Items
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerHeader = navigationView.getHeaderView(0);
        mDrawerHeader.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        // Tabs Pattern
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mHomePagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //startActivity(new Intent(this, ProfileActivity.class));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (tabLayout.getSelectedTabPosition()) {
                    case 0: mIntent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(mIntent);
                        break;
                    case 1:
                        break;
                    case 2: mIntent = new Intent(MainActivity.this, SearchGroupActivity.class);
                        startActivity(mIntent);
                        break;


                }
            }



    });
        NetworkService.startActionConnectedUserInfo(this);
        getSupportLoaderManager().initLoader(Const.Loaders.USER_LOADER_ID, null, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(Const.Loaders.USER_LOADER_ID, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<UserProfile> onCreateLoader(int id, Bundle args) {
        return new UserLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<UserProfile> loader, UserProfile user) {
        TextView header = (TextView) mDrawerHeader.findViewById(R.id.drawer_header_name);
        ImageView profilePicture = (ImageView) mDrawerHeader.findViewById(R.id.drawer_header_picture);
        if (user != null) {
            header.setText(user.mDisplayName);
            Picasso.with(MainActivity.this).load(user.mPicture).into(profilePicture);
            profilePicture.setVisibility(View.VISIBLE);
            mDrawerHeader.setOnClickListener(null);
        } else {
            header.setText(R.string.connect);
            profilePicture.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<UserProfile> loader) {

    }

    @Override
    public void onClick(View v) {
        LoginActivity.startActivity(this);
    }

}


