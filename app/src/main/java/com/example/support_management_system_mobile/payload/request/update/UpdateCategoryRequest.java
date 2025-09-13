package com.example.support_management_system_mobile.payload.request.update;

public record UpdateCategoryRequest(
        Long categoryID,
        String name
) { }
