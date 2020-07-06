package com.example.restauranthealthinspector.TextUI;

import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.InspectionsManager;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;

import java.io.FileNotFoundException;

/**
 * Print Restaurant and Inspection List
 */
public class TextUI {

    public void printRestaurants() throws FileNotFoundException {
        RestaurantsManager manager = RestaurantsManager.getInstance();
        for (Restaurant restaurant : manager) {
            System.out.println(restaurant);
        }
    }

}
