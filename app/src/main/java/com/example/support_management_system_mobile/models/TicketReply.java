package com.example.support_management_system_mobile.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class TicketReply implements Serializable {
    private Long id;
    private User user;
    private String content;
    private final LocalDate createdDate;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TicketReply that = (TicketReply) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(content, that.content) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, createdDate, user);
    }
}
