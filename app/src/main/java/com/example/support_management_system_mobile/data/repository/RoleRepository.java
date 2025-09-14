package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.data.models.Role;
import com.example.support_management_system_mobile.data.api.APIService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class RoleRepository {
    private final APIService apiService;

    @Inject
    public RoleRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void getRoles(Callback<List<Role>> callback) {
        apiService.getRoles().enqueue(callback);
    }
}
