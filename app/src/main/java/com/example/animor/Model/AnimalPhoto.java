package com.example.animor.Model;

public class AnimalPhoto {
    private int photoId;
    private int animalId;
    private String photoUrl;
    private boolean isCoverPhoto;
    private int displayOrder;

    public AnimalPhoto() {}

    public AnimalPhoto(int photoId, int animalId, String photoUrl, boolean isCoverPhoto, int displayOrder) {
        this.photoId = photoId;
        this.animalId = animalId;
        this.photoUrl = photoUrl;
        this.isCoverPhoto = isCoverPhoto;
        this.displayOrder = displayOrder;
    }

    // Getters and setters
    public int getPhotoId() { return photoId; }
    public void setPhotoId(int photoId) { this.photoId = photoId; }
    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public boolean isCoverPhoto() { return isCoverPhoto; }
    public void setCoverPhoto(boolean coverPhoto) { isCoverPhoto = coverPhoto; }
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}