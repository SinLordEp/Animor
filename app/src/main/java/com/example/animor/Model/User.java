package com.example.animor.Model;
public class User {
    private String deviceToken;
    private String userFid;
    private String userName;
    private String email;
    private String phone;

    // Constructors, getters and setters
    public User() {}

    public User(String deviceToken, String userFid, String userName, String email) {
        this.deviceToken = deviceToken;
        this.userFid = userFid;
        this.userName = userName;
        this.email = email;
    }

    public String getUserFid() {
        return userFid;
    }

    public void setUserFid(String userFid) {
        this.userFid = userFid;
    }

    // Getters and setters
    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}