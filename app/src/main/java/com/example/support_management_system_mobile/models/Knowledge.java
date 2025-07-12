package com.example.support_management_system_mobile.models;

import java.time.LocalDate;

public class Knowledge {
    private Long id;
    private String title;
    private String content;
    private LocalDate createdDate;
    private Software software;

    public Knowledge(Long id, String title, String content, LocalDate createdDate, Software software) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.software = software;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }
}
