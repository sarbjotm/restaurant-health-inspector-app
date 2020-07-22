package com.example.restauranthealthinspector.model;

import android.content.Context;

import com.example.restauranthealthinspector.R;

import java.util.concurrent.ThreadLocalRandom;

/**
 * RestaurantIcon to store different icons for each restaurant.
 */
public class RestaurantIcon {
    private String restaurantCode;
    private String restaurantName;
    private int iconID;
    private int[] savedIcon = {R.drawable.restaurant_icon_burger, //0
            R.drawable.restaurant_icon_seafood,
            R.drawable.restaurant_icon_pizza,
            R.drawable.restaurant_icon_sushi,
            R.drawable.restaurant_icon_chicken,
            R.drawable.restaurant_icon_aw, //5
            R.drawable.restaurant_icon_burgerking,
            R.drawable.restaurant_icon_dq,
            R.drawable.restaurant_icon_kfc,
            R.drawable.restaurant_icon_mcdonalds,
            R.drawable.restaurant_icon_seveneleven, //10
            R.drawable.restaurant_icon_fresh_slice,
            R.drawable.restaurant_icon_bp, //12
            R.drawable.restaurant_icon,
            R.drawable.random_restaurant_icon1,
            R.drawable.random_restaurant_icon2,
            R.drawable.random_restaurant_icon3,
            R.drawable.random_restaurant_icon4,



    };

    public RestaurantIcon(Context context, String restaurantCode, String restaurantName) {
        this.restaurantCode = restaurantCode;
        this.restaurantName = restaurantName;
        generateIconID(context);
    }

    public int getIconID() {
        return iconID;
    }

    private void generateIconID (Context context) {
        if ( (restaurantName.contains("A&W")) || (restaurantName.contains("A & W ")) ){
            iconID = savedIcon[5];
        }

        else if ( restaurantName.contains("Burger King") ){
            iconID = savedIcon[6];
        }

        else if ( restaurantName.contains("Dairy Queen") ){
            iconID = savedIcon[7];
        }

        else if ( restaurantName.contains("KFC") ){
            iconID = savedIcon[8];
        }

        else if ( restaurantName.contains("McDonald's") ){
            iconID = savedIcon[9];
        }

        else if (restaurantName.contains("Fresh Slice")) {
            iconID = savedIcon[11];
        }

        else if (restaurantName.contains("Boston Pizza")) {
            iconID = savedIcon[12];
        }

        else if ( (restaurantName.contains("7-Eleven")) || restaurantName.contains("7-ElevenStore")) {
            iconID = savedIcon[10];
        }

        else if ( (restaurantName.contains("Burger")) || restaurantName.contains("burger")) {
            iconID = savedIcon[0];
        }

        else if ( (restaurantName.contains("Seafood")) || restaurantName.contains("seafood")) {
            iconID = savedIcon[1];
        }

        else if ( (restaurantName.contains("Pizza")) || restaurantName.contains("pizza")) {
            iconID = savedIcon[2];
        }

        else if ( (restaurantName.contains("Sushi")) || restaurantName.contains("sushi")) {
            iconID = savedIcon[3];
        }

        else if ( (restaurantName.contains("Chicken")) || restaurantName.contains("chicken")) {
            iconID = savedIcon[4];
        }

        else{
            int randomNum = ThreadLocalRandom.current().nextInt(13, 17 + 1);
            iconID = savedIcon[randomNum];

        }


    }
}
