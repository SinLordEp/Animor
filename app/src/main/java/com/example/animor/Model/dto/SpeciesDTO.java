package com.example.animor.Model.dto;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class SpeciesDTO implements Serializable {
    private int speciesId;
    private String speciesName;

    public SpeciesDTO() {}

    public SpeciesDTO(int speciesId, String speciesName) {
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