package com.example.animor.Model;

public class AnimalTag {
    private int animalId;
    private int tagId;

    public AnimalTag() {}

    public AnimalTag(int animalId, int tagId) {
        this.animalId = animalId;
        this.tagId = tagId;
    }

    // Getters and setters
    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }
    public int getTagId() { return tagId; }
    public void setTagId(int tagId) { this.tagId = tagId; }
}