package com.example.animor.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Animal implements Serializable {

    @JsonProperty("animalId")
    private Integer animalId;
    @JsonProperty("animalName")
    private String animalName;
    @JsonProperty("speciesId")
    private Integer speciesId;
    @JsonProperty("birthDate")
    private LocalDate birthDate;
    @JsonProperty("isBirthDateEstimated")
    private Boolean isBirthDateEstimated;

    private String town;
    @JsonProperty("sex")
    private String sex;
    @JsonProperty("size")
    private String size;
    @JsonProperty("animalDescription")
    private String animalDescription;
    String image;
    @JsonProperty("isNeutered")
    private Boolean isNeutered;
    @JsonProperty("microchipNumber")
    private String microchipNumber;
    private LocalDateTime createdAt;
    @JsonProperty("isAdopted")
    private Boolean isAdopted;
    private ArrayList<Tag> tagList;
    private ArrayList<AnimalPhoto> animalPhotoList;

    // Constructor vacío
    public Animal() {
    }

    public Animal(String animalName, Integer speciesId, LocalDate birthDate, Boolean isBirthDateEstimated, String sex, String size, String animalDescription, Boolean isNeutered, String microchipNumber, Boolean isAdopted, ArrayList<Tag> tagList, ArrayList<AnimalPhoto> animalPhotoList) {
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
        this.tagList = tagList;
        this.animalPhotoList = animalPhotoList;


    }

    // Constructor con todos los campos excepto ID (para inserciones)
    public Animal(String animalName, Integer speciesId, LocalDate birthDate,
                  Boolean isBirthDateEstimated, String sex, String town, String size,
                  String animalDescription, Boolean isNeutered,
                  String microchipNumber, Boolean isAdopted) {
        this.animalName = animalName;
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

    public ArrayList<AnimalPhoto> getAnimalPhotoList() {
        return animalPhotoList;
    }

    public void setAnimalPhotoList(ArrayList<AnimalPhoto> animalPhotoList) {
        this.animalPhotoList = animalPhotoList;
    }

    public ArrayList<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<Tag> tagList) {
        this.tagList = tagList;
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

    public String getSex() {
        return sex;
    }
//
//    public String getSexTranslated() {
//        if (sex.equals("Male")) {
//            sex = "macho";
//        }
//        if (sex.equals("Female")){
//            sex = "hembra";
//        }
//        if(sex.equals("Unkown")){
//            sex = "desconocido";
//        }
//        return sex;
//    }

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
}