package com.example.animor.Model;

public class FavoriteItem {
    private String name;
    private String city;
    private String sex;
    private String species;
    private int imageResId; // id del recurso de imagen
    private boolean isFavorite;

    public FavoriteItem(String name, String city, String sex, String species, int imageResId, boolean isFavorite) {
        this.name = name;
        this.city = city;
        this.sex = sex;
        this.species = species;
        this.imageResId = imageResId;
        this.isFavorite = isFavorite;
    }

    // Getters y Setters
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getSex() { return sex; }
    public String getSpecies() { return species; }
    public int getImageResId() { return imageResId; }
    public boolean isFavorite() { return isFavorite; }

    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
