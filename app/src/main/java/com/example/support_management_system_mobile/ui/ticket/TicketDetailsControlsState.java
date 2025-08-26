package com.example.support_management_system_mobile.ui.ticket;

public class TicketDetailsControlsState {
    public final boolean canEditTicket;
    public final boolean canDeleteTicket;
    public final boolean canChangeStatus;
    public final boolean canViewImages;
    public final boolean canEditImages;
    public final boolean canAddReply;
    public final boolean canDeleteReply;

    public TicketDetailsControlsState(boolean canEditTicket, boolean canDeleteTicket, boolean canChangeStatus, boolean canViewImages,
                                  boolean canEditImages, boolean canAddReply, boolean canDeleteReply) {
        this.canEditTicket = canEditTicket;
        this.canDeleteTicket = canDeleteTicket;
        this.canChangeStatus = canChangeStatus;
        this.canViewImages = canViewImages;
        this.canEditImages = canEditImages;
        this.canAddReply = canAddReply;
        this.canDeleteReply = canDeleteReply;
    }
}
