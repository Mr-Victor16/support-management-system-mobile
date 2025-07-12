package com.example.support_management_system_mobile.payload.request;

public class AddTicketReplyRequest {
    private Long ticketID;
    private String content;

    public AddTicketReplyRequest(Long ticketID, String content) {
        this.ticketID = ticketID;
        this.content = content;
    }

    public Long getTicketID() {
        return ticketID;
    }

    public void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
