package com.example.support_management_system_mobile.data.payload.response;

public record StatusResponse(
        Long statusID,
        String name,
        boolean closeTicket,
        boolean defaultStatus,
        Long useNumber
) { }
