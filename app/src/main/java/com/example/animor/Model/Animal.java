package com.example.animor.Model;

import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Animal implements Serializable {

    @JsonProperty("animal_id")
    private Integer animalId;
    @JsonProperty("animal_name")
    private String animalName;
    @JsonProperty("species_id")
    private Integer speciesId;
    @JsonProperty("birth_date")
    private LocalDate birthDate;
    private Boolean isBirthDateEstimated;
    private Sex sex;
    private String size;
    private String animalDescription;
    private Boolean isNeutered;
    private String microchipNumber;
    private LocalDateTime createdAt;
    private Boolean isAdopted;

    // Enumeración para el campo sex
    public enum Sex {
        MALE, FEMALE, UNKNOWN
    }

    // Constructor vacío
    public Animal() {
    }

    // Constructor con todos los campos excepto ID (para inserciones)
    public Animal(String animalName, Integer speciesId, LocalDate birthDate,
                  Boolean isBirthDateEstimated, Sex sex, String size,
                  String animalDescription, Boolean isNeutered,
                  String microchipNumber, Boolean isAdopted) {
        this.animalName = animalName;
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

    // Getters y Setters
    public Integer getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
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

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
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
                ", animalName='" + animalName + '\'' +
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