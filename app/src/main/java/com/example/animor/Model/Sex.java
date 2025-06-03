package com.example.animor.Model;

public enum Sex {
    Male("Male"),
    Female("Female"),
    Unknown("Unknown");

    private final String displayName;

    // Constructor
    Sex(String displayName) {
        this.displayName = displayName;
    }

    // Método para obtener el nombre legible
    public String getDisplayName() {
        return displayName;
    }

    // Método estático para obtener un enum desde un string (opcional)
    public static Sex fromString(String text) {
        for (Sex sex : Sex.values()) {
            if (sex.displayName.equalsIgnoreCase(text)) {
                return sex;
            }
        }
        return Unknown; // Valor por defecto si no se encuentra
    }
}
