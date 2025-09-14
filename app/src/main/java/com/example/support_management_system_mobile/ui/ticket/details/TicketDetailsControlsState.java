package com.example.support_management_system_mobile.ui.ticket.details;

public record TicketDetailsControlsState(
        boolean canEditTicket,
        boolean canDeleteTicket,
        boolean canChangeStatus,
        boolean canViewImages,
        boolean canEditImages,
        boolean canAddReply,
        boolean canDeleteReply
) { }