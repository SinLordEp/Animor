package com.example.animor.Model.entity;

import java.io.Serializable;

public class User implements Serializable {
    private long userId;
    private String userName;
    private String email;
    private String photoUrl;
    private String userToken;
    public User() {}

    public User(String userToken, String userPhoto, long userId, String userName, String email) {
        this.userToken = userToken;
        this.photoUrl = userPhoto;
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
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}