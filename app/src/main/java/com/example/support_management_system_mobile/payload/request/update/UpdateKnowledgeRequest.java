package com.example.support_management_system_mobile.payload.request.update;

public record UpdateKnowledgeRequest(
        Long knowledgeID,
        String title,
        String content,
        Long softwareID
){ }
