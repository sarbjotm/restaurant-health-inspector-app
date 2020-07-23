package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restauranthealthinspector.MapsActivity;
import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Date;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantIcon;
import com.example.restauranthealthinspector.model.RestaurantsManager;
import com.example.restauranthealthinspector.model.online.DataLoad;
import com.example.restauranthealthinspector.model.online.DataRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A list of restaurants with brief inspections report.
 */
public class RestaurantListActivity extends AppCompatActivity {
        private RestaurantsManager myRestaurants;
        private static final String TAG = "RestaurantListActivity";
        private static final int ERROR_DIALOG_REQUEST = 9001;
        private boolean isAvailable = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_restaurant_list);

                //startActivity(new Intent(this, MapsActivity.class));
                permissionCheck();
                setupMapButton();

                if(!isServicesOK()){
                    return;
                }

                Intent intent = getIntent();
                boolean data = intent.getBooleanExtra("data", false);
                if (data) {
                        try {
                                myRestaurants = RestaurantsManager.getInstance(null,null);
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        populateListView();
                        setUpRestaurantClick();
                        return;
                }


                DataLoad dataLoad = new DataLoad(this);
                isNetworkAvailable();
                if ((beenXHours(20)) && (isAvailable)) {
                        String restaurantURL = getResources().getString(R.string.restaurantURL);
                        DataRequest restaurantData = new DataRequest(restaurantURL);
                        String inspectionURL = getResources().getString(R.string.inspectionURL);
                        DataRequest inspectionData = new DataRequest(inspectionURL);

                        if (needsUpdate(restaurantData, inspectionData)) {
                                openDialog(restaurantData, inspectionData, dataLoad);
                                return;
                        } else {
                                dataLoad.loadData();
                        }
                } else {
                        dataLoad.loadData();
                }

                try {
                        myRestaurants = RestaurantsManager.getInstance(null,null);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                populateListView();
                setUpRestaurantClick();


        }



        private void init(){
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
        }

        private void isNetworkAvailable() {
                ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());

                if (capabilities != null) {
                        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                                isAvailable = true;
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                                isAvailable = true;
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                                isAvailable = true;
                        }
                        else{
                                isAvailable = false;
                        }
                }



        }


        public boolean isServicesOK(){ //check the user device.
                Log.d(TAG, "isServicesOK: checking google services version");
                int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(RestaurantListActivity.this);
                if(available == ConnectionResult.SUCCESS){
                        Log.d(TAG, "isServicesOK: Google Play Services is working");
                        return true;
                }
                else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
                        Log.d(TAG, "isServicesOK: an error occured but we can fix it");
                        Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(RestaurantListActivity.this, available, ERROR_DIALOG_REQUEST);
                }
                else{
                        Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
                }
                return false;
        }


        private void permissionCheck() {
                //Ask for permissions to download
                PermissionListener permissionlistener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {

                        }
                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(RestaurantListActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT)
                                        .show();
                                finish();
                        }

                };
                TedPermission.with(RestaurantListActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedTitle("Permission denied")
                        .setDeniedMessage(
                                "If you reject the permission,you can not use this application \n Please allow permissions at [Setting] > [Permission]")
                        .setGotoSettingButtonText("Go to Settings")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET)
                        .check();
        }

        private void setupMapButton() {
                ImageButton btn = findViewById(R.id.restlist_imgbtnMap);
                btn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                                Intent intent = new Intent(RestaurantListActivity.this, MapsActivity.class);
                                finish();
                                startActivity(intent);
                        }
                });
        }

        private boolean beenXHours(int hours) {
                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                long lastUpdated = sharedPreferences.getLong("lastUpdated", 0);
                java.util.Date currentDate = new java.util.Date();

                long timeDifference = currentDate.getTime() - lastUpdated;
                long hoursDifference = TimeUnit.HOURS.convert(timeDifference, TimeUnit.MILLISECONDS);

                return hoursDifference > hours;

        }

        private boolean needsUpdate(DataRequest restaurantData, DataRequest inspectionData) {
                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

                String restLastModified = sharedPreferences.getString("restaurantLastModified", "");
                if (!restLastModified.equals(restaurantData.getLastModified())) {
                        return true;
                }

                String inspectLastModified = sharedPreferences.getString("inspectionLastModified", "");
                if (!inspectLastModified.equals(inspectionData.getLastModified())) {
                        return true;
                }

                return false;
        }

        private void openDialog(DataRequest restaurantData, DataRequest inspectionData, DataLoad dataLoad) {
                FragmentManager manager = getSupportFragmentManager();
                UpdateDialog dialog = new UpdateDialog(this, restaurantData, inspectionData, dataLoad);
                dialog.show(manager, "Update");
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
//                        RestaurantIcon currentID = new RestaurantIcon(RestaurantListActivity.this,currentRestaurant.getRestaurantName());
                        currentRestaurant.setIconID(RestaurantListActivity.this, currentRestaurant.getRestaurantName());

                        TextView restaurantName = itemView.findViewById(R.id.listR_txtRestaurantName);
                        restaurantName.setText(currentRestaurant.getRestaurantName());

                        ImageView restaurantImage = itemView.findViewById(R.id.listR_imgRestaurantIcon);
                        restaurantImage.setImageResource(currentRestaurant.getIconID());

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
                                intent.putExtra("fromMap", false);
                                startActivity(intent);
                        }
                });
        }


}

