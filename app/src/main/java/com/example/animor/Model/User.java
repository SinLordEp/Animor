package com.example.animor.Model;
public class User {
    private String userToken;
    private String userName;
    private String email;
    private String phone;

    // Constructors, getters and setters
    public User() {}

    public User(String userId, String userName, String email) {
        this.userToken = userId;
        this.userName = userName;
        this.email = email;
    }

    // Getters and setters
    public String getUserToken() { return userToken; }
    public void setUserToken(String userToken) { this.userToken = userToken; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}