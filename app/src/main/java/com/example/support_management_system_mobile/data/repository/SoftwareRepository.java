package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.models.Software;
import com.example.support_management_system_mobile.network.APIService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class SoftwareRepository {
    private final APIService apiService;

    @Inject
    public SoftwareRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void getSoftwareList(Callback<List<Software>> callback) {
        apiService.getSoftwareList().enqueue(callback);
    }
}
