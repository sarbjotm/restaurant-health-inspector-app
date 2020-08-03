package com.example.restauranthealthinspector.model;

public class SearchFilter {
    private String search;
    private String word;
    private Restaurant restaurant;
    private boolean checkFlag;

    public boolean isSearched(Restaurant restaurant) {
        this.restaurant = restaurant;
        String[] searchWords = search.split(" ");

        for (String word : searchWords) {
            checkFlag = false;
            word = word.toLowerCase();

        }


        return false;
    }

    private boolean hasHazardLevel() {
        if (word.equals("high") || word.equals("medium") || word.equals("low")) {

            if (!hasInspections()) {
                return false;
            }

            String hazard = restaurant.getInspectionsManager().getInspectionList().get(1).getHazardRating();
            hazard = hazard.toLowerCase();

            if (word.equals(hazard)) {
                checkFlag = true;
                return true;
            }
        }

        return true;
    }

    private boolean hasInspections() {
        return restaurant.getInspectionsManager().getInspectionList().size() != 0;
    }

    private boolean hasNCriticalViolations() {
        return true;
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
