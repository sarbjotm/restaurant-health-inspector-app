/**
 * store Inspections into inspectionManager.
 * store Restaurants into RestaurantsManager.
 */

package com.example.resturanthealthinspector.TextUI;

import com.example.resturanthealthinspector.Model.Inspection;
import com.example.resturanthealthinspector.Model.InspectionsManager;
import com.example.resturanthealthinspector.Model.Restaurant;
import com.example.resturanthealthinspector.Model.RestaurantsManager;

import java.io.FileNotFoundException;

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
