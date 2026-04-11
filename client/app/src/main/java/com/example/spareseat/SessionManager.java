package com.example.spareseat;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.spareseat.model.UserResponse;

public class SessionManager {

    private static final String PREFS_NAME = "spareseat_session";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_ROLE = "user_role";

    public static void save(Context context, UserResponse user) {
        SharedPreferences.Editor editor = prefs(context).edit();
        boolean hasValidUser = user != null && user.getId() != null;
        editor.putBoolean(KEY_LOGGED_IN, hasValidUser);
        if (hasValidUser) {
            editor.putLong(KEY_USER_ID, user.getId());
            editor.putString(KEY_USER_NAME, user.getName());
            editor.putString(KEY_USER_EMAIL, user.getEmail());
            editor.putString(KEY_USER_PHONE, user.getPhoneNumber());
            editor.putString(KEY_USER_ROLE, user.getRole());
        } else {
            editor.remove(KEY_USER_ID);
            editor.remove(KEY_USER_NAME);
            editor.remove(KEY_USER_EMAIL);
            editor.remove(KEY_USER_PHONE);
            editor.remove(KEY_USER_ROLE);
        }
        editor.apply();
    }

    public static void clear(Context context) {
        prefs(context).edit().clear().apply();
    }

    public static boolean isLoggedIn(Context context) {
        return prefs(context).getBoolean(KEY_LOGGED_IN, false);
    }

    public static String getUserName(Context context) {
        return prefs(context).getString(KEY_USER_NAME, "");
    }

    public static long getUserId(Context context) {
        return prefs(context).getLong(KEY_USER_ID, -1L);
    }

    public static String getUserRole(Context context) {
        return prefs(context).getString(KEY_USER_ROLE, "");
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
