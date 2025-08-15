package com.example.support_management_system_mobile.payload.request;

public class UpdateTicketStatusRequest {
    private Long ticketID;
    private Long statusID;

    public UpdateTicketStatusRequest(Long ticketID, Long statusID) {
        this.ticketID = ticketID;
        this.statusID = statusID;
    }

    public Long getTicketID() {
        return ticketID;
    }

    public void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
    }

    public Long getStatusID() {
        return statusID;
    }

    public void setStatusID(Long statusID) {
        this.statusID = statusID;
    }
}
