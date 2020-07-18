package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.restauranthealthinspector.MapsActivity;
import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.AppController;
import com.example.restauranthealthinspector.model.Date;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A list of restaurants with brief inspections report.
 */
public class RestaurantListActivity extends AppCompatActivity {
        private RestaurantsManager myRestaurants;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_restaurant_list);

                startActivity(new Intent(this, MapsActivity.class));

                FragmentManager manager = getSupportFragmentManager();
                UpdateDialog dialog = new UpdateDialog();
                dialog.show(manager, "Update");

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean requestData = sharedPreferences.getBoolean("requestData", false);

                if (requestData) {
                        loadData();
                }

                try {
                        populateRestaurants();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                populateListView();
                setUpRestaurantClick();




        }

        private void loadData() {
                String restaurantURL = getResources().getString(R.string.restaurantURL);
                getData(restaurantURL);
                String inspectionURL = getResources().getString(R.string.inspectionURL);
                getData(inspectionURL);
        }

        // Code refer from Android JSON parsing using Volley
        // https://www.androidhive.info/2014/09/android-json-parsing-using-volley/
        private void getData(String url) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                        try {
                                                JSONObject result = response.getJSONObject("result");
                                                JSONArray resourceArray = result.getJSONArray("resources");

                                                for (int i = 0; i < resourceArray.length(); i++) {
                                                        JSONObject resource = (JSONObject) resourceArray.get(i);

                                                        String format = resource.getString("format");
                                                        if (format.equals("CSV")) {
                                                                String lastModified = resource.getString("last_modified");
                                                                String dataURL = resource.getString("url");
                                                                String name = resource.getString("name");

                                                                Log.i("URL", dataURL);
                                                                Log.i("lastModified", lastModified);
                                                                downloadData(dataURL, lastModified, name);
                                                                break;
                                                        }
                                                }

                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                                Log.i("data", Objects.requireNonNull(e.getMessage()));
                                        }
                                }

                                }, new Response.ErrorListener () {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                        Log.i("data", Objects.requireNonNull(error.getMessage()));
                                }
                        } );
                AppController.getInstance().addToRequestQueue(request);
        }

        private void downloadData(String dataURL, String lastModified, String name) {
                String fileName = name + ".csv";

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(dataURL));
                request.setDescription("Data");
                request.setTitle(name);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
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

                        ImageView restaurantImage = itemView.findViewById(R.id.listR_imgRestaurantIcon);
                        restaurantImage.setImageResource(currentRestaurant.getIconID(RestaurantListActivity.this));

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
                                TextView issues = itemView.findViewById(R.id.listR_txtIssues);
                                issues.setVisibility(View.INVISIBLE);
                                restaurantIssues.setVisibility(View.INVISIBLE);
                                restaurantHazardLevel.setVisibility(View.INVISIBLE);
                                restaurantDate.setVisibility(View.INVISIBLE);
                                restaurantHazardImage.setVisibility(View.INVISIBLE);

                                TextView inspection = itemView.findViewById(R.id.listR_txtInspection);
                                String noInspection = getResources().getString(R.string.no_inspections_recorded);
                                inspection.setText(noInspection);

                        }
                        return itemView;
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

