package com.example.support_management_system_mobile.payload.request.add;

public class AddKnowledgeRequest {
    private String title;
    private String content;
    private Long softwareID;

    public AddKnowledgeRequest(String title, String content, Long softwareID) {
        this.title = title;
        this.content = content;
        this.softwareID = softwareID;
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
