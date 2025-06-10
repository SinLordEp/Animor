package com.example.animor.Model.request;

import java.io.Serializable;

public class TagRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int tagId;
    private String tagName;

    public TagRequest() {
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
