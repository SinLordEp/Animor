package com.example.animor.Model;
public class User {
    private int userId;
    private int googleId;
    private String userName;
    private String email;
    private String phone;

    // Constructors, getters and setters
    public User() {}

    public User(int userId, int googleId, String userName, String email, String phone) {
        this.userId = userId;
        this.googleId = googleId;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getGoogleId() { return googleId; }
    public void setGoogleId(int googleId) { this.googleId = googleId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}