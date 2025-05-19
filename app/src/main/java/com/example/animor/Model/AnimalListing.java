package com.example.animor.Model;

import java.time.LocalDateTime;

public class AnimalListing {
    private int listingId;
    private int animalId;
    private int userId;
    private int locationId;
    private String contactPhone;
    private String contactEmail;
    private LocalDateTime createdAt;

    public AnimalListing() {}

    public AnimalListing(int listingId, int animalId, int userId, int locationId,
                         String contactPhone, String contactEmail, LocalDateTime createdAt) {
        this.listingId = listingId;
        this.animalId = animalId;
        this.userId = userId;
        this.locationId = locationId;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }
    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}