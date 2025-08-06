package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.network.APIService;
import com.example.support_management_system_mobile.payload.request.UpdateProfileRequest;

import javax.inject.Inject;

import retrofit2.Callback;

public class ProfileRepository {
    private final APIService apiService;

    @Inject
    public ProfileRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void update(String authToken, UpdateProfileRequest request, Callback<Void> callback){
        apiService.updateProfile(authToken, request).enqueue(callback);
    }
}
