package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Address;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantIcon;
import com.example.restauranthealthinspector.model.RestaurantsManager;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Details about a restaurant with a list of inspections.
 */
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

        setUpBackButton();
        loadRestaurant();
        fillRestaurantDetails();

        populateListView();
        setUpInspectionClick();

        noInspectionsMessage();
    }

    private void setUpBackButton() {
        ImageButton btn = findViewById(R.id.rest_imgbtnBack);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantActivity.this, RestaurantListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void loadRestaurant() {
        Intent intent = getIntent();
        indexRestaurant = intent.getIntExtra("indexRestaurant", 0);
        restaurant = myRestaurants.get(indexRestaurant);
        inspections = restaurant.getInspectionsManager().getInspectionList();
    }

    private void fillRestaurantDetails() {
        ImageView restaurantImage = findViewById(R.id.rest_imgRestaurant);
        TextView restName = findViewById(R.id.rest_txtName);
        String restaurantName = restaurant.getRestaurantName();
        restName.setText(restaurantName);

//        RestaurantIcon restaurantIcon = new RestaurantIcon(RestaurantActivity.this, restaurantName);
        restaurantImage.setImageResource(restaurant.getIconID());

        Address address = restaurant.getAddress();
        TextView restAddress = findViewById(R.id.rest_txtAddress);
        String restaurantAddress = address.getStreetAddress() + ", " + address.getCity();
        restAddress.setText(restaurantAddress);

        TextView restLatitude = findViewById(R.id.rest_txtLatitude);
        String latitude = getResources().getString(R.string.latitude);
        latitude += " " + address.getLatitude();
        restLatitude.setText(latitude);

        TextView restLongitude = findViewById(R.id.rest_txtLongitude);
        String longitude = getResources().getString(R.string.longitude);
        longitude += " " + address.getLongitude();
        restLongitude.setText(longitude);
    }

    private void populateListView(){
        ArrayAdapter<Inspection> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.rest_listInspections);
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

            TextView numCritical = itemView.findViewById(R.id.listI_txtCriticalNumAmount);
            numCritical.setText(String.valueOf(currentInspection.getNumCritical()));

            TextView numNonCritical = itemView.findViewById(R.id.listI_txtNonCriticalNumAmount);
            numNonCritical.setText(String.valueOf(currentInspection.getNumNonCritical()));

            hazard(itemView, currentInspection);

            TextView inspectionDate = itemView.findViewById(R.id.listI_txtDateNum);

            try {
                inspectionDate.setText(currentInspection.getInspectionDate().getSmartDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return itemView;
        }
    }

    private void hazard(View itemView, Inspection inspection) {
        TextView hazardLevel = itemView.findViewById(R.id.listI_txtHazardNum);
        String getHazardLevel = inspection.getHazardRating();
        hazardLevel.setText(getHazardLevel);

        ImageView hazardSymbol = itemView.findViewById(R.id.listI_imgHazard);

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

    private void noInspectionsMessage() {
        if (restaurant.getInspectionsManager().getInspectionList().size() != 0) {
            TextView textView = findViewById(R.id.rest_txtNoInspections);
            textView.setVisibility(View.INVISIBLE);
        }
    }
}