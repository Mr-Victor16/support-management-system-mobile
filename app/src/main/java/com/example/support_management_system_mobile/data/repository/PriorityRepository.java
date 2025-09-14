package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.data.models.Priority;
import com.example.support_management_system_mobile.data.api.APIService;
import com.example.support_management_system_mobile.data.payload.request.add.AddPriorityRequest;
import com.example.support_management_system_mobile.data.payload.request.update.UpdatePriorityRequest;
import com.example.support_management_system_mobile.data.payload.response.PriorityResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class PriorityRepository {
    private final APIService apiService;

    @Inject
    public PriorityRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void getPriorities(Callback<List<Priority>> callback) {
        apiService.getAllPriorities().enqueue(callback);
    }

    public void getPrioritiesWithUseNumber(Callback<List<PriorityResponse>> callback) {
        apiService.getAllPrioritiesWithUseNumber().enqueue(callback);
    }

    public void createPriority(String name, Callback<Void> callback) {
        apiService.addPriority(new AddPriorityRequest(name)).enqueue(callback);
    }

    public void updatePriority(Long priorityId, String newName, Callback<Void> callback) {
        apiService.updatePriority(new UpdatePriorityRequest(priorityId, newName)).enqueue(callback);
    }

    public void deletePriority(Long priorityId, Callback<Void> callback) {
        apiService.deletePriority(priorityId).enqueue(callback);
    }
}
