package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.example.restauranthealthinspector.model.Date;
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

/**
 * A list of restaurants with brief inspections report.
 */
public class RestaurantListActivity extends AppCompatActivity {
        private RestaurantsManager myRestaurants;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_restaurant_list);

                try {
                        populateRestaurants();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                populateListView();
                setUpRestaurantClick();

        }

        // Code from Brian Fraser videos
        // Read CSV Resource File: Android Programming
        private void populateRestaurants() throws IOException {
                InputStream inputRestaurant = getResources().openRawResource(R.raw.restaurants_itr1);
                BufferedReader readerRestaurants = new BufferedReader(
                        new InputStreamReader(inputRestaurant, StandardCharsets.UTF_8)
                );

                InputStream inputInspections = getResources().openRawResource(R.raw.inspectionreports_itr1);
                BufferedReader readerInspections = new BufferedReader(
                        new InputStreamReader(inputInspections, StandardCharsets.UTF_8)
                );

                myRestaurants = RestaurantsManager.getInstance(readerRestaurants, readerInspections);
        }

        private void populateListView() {
                ArrayAdapter<Restaurant> adapter = new MyListAdapter();
                ListView list = findViewById(R.id.restlist_listRestaurants);
                list.setAdapter(adapter);
        }

        private class MyListAdapter extends ArrayAdapter<Restaurant>{
                public MyListAdapter(){
                        super(RestaurantListActivity.this, R.layout.list_restaurants, myRestaurants.getRestaurants());
                }


                @SuppressLint("SetTextI18n")
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        View itemView = convertView;
                        if (itemView == null){
                                itemView = getLayoutInflater().inflate(R.layout.list_restaurants, parent, false);
                        }
                        Restaurant currentRestaurant = myRestaurants.get(position);

                        TextView restaurantName = itemView.findViewById(R.id.listR_txtRestaurantName);
                        restaurantName.setText(currentRestaurant.getRestaurantName());

                        restaurantImage(itemView, currentRestaurant);

                        TextView restaurantIssues = itemView.findViewById(R.id.listR_txtIssuesNum);
                        ArrayList<Inspection> inspections = currentRestaurant.getInspectionsManager().getInspectionList();

                        TextView restaurantHazardLevel = itemView.findViewById(R.id.listR_txtHazardLevel);
                        ImageView restaurantHazardImage = itemView.findViewById(R.id.listR_imgHazard);
                        TextView restaurantDate = itemView.findViewById(R.id.listR_txtCustomDate);

                        if (inspections.size() != 0 ) {
                                Inspection inspection = inspections.get(0);
                                hazard(itemView, inspection);

                                int issues = inspection.getNumCritical() + inspection.getNumNonCritical();
                                restaurantIssues.setText(Integer.toString(issues));

                                Date date =  currentRestaurant.getInspectionsManager().get(0).getInspectionDate();
                                try {
                                        restaurantDate.setText(date.getSmartDate());
                                } catch (ParseException e) {
                                        e.printStackTrace();
                                }

                        } else {
                                restaurantIssues.setText(Integer.toString(0));
                                restaurantHazardLevel.setText("N/A");
                                restaurantDate.setText("N/A");
                                restaurantHazardImage.setVisibility(View.INVISIBLE);
                        }

                        return itemView;
                }
        }

        private void restaurantImage(View itemView, Restaurant restaurant) {
                ImageView restaurantImage = itemView.findViewById(R.id.listR_imgRestaurantIcon);
                switch (restaurant.getRestaurantName()) {
                        case "104 Sushi & Co.":
                                restaurantImage.setImageResource(R.drawable.restaurant_icon_sushi);
                                break;
                        case "Lee Yuen Seafood Restaurant":
                                restaurantImage.setImageResource(R.drawable.restaurant_icon_seafood);
                                break;
                        case "Pattullo A&W":
                                restaurantImage.setImageResource(R.drawable.restaurant_icon_burger);
                                break;
                        case "Zugba Flame Grilled Chicken":
                                restaurantImage.setImageResource(R.drawable.restaurant_icon_chicken);
                                break;
                        case "Top in Town Pizza":
                        case "Top In Town Pizza":
                                restaurantImage.setImageResource(R.drawable.restaurant_icon_pizza);
                                break;
                }
        }

        private void hazard(View itemView, Inspection inspection) {
                String hazardRating = inspection.getHazardRating();

                TextView restaurantHazardLevel = itemView.findViewById(R.id.listR_txtHazardLevel);
                restaurantHazardLevel.setText(hazardRating);

                ImageView restaurantHazardImage = itemView.findViewById(R.id.listR_imgHazard);
                restaurantHazardImage.setVisibility(View.VISIBLE);

                if(hazardRating.equals("Low")){
                        restaurantHazardImage.setImageResource(R.drawable.hazard_low);
                        restaurantHazardLevel.setTextColor(Color.parseColor("#82F965"));
                }
                else if(hazardRating.equals("Moderate")){
                        restaurantHazardImage.setImageResource(R.drawable.hazard_moderate);
                        restaurantHazardLevel.setTextColor(Color.parseColor("#F08D47"));
                }
                else{
                        restaurantHazardImage.setImageResource(R.drawable.hazard_high);
                        restaurantHazardLevel.setTextColor(Color.parseColor("#EC4A26"));
                }

        }

        private void setUpRestaurantClick() {
                ListView list = findViewById(R.id.restlist_listRestaurants);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View viewClicked,
                                                int position, long id) {
                                Intent intent = new Intent(RestaurantListActivity.this, RestaurantActivity.class);
                                intent.putExtra("indexRestaurant", position);
                                startActivity(intent);
                        }
                });
        }


}

