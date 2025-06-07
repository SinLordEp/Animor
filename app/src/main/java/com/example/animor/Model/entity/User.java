package com.example.animor.Model.entity;

import java.io.Serializable;

public class User implements Serializable {
    private String userToken;
    private long userId;
    private String userName;
    private String email;
    private String userPhoto;
    public User() {}

    public User(String userToken, String userPhoto, long userId, String userName, String email) {
        this.userToken = userToken;
        this.userPhoto = userPhoto;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() {
        return userPhoto;
    }

    public void setPhotoUrl(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}