/**
 * A restaurant class.
 */
package com.example.resturanthealthinspector.Model;

public class Restaurant {
    private String trackingNumber;
    private String restaurantName;
    private String address;
    private String city;
    private String facType;
    private double latitude;
    private double longitude;

    public Restaurant(String trackingNumber, String restaurantName, String address, String city, String facType, double latitude, double longitude) {
        this.trackingNumber = trackingNumber;
        this.restaurantName = restaurantName;
        this.address = address;
        this.city = city;
        this.facType = facType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getFacType() {
        return facType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", facType='" + facType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
