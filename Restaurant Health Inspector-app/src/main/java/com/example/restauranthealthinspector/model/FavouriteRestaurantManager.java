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
public class FavouriteRestaurantsManager{
    private static ArrayList<Restaurant> favouriteRestaurantList = new ArrayList<>();

    private FavouriteRestaurantsManager(){

    }



    private static void sortRestaurants () {
        Collections.sort(favouriteRestaurantList, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return r1.getRestaurantName().toLowerCase().compareTo(r2.getRestaurantName().toLowerCase());
            }
        });
    }



    public void add(Restaurant restaurant){
        favouriteRestaurantList.add(restaurant);
    }

    public Restaurant get(int index){
        return favouriteRestaurantList.get(index);
    }

    public static ArrayList<Restaurant> getRestaurants(){
        return favouriteRestaurantList;
    }

    public void delete(Restaurant restaurant){
        favouriteRestaurantList.remove(restaurant);
    }

}
