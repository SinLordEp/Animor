package com.example.animor.Model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Photo implements Serializable {
    private long photoId;
    private String photoUrl;
    @JsonProperty("isCoverPhoto")
    private boolean isCoverPhoto;
    private int displayOrder;
    private String filePath;

    public Photo() {}

    public Photo(int photoId, String photoUrl, boolean isCoverPhoto, int displayOrder) { /*, String filePath*/
        this.photoId = photoId;
        this.photoUrl = photoUrl;
        this.isCoverPhoto = isCoverPhoto;
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
    public boolean getIsCoverPhoto() {return isCoverPhoto;}
    public void setIsCoverPhoto(boolean coverPhoto) {isCoverPhoto = coverPhoto;}

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}