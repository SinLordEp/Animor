package com.example.animor.Model;

public class Species {
    private int speciesId;
    private String name;

    public Species() {}

    public Species(int speciesId, String name) {
        this.speciesId = speciesId;
        this.name = name;
    }

    // Getters and setters
    public int getSpeciesId() { return speciesId; }
    public void setSpeciesId(int speciesId) { this.speciesId = speciesId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}