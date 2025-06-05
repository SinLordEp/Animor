package com.example.animor.Model.entity;


import com.example.animor.Model.dto.UserSimple;

import java.io.Serializable;

public class AnimalListing implements Serializable {
    private int listingId;
    private Animal animal;
    private UserSimple user;
    private Location location;
    private String contactPhone;
    private String contactEmail;
    private int distance;

    public AnimalListing() {}

    // Getters and setters
    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    public UserSimple getUser() {
        return user;
    }

    public void setUser(UserSimple user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}