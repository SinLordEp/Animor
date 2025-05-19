package com.example.animor.Model;

public class Location {
    private int locationId;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String address;
    private String latitude;
    private String longitude;

    public Location() {}

    public Location(int locationId, String city, String province, String country,
                    String postalCode, String address, String latitude, String longitude) {
        this.locationId = locationId;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
}