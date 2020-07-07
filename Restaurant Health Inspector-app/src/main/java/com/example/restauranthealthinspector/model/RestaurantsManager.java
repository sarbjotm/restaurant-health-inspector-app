package com.example.restauranthealthinspector.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A manager to store Restaurants.
 */
public class RestaurantsManager implements Iterable<Restaurant>{
    private static ArrayList<Restaurant> restaurantList = new ArrayList<>();
    private static RestaurantsManager instance;

    private RestaurantsManager(){

    }

    public static RestaurantsManager getInstance(BufferedReader readerRestaurants, BufferedReader readerInspections) throws IOException {
        if (instance == null){
            instance = new RestaurantsManager();
            storeRestaurants(readerRestaurants);
            splitInspections(readerInspections);
        }
        return instance;
    }

    private static void storeRestaurants(BufferedReader reader) throws IOException {
        Restaurant restaurant;
        Address address;
        String line;
        reader.readLine();

        while ( (line = reader.readLine()) != null) {
            line = line.replaceAll("\"", "");
            String[] lineArray = line.split(",");
            address = generateAddress(lineArray);
            restaurant = new Restaurant(lineArray[0], lineArray[1], address);
            instance.add(restaurant);
        }
    }

    private static Address generateAddress(String[] lineArray) {
        String streetAddress = lineArray[2];
        String city = lineArray[3];
        double latitude = Double.parseDouble(lineArray[5]);
        double longitude = Double.parseDouble(lineArray[6]);
        return new Address(streetAddress, city, latitude, longitude);
    }

    private static void splitInspections(BufferedReader reader) throws IOException {
        String trackingNumber;
        String line;
        reader.readLine();

        while ( (line = reader.readLine()) != null) {
            line = line.replaceAll("\"", "");
            String[] lineArray = line.split(",");

            for (Restaurant restaurant:restaurantList) {
                trackingNumber = restaurant.getTrackingNumber();
                if (trackingNumber.equals(lineArray[0])) {
                     restaurant.getInspectionsManager().addFromLineArray(lineArray);
                }
            }
        }
    }

    public void add(Restaurant restaurant){
        restaurantList.add(restaurant);
    }

    public Restaurant get(int index){
        return restaurantList.get(index);
    }

    public ArrayList<Restaurant> getRestaurants(){
        return restaurantList;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }
}
