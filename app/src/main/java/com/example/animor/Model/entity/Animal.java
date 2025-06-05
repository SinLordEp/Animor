package com.example.animor.Model.entity;

import com.example.animor.Model.dto.AnimalDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Animal implements Serializable {

    @JsonProperty("animalId")
    private Long animalId;
    @JsonProperty("animalName")
    private String animalName;
    @JsonProperty("speciesId")
    private Integer speciesId;
    @JsonProperty("birthDate")
    private LocalDate birthDate;
    @JsonProperty("isBirthDateEstimated")
    private boolean isBirthDateEstimated;
    @JsonProperty("sex")
    private Sex sex;
    @JsonProperty("size")
    private String size;
    @JsonProperty("animalDescription")
    private String animalDescription;
    String image;
    @JsonProperty("isNeutered")
    private boolean isNeutered;
    @JsonProperty("microchipNumber")
    private String microchipNumber;
    private LocalDateTime createdAt;
    @JsonProperty("isAdopted")
    private boolean isAdopted;
    @JsonProperty("tagList")
    private List<Tag> tagList;
    @JsonProperty("photoList")
    private List<Photo> photoList;

    // Constructor vacío
    public Animal() {
    }

    public Animal(Long animalId, String animalName, Integer speciesId, LocalDate birthDate, Boolean isBirthDateEstimated, Sex sex, String size, String animalDescription, Boolean isNeutered, String microchipNumber, Boolean isAdopted, List<Tag> tagList, List<Photo> photoList) {
        this.animalId = animalId;
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
        this.photoList = photoList;
    }

    // Constructor con todos los campos excepto ID (para inserciones)
    public Animal(Long animalId, String animalName, Integer speciesId, LocalDate birthDate,
                  Boolean isBirthDateEstimated, Sex sex, String size,
                  String animalDescription, Boolean isNeutered,
                  String microchipNumber, Boolean isAdopted) {
        this.animalId = animalId;
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

    public List<Photo> getAnimalPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Getters y Setters
    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
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

    @JsonIgnore
    public static Animal fromDTO(AnimalDTO animalDTO){
        return new Animal(animalDTO.getAnimalId(), animalDTO.getAnimalName(), animalDTO.getSpeciesId(),
                animalDTO.getBirthDate(), animalDTO.isBirthDateEstimated(), animalDTO.getSex(), animalDTO.getSize(),
                animalDTO.getAnimalDescription(), animalDTO.isNeutered(), animalDTO.getMicrochipNumber(), animalDTO.isAdopted());
    }
    @JsonIgnore
    public static List<Animal> fromDTOList(List<AnimalDTO> animalDTOList){
        return animalDTOList.stream().map(Animal::fromDTO).collect(Collectors.toList());
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

}