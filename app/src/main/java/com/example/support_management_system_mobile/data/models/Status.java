package com.example.support_management_system_mobile.data.models;

import java.io.Serializable;

public class Status implements Serializable {
    private Long id;
    private String name;
    private boolean closeTicket;
    private boolean defaultStatus = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
