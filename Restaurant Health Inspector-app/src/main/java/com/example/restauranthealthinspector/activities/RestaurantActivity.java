package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Address;
import com.example.restauranthealthinspector.model.FavouriteRestaurantManager;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;
import com.google.gson.Gson;

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
    private String nameRestaurant;
    private boolean fromMap;
    private FavouriteRestaurantManager myFavouriteRestaurants;
    ArrayList<Inspection> inspections;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        try {
            myRestaurants = RestaurantsManager.getInstance(null, null);
            myFavouriteRestaurants = FavouriteRestaurantManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setUpBackButton();
        setUpToMapButton();
        loadRestaurant();
        fillRestaurantDetails();

        populateListView();
        setUpInspectionClick();

        noInspectionsMessage();

        setUpFavouriteButton();



    }

    private void setUpFavouriteButton(){
        final Button btn = (Button) findViewById(R.id.rest_btnFavourite);
        final TextView restName = findViewById(R.id.rest_txtName);
        final TextView restFav = findViewById(R.id.rest_txtFav);
//        final Button btn = (Button) findViewById(R.id.rest_btnFavourite);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.getText().toString().contains("Favourite")){
                    restaurant.setFavourite(true);
                    btn.setText(R.string.unfavourite);
                    myFavouriteRestaurants.add(restaurant);
                    restName.setTextColor(Color.parseColor("#FFFF00"));
                    restFav.setVisibility(View.VISIBLE);
                    Toast.makeText(RestaurantActivity.this, "Favourited Restaurant", Toast.LENGTH_SHORT).show();
                    saveData();
                }

                else if(btn.getText().toString().contains("Un-favourite")){
                    myFavouriteRestaurants.delete(restaurant);
                    restaurant.setFavourite(false);
                    btn.setText(R.string.favourite);
                    restName.setTextColor(Color.parseColor("#FFFFFF"));
                    restFav.setVisibility(View.INVISIBLE);
                    Toast.makeText(RestaurantActivity.this, "Un-Favourited Restaurant", Toast.LENGTH_SHORT).show();
                    saveData();
                }
            }
        });
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myFavouriteRestaurants.getFavouriteList());
        editor.putString("task list", json);
        editor.apply();
    }



    private void setUpToMapButton(){
        TextView lngBtn = findViewById(R.id.rest_txtLongitude);
        TextView latBtn = findViewById(R.id.rest_txtLatitude);
        lngBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantActivity.this, MapsActivity.class);
                Toast.makeText(RestaurantActivity.this, "latitude:" + restaurant.getAddress().getLatitude(), Toast.LENGTH_SHORT).show();
                Toast.makeText(RestaurantActivity.this, "longitude:" + restaurant.getAddress().getLongitude(), Toast.LENGTH_SHORT).show();
                intent.putExtra("longitude", restaurant.getAddress().getLongitude());
                intent.putExtra("latitude", restaurant.getAddress().getLatitude());
                intent.putExtra("fromGPS", true);
                startActivity(intent);
            }
        });
        latBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantActivity.this, MapsActivity.class);
                Toast.makeText(RestaurantActivity.this, "latitude:" + restaurant.getAddress().getLatitude(), Toast.LENGTH_SHORT).show();
                intent.putExtra("longitude", restaurant.getAddress().getLongitude());
                intent.putExtra("latitude", restaurant.getAddress().getLatitude());
                intent.putExtra("fromGPS", true);
                startActivity(intent);
            }
        });
    }


    private void setUpBackButton() {
        ImageButton btn = findViewById(R.id.rest_imgbtnBack);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent;
                if(!fromMap){
                    intent = new Intent(RestaurantActivity.this, RestaurantListActivity.class);
                }
                else{
                    intent = new Intent(RestaurantActivity.this, MapsActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void loadRestaurant() {
        Intent intent = getIntent();
        //indexRestaurant = intent.getIntExtra("indexRestaurant", 0);
        fromMap = intent.getBooleanExtra("fromMap", false);
        nameRestaurant = intent.getStringExtra("nameRestaurant");
        indexRestaurant = findIndexRestaurant(nameRestaurant);
        restaurant = myRestaurants.get(indexRestaurant);
        inspections = restaurant.getInspectionsManager().getInspectionList();
    }

    private int findIndexRestaurant(String nameRestaurant){
        int i = 0;
        for (Restaurant restaurant : myRestaurants){
           restaurant.setIconID(RestaurantActivity.this, restaurant.getRestaurantName());
            if (restaurant.getRestaurantName().equals(nameRestaurant)){
                return i;
            }
            i++;
        }
        return 0;
    }

    private void fillRestaurantDetails() {
        Button btn = (Button) findViewById(R.id.rest_btnFavourite);
        TextView restFav = findViewById(R.id.rest_txtFav);

        ImageView restaurantImage = findViewById(R.id.rest_imgRestaurant);
        TextView restName = findViewById(R.id.rest_txtName);
        String restaurantName = restaurant.getRestaurantName();
        restName.setText(restaurantName);
        if ( (restaurant.getFavourite()) || (myFavouriteRestaurants.getFavouriteList().contains(restaurant))){

            restName.setTextColor(Color.parseColor("#FFFF00"));
            restFav.setVisibility(View.VISIBLE);


        }

        else{

            restName.setTextColor(Color.parseColor("#FFFFFF"));
            restFav.setVisibility(View.INVISIBLE);

        }

        restaurantImage.setImageResource(restaurant.getIconID());

//        RestaurantIcon restaurantIcon = new RestaurantIcon(RestaurantActivity.this, restaurantName);

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


        if(restaurant.getFavourite()){
            btn.setText(R.string.unfavourite);
        }

        else{
            btn.setText(R.string.favourite);
        }




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