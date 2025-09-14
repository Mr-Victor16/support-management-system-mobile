package com.example.support_management_system_mobile.data.payload.request.add;

public record AddTicketReplyRequest(
        Long ticketID,
        String content
) { }
