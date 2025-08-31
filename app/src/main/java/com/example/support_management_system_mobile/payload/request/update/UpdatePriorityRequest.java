package com.example.support_management_system_mobile.payload.request.update;

public class UpdatePriorityRequest {
    private Long priorityID;
    private String name;

    public UpdatePriorityRequest(Long priorityID, String name) {
        this.priorityID = priorityID;
        this.name = name;
    }

    public Long getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(Long priorityID) {
        this.priorityID = priorityID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
