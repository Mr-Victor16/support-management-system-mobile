package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.data.api.APIService;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateProfileRequest;

import javax.inject.Inject;

import retrofit2.Callback;

public class ProfileRepository {
    private final APIService apiService;

    @Inject
    public ProfileRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void update(UpdateProfileRequest request, Callback<Void> callback){
        apiService.updateProfile(request).enqueue(callback);
    }
}
