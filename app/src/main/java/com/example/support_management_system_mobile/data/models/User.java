package com.example.support_management_system_mobile.data.models;

import java.io.Serializable;

public record User(
        Long id,
        String username,
        String email,
        String name,
        String surname,
        Role role
) implements Serializable { }
