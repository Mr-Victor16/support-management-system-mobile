package com.example.support_management_system_mobile.data.payload.request;

public record RegisterRequest(
        String username,
        String password,
        String email,
        String name,
        String surname
) { }
