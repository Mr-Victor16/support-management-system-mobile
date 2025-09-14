package com.example.support_management_system_mobile.data.payload.request.update;

public record UpdateTicketStatusRequest(
        Long ticketID,
        Long statusID
) { }
