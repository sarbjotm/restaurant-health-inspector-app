package com.example.restauranthealthinspector.model;

import android.content.Context;

/**
 * A restaurant class to store the tracking number, name , address,
 * inspections reports and an icon ID for a restaurant.
 */
public class Restaurant {
    private String trackingNumber;
    private String restaurantName;
    private Address address;
    private RestaurantIcon iconID;
    private InspectionsManager inspectionsManager = new InspectionsManager();
    private boolean isFavourite = false;

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
        return iconID.getIconID();
    }

    public void setIconID(Context context){
        iconID = new RestaurantIcon(context,restaurantName);

    }

    public boolean getFavourite(){
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite){
        this.isFavourite = isFavourite;
    }
}
