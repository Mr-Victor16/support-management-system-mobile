package com.example.support_management_system_mobile.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.support_management_system_mobile.models.User;
import com.example.support_management_system_mobile.payload.response.LoginResponse;

public class JWTUtils {
    private static final String PREF_NAME = "user_session_prefs";

    private static final String TOKEN_KEY = "jwt_token";
    private static final String ID_KEY = "user_id";
    private static final String USERNAME_KEY = "username";
    private static final String NAME_KEY = "name";
    private static final String SURNAME_KEY = "surname";
    private static final String EMAIL_KEY = "email";
    private static final String ROLE_KEY = "role";

    public static void saveData(Context context, LoginResponse response) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(TOKEN_KEY, response.getToken());
        editor.putLong(ID_KEY, response.getId());
        editor.putString(USERNAME_KEY, response.getUsername());
        editor.putString(NAME_KEY, response.getName());
        editor.putString(SURNAME_KEY, response.getSurname());
        editor.putString(EMAIL_KEY, response.getEmail());
        editor.putString(ROLE_KEY, response.getRole());
        editor.apply();
    }

    public static void clearData(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(TOKEN_KEY);
        editor.remove(ID_KEY);
        editor.remove(USERNAME_KEY);
        editor.remove(NAME_KEY);
        editor.remove(SURNAME_KEY);
        editor.remove(EMAIL_KEY);
        editor.remove(ROLE_KEY);
        editor.apply();
    }

    public static String getUserRole(Context context) {
        return getPrefs(context).getString(ROLE_KEY, "UNKNOWN");
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken(Context context) {
        return getPrefs(context).getString(TOKEN_KEY, null);
    }

    public static String getUsername(Context context) {
        return getPrefs(context).getString(USERNAME_KEY, null);
    }

    public static String getName(Context context) {
        return getPrefs(context).getString(NAME_KEY, null);
    }

    public static String getSurname(Context context) {
        return getPrefs(context).getString(SURNAME_KEY, null);
    }

    public static String getEmail(Context context) {
        return getPrefs(context).getString(EMAIL_KEY, null);
    }

    public static long getUserId(Context context) {
        return getPrefs(context).getLong(ID_KEY, -1);
    }

    public static void setName(Context context, String name) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(NAME_KEY, name);
        editor.apply();
    }

    public static void setSurname(Context context, String surname) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(SURNAME_KEY, surname);
        editor.apply();
    }

    public static User getCurrentUser(Context context) {
        Long id = getUserId(context);
        String username = getUsername(context);
        String role = getUserRole(context);
        return new User(id, username, role);
    }
}
