package com.example.support_management_system_mobile.payload.request.add;

public class AddPriorityRequest {
    private String name;

    public AddPriorityRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
