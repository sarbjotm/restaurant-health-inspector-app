/**
 * store Inspections into inspectionManager.
 * store Restaurants into RestaurantsManager.
 */

package com.example.resturanthealthinspector.TextUI;

import com.example.resturanthealthinspector.Model.Inspection;
import com.example.resturanthealthinspector.Model.InspectionsManager;
import com.example.resturanthealthinspector.Model.Restaurant;
import com.example.resturanthealthinspector.Model.RestaurantsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextUI {
    public TextUI() throws FileNotFoundException {
        storeRestaurants();
        storeInspections();
    }

    private void storeRestaurants() throws FileNotFoundException {
        File file = new File("./src/main/res/raw/restaurants_itr1.csv");
        System.out.println(file.getAbsolutePath());
        Scanner scan = null;
        scan = new Scanner(file);
        Restaurant restaurant;
        RestaurantsManager manager;
        manager = RestaurantsManager.getInstance();
        String line;
        line = scan.nextLine();
        double latitude;
        double longitude;
        while (scan.hasNextLine()){
            line = scan.nextLine();
            String[] lineArray = line.split(",");
            latitude = Double.parseDouble(lineArray[5]);
            longitude = Double.parseDouble(lineArray[6]);
            restaurant = new Restaurant(lineArray[0], lineArray[1], lineArray[2], lineArray[3], lineArray[4], latitude, longitude);
            manager.addRestaurant(restaurant);
        }
        scan.close();
    }

    private void storeInspections() throws FileNotFoundException {
        File file = new File("./src/main/res/raw/inspectionreports_itr1.csv");
        Scanner scan = null;
        scan = new Scanner(file);
        Inspection inspection;
        InspectionsManager manager;
        manager = InspectionsManager.getInstance();
        String line;
        line = scan.nextLine();
        int inspectionDate;
        int numCritical;
        int numNonCritical;
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            String[] lineArray = line.split(",");
            inspectionDate = Integer.parseInt(lineArray[1]);
            numCritical = Integer.parseInt(lineArray[3]);
            numNonCritical = Integer.parseInt(lineArray[4]);

            if (lineArray.length == 6){
                inspection = new Inspection(lineArray[0], inspectionDate, lineArray[2], numCritical, numNonCritical, lineArray[5], "");
            }
            else{
                inspection = new Inspection(lineArray[0], inspectionDate, lineArray[2], numCritical, numNonCritical, lineArray[5], lineArray[6]);
            }
            manager.addInspection(inspection);
        }
        scan.close();
    }

}
