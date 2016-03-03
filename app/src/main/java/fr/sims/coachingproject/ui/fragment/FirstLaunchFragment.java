package fr.sims.coachingproject.ui.fragment;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.sims.coachingproject.R;

/**
 * Created by Donovan on 02/03/2016.
 */
public class FirstLaunchFragment extends GenericFragment {

    public static final String TABS_TITLE = "Tutorial";

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int mSectionNumber;

    public static FirstLaunchFragment newInstance(int sectionNumber) {
        FirstLaunchFragment fragment = new FirstLaunchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindArguments(Bundle args) {
        super.bindArguments(args);
        mSectionNumber = args.getInt(ARG_SECTION_NUMBER, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_first_launch;
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);

        int firstLaunchItemNumber = getResources().getStringArray(R.array.first_launch_title).length;


        Resources res = getResources();

        String title = res.getStringArray(R.array.first_launch_title)[mSectionNumber];
        String description = res.getStringArray(R.array.first_launch_description)[mSectionNumber];

        TypedArray imgs = getResources().obtainTypedArray(R.array.first_launch_image);
        int pictureId = imgs.getResourceId(mSectionNumber, -1);

        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.description)).setText(description);
        ((ImageView) view.findViewById(R.id.picture)).setImageResource(pictureId);
    }
}
