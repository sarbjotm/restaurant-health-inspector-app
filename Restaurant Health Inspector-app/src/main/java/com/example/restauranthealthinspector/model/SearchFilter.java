package com.example.restauranthealthinspector.model;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * SearchFilter use to filter the restaurant list
 * Holds the search word and goes through a boolean with restaurant
 */
public class SearchFilter {
    private static SearchFilter instance;
    private String search;
    private String word;
    private Restaurant restaurant;
    private boolean checkFlag;

    private SearchFilter () {

    }

    public static SearchFilter getInstance() {
        if (instance == null) {
            instance = new SearchFilter();
        }
        return instance;
    }


    public boolean inFilter(Restaurant restaurant) {
        this.restaurant = restaurant;
        if (search == null || search.equals("")) {
            return true;
        }

        String[] searchWords = search.split(" ");

        for (String word : searchWords) {
            checkFlag = false;
            this.word = word.toLowerCase();

            if (hasHazardLevel() || hasNCriticalViolations() || hasFavourite()) {
                continue;
            }

            if (checkFlag) {
                return false;
            }

            if (!hasName()) {
                return false;
            }
        }

        return true;
    }

    private boolean hasHazardLevel() {
        if (word.equals("high") || word.equals("moderate") || word.equals("low")) {
            checkFlag = true;
            if (!hasInspections()) {
                return false;
            }

            String hazard = restaurant.getInspectionsManager().getInspectionList().get(0).getHazardRating();
            hazard = hazard.toLowerCase();

            return word.equals(hazard);
        }

        return false;
    }

    private boolean hasInspections() {
        return restaurant.getInspectionsManager().getInspectionList().size() != 0;
    }

    private boolean hasNCriticalViolations() {
        if (word.contains("<=") || word.contains(">=")) {
            int violationCount = 0;
            checkFlag = true;

            if (!hasInspections()) {
                return false;
            }
            ArrayList<Inspection> inspections = restaurant.getInspectionsManager().getInspectionList();

            for (Inspection inspection : inspections) {
                if (isYearOlder(inspection)) {
                    break;
                }
                violationCount += inspection.getNumCritical() + inspection.getNumNonCritical();
            }

            int violationN;
            if (word.contains("<=")) {
                violationN = wordToNumber();
                return violationN <= violationCount;
            } else {
                violationN = wordToNumber();
                return violationN >= violationCount;
            }
        }
        return false;
    }

    private boolean isYearOlder(Inspection inspection) {
        long daysDifference = 0;
        try {
            daysDifference = inspection.getInspectionDate().getDayDifference();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return daysDifference > 365;
    }

    private int wordToNumber() throws NumberFormatException {
        String numberString = word;
        numberString = numberString.replaceAll("<=", " ");
        numberString = numberString.replaceAll(">=", " ");
        numberString = numberString.trim();
        return Integer.parseInt(numberString);


    }

    private boolean hasFavourite() {
        if (word.equals("favourite")) {
            checkFlag = true;
            return restaurant.getFavourite();
        }

        return false;
    }

    private boolean hasName() {
        String name = restaurant.getRestaurantName();
        name = name.toLowerCase();
        return name.contains(word);
    }


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
