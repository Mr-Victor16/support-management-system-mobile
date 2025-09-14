package com.example.support_management_system_mobile.data.payload.response;

import com.example.support_management_system_mobile.data.models.Role;

public record UserDetailsResponse(
        Long id,
        String username,
        String name,
        String surname,
        String email,
        Role role
) { }
