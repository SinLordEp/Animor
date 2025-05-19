package com.example.animor.Model;

import java.time.LocalDate;

public class AnimalHistory {
    private int historyId;
    private int animalId;
    private String medicalOperation;
    private LocalDate operationDate;
    private LocalDate expirationDate;
    private String medicalDescription;

    public AnimalHistory() {}

    public AnimalHistory(int historyId, int animalId, String medicalOperation,
                         LocalDate operationDate, LocalDate expirationDate, String medicalDescription) {
        this.historyId = historyId;
        this.animalId = animalId;
        this.medicalOperation = medicalOperation;
        this.operationDate = operationDate;
        this.expirationDate = expirationDate;
        this.medicalDescription = medicalDescription;
    }

    // Getters and setters
    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }
    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }
    public String getMedicalOperation() { return medicalOperation; }
    public void setMedicalOperation(String medicalOperation) { this.medicalOperation = medicalOperation; }
    public LocalDate getOperationDate() { return operationDate; }
    public void setOperationDate(LocalDate operationDate) { this.operationDate = operationDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public String getMedicalDescription() { return medicalDescription; }
    public void setMedicalDescription(String medicalDescription) { this.medicalDescription = medicalDescription; }
}