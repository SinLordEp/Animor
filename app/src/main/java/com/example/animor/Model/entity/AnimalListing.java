package com.example.animor.Model.entity;


import com.example.animor.Model.dto.AnimalDTO;
import com.example.animor.Model.dto.ListingDTO;
import com.example.animor.Model.dto.UserSimple;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class AnimalListing implements Serializable {
    private static final long serialVersionUID = 1L;
    private long listingId;
    private Animal animal;
    private UserSimple user;
    private Location location;
    private String contactPhone;
    private String contactEmail;
    private int distance;

    public AnimalListing() {}

    // Getters and setters
    public long getListingId() { return listingId; }
    public void setListingId(long listingId) { this.listingId = listingId; }
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

    @JsonIgnore
    public static AnimalListing fromDTO(ListingDTO listingDTO) {
        if (listingDTO == null) return null;
        AnimalListing animalListing = new AnimalListing();
        animalListing.setListingId(listingDTO.getListingId());
        animalListing.setContactPhone(listingDTO.getContactPhone());
        animalListing.setContactEmail(listingDTO.getContactEmail());
        animalListing.setUser(listingDTO.getUser());
        animalListing.setLocation(Location.fromDTOLocation(listingDTO.getLocation()));
        animalListing.setAnimal(Animal.fromDTO(listingDTO.getAnimal()));
        animalListing.setDistance(listingDTO.getDistance());
        return animalListing;
    }
    @JsonIgnore
    public static List<Animal> fromDTOList(List<AnimalDTO> animalDTOList){
        return animalDTOList.stream().map(Animal::fromDTO).collect(Collectors.toList());
    }
}