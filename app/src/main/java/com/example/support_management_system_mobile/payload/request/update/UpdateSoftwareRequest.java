package com.example.support_management_system_mobile.payload.request.update;

public class UpdateSoftwareRequest {
    private Long softwareID;
    private String name;
    private String description;

    public UpdateSoftwareRequest(Long softwareID, String name, String description) {
        this.softwareID = softwareID;
        this.name = name;
        this.description = description;
    }

    public Long getSoftwareID() {
        return softwareID;
    }

    public void setSoftwareID(Long softwareID) {
        this.softwareID = softwareID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
