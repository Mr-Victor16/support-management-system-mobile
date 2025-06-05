package com.example.support_management_system_mobile.api;

import com.example.support_management_system_mobile.payload.request.LoginRequest;
import com.example.support_management_system_mobile.payload.request.RegisterRequest;
import com.example.support_management_system_mobile.payload.response.Knowledge;
import com.example.support_management_system_mobile.payload.response.LoginResponse;
import com.example.support_management_system_mobile.payload.response.Software;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<String> register(@Body RegisterRequest request);

    @GET("/api/software")
    Call<List<Software>> getSupportedSoftwareList();

    @GET("/api/knowledge-bases")
    Call<List<Knowledge>> getKnowledgeItems();
}
