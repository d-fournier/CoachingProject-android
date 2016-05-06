package fr.sims.coachingproject.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fr.sims.coachingproject.R;

/**
 * Created by dfour on 11/03/2016.
 */
public class ImageUtil {

    public static void loadProfilePicture(Context ctx, String url, ImageView iv) {
        if(url == null || url.isEmpty()) {
            iv.setImageResource(R.drawable.ic_person_96dp_accent);
        } else {
            Picasso.with(ctx).load(url).placeholder(R.drawable.ic_person_96dp_accent).error(R.drawable.ic_person_96dp_accent).into(iv);
        }
    }

}
