package com.example.restauranthealthinspector.model;

/**
 * A restaurant class.
 */
public class Restaurant {
    private String trackingNumber;
    private String restaurantName;
    private Address address;
    private InspectionsManager inspectionsManager = new InspectionsManager();
    private int iconID;

    public Restaurant(String trackingNumber, String restaurantName, Address address) {
        this.trackingNumber = trackingNumber;
        this.restaurantName = restaurantName;
        this.address = address;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public Address getAddress() {
        return address;
    }

    public InspectionsManager getInspectionsManager() {
        return inspectionsManager;
    }

    public int getIconID() {
        return iconID;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", address=" + address +
                ", inspectionsManager=" + inspectionsManager +
                '}';
    }
}
