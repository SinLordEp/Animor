package com.example.animor.Model.dto;

import java.io.Serializable;

public class TagDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer tagId;
    private String tagName;

    public TagDTO() {}

    public TagDTO(Integer tagId, String tagName) {
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

    @Override
    public String toString() {
        return tagName;
    }
}
