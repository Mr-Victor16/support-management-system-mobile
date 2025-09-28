package com.example.support_management_system_mobile.utils;

import android.content.SharedPreferences;

import com.example.support_management_system_mobile.data.models.Role;
import com.example.support_management_system_mobile.data.models.User;
import com.example.support_management_system_mobile.data.payload.response.LoginResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SessionManager {
    private static final String TOKEN_KEY = "jwt_token";
    private static final String ID_KEY = "user_id";
    private static final String USERNAME_KEY = "username";
    private static final String NAME_KEY = "name";
    private static final String SURNAME_KEY = "surname";
    private static final String EMAIL_KEY = "email";
    private static final String ROLE_KEY = "role";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    @Inject
    public SessionManager(SharedPreferences sharedPreferences) {
        this.prefs = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public void saveData(LoginResponse response) {
        editor.putString(TOKEN_KEY, response.token());
        editor.putLong(ID_KEY, response.id());
        editor.putString(USERNAME_KEY, response.username());
        editor.putString(NAME_KEY, response.name());
        editor.putString(SURNAME_KEY, response.surname());
        editor.putString(EMAIL_KEY, response.email());
        editor.putString(ROLE_KEY, response.role());
        editor.apply();
    }

    public void clearData() {
        editor.clear().apply();
    }

    public String getToken() {
        return prefs.getString(TOKEN_KEY, null);
    }

    public String getUserRole() {
        return prefs.getString(ROLE_KEY, "UNKNOWN");
    }

    public User getCurrentUser() {
        long id = prefs.getLong(ID_KEY, -1);
        String username = prefs.getString(USERNAME_KEY, null);
        String email = prefs.getString(EMAIL_KEY, null);
        String name = prefs.getString(NAME_KEY, null);
        String surname = prefs.getString(SURNAME_KEY, null);

        if (id == -1 || username == null || email == null || name == null || surname == null) {
            return null;
        }

        String roleStr = getUserRole();
        Role.Types roleType = Role.Types.fromString(roleStr);
        return new User(id, username, email, name, surname, new Role(roleType));
    }

    public void setName(String name) {
        editor.putString(NAME_KEY, name);
        editor.apply();
    }

    public void setSurname(String surname) {
        editor.putString(SURNAME_KEY, surname);
        editor.apply();
    }
}
