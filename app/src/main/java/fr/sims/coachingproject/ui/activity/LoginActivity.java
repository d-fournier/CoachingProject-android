package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.ui.fragment.LoginFragment;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    LoginFragment mFragment;

    public static void startActivity(Context ctx){
        Intent intent = new Intent(ctx,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFragment=LoginFragment.newInstance(false);
        getFragmentManager().beginTransaction().add(0,mFragment).commit();
    }


}

