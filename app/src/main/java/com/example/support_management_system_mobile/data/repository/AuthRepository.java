package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.data.api.APIService;
import com.example.support_management_system_mobile.data.payload.request.LoginRequest;
import com.example.support_management_system_mobile.data.payload.request.RegisterRequest;
import com.example.support_management_system_mobile.data.payload.response.LoginResponse;

import javax.inject.Inject;

import retrofit2.Callback;

public class AuthRepository {
    private final APIService apiService;

    @Inject
    public AuthRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void login(LoginRequest request, Callback<LoginResponse> callback) {
        apiService.login(request).enqueue(callback);
    }

    public void register(RegisterRequest request, Callback<String> callback) {
        apiService.register(request).enqueue(callback);
    }
}
