package com.example.support_management_system_mobile.payload.request;

public class AddTicketRequest {
    private String title;
    private String description;
    private Long categoryID;
    private Long priorityID;
    private String version;
    private Long softwareID;

    public AddTicketRequest(String title, String description, Long categoryID, Long priorityID, String version, Long softwareID) {
        this.title = title;
        this.description = description;
        this.categoryID = categoryID;
        this.priorityID = priorityID;
        this.version = version;
        this.softwareID = softwareID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public Long getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(Long priorityID) {
        this.priorityID = priorityID;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getSoftwareID() {
        return softwareID;
    }

    public void setSoftwareID(Long softwareID) {
        this.softwareID = softwareID;
    }
}
