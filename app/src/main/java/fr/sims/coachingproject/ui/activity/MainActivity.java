package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import com.activeandroid.ActiveAndroid;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.local.UserLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.model.UserSportLevel;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.adapter.pager.HomePagerAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.ImageUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

import static fr.sims.coachingproject.service.NetworkService.startActionCoachingRelations;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<UserProfile>, View.OnClickListener {


    HomePagerAdapter mHomePagerAdapter;
    ViewPager mViewPager;
    View mDrawerHeader;
    NavigationView mNavigationView;

    private long mConnectedUserId;

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
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerHeader = mNavigationView.getHeaderView(0);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Tabs Pattern
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mHomePagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        SearchActivity.startActivity(getApplicationContext(), false, -1, true);
                        break;
                    case 1:
                        break;
                    case 2:
                        SearchGroupActivity.startActivity(getApplicationContext());
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
        switch (id) {
            case R.id.nav_blog_post_new:
                PostCreationActivity.startActivity(this);
                break;
            case R.id.nav_settings:

                break;
            case R.id.nav_share:

                break;
            case R.id.nav_disconnect:
                disconnect();
                LoginActivity.startActivity(getApplication());
                break;
            case R.id.nav_about:
                // TODO
                break;
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
            mConnectedUserId = user.mIdDb;
            header.setText(user.mDisplayName);
            ImageUtil.loadProfilePicture(this, user.mPicture, profilePicture);
            profilePicture.setVisibility(View.VISIBLE);
            mNavigationView.getMenu().findItem(R.id.nav_disconnect).setVisible(true);

        } else {
            mConnectedUserId = -1;
            header.setText(R.string.connect);
            profilePicture.setVisibility(View.GONE);
            mNavigationView.getMenu().findItem(R.id.nav_disconnect).setVisible(false);
        }
        mDrawerHeader.setOnClickListener(this);
    }

    @Override
    public void onLoaderReset(Loader<UserProfile> loader) {
    }

    protected void disconnect() {
        SharedPreferences settings = getSharedPreferences(Const.SharedPref.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        Editor e = settings.edit();
        e.remove(Const.SharedPref.CURRENT_TOKEN);
        e.remove(Const.SharedPref.CURRENT_USER_ID);
        e.remove(Const.SharedPref.IS_GCM_TOKEN_SENT_TO_SERVER);
        e.apply();


        ActiveAndroid.beginTransaction();
        try {
            UserSportLevel.clear();
            SportLevel.clear();
            Message.clear();
            CoachingRelation.clear();
            Group.clear();
            Sport.clear();
            UserProfile.clear();
            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ActiveAndroid.endTransaction();
        }
        getSupportLoaderManager().restartLoader(Const.Loaders.USER_LOADER_ID, null, this);
    }


    @Override
    public void onClick(View v) {
        if (mConnectedUserId != -1)
            ProfileActivity.startActivity(this, mConnectedUserId);
        else
            LoginActivity.startActivity(this);
    }
}


