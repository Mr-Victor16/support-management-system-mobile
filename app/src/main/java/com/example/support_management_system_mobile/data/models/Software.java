package com.example.support_management_system_mobile.data.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public record Software(Long id, String name, String description) implements Serializable {

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
