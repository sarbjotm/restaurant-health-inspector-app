package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.ArrayList;

public class RestaurantListActivity extends AppCompatActivity {
        private RestaurantsManager myRestaurants;
        private int issues;
        private String level;
        private String date;
        private String day;
        private String month;
        private String year;

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
                ListView list = (ListView) findViewById(R.id.restlist_listRestaurants);
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
                        TextView restaurantName = (TextView) itemView.findViewById(R.id.listR_txtRestaurantName);
                        restaurantName.setText(currentRestaurant.getRestaurantName());

                        TextView restaurantIssues = (TextView) itemView.findViewById(R.id.listR_txtIssuesNum);

                        ArrayList<Inspection> inspections = currentRestaurant.getInspectionsManager().getInspectionList();

                        if (inspections.size() != 0 ) {
                                issues = inspections.get(0).getNumCritical() + inspections.get(0).getNumNonCritical();
                                restaurantIssues.setText(Integer.toString(issues));

                        } else {
                                restaurantIssues.setText(Integer.toString(0));
                        }

                        TextView restaurantHazardLevel = (TextView) itemView.findViewById(R.id.listR_txtHazardLevel);
                        ImageView restaurantHazardImage = (ImageView) itemView.findViewById(R.id.listR_imgHazard);
                        TextView restaurantDate = (TextView) itemView.findViewById(R.id.listR_txtCustomDate);
                        if (inspections.size() != 0 ) {
                                level = inspections.get(0).getHazardRating();
                                restaurantHazardLevel.setText(level);
                                if(level.equals("Low")){
                                        restaurantHazardImage.setImageResource(R.drawable.hazard_low);
                                }
                                else if(level.equals("Moderate")){
                                        restaurantHazardImage.setImageResource(R.drawable.hazard_moderate);
                                }
                                else{
                                        restaurantHazardImage.setImageResource(R.drawable.hazard_high);
                                }
                                date =  new String(String.valueOf(currentRestaurant.getInspectionsManager().get(0).getInspectionDate()));
                                char[] charArrayDate = date.toCharArray();
                                if(charArrayDate[11] == '\''){
                                        day = new StringBuilder().append(charArrayDate[10]).toString();
                                }
                                else{
                                        day = new StringBuilder().append(charArrayDate[10]).append(charArrayDate[11]).toString();
                                }

                                if (charArrayDate[22] == '\''){
                                        month = new StringBuilder().append(charArrayDate[21]).toString();
                                }
                                else{
                                        month = new StringBuilder().append(charArrayDate[21]).append(charArrayDate[22]).toString();
                                }

                                year = new StringBuilder().append(charArrayDate[32]).append(charArrayDate[33]).append(charArrayDate[34]).append(charArrayDate[35]).toString();

                                restaurantDate.setText(month + " " + day + " " + year);
                              //  Log.e("ERROR123", date);

                        } else {
                                restaurantHazardLevel.setText("N/A");
                                restaurantDate.setText("N/A");
                        }





                        return itemView;
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

