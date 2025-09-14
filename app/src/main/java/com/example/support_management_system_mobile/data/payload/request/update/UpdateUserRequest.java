package com.example.support_management_system_mobile.data.payload.request.update;

public record UpdateUserRequest(
        Long userID,
        String username,
        String email,
        String name,
        String surname,
        String role
) { }
