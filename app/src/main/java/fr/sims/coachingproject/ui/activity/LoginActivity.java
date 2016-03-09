package fr.sims.coachingproject.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.model.fakejson.LoginRequest;
import fr.sims.coachingproject.model.fakejson.LoginResponse;
import fr.sims.coachingproject.service.gcmService.RegistrationGCMIntentService;
import fr.sims.coachingproject.ui.fragment.LoginFragment;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    LoginFragment mFragment;

    public static void startActivity(Context ctx){
        Intent intent = new Intent(ctx,LoginActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFragment=LoginFragment.newInstance(false);
        getSupportFragmentManager().beginTransaction().add(0,mFragment).commit();


    }


}

