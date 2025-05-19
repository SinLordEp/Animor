package com.example.animor.Model;

public class Favorite {
    private int userId;
    private int listingId;

    public Favorite() {}

    public Favorite(int userId, int listingId) {
        this.userId = userId;
        this.listingId = listingId;
    }

    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }
}