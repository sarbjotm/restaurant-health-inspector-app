package com.example.restauranthealthinspector.model.online;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.RestaurantsManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DataLoad {
    private Context context;
    private RestaurantsManager myRestaurants;
    private InputStream inputRestaurants;
    private InputStream inputInspections;

    public DataLoad(Context context) {
        this.context = context;
    }

    public void loadSample() {
        inputRestaurants = context.getResources().openRawResource(R.raw.restaurants_itr1);
        inputInspections = context.getResources().openRawResource(R.raw.inspectionreports_itr1);

        try {
            populateRestaurants();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String restaurantFileName = sharedPreferences.getString("restaurantFileName", "");
        String inspectionFileName = sharedPreferences.getString("inspectionFileName", "");

        try {
            inputRestaurants = getInputStream(restaurantFileName);
            inputInspections = getInputStream(inspectionFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            loadSample();
        }
        try {
            populateRestaurants();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private InputStream getInputStream(String fileName) throws FileNotFoundException {
        Log.i("filename", fileName);
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        return new FileInputStream(file);
    }


    // Code from Brian Fraser videos
    // Read CSV Resource File: Android Programming
    private void populateRestaurants() throws IOException {
        BufferedReader readerRestaurants = new BufferedReader(
                new InputStreamReader(inputRestaurants, StandardCharsets.UTF_8)
        );

        BufferedReader readerInspections = new BufferedReader(
                new InputStreamReader(inputInspections, StandardCharsets.UTF_8)
        );
        myRestaurants = RestaurantsManager.getInstance(readerRestaurants, readerInspections);
    }

}
