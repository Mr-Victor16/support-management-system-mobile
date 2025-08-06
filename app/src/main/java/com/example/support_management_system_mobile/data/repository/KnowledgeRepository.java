package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.models.Knowledge;
import com.example.support_management_system_mobile.network.APIService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Callback;

public class KnowledgeRepository {
    private final APIService apiService;

    @Inject
    public KnowledgeRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public void getKnowledgeItems(Callback<List<Knowledge>> callback) {
        apiService.getKnowledgeItems().enqueue(callback);
    }
}
