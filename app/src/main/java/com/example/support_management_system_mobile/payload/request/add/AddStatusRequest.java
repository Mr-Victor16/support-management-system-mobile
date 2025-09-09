package com.example.support_management_system_mobile.payload.request.add;

public class AddStatusRequest {
    private String name;

    private Boolean closeTicket;

    private Boolean defaultStatus;

    public AddStatusRequest(String name, Boolean closeTicket, Boolean defaultStatus) {
        this.name = name;
        this.closeTicket = closeTicket;
        this.defaultStatus = defaultStatus;
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
