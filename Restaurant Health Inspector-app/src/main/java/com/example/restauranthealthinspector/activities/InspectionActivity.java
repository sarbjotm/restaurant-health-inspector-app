package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.InspectionsManager;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;

import java.io.IOException;

public class InspectionActivity extends AppCompatActivity {
    private RestaurantsManager myRestaurants;
    private Inspection inspection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        try {
            myRestaurants = RestaurantsManager.getInstance(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadInspection();
    }

    private void loadInspection() {
        Intent intent = getIntent();
        int indexRestaurant = intent.getIntExtra("indexRestaurant", 0);
        int indexInspection = intent.getIntExtra("indexInspection", 0);
        Restaurant restaurant = myRestaurants.get(indexRestaurant);
        InspectionsManager inspectionsManager = restaurant.getInspectionsManager();
        inspection = inspectionsManager.get(indexInspection);
    }
}