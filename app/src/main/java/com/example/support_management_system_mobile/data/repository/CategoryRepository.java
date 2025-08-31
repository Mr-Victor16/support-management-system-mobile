package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.models.Category;
import com.example.support_management_system_mobile.network.APIService;
import com.example.support_management_system_mobile.payload.request.add.AddCategoryRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateCategoryRequest;
import com.example.support_management_system_mobile.payload.response.CategoryResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class CategoryRepository {
    private final APIService apiService;

    @Inject
    public CategoryRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void getCategories(Callback<List<Category>> callback) {
        apiService.getAllCategories().enqueue(callback);
    }

    public void getCategoriesWithUseNumber(Callback<List<CategoryResponse>> callback) {
        apiService.getAllCategoriesWithUseNumber().enqueue(callback);
    }

    public void createCategory(String name, Callback<Void> callback) {
        apiService.addCategory(new AddCategoryRequest(name)).enqueue(callback);
    }

    public void updateCategory(Long categoryId, String newName, Callback<Void> callback) {
        apiService.updateCategory(new UpdateCategoryRequest(categoryId, newName)).enqueue(callback);
    }

    public void deleteCategory(Long categoryId, Callback<Void> callback) {
        apiService.deleteCategory(categoryId).enqueue(callback);
    }
}
