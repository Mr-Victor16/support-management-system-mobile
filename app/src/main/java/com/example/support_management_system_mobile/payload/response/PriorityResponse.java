package com.example.support_management_system_mobile.payload.response;

import java.util.Objects;

public class PriorityResponse {
    private Long priorityID;
    private String name;
    private Long useNumber;

    public PriorityResponse(Long priorityID, String name, Long useNumber) {
        this.priorityID = priorityID;
        this.name = name;
        this.useNumber = useNumber;
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

        PriorityResponse that = (PriorityResponse) o;
        return useNumber == that.useNumber && Objects.equals(priorityID, that.priorityID) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priorityID, name, useNumber);
    }
}
