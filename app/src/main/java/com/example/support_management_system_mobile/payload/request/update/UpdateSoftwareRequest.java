package com.example.support_management_system_mobile.payload.request.update;

public record UpdateSoftwareRequest(
        Long softwareID,
        String name,
        String description
) { }
