package com.example.support_management_system_mobile.data.models;

import java.time.LocalDate;

public record Knowledge(
        Long id,
        String title,
        String content,
        LocalDate createdDate,
        Software software
) { }
