package com.example.support_management_system_mobile.payload.request.update;

public class UpdateKnowledgeRequest {
    private Long knowledgeID;
    private String title;
    private String content;
    private Long softwareID;

    public UpdateKnowledgeRequest(Long knowledgeID, String title, String content, Long softwareID) {
        this.knowledgeID = knowledgeID;
        this.title = title;
        this.content = content;
        this.softwareID = softwareID;
    }

    public Long getKnowledgeID() {
        return knowledgeID;
    }

    public void setKnowledgeID(Long knowledgeID) {
        this.knowledgeID = knowledgeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSoftwareID() {
        return softwareID;
    }

    public void setSoftwareID(Long softwareID) {
        this.softwareID = softwareID;
    }
}
