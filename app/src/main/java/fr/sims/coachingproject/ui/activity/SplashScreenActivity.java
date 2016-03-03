package fr.sims.coachingproject.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    launchHomeActivity();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void launchHomeActivity(){
        if(SharedPrefUtil.isFirstLaunch(this)) {
            FirstLaunchActivity.startActivity(this);
        } else {
            MainActivity.startActivity(this);
        }
        finish();
    }
}
