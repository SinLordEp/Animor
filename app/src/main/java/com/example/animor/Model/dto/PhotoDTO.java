package com.example.animor.Model.dto;

import com.example.animor.Model.entity.Photo;

public class PhotoDTO {
    private long photoId;
    private String photoUrl;
    private boolean isCoverPhoto;
    private int displayOrder;

    public PhotoDTO() {
    }

    public PhotoDTO(long photoId, String photoUrl, boolean isCoverPhoto, int displayOrder) {
        this.photoId = photoId;
        this.photoUrl = photoUrl;
        this.isCoverPhoto = isCoverPhoto;
        this.displayOrder = displayOrder;
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
        return isCoverPhoto;
    }

    public void setCoverPhoto(boolean coverPhoto) {
        isCoverPhoto = coverPhoto;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public static PhotoDTO fromEntity(Photo photo){
        return new PhotoDTO(photo.getPhotoId(), photo.getPhotoUrl(), photo.getIsCoverPhoto(), photo.getDisplayOrder());
    }

}
