package fr.sims.coachingproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;

/**
 * Created by Segolene on 21/03/2016.
 */
public class AboutActivity  extends AppCompatActivity {

    public static void startActivity(Context ctx){
        Intent intent = new Intent(ctx,AboutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ListView authorsView=(ListView)findViewById(R.id.authors_list);
        List<String> authorsList=new ArrayList<>();
        authorsList.add("Donovan Fournier, software engineer");
        authorsList.add("Benjamin Legrand,software engineer");
        authorsList.add("Ségolène Minjard, software engineer");
        authorsList.add("Anthony Barbosa, electrical engineer");
        authorsList.add("Cen Zhenjie, electrical engineer");
        authorsList.add("Li Huang, electrical engineer");

        authorsView.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.item_about, authorsList));
    }
}
