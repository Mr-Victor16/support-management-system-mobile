package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.data.api.APIService;
import com.example.support_management_system_mobile.data.payload.request.add.AddUserRequest;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateUserRequest;
import com.example.support_management_system_mobile.data.payload.response.UserDetailsResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class UserRepository {
    private final APIService apiService;

    @Inject
    public UserRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void getUsers(Callback<List<UserDetailsResponse>> callback) {
        apiService.getUsers().enqueue(callback);
    }

    public void getUserById(Long userId, Callback<UserDetailsResponse> callback) {
        apiService.getUser(userId).enqueue(callback);
    }

    public void createUser(AddUserRequest request, Callback<Void> callback) {
        apiService.addUser(request).enqueue(callback);
    }

    public void updateUser(UpdateUserRequest request, Callback<Void> callback) {
        apiService.updateUser(request).enqueue(callback);
    }

    public void deleteUser(Long userId, Callback<Void> callback) {
        apiService.deleteUser(userId).enqueue(callback);
    }
}
