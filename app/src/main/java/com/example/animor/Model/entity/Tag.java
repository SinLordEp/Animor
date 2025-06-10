package com.example.animor.Model.entity;

import androidx.annotation.NonNull;

import com.example.animor.Model.dto.TagDTO;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public static Tag fromDTO(TagDTO tagDTO){
        return new Tag(tagDTO.getTagId(), tagDTO.getTagName());
    }
    public static List<Tag> fromDTOList(List<TagDTO> tagDTOList){
        return tagDTOList.stream().map(Tag::fromDTO).collect(Collectors.toList());
    }
}
