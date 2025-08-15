package com.example.support_management_system_mobile.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Priority implements Serializable {
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

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Priority priority = (Priority) o;

        return Objects.equals(id, priority.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
