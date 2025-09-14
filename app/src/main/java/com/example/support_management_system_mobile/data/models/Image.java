package com.example.support_management_system_mobile.data.models;

import java.io.Serializable;
import java.util.Objects;

public class Image implements Serializable {
    private Long id;
    private String name;

    private String content;

    public Image(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(id, image.id) &&
                Objects.equals(name, image.name) &&
                Objects.equals(content, image.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, content);
    }
}
