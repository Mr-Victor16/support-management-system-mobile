package com.example.support_management_system_mobile.utils;

import com.example.support_management_system_mobile.data.models.User;
import com.example.support_management_system_mobile.data.payload.response.LoginResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthContext {
    private final SessionManager sessionManager;

    @Inject
    public AuthContext(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void login(LoginResponse response) {
        sessionManager.saveData(response);
    }

    public void logout() {
        sessionManager.clearData();
    }

    public String getAuthToken() {
        String token = sessionManager.getToken();
        return (token != null) ? "Bearer " + token : null;
    }

    public String getToken() {
        return sessionManager.getToken();
    }

    public String getUserRole() {
        return sessionManager.getUserRole();
    }

    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public boolean isUser() {
        String role = getUserRole();
        return role != null && role.equals("ROLE_USER");
    }

    public boolean isOperatorOrAdmin() {
        String role = getUserRole();
        return role != null && (role.contains("OPERATOR") || role.contains("ADMIN"));
    }

    public boolean isAdmin() {
        String role = getUserRole();
        return role != null && role.contains("ADMIN");
    }

    public void setFullName(String name, String surname) {
        sessionManager.setName(name);
        sessionManager.setSurname(surname);
    }

    public String getSurname() {
        User currentUser = sessionManager.getCurrentUser();
        return currentUser.getSurname();
    }

    public String getName() {
        User currentUser = sessionManager.getCurrentUser();
        return currentUser.getName();
    }
}
