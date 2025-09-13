package com.example.support_management_system_mobile.payload.request.add;

public record AddTicketReplyRequest(
        Long ticketID,
        String content
) { }
