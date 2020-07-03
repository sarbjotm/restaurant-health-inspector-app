/**
 * A manager to store Restaurants.
 */
package com.example.resturanthealthinspector.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class RestaurantsManager implements Iterable<Restaurant>{
    private ArrayList<Restaurant> restaurantList = new ArrayList<>();
    private static RestaurantsManager instance;

    private RestaurantsManager(){

    }

    public RestaurantsManager getInstance() throws FileNotFoundException {
        if (instance == null){
            instance = new RestaurantsManager();
            storeRestaurants();
        }
        return instance;
    }

    private void storeRestaurants() throws FileNotFoundException {
        File file = new File("./src/main/res/raw/restaurants_itr1.csv");
        Scanner scan = new Scanner(file);
        Restaurant restaurant;
        String line = scan.nextLine();
        double latitude;
        double longitude;

        while (scan.hasNextLine()){
            line = scan.nextLine();
            String[] lineArray = line.split(",");
            latitude = Double.parseDouble(lineArray[5]);
            longitude = Double.parseDouble(lineArray[6]);
            restaurant = new Restaurant(lineArray[0], lineArray[1], lineArray[2], lineArray[3], lineArray[4], latitude, longitude);
            restaurantList.add(restaurant);
        }
        scan.close();
    }

    public void add(Restaurant restaurant){
        restaurantList.add(restaurant);
    }

    public Restaurant get(int index){
        return restaurantList.get(index);
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }
}
