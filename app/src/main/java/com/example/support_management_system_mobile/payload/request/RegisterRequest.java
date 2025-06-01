package com.example.support_management_system_mobile.payload.request;

public class RegisterRequest {
    String username;
    String password;
    String email;
    String name;
    String surname;

    public RegisterRequest(String username, String password, String email, String name, String surname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }
}
