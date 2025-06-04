package com.example.animor.Model.request;

import com.example.animor.Model.entity.Sex;

import java.time.LocalDate;
import java.util.List;

public class AnimalRequest {
    private String animalName;
    private int speciesId;
    private LocalDate birthDate;
    private boolean isBirthDateEstimated;
    private Sex sex;
    private String size;
    private String animalDescription;
    private boolean isNeutered;
    private String microchipNumber;
    private boolean isAdopted;
    private List<PhotoRequest> photoList;
    private List<TagRequest> tagList;

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isBirthDateEstimated() {
        return isBirthDateEstimated;
    }

    public void setBirthDateEstimated(boolean birthDateEstimated) {
        isBirthDateEstimated = birthDateEstimated;
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

    public boolean isNeutered() {
        return isNeutered;
    }

    public void setNeutered(boolean neutered) {
        isNeutered = neutered;
    }

    public String getMicrochipNumber() {
        return microchipNumber;
    }

    public void setMicrochipNumber(String microchipNumber) {
        this.microchipNumber = microchipNumber;
    }

    public boolean isAdopted() {
        return isAdopted;
    }

    public void setAdopted(boolean adopted) {
        isAdopted = adopted;
    }

    public List<PhotoRequest> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<PhotoRequest> photoList) {
        this.photoList = photoList;
    }

    public List<TagRequest> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagRequest> tagList) {
        this.tagList = tagList;
    }
}
