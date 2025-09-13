package com.example.support_management_system_mobile.payload.response;

import com.example.support_management_system_mobile.models.Role;

public record UserDetailsResponse(
        Long id,
        String username,
        String name,
        String surname,
        String email,
        Role role
) { }
