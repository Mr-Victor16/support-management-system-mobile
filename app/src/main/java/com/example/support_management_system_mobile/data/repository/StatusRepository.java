package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.models.Status;
import com.example.support_management_system_mobile.network.APIService;
import com.example.support_management_system_mobile.payload.request.add.AddStatusRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateStatusRequest;
import com.example.support_management_system_mobile.payload.response.StatusResponse;

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

    public void getStatusesWithUseNumber(Callback<List<StatusResponse>> callback) {
        apiService.getStatusesWithUseNumber().enqueue(callback);
    }

    public void createStatus(AddStatusRequest request, Callback<Void> callback) {
        apiService.addStatus(request).enqueue(callback);
    }

    public void updateStatus(UpdateStatusRequest request, Callback<Void> callback) {
        apiService.updateStatus(request).enqueue(callback);
    }

    public void deleteStatus(Long statusId, Callback<Void> callback) {
        apiService.deleteStatus(statusId).enqueue(callback);
    }
}
