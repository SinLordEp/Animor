package com.example.animor.Model.request;

public class PhotoRequest {
    private long photoId;
    private String photoUrl;
    private String filePath;
    private boolean isCoverPhoto;
    private int displayOrder;

    public PhotoRequest() {
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
}
