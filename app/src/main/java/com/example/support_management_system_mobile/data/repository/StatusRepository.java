package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.models.Status;
import com.example.support_management_system_mobile.network.APIService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class StatusRepository {
    private final APIService apiService;

    @Inject
    public StatusRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void getStatuses(Callback<List<Status>> callback) {
        apiService.getAllStatuses().enqueue(callback);
    }
}
