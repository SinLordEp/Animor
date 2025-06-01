package com.example.animor.Model;

import androidx.annotation.NonNull;

public class Tag {
    private String tagName;

    public Tag() {}

    public Tag(String tagName) {
        this.tagName = tagName;
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
