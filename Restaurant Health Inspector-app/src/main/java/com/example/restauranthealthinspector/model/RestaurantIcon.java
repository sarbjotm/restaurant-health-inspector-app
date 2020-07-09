package com.example.restauranthealthinspector.model;

import android.content.Context;

import com.example.restauranthealthinspector.R;

/**
 * RestaurantIcon to store different icons for each restaurant.
 */
public class RestaurantIcon {
    private String restaurantCode;
    private int iconID;
    private int[] savedIcon = {R.drawable.restaurant_icon_burger,
            R.drawable.restaurant_icon_seafood,
            R.drawable.restaurant_icon_pizza,
            R.drawable.restaurant_icon_sushi,
            R.drawable.restaurant_icon_chicken,
            R.drawable.restaurant_icon};

    public RestaurantIcon(Context context, String restaurantCode) {
        this.restaurantCode = restaurantCode;
        generateIconID(context);
    }

    public int getIconID() {
        return iconID;
    }

    private void generateIconID (Context context) {
        switch (restaurantCode) {
            case "SDFO-8HKP7E":
                iconID = savedIcon[0];
                break;
            case "SHEN-B7BNSR":
            case "SWOD-AHZUMF":
                iconID = savedIcon[1];
                break;
            case "SDFO-8GPUJX":
            case "SHEN-ANSLB4":
                iconID = savedIcon[2];
                break;
            case "SWOD-APSP3X":
                iconID = savedIcon[3];
                break;
            case "SPLH-9NEUHG":
                iconID = savedIcon[4];
                break;
            default:
                iconID = savedIcon[5];
        }

    }
}
