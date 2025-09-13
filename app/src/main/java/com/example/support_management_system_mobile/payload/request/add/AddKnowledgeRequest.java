package com.example.support_management_system_mobile.payload.request.add;

public record AddKnowledgeRequest(
        Long softwareID,
        String title,
        String content
) { }
