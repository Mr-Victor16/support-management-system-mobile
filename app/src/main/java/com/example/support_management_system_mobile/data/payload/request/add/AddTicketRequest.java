package com.example.support_management_system_mobile.data.payload.request.add;

public record AddTicketRequest(
        String title,
        String description,
        Long categoryID,
        Long priorityID,
        String version,
        Long softwareID
) { }
