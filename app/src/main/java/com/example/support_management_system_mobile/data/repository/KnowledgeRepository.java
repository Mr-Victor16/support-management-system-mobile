package com.example.support_management_system_mobile.data.repository;

import com.example.support_management_system_mobile.data.models.Knowledge;
import com.example.support_management_system_mobile.data.api.APIService;
import com.example.support_management_system_mobile.data.payload.request.add.AddKnowledgeRequest;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateKnowledgeRequest;

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

    public void getKnowledgeItemById(Long knowledgeId, Callback<Knowledge> callback) {
        apiService.getKnowledgeItemById(knowledgeId).enqueue(callback);
    }

    public void createKnowledgeItem(AddKnowledgeRequest request, Callback<Void> callback) {
        apiService.addKnowledge(request).enqueue(callback);
    }

    public void updateKnowledgeItem(UpdateKnowledgeRequest request, Callback<Void> callback) {
        apiService.updateKnowledge(request).enqueue(callback);
    }

    public void deleteKnowledgeItem(Long knowledgeId, Callback<Void> callback) {
        apiService.deleteKnowledge(knowledgeId).enqueue(callback);
    }
}
