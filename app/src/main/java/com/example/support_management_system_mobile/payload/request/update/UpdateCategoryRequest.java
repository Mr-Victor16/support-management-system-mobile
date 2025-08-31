package com.example.support_management_system_mobile.payload.request.update;

public class UpdateCategoryRequest {
    private Long categoryID;
    private String name;

    public UpdateCategoryRequest(Long categoryID, String name) {
        this.categoryID = categoryID;
        this.name = name;
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
}
