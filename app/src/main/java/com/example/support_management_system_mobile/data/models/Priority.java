package com.example.support_management_system_mobile.data.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Priority implements Serializable {
    private Long id;
    private String name;

    public Priority(Long id, String name) {
        this.id = id;
        this.name = name;
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
