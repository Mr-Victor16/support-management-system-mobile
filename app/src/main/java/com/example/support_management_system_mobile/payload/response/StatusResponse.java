package com.example.support_management_system_mobile.payload.response;

import java.util.Objects;

public class StatusResponse {
    private Long statusID;
    private String name;
    private boolean closeTicket;
    private boolean defaultStatus;
    private Long useNumber;

    public StatusResponse(Long statusID, String name, boolean closeTicket, boolean defaultStatus, Long useNumber) {
        this.statusID = statusID;
        this.name = name;
        this.closeTicket = closeTicket;
        this.defaultStatus = defaultStatus;
        this.useNumber = useNumber;
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

    public boolean isCloseTicket() {
        return closeTicket;
    }

    public void setCloseTicket(boolean closeTicket) {
        this.closeTicket = closeTicket;
    }

    public boolean isDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(boolean defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public Long getUseNumber() {
        return useNumber;
    }

    public void setUseNumber(Long useNumber) {
        this.useNumber = useNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatusResponse that = (StatusResponse) o;
        return closeTicket == that.closeTicket &&
                defaultStatus == that.defaultStatus &&
                Objects.equals(statusID, that.statusID) &&
                Objects.equals(name, that.name) &&
                Objects.equals(useNumber, that.useNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusID, name, closeTicket, defaultStatus, useNumber);
    }
}
