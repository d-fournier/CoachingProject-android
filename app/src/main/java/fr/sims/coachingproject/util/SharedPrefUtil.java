package fr.sims.coachingproject.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Donovan on 15/02/2016.
 */
public class SharedPrefUtil {

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences(Const.SharedPref.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static long getConnectedUserId(Context ctx) {
        return getSharedPreferences(ctx).getLong(Const.SharedPref.CURRENT_USER_ID, -1);
    }

    public static void putConnectedUserId(Context ctx, long id) {
        getSharedPreferences(ctx).edit().putLong(Const.SharedPref.CURRENT_USER_ID, id).apply();
    }

    public static String getConnectedToken(Context ctx) {
        return getSharedPreferences(ctx).getString(Const.SharedPref.CURRENT_TOKEN, "");
    }

    public static void putConnectedToken(Context ctx, String token) {
        getSharedPreferences(ctx).edit().putString(Const.SharedPref.CURRENT_TOKEN, token).apply();
    }

    public static boolean sportsAndLevelsLoaded(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(Const.SharedPref.LEVELS_SPORTS_LOADED, false);
    }

    public static void putSportsAndLevelsLoaded(Context ctx, boolean sportsLevelsLoaded) {
        getSharedPreferences(ctx).edit().putBoolean(Const.SharedPref.LEVELS_SPORTS_LOADED, sportsLevelsLoaded).apply();
    }

    public static boolean isFirstLaunch(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(Const.SharedPref.IS_FIRST_LAUNCH, true);
    }

    public static void putIsFirstLaunch(Context ctx, boolean firstLaunch) {
        getSharedPreferences(ctx).edit().putBoolean(Const.SharedPref.IS_FIRST_LAUNCH, firstLaunch).apply();
    }

    public static boolean isGCMTokenSentToServer(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(Const.SharedPref.IS_GCM_TOKEN_SENT_TO_SERVER, false);
    }

    public static void putIsGCMTokenSentToServer(Context ctx, boolean isRegister) {
        getSharedPreferences(ctx).edit().putBoolean(Const.SharedPref.IS_GCM_TOKEN_SENT_TO_SERVER, isRegister).apply();
    }

    public static ArrayList<String> getNotificationContent(Context ctx, String tag) {
        String s = getSharedPreferences(ctx).getString(Const.SharedPref.NOTIFICATION_CONTENT + tag, "");
        if (s.isEmpty())
            return new ArrayList<>();
        else
            return new ArrayList<>(Arrays.asList(TextUtils.split(s, "--;--")));
    }

    public static void putNotificationContent(Context ctx, String tag, List<String> list) {
        String res = TextUtils.join("--;--", list);
        getSharedPreferences(ctx).edit().putString(Const.SharedPref.NOTIFICATION_CONTENT + tag, res).apply();
    }

    public static void clearNotificationContent(Context ctx, String tag) {
        getSharedPreferences(ctx).edit().remove(Const.SharedPref.NOTIFICATION_CONTENT + tag).apply();
    }

}
