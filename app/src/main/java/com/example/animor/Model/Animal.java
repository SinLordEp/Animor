package com.example.animor.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Animal implements Serializable {

    @JsonProperty("animal_id")
    private Integer animalId;
    @JsonProperty("animal_name")
    private String Name;
    @JsonProperty("species_id")
    private Integer speciesId;
    @JsonProperty("birth_date")
    private LocalDate birthDate;
    @JsonProperty("is_birth_date_estimated")
    private Boolean isBirthDateEstimated;

    private String town;
    @JsonProperty("sex")
    private String sex;
    @JsonProperty("size")
    private String size;
    @JsonProperty("animal_description")
    private String animalDescription;
    String image;
    @JsonProperty("is_neutered")
    private Boolean isNeutered;
    @JsonProperty("microchip_number")
    private String microchipNumber;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("is_adopted")
    private Boolean isAdopted;

    // Constructor vacío
    public Animal() {
    }

    public Animal(String Name, Integer speciesId, LocalDate birthDate, Boolean isBirthDateEstimated, String sex, String size, String animalDescription, Boolean isNeutered, String microchipNumber, Boolean isAdopted) {
        this.Name = Name;
        this.speciesId = speciesId;
        this.birthDate = birthDate;
        this.isBirthDateEstimated = isBirthDateEstimated;
        this.sex = sex;
        this.size = size;
        this.animalDescription = animalDescription;
        this.isNeutered = isNeutered;
        this.microchipNumber = microchipNumber;
        this.isAdopted = isAdopted;
    }

    // Constructor con todos los campos excepto ID (para inserciones)
    public Animal(String Name, Integer speciesId, LocalDate birthDate,
                  Boolean isBirthDateEstimated, String sex, String town, String size,
                  String animalDescription, Boolean isNeutered,
                  String microchipNumber, Boolean isAdopted) {
        this.Name = Name;
        this.speciesId = speciesId;
        this.birthDate = birthDate;
        this.isBirthDateEstimated = isBirthDateEstimated;
        this.sex = sex;
        this.town = town;
        this.size = size;
        this.animalDescription = animalDescription;
        this.isNeutered = isNeutered;
        this.microchipNumber = microchipNumber;
        this.isAdopted = isAdopted;
    }

    public String getTown() {
        return town;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTown(String town) {
        this.town = town;
    }

    // Getters y Setters
    public Integer getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public Integer getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(Integer speciesId) {
        this.speciesId = speciesId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getIsBirthDateEstimated() {
        return isBirthDateEstimated;
    }

    public void setIsBirthDateEstimated(Boolean isBirthDateEstimated) {
        this.isBirthDateEstimated = isBirthDateEstimated;
    }

    public String getSex() {
        if (sex.equals("Male")) {
            sex = "macho";
        }
        if (sex.equals("Female")){
            sex = "hembra";
        }
        if(sex.equals("Unkown")){
            sex = "desconocido";
        }
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAnimalDescription() {
        return animalDescription;
    }

    public void setAnimalDescription(String animalDescription) {
        this.animalDescription = animalDescription;
    }

    public Boolean getIsNeutered() {
        return isNeutered;
    }

    public void setIsNeutered(Boolean isNeutered) {
        this.isNeutered = isNeutered;
    }

    public String getMicrochipNumber() {
        return microchipNumber;
    }

    public void setMicrochipNumber(String microchipNumber) {
        this.microchipNumber = microchipNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsAdopted() {
        return isAdopted;
    }

    public void setIsAdopted(Boolean isAdopted) {
        this.isAdopted = isAdopted;
    }

    // Método toString()
    @Override
    public String toString() {
        return "Animal{" +
                "animalId=" + animalId +
                ", animalName='" + Name + '\'' +
                ", speciesId=" + speciesId +
                ", birthDate=" + birthDate +
                ", isBirthDateEstimated=" + isBirthDateEstimated +
                ", sex=" + sex +
                ", size='" + size + '\'' +
                ", animalDescription='" + animalDescription + '\'' +
                ", isNeutered=" + isNeutered +
                ", microchipNumber='" + microchipNumber + '\'' +
                ", createdAt=" + createdAt +
                ", isAdopted=" + isAdopted +
                '}';
    }
}