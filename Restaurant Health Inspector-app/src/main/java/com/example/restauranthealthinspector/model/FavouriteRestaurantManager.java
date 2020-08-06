package com.example.restauranthealthinspector.model;


import android.util.Log;

import java.util.ArrayList;

import java.util.Iterator;

/**
 * An array list of restaurants.
 */
public class FavouriteRestaurantManager implements Iterable<Restaurant>{
    private static ArrayList<Restaurant> favouriteRestaurantList = new ArrayList<>();
    private static FavouriteRestaurantManager instance;
    private ArrayList<String> restaurantNames = new ArrayList<>();

    private FavouriteRestaurantManager(){

    }

    public static FavouriteRestaurantManager getInstance(){
        if (instance == null){
            instance = new FavouriteRestaurantManager();
        }
        return instance;
    }

    public ArrayList<Restaurant> getFavouriteList() {
        return favouriteRestaurantList;
    }

    public void add(Restaurant restaurant){
        favouriteRestaurantList.add(restaurant);
    }

    public void delete(Restaurant restaurant){
        favouriteRestaurantList.remove(restaurant);
    }

    public ArrayList<String> getRestaurantNames() {
        return restaurantNames;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return favouriteRestaurantList.iterator();
    }
}
