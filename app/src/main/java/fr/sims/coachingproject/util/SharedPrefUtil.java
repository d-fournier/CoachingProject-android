package fr.sims.coachingproject.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Donovan on 15/02/2016.
 */
public class SharedPrefUtil {

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences(Const.SharedPref.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static long getConnectedUserId(Context ctx){
        return getSharedPreferences(ctx).getLong(Const.SharedPref.CURRENT_USER_ID, -1);
    }

    public static void putConnectedUserId(Context ctx, long id){
        getSharedPreferences(ctx).edit().putLong(Const.SharedPref.CURRENT_USER_ID, id).apply();
    }

    public static String getConnectedToken(Context ctx){
        return getSharedPreferences(ctx).getString(Const.SharedPref.CURRENT_TOKEN, "");
    }

    public static void putConnectedToken(Context ctx, String token){
        getSharedPreferences(ctx).edit().putString(Const.SharedPref.CURRENT_TOKEN, token).apply();
    }
}
