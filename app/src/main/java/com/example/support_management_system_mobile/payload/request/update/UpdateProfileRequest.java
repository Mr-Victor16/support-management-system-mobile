package com.example.support_management_system_mobile.payload.request.update;

public record UpdateProfileRequest(
        String name,
        String surname,
        String password
) { }
