package com.example.support_management_system_mobile.models;

import java.time.LocalDate;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Knowledge knowledge = (Knowledge) o;

        return Objects.equals(id, knowledge.id) &&
                Objects.equals(title, knowledge.title) &&
                Objects.equals(content, knowledge.content) &&
                Objects.equals(createdDate, knowledge.createdDate) &&
                Objects.equals(software, knowledge.software);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, createdDate, software);
    }
}
