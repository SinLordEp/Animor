package com.example.animor.Model.entity;

import com.example.animor.Model.dto.PhotoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private long photoId;
    private String photoUrl;
    @JsonProperty("coverPhoto")
    private boolean coverPhoto;
    private int displayOrder;
    private String filePath;

    public Photo() {}

    public Photo(int photoId, String photoUrl, boolean coverPhoto, int displayOrder, String filePath) {
        this.photoId = photoId;
        this.photoUrl = photoUrl;
        this.coverPhoto = coverPhoto;
        this.displayOrder = displayOrder;
        this.filePath = filePath;

    }
    // Getters and setters

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getPhotoId() { return photoId; }
    public void setPhotoId(long photoId) { this.photoId = photoId; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public boolean getIsCoverPhoto() {return coverPhoto;}
    public void setIsCoverPhoto(boolean coverPhoto) {
        this.coverPhoto = coverPhoto;}

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
    public static Photo fromDTO(PhotoDTO photoDTO) {
        if (photoDTO == null) return null;

        Photo photo = new Photo();
        photo.setPhotoId(photoDTO.getPhotoId());
        photo.setPhotoUrl(photoDTO.getPhotoUrl());
        photo.setIsCoverPhoto(photoDTO.isCoverPhoto());
        photo.setDisplayOrder(photoDTO.getDisplayOrder());
        photo.setFilePath(photoDTO.getFilePath());
        return photo;
    }
    public static List<Photo> fromDTOList(List<PhotoDTO> photoDTOList) {
        if (photoDTOList == null) return new ArrayList<>();

        List<Photo> photoList = new ArrayList<>();
        for (PhotoDTO photoDTO : photoDTOList) {
            photoList.add(fromDTO(photoDTO));
        }
        return photoList;
    }
}