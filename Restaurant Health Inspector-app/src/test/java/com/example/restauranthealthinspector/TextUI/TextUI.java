package com.example.restauranthealthinspector.TextUI;

import com.example.restauranthealthinspector.Model.Inspection;
import com.example.restauranthealthinspector.Model.InspectionsManager;
import com.example.restauranthealthinspector.Model.Restaurant;
import com.example.restauranthealthinspector.Model.RestaurantsManager;

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

    public void printInspections() throws FileNotFoundException {
        InspectionsManager manager = InspectionsManager.getInstance();
        for (Inspection inspection : manager) {
            System.out.println(inspection);
        }
    }

}
