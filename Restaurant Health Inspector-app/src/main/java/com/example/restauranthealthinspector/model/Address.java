package com.example.restauranthealthinspector.model;

/**
 * Address of a restaurant with attributes address, city and GPS coordinates.
 */
public class Address {
    private String streetAddress;
    private String city;
    private double latitude;
    private double longitude;

    public Address(String streetAddress, String city, double latitude, double longitude) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
