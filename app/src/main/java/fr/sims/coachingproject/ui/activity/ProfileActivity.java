package fr.sims.coachingproject.ui.activity;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.UserLoader;
import fr.sims.coachingproject.model.UserProfile;

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<UserProfile> {

    private static final String[] messages = new String[] {
            "Message : Demande de coaching", "Message : Demande de coaching",
            "Message : Demande de coaching", "Message : Demande de coaching",
            "Message : Demande de coaching"
    };

    private static final String[] sports = new String[] {
            "Saisons 2012 à 2014 : Sporting Club de Bastia : CFA 2 ( Convention payée ).",
            "Saisons 2010 à 2012 : Sporting Club de Bastia : U 19 National ( Convention payée ).",
            "Saisons 2007 à 2010 : Sporting Club de Bastia : U 17 National ( Contrat aspirant ).",
            "Saisons 2005 à 2007 : Antony sport football : U 13 DH et U 14 fédéraux."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button btn = (Button) findViewById(R.id.button1);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Picasso.with(ProfileActivity.this).load("https://i1.wp.com/www.techrepublic.com/bundles/techrepubliccore/images/icons/standard/icon-user-default.png").into(view);
            }
        });


        getSupportLoaderManager().initLoader(0, null, this);

        // fill message list
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, messages));

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // fill sport list
        ListView lv_sport = (ListView) findViewById(R.id.listView1);
        lv_sport.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, sports));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<UserProfile> onCreateLoader(int id, Bundle args) {
        return new UserLoader(this, 1);
    }

    @Override
    public void onLoadFinished(Loader<UserProfile> loader, UserProfile data) {
        // Get components id
        TextView tv_Id = (TextView) findViewById(R.id.textID);
        TextView tv_Name = (TextView) findViewById(R.id.textName);
        TextView tv_Birthday = (TextView) findViewById(R.id.textBirthday);
        TextView tv_City = (TextView) findViewById(R.id.textCity);
        TextView tv_IsCoach = (TextView) findViewById(R.id.textIsCoach);
        TextView tv_Mail = (TextView) findViewById(R.id.textMail);
        ImageView iv_Picture = (ImageView) findViewById(R.id.imagePicture);

        // Set values
        tv_Id.setText(Long.toString(data.mIdDb));
        tv_Name.setText(data.mDisplayName);
        tv_Birthday.setText(data.mBirthdate);
        tv_City.setText(data.mCity);
        tv_IsCoach.setText(Boolean.toString(data.mIsCoach));
        tv_Mail.setText(data.mDisplayName);
        Picasso.with(ProfileActivity.this).load(data.mPicture).into(iv_Picture);
    }

    @Override
    public void onLoaderReset(Loader<UserProfile> loader) {

    }

}
