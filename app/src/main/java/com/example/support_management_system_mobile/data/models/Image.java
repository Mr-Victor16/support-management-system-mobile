package com.example.support_management_system_mobile.data.models;

import java.io.Serializable;

public record Image(
        Long id,
        String name,
        String content
) implements Serializable { }
