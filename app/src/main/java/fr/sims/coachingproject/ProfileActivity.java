package fr.sims.coachingproject;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private List<Integer> mData = null;
    private ListView list_message;

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
        Button button1 = (Button) findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "You clicked Button 1", Toast.LENGTH_SHORT).show();
            }
        });



/*        list_message = (ListView) findViewById(R.id.listView);
        mData = new LinkedList<Integer>();
        mData.add(1);
        mData.add(2);
        mData.add(3);
        mData.add(4);
        mData.add(5);*/
        // fill message list
        ListView lv = (ListView) findViewById(R.id.listView);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, messages));

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // fill sport list
        ListView lv_sport = (ListView) findViewById(R.id.listView1);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        lv_sport.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, sports));

    }
}
