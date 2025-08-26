package com.example.support_management_system_mobile.auth;

import android.content.Context;

import com.example.support_management_system_mobile.models.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class AuthContext {
    private final Context appContext;

    @Inject
    public AuthContext(@ApplicationContext Context context) {
        this.appContext = context;
    }

    public String getAuthToken() {
        return "Bearer " + JWTUtils.getToken(appContext);
    }

    public String getUserRole() {
        return JWTUtils.getUserRole(appContext);
    }

    public boolean isUser() {
        String role = getUserRole();
        return role != null && role.equals("ROLE_USER");
    }

    public boolean isOperatorOrAdmin() {
        String role = getUserRole();
        return role != null && (role.contains("OPERATOR") || role.contains("ADMIN"));
    }

    public User getCurrentUser() {
        return JWTUtils.getCurrentUser(appContext);
    }

    public void logout(){
        JWTUtils.clearData(appContext);
    }
}
