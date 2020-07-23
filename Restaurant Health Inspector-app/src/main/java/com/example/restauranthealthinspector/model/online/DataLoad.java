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
    private InputStream inputRestaurants;
    private InputStream inputInspections;
    private String restaurantFileName;
    private String inspectionFileName;

    public DataLoad(Context context) {
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        restaurantFileName = sharedPreferences.getString("restaurantFileName", "");
        inspectionFileName = sharedPreferences.getString("inspectionFileName", "");
        Log.i("DataLoad", restaurantFileName);
        Log.i("DataLoad", inspectionFileName);
    }

    public void saveFileName(DataRequest restaurantData, DataRequest inspectionData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        restaurantFileName = restaurantData.getName() + ".csv";
        inspectionFileName = inspectionData.getName() + ".csv";
        Log.i("saving", restaurantFileName);
        Log.i("saving", inspectionFileName);

        editor.putString("restaurantFileName", restaurantFileName);
        editor.putString("inspectionFileName", inspectionFileName);

        editor.putString("restaurantLastModified", restaurantData.getLastModified());
        editor.putString("inspectionLastModified", inspectionData.getLastModified());
        editor.apply();
    }

    public void loadData() {
        try {
            Log.i("loading", restaurantFileName);
            Log.i("loading", inspectionFileName);
            inputRestaurants = getInputStream(restaurantFileName);
            inputInspections = getInputStream(inspectionFileName);
            updateDate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            getSample();
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

    private void updateDate() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        java.util.Date currentDate = new java.util.Date();
        editor.putLong("lastUpdated", currentDate.getTime());
        editor.apply();
    }

    private void getSample() {
        inputRestaurants = context.getResources().openRawResource(R.raw.restaurants_itr1);
        inputInspections = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
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
        RestaurantsManager.getInstance(readerRestaurants, readerInspections);
    }

}
