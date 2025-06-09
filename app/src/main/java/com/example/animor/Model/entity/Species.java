package com.example.animor.Model.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Species implements Serializable {
    private static final long serialVersionUID = 1L;

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