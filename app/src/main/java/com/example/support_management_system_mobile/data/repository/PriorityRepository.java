package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.models.Priority;
import com.example.support_management_system_mobile.network.APIService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class PriorityRepository {
    private final APIService apiService;
    private final AuthContext authContext;

    @Inject
    public PriorityRepository(APIService apiService, AuthContext authContext) {
        this.apiService = apiService;
        this.authContext = authContext;
    }

    public void getPriorities(Callback<List<Priority>> callback) {
        apiService.getAllPriorities(authContext.getAuthToken()).enqueue(callback);
    }
}
