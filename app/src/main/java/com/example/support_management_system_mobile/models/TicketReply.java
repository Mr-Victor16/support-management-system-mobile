package com.example.support_management_system_mobile.models;

import java.io.Serializable;
import java.time.LocalDate;

public class TicketReply implements Serializable {
    private Long id;
    private User user;
    private String content;
    private LocalDate createdDate;

    public TicketReply(User user, String content, LocalDate createdDate) {
        this.user = user;
        this.content = content;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
