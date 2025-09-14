package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.data.models.Software;
import com.example.support_management_system_mobile.data.api.APIService;
import com.example.support_management_system_mobile.data.payload.request.add.AddSoftwareRequest;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateSoftwareRequest;
import com.example.support_management_system_mobile.data.payload.response.SoftwareResponse;

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

    public void getSoftwareListWithUseNumber(Callback<List<SoftwareResponse>> callback) {
        apiService.getSoftwareListWithUseNumber().enqueue(callback);
    }

    public void createSoftware(String name, String description, Callback<Void> callback) {
        apiService.addSoftware(new AddSoftwareRequest(name, description)).enqueue(callback);
    }

    public void updateSoftware(Long softwareId, String name, String description, Callback<Void> callback) {
        apiService.updateSoftware(new UpdateSoftwareRequest(softwareId, name, description)).enqueue(callback);
    }

    public void deleteSoftware(Long softwareId, Callback<Void> callback) {
        apiService.deleteSoftware(softwareId).enqueue(callback);
    }
}
