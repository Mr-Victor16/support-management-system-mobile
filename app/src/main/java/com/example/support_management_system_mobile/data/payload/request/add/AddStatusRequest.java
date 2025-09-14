package com.example.support_management_system_mobile.data.payload.request.add;

public record AddStatusRequest(
        String name,
        boolean closeTicket,
        boolean defaultStatus
) { }
