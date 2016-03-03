package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Segolene on 02/03/2016.
 */
public class ProfileSportListAdapter extends ArrayAdapter{

    private List<SportLevel> mLevels;
    private LayoutInflater mInflater;

    public ProfileSportListAdapter(Context context) {
        super(context, R.layout.sport_list);
        mLevels=new ArrayList<>();
        mInflater = LayoutInflater.from( context );
    }

    public void setData(SportLevel[] dataset){
        mLevels.clear();
        Collections.addAll(mLevels, dataset);
        this.notifyDataSetChanged();
    }

    public void clearData(){
        mLevels.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mLevels.size();
    }

    @Override
    public SportLevel getItem(int position) {

        return mLevels.get(position);
    }

    @Override
    public long getItemId(int position) {

        return mLevels.get(position).mIdDb;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.sport_list,null );
        TextView sport=(TextView)convertView.findViewById(R.id.sport_name);
        TextView level=(TextView)convertView.findViewById(R.id.level_title);
        sport.setText(mLevels.get(position).mSport.mName);
        level.setText(mLevels.get(position).mTitle);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.message_item;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return mLevels.size()==0;
    }
}
