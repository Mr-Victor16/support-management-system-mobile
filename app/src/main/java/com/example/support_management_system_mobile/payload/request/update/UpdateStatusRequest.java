package com.example.support_management_system_mobile.payload.request.update;

public record UpdateStatusRequest(
        Long statusID,
        String name,
        boolean closeTicket,
        boolean defaultStatus
) { }
