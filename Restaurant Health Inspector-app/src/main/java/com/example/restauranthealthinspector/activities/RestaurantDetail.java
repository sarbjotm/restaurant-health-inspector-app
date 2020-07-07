package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;

import java.io.FileNotFoundException;

public class RestaurantDetail extends AppCompatActivity {
    TextView restName = (TextView)findViewById(R.id.restName);
    TextView restAddress = (TextView)findViewById(R.id.restAddress);
    TextView restLatitude = (TextView)findViewById(R.id.restLatitude);
    TextView restLongitude = (TextView)findViewById(R.id.restLongitude);
    final String RESTAURANT_TRACKING_NUMBER = "SDFO-8HKP7E";
    String restaurantName;
    String restaurantAddress;
    double restaurantLatitude;
    double restaurantLongitude;
    RestaurantsManager restManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        try {
            restManager = RestaurantsManager.getInstance();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int i = 0;
        while (restManager.get(i) != null){
            if (restManager.get(i).getTrackingNumber() == RESTAURANT_TRACKING_NUMBER){
                restaurantName = restManager.get(i).getRestaurantName();
                restName.setText(restaurantName);
                restaurantAddress =
                        restManager.get(i).getAddress().getStreetAddress() +
                                ", " + restManager.get(i).getAddress().getCity();
                restAddress.setText(restaurantAddress);
                restaurantLatitude = restManager.get(i).getAddress().getLatitude();
                restLatitude.setText(Double.toString(restaurantLatitude));
                restLongitude.setText(Double.toString(restaurantLongitude));
                restaurantLongitude = restManager.get(i).getAddress().getLongitude();
            }
        }


    }
}