package com.example.support_management_system_mobile.data.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public record Category(Long id, String name) implements Serializable {

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
