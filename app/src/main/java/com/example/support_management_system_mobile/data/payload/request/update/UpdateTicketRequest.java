package com.example.support_management_system_mobile.data.payload.request.update;

public record UpdateTicketRequest(
        Long ticketID,
        String title,
        String description,
        Long categoryID,
        Long priorityID,
        String version,
        Long softwareID
) { }
