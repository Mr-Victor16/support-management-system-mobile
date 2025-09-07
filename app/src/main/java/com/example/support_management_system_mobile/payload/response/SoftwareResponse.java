package com.example.support_management_system_mobile.payload.response;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class SoftwareResponse {
    private Long softwareID;
    private String name;
    private String description;
    private Long useNumberTicket;
    private Long useNumberKnowledge;

    public SoftwareResponse(Long softwareID, String name, String description, Long useNumberTicket, Long useNumberKnowledge) {
        this.softwareID = softwareID;
        this.name = name;
        this.description = description;
        this.useNumberTicket = useNumberTicket;
        this.useNumberKnowledge = useNumberKnowledge;
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

    public Long getUseNumberTicket() {
        return useNumberTicket;
    }

    public void setUseNumberTicket(Long useNumberTicket) {
        this.useNumberTicket = useNumberTicket;
    }

    public Long getUseNumberKnowledge() {
        return useNumberKnowledge;
    }

    public void setUseNumberKnowledge(Long useNumberKnowledge) {
        this.useNumberKnowledge = useNumberKnowledge;
    }

    @Override
    public int hashCode() {
        return Objects.hash(softwareID, name, description, useNumberTicket, useNumberKnowledge);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SoftwareResponse that = (SoftwareResponse) obj;
        return softwareID == that.softwareID &&
                useNumberTicket == that.useNumberTicket &&
                useNumberKnowledge == that.useNumberKnowledge &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @NonNull
    @Override
    public String toString() {
        return name != null ? name : "";
    }
}
