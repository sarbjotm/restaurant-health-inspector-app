package com.example.restauranthealthinspector.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * An array list of restaurants.
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
            sortRestaurants();
            splitInspections(readerInspections);
            sortInspections();
        }
        return instance;
    }

    private static void storeRestaurants(BufferedReader reader) throws IOException {
        Restaurant restaurant;
        Address address;
        String line;
        reader.readLine();

        while ( (line = reader.readLine()) != null) {
            //line = line.replaceAll("\"", "");
            String[] lineArray = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            for (int i = 0; i < lineArray.length; i++) {
                lineArray[i] = lineArray[i].replaceAll("\"", "");
            }
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

    private static void sortRestaurants () {
        Collections.sort(restaurantList, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return r1.getRestaurantName().toLowerCase().compareTo(r2.getRestaurantName().toLowerCase());
            }
        });
    }

    private static void splitInspections(BufferedReader reader) throws IOException {
        String trackingNumber;
        String line;
        reader.readLine();

        while ( (line = reader.readLine()) != null) {
            //line = line.replaceAll("\"", "");
            String[] lineArray = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            for (Restaurant restaurant:restaurantList) {
                trackingNumber = restaurant.getTrackingNumber();
                if (trackingNumber.equals(lineArray[0])) {
                    //Log.i("inspection", line);
                    restaurant.getInspectionsManager().addFromLineArray(lineArray);
                }
            }
        }
    }

    private static void sortInspections() {
        for (Restaurant restaurant:restaurantList) {
            restaurant.getInspectionsManager().sortInspections();
        }
    }

    public void add(Restaurant restaurant){
        restaurantList.add(restaurant);
    }

    public Restaurant get(int index){
        return restaurantList.get(index);
    }

    public static ArrayList<Restaurant> getRestaurants(){
        return restaurantList;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }
}
