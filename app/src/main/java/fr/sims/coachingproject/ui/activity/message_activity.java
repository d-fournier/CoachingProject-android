package fr.sims.coachingproject.ui.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

public class message_activity extends AppCompatActivity implements View.OnClickListener {
    private long mId;
    private String mConnectedToken;
    private long  mConnectedUserId;

    private String mConnectedUrl;
    private String mConnectedBody;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_activity);

     //   Intent mIntent = getIntent();
     //   mId = mIntent.getLongExtra("id", 0);


            mConnectedToken = SharedPrefUtil.getConnectedToken(this);

            mConnectedUserId = SharedPrefUtil.getConnectedUserId(this);




        Button btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText et = (EditText) findViewById(R.id.editMessage);
        String mMessage = et.getText().toString();


    //    tv.setText(mMessage);


        mConnectedUrl="coachingproject.herokuapp.com/api/messages/";

        try {
            final TextView tv = (TextView) findViewById(R.id.test);

            JSONObject parent = new JSONObject();
            parent.put("content",mMessage);
            parent.put("to_relation", "1");
            parent.put("is_pinned","false");

            mConnectedBody = parent.toString(2);
            class SendRequest extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    String response = NetworkUtil.post("https://coachingproject.herokuapp.com/api/messages/", mConnectedToken, mConnectedBody);
               //     if (response.length() == 0){
               //         tv.setText("Error");
               //     } else {
                //        tv.setText(response);
                //    }
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                }

                @Override
                protected void onPreExecute() {
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                }
            }
            new SendRequest().execute("");
            //post("https://coachingproject.herokuapp.com/api/relations/", mConnectedToken, parent.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
         //   popupWindow.dismiss();
        }
    }

 //       NetworkUtil.post(String url, String token, String body)


        //      Log.i("id", Long.toString(mId));
            //Picasso.with(ProfileActivity.this).load("https://i1.wp.com/www.techrepublic.com/bundles/techrepubliccore/images/icons/standard/icon-user-default.png").into(view);

}
