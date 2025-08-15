package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.models.Status;
import com.example.support_management_system_mobile.network.APIService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class StatusRepository {
    private final APIService apiService;
    private final AuthContext authContext;

    @Inject
    public StatusRepository(APIService apiService, AuthContext authContext) {
        this.apiService = apiService;
        this.authContext = authContext;
    }

    public void getStatuses(Callback<List<Status>> callback) {
        apiService.getAllStatuses(authContext.getAuthToken()).enqueue(callback);
    }
}
