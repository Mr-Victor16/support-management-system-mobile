package com.example.support_management_system_mobile.data.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Ticket implements Serializable {
    private Long id;
    private String title;
    private String description;
    private List<Image> images;
    private LocalDate createdDate;
    private Category category;
    private Priority priority;
    private Status status;
    private String version;
    private Software software;
    private List<TicketReply> replies;
    private User user;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Image> getImages() {
        return images;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public List<TicketReply> getReplies() {
        return replies;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id) &&
                Objects.equals(title, ticket.title) &&
                Objects.equals(description, ticket.description) &&
                Objects.equals(createdDate, ticket.createdDate) &&
                Objects.equals(category, ticket.category) &&
                Objects.equals(priority, ticket.priority) &&
                Objects.equals(status, ticket.status) &&
                Objects.equals(version, ticket.version) &&
                Objects.equals(software, ticket.software);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, createdDate, category, priority, status, version, software);
    }
}
