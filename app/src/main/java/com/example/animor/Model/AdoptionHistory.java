package com.example.animor.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class AdoptionHistory {
    private int adoptionId;
    private int animalId;
    @JsonProperty("adoption_date")
    private LocalDate adoptionDate;
    @JsonProperty("return_date")
    private LocalDate returnDate;
    @JsonProperty("adoption_description")
    private String adoptionDescription;

    public AdoptionHistory() {}

    public AdoptionHistory(int adoptionId, int animalId, LocalDate adoptionDate,
                           LocalDate returnDate, String adoptionDescription) {
        this.adoptionId = adoptionId;
        this.animalId = animalId;
        this.adoptionDate = adoptionDate;
        this.returnDate = returnDate;
        this.adoptionDescription = adoptionDescription;
    }

    // Getters and setters
    public int getAdoptionId() { return adoptionId; }
    public void setAdoptionId(int adoptionId) { this.adoptionId = adoptionId; }
    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }
    public LocalDate getAdoptionDate() { return adoptionDate; }
    public void setAdoptionDate(LocalDate adoptionDate) { this.adoptionDate = adoptionDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getAdoptionDescription() { return adoptionDescription; }
    public void setAdoptionDescription(String adoptionDescription) { this.adoptionDescription = adoptionDescription; }
}