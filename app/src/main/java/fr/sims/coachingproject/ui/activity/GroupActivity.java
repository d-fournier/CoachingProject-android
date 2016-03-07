package fr.sims.coachingproject.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Text;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Zhenjie CEN on 2016/3/6.
 */
public class GroupActivity extends AppCompatActivity {

    private TextView mGroupName;
    private TextView mGroupDescription;
    private TextView mGroupCreationDate;
    private TextView mGroupCreator;
    private TextView mGroupSport;

    private String mGroupIdDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        Intent intent = getIntent();
        mGroupIdDb = intent.getExtras().getString("groupIdDb");

        mGroupName = (TextView) findViewById(R.id.group_name);
        mGroupDescription = (TextView) findViewById(R.id.group_description);
        mGroupCreationDate = (TextView) findViewById(R.id.group_creation_date);
        mGroupCreator = (TextView) findViewById(R.id.group_creator);
        mGroupSport = (TextView) findViewById(R.id.group_sport);


        new FillGroupPage().execute(true);


    }

    private class FillGroupPage extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (params.length > 0) {

                String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mGroupIdDb + "/";
                String token = SharedPrefUtil.getConnectedToken(getApplicationContext());

                NetworkUtil.Response res = NetworkUtil.get(url, token);

                if (!res.getBody().isEmpty()) {
                    // TODO String.valueOf(Group.parseItem(res.getBody())) --> UserGroup@null ?
                    Log.d("****", String.valueOf(Group.parseItem(res.getBody())));
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

}
