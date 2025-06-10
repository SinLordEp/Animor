package com.example.animor.Model.dto;

import com.example.animor.Model.entity.Photo;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoDTO {
    private long photoId;
    private String photoUrl;
    @JsonProperty("coverPhoto")
    private boolean coverPhoto;
    private int displayOrder;
    private String filePath;

    public PhotoDTO() {
    }

    public PhotoDTO(long photoId, String photoUrl, boolean coverPhoto, int displayOrder, String filePath) {
        this.photoId = photoId;
        this.photoUrl = photoUrl;
        this.coverPhoto = coverPhoto;
        this.displayOrder = displayOrder;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(boolean coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public static PhotoDTO fromEntity(Photo photo){
        return new PhotoDTO(photo.getPhotoId(), photo.getPhotoUrl(), photo.getIsCoverPhoto(), photo.getDisplayOrder(), photo.getFilePath());
    }

}
