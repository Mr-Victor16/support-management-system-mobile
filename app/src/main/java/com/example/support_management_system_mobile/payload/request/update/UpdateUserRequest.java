package com.example.support_management_system_mobile.payload.request.update;

public class UpdateUserRequest {
    private Long userID;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String role;

    public UpdateUserRequest(Long userID, String username, String email, String name, String surname, String role) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
