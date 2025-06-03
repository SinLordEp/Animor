package com.example.animor.Model;

import androidx.annotation.NonNull;

public class Tag {
    private Integer tagId;
    private String tagName;

    public Tag() {}

    public Tag(Integer tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    // Getters and setters
    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    @NonNull
    @Override
    public String toString() {
        return tagName; // Esto es lo que se mostrará en el ListView
    }
}
