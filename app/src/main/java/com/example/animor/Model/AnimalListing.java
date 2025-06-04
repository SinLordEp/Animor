package com.example.animor.Model;


import java.io.Serializable;
import java.time.LocalDateTime;

public class AnimalListing implements Serializable {
    private int listingId;
    private Animal animal;
    private long userId;
    Location location;
    private String contactPhone;
    private String contactEmail;

    public AnimalListing() {}

    public AnimalListing(int listingId, Animal animal, Location location, int userId,
                         String contactPhone, String contactEmail, LocalDateTime createdAt) {
        this.listingId = listingId;
        this.animal = animal;
        this.userId = userId;
        this.location = location;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    // Getters and setters
    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public Location getLocationRequest() {
        return location;
    }

    public void setLocationRequest(Location location) {
        this.location = location;
    }
}