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

    private FavouriteRestaurantManager(){

    }

    public static FavouriteRestaurantManager getInstance(){
        if (instance == null){
            instance = new FavouriteRestaurantManager();
            Log.e("ERROR123","123");
        }
        return instance;
    }

    private static void storeFavouriteRestaurants(Restaurant restaurant) {
        instance.add(restaurant);
    }

    public ArrayList<Restaurant> getFavouriteList() {
        return favouriteRestaurantList;
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
    @Override
    public Iterator<Restaurant> iterator() {
        return favouriteRestaurantList.iterator();
    }
}
