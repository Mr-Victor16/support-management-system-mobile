package com.example.support_management_system_mobile.data.payload.request.add;

public record AddUserRequest(
        String username,
        String password,
        String email,
        String name,
        String surname,
        String role
) { }
