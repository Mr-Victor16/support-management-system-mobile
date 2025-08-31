package com.example.support_management_system_mobile.payload.response;

import androidx.annotation.NonNull;

import java.util.Objects;

public class CategoryResponse {
    private Long categoryID;
    private String name;
    private Long useNumber;

    public CategoryResponse(Long categoryID, String name, Long useNumber) {
        this.categoryID = categoryID;
        this.name = name;
        this.useNumber = useNumber;
    }

    public Long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
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

    @NonNull
    @Override
    public String toString() {
        return name != null ? name : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryResponse that = (CategoryResponse) o;
        return Objects.equals(categoryID, that.categoryID) &&
                useNumber == that.useNumber &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryID, name, useNumber);
    }
}
