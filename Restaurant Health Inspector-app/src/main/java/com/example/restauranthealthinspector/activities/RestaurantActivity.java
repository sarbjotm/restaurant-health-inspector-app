package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {
    private RestaurantsManager myRestaurants;
    private Restaurant restaurant;
    private int indexRestaurant;
    private RestaurantsManager listRestaurants;
    ArrayList<Inspection> inspections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        try {
            myRestaurants = RestaurantsManager.getInstance(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        indexRestaurant = intent.getIntExtra("indexRestaurant", 0);
        restaurant = myRestaurants.get(indexRestaurant);
        inspections = restaurant.getInspectionsManager().getInspectionList();

        loadRestaurant();
        populateListView();
    }

    private void populateListView(){
        ArrayAdapter<Inspection> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.rest_listInspections);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Inspection> {
        public MyListAdapter(){
            super(RestaurantActivity.this, R.layout.list_inspections, inspections);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.list_inspections, parent, false);
            }

            Inspection currentInspection = inspections.get(position);
            TextView numCritical = (TextView) itemView.findViewById(R.id.listI_txtCriticalNum);
            numCritical.setText(Integer.toString(currentInspection.getNumCritical()));
            TextView numNonCritical = (TextView) itemView.findViewById(R.id.listI_txtNonCriticalNum);
            numNonCritical.setText(Integer.toString(currentInspection.getNumNonCritical()));
            TextView hazardLevel = (TextView) itemView.findViewById(R.id.listI_txtHazardNum);
            hazardLevel.setText(currentInspection.getHazardRating());

            return itemView;
        }
    }

    private void loadRestaurant() {
        Intent intent = getIntent();
        indexRestaurant = intent.getIntExtra("indexRestaurant", 0);

        restaurant = myRestaurants.get(indexRestaurant);

        TextView restName = (TextView)findViewById(R.id.rest_txtName);
        String restaurantName = restaurant.getRestaurantName();
        restName.setText(restaurantName);

        TextView restAddress = (TextView)findViewById(R.id.rest_txtAddress);
        String restaurantAddress = restaurant.getAddress().getStreetAddress() +
                        ", " + restaurant.getAddress().getCity();
        restAddress.setText("Address: " + restaurantAddress);

        TextView restLatitude = (TextView)findViewById(R.id.rest_txtLatitude);
        double restaurantLatitude = restaurant.getAddress().getLatitude();
        restLatitude.setText("Latitude: " + Double.toString(restaurantLatitude));


        TextView restLongitude = (TextView)findViewById(R.id.rest_txtLongitude);
        double restaurantLongitude = restaurant.getAddress().getLongitude();
        restLongitude.setText("Longitude: " + Double.toString(restaurantLongitude));
    }

    private void setUpInspectionClick() {
        ListView list = findViewById(R.id.restlist_listRestaurants);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                Intent intent = new Intent(RestaurantActivity.this, InspectionActivity.class);
                intent.putExtra("indexRestaurant", indexRestaurant);
                intent.putExtra("indexInspection", position);
                startActivity(intent);
            }
        });
    }
}