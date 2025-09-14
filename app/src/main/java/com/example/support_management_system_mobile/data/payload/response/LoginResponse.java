package com.example.support_management_system_mobile.data.payload.response;

public record LoginResponse(
        Long id,
        String username,
        String name,
        String surname,
        String email,
        String token,
        String role
) { }
