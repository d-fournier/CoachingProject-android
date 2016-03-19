package fr.sims.coachingproject.ui.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.service.gcmService.RegistrationGCMIntentService;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 3000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            RegistrationGCMIntentService.startActionRegistrationGCM(this);
        }

        if(SharedPrefUtil.isFirstLaunch(this)) {
            NetworkService.startActionSports(this);
            NetworkService.startActionLevels(this);
        }


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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
}
