/**
 * Details about a restaurant with a list of inspections.
 */
package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import java.text.ParseException;
import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {
    private RestaurantsManager myRestaurants;
    private Restaurant restaurant;
    private int indexRestaurant;
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
        loadRestaurant();
        populateListView();
        setUpInspectionClick();
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
            TextView numCritical = (TextView) itemView.findViewById(R.id.listI_txtCriticalNumAmount);
            numCritical.setText(Integer.toString(currentInspection.getNumCritical()));
            TextView numNonCritical = (TextView) itemView.findViewById(R.id.listI_txtNonCriticalNumAmount);
            numNonCritical.setText(Integer.toString(currentInspection.getNumNonCritical()));
            TextView hazardLevel = (TextView) itemView.findViewById(R.id.listI_txtHazardNum);
            String getHazardLevel = currentInspection.getHazardRating();
            hazardLevel.setText(getHazardLevel);
            ImageView hazardSymbol = (ImageView) itemView.findViewById(R.id.listI_imgHazard);
            TextView inspectionDate = (TextView) itemView.findViewById(R.id.listI_txtDateNum);
            try {
                inspectionDate.setText(currentInspection.getInspectionDate().getSmartDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (getHazardLevel.equals("Low")){
                hazardSymbol.setImageResource(R.drawable.hazard_low);
                hazardLevel.setTextColor(Color.parseColor("#82F965"));
            }
            else if (getHazardLevel.equals("Moderate")){
                hazardSymbol.setImageResource(R.drawable.hazard_moderate);
                hazardLevel.setTextColor(Color.parseColor("#F08D47"));
            }
            else{
                hazardSymbol.setImageResource((R.drawable.hazard_high));
                hazardLevel.setTextColor(Color.parseColor("#EC4A26"));
            }
            return itemView;
        }
    }

    private void loadRestaurant() {
        Intent intent = getIntent();
        indexRestaurant = intent.getIntExtra("indexRestaurant", 0);
        restaurant = myRestaurants.get(indexRestaurant);
        inspections = restaurant.getInspectionsManager().getInspectionList();
        ImageView restaurantImage = (ImageView) findViewById(R.id.imageViewRestaurant2nd);
        TextView restName = (TextView)findViewById(R.id.rest_txtName);
        String restaurantName = restaurant.getRestaurantName();
        restName.setText(restaurantName);

        if ( (restaurantName.equals("104 Sushi & Co."))){
            restaurantImage.setImageResource(R.drawable.restaurant_icon_sushi);
        }

        else if(restaurantName.equals("Lee Yuen Seafood Restaurant")){
            restaurantImage.setImageResource(R.drawable.restaurant_icon_seafood);
        }

        else if(restaurantName.equals("Pattullo A&W")){
            restaurantImage.setImageResource(R.drawable.restaurant_icon_burger);
        }

        else if(restaurantName.equals("Zugba Flame Grilled Chicken")){
            restaurantImage.setImageResource(R.drawable.restaurant_icon_chicken);
        }

        else if((restaurantName.equals("Top in Town Pizza")) || restaurantName.equals("Top In Town Pizza")){
            restaurantImage.setImageResource(R.drawable.restaurant_icon_pizza);
        }


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
        ListView list = findViewById(R.id.rest_listInspections);
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