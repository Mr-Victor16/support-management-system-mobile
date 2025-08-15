package com.example.support_management_system_mobile.ui.ticket;

public class TicketDetailsControlsState {
    public final boolean canEditTicket;
    public final boolean canDeleteTicket;
    public final boolean canChangeStatus;
    public final boolean canManageImages;
    public final boolean canAddReply;
    public final boolean canDeleteReply;

    public TicketDetailsControlsState(boolean canEditTicket, boolean canDeleteTicket, boolean canChangeStatus,
                                      boolean canManageImages, boolean canAddReply, boolean canDeleteReply) {
        this.canEditTicket = canEditTicket;
        this.canDeleteTicket = canDeleteTicket;
        this.canChangeStatus = canChangeStatus;
        this.canManageImages = canManageImages;
        this.canAddReply = canAddReply;
        this.canDeleteReply = canDeleteReply;
    }
}
