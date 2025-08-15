package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.models.Category;
import com.example.support_management_system_mobile.network.APIService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class CategoryRepository {
    private final APIService apiService;
    private final AuthContext authContext;

    @Inject
    public CategoryRepository(APIService apiService, AuthContext authContext) {
        this.apiService = apiService;
        this.authContext = authContext;
    }

    public void getCategories(Callback<List<Category>> callback) {
        apiService.getAllCategories(authContext.getAuthToken()).enqueue(callback);
    }
}
