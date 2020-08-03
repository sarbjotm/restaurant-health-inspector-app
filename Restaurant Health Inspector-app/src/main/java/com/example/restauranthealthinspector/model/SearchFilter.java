package com.example.restauranthealthinspector.model;

import java.util.ArrayList;

public class SearchFilter {
    private String search;
    private String word;
    private Restaurant restaurant;
    private boolean checkFlag;

    public boolean inFilter(Restaurant restaurant) {
        this.restaurant = restaurant;
        String[] searchWords = search.split(" ");

        for (String word : searchWords) {
            checkFlag = false;
            word = word.toLowerCase();

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

            String hazard = restaurant.getInspectionsManager().getInspectionList().get(1).getHazardRating();
            hazard = hazard.toLowerCase();

            if (word.equals(hazard)) {
                return true;
            }
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
                if (isYearOld(inspection)) {
                    break;
                }
                violationCount += inspection.getNumCritical() + inspection.getNumNonCritical();
            }


        }

        return false;
    }

    private boolean isYearOld (Inspection inspection) {

        return false;
    }

    private boolean hasFavourite() {
        if (word.equals("favourite")) {
            checkFlag = true;

            return false;
        }

        return true;
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
