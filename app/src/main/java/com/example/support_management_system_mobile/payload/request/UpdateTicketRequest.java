package com.example.support_management_system_mobile.payload.request;

public class UpdateTicketRequest {
    Long ticketID;
    String title;
    String description;
    Long categoryID;
    Long priorityID;
    String version;
    Long softwareID;

    public UpdateTicketRequest(Long ticketID, String title, String description, Long categoryID, Long priorityID, String version, Long softwareID) {
        this.ticketID = ticketID;
        this.title = title;
        this.description = description;
        this.categoryID = categoryID;
        this.priorityID = priorityID;
        this.version = version;
        this.softwareID = softwareID;
    }

    public Long getTicketID() {
        return ticketID;
    }

    public void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
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
