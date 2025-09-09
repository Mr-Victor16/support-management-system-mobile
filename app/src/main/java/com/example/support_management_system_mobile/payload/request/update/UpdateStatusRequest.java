package com.example.support_management_system_mobile.payload.request.update;

public class UpdateStatusRequest {
    private Long statusID;
    private String name;
    private Boolean closeTicket;
    private Boolean defaultStatus;

    public UpdateStatusRequest(Long statusID, String name, Boolean closeTicket, Boolean defaultStatus) {
        this.statusID = statusID;
        this.name = name;
        this.closeTicket = closeTicket;
        this.defaultStatus = defaultStatus;
    }

    public Long getStatusID() {
        return statusID;
    }

    public void setStatusID(Long statusID) {
        this.statusID = statusID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCloseTicket() {
        return closeTicket;
    }

    public void setCloseTicket(Boolean closeTicket) {
        this.closeTicket = closeTicket;
    }

    public Boolean getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(Boolean defaultStatus) {
        this.defaultStatus = defaultStatus;
    }
}
