package com.example.support_management_system_mobile.models;

public class Priority {
    private Long id;
    private String name;
    private Integer maxTime;

    public Priority(String name, Integer maxTime) {
        this.name = name;
        this.maxTime = maxTime;
    }

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

    public Integer getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Integer maxTime) {
        this.maxTime = maxTime;
    }
}
