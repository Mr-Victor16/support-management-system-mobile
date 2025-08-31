package com.example.support_management_system_mobile.ui.management.category;

import com.example.support_management_system_mobile.payload.response.CategoryResponse;

import java.util.List;

public abstract class CategoryListUIState {
    private CategoryListUIState() {}

    public static class Loading extends CategoryListUIState {}

    public static class Success extends CategoryListUIState {
        public final List<CategoryResponse> categories;
        public Success(List<CategoryResponse> categories) { this.categories = categories; }
    }

    public static class Error extends CategoryListUIState {
        public final String message;
        public Error(String message) { this.message = message; }
    }
}
