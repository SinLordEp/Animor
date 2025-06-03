package com.example.animor.Model;

import androidx.annotation.NonNull;

public class Species {
    private int speciesId;
    private String speciesName;

    public Species() {}

    public Species(int speciesId, String speciesName) {
        this.speciesId = speciesId;
        this.speciesName = speciesName;
    }

    // Getters and setters
    public int getSpeciesId() { return speciesId; }
    public void setSpeciesId(int speciesId) { this.speciesId = speciesId; }
    public String getSpeciesName() { return speciesName; }
    public void setSpeciesName(String speciesName) { this.speciesName = speciesName; }

    @NonNull
    @Override
    public String toString() {
        return  speciesName;
    }
}