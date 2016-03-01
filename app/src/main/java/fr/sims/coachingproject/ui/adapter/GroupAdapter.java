package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Group;

/**
 * Created by Benjamin on 01/03/2016.
 */
public class GroupAdapter extends ArrayAdapter {

    private ViewHolder vh;
    private List<Group> mGroupList;

    public GroupAdapter(Context context, int resource) {
        super(context, resource);
        vh = new ViewHolder();
        mGroupList = new ArrayList<>();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_group, parent, false);
            vh.name = (TextView)convertView.findViewById(R.id.group_item_name);
            vh.description = (TextView)convertView.findViewById(R.id.group_item_description);
            vh.sport = (TextView)convertView.findViewById(R.id.group_item_sport);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }

        Group g = mGroupList.get(position);
        vh.name.setText(g.mName);
        vh.description.setText(g.mDescription);
        vh.sport.setText(g.mSport.mName);
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView description;
        TextView sport;
    }

    @Override
    public int getCount() {
        return mGroupList.size();
    }

    public void setData(List<Group> gList){
        mGroupList = gList;
    }

    public void clearData(){
        mGroupList.clear();
    }
}
