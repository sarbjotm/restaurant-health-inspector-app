package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Date;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;
import com.example.restauranthealthinspector.model.FavouriteRestaurantManager;
import com.example.restauranthealthinspector.model.online.DataLoad;
import com.example.restauranthealthinspector.model.online.DataRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A list of restaurants with brief inspections report.
 */
public class RestaurantListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
        private RestaurantsManager myRestaurants;
        private FavouriteRestaurantManager myFavouriteRestaurants;
//        private static ArrayList<Restaurant> favouriteRestaurantListActivity = new ArrayList<>();
        private static final String TAG = "RestaurantListActivity";
        private static final int ERROR_DIALOG_REQUEST = 9001;
        private ArrayAdapter<Restaurant> adapter;
        ArrayList<Restaurant> favouriteRestaurant;
        ArrayList<String> favouriteRestaurantNames = new ArrayList<String>();



        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_restaurant_list);

                permissionCheck();
                setupMapButton();

                Intent intent = getIntent();
                boolean data = intent.getBooleanExtra("data", false);
                boolean fromDialog = intent.getBooleanExtra("fromDialog", false);



                if (data) {
                        try {
                                myRestaurants = RestaurantsManager.getInstance(null,null);
                                myFavouriteRestaurants = FavouriteRestaurantManager.getInstance();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        populateListView();
                        setUpRestaurantClick();
                        setUpSearch();
                        return;
                }

                if (fromDialog) {
                        startMapActivity();
                }

                DataLoad dataLoad = new DataLoad(this);

                if ((beenXHours(20)) && isNetworkAvailable()) {
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

                startMapActivity();
        }

        // Code refer from stack overflow
        // https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
        private void startMapActivity(){
                LocationManager lm = (LocationManager)RestaurantListActivity.this.getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}


                if(!gps_enabled && !network_enabled) {
                        String locationText = getString(R.string.turn_on_location);
                        Toast.makeText(RestaurantListActivity.this, locationText, Toast.LENGTH_SHORT).show();
                }
                else {
                        Intent intent = new Intent(RestaurantListActivity.this, MapsActivity.class);
                        finish();
                        startActivity(intent);
                }
        }

        private boolean isNetworkAvailable() {
                ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());

                if (capabilities != null) {
                        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                                return true;
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                                return true;
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                                return true;
                        }
                        else{
                                return false;
                        }
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
                                String permissionText = getString(R.string.permission_denied);
                                Toast.makeText(RestaurantListActivity.this, permissionText + "\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT)
                                        .show();
                                finish();
                        }

                };
                TedPermission.with(RestaurantListActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedTitle(getString(R.string.permission_denied))
                        .setDeniedMessage(getString(R.string.denied_message))
                        .setGotoSettingButtonText(R.string.go_to_setting)
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET)
                        .check();
        }

        private void setupMapButton() {
                ImageButton btn = findViewById(R.id.restlist_imgbtnMap);
                btn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                                startMapActivity();
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
                adapter = new MyListAdapter();
                ListView list = findViewById(R.id.restlist_listRestaurants);
                list.setAdapter(adapter);
                list.setTextFilterEnabled(true);
        }

        private void  setUpSearch() {
                SearchView searchView = findViewById(R.id.restlist_search);
                //searchView.setIconifiedByDefault(false);
                searchView.setOnQueryTextListener(this);
                searchView.setSubmitButtonEnabled(true);
                searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean isFocused) {
                                TextView textView = findViewById(R.id.restlist_txtRestaurant);
                                if (isFocused) {
                                        textView.setVisibility(View.INVISIBLE);
                                } else {
                                        textView.setVisibility(View.VISIBLE);
                                }
                        }
                });
        }

        @Override
        public boolean onQueryTextChange(String text) {
                Filter filter = adapter.getFilter();
                if (TextUtils.isEmpty(text)) {
                        filter.filter("");
                } else {
                        filter.filter(text);
                }
                return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
                return false;
        }

        private class MyListAdapter extends ArrayAdapter<Restaurant> implements Filterable {
                private ArrayList<Restaurant> originalRestaurants;
                private ArrayList<Restaurant> restaurants;

                public MyListAdapter(){
                        super(RestaurantListActivity.this, R.layout.list_restaurants, myRestaurants.getRestaurants());
                        this.restaurants = myRestaurants.getRestaurants();
                }

                // Refer from StackOverflow
                // https://stackoverflow.com/questions/23422072/searchview-in-listview-having-a-custom-adapter
                public Filter getFilter() {
                        return new Filter() {

                                @Override
                                protected FilterResults performFiltering(CharSequence charSequence) {
                                        final FilterResults filterResults = new FilterResults();
                                        final ArrayList<Restaurant> results = new ArrayList<>();
                                        if (originalRestaurants == null) {
                                                originalRestaurants = restaurants;
                                        }

                                        if (charSequence != null) {
                                                if (originalRestaurants != null && originalRestaurants.size() > 0) {
                                                        for (final Restaurant restaurant : originalRestaurants) {
                                                                if (restaurant.getRestaurantName().toLowerCase().contains(charSequence.toString())) {
                                                                        results.add(restaurant);
                                                                }
                                                        }
                                                }
                                                filterResults.values = results;
                                        }
                                        return filterResults;
                                }

                                @SuppressWarnings("unchecked")
                                @Override
                                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                                       restaurants = (ArrayList<Restaurant>) filterResults.values;
                                       notifyDataSetChanged();
                                }
                        };
                }

                public void notifyDataSetChanged() {
                        super.notifyDataSetChanged();
                }

                @SuppressLint("SetTextI18n")
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        View itemView = convertView;
                        if (itemView == null){
                                itemView = getLayoutInflater().inflate(R.layout.list_restaurants, parent, false);
                        }

                        Restaurant currentRestaurant = restaurants.get(position);
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

                        ImageView restaurantImageFav = itemView.findViewById(R.id.listR_imgRestaurantFavourite);

//                        restFav.setVisibility(View.INVISIBLE);

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
                                String noInspection = getString(R.string.no_inspections_recorded);
                                inspection.setText(noInspection);

                        }


                                loadDataFavourite();
                                saveData();


                        if ((currentRestaurant.getFavourite())|| (myFavouriteRestaurants.getFavouriteList().contains(currentRestaurant))){
                                currentRestaurant.setFavourite(true);
                                restaurantName.setTextColor(Color.parseColor("#FFFF00"));
                                restaurantImageFav.setVisibility(View.VISIBLE);
                        }

                        else if(favouriteRestaurantNames.contains(currentRestaurant.getRestaurantName())){
                                currentRestaurant.setFavourite(true);
                                restaurantName.setTextColor(Color.parseColor("#FFFF00"));
                                restaurantImageFav.setVisibility(View.VISIBLE);
                        }

                        else{
                                restaurantName.setTextColor(Color.parseColor("#FFFFFF"));
                                restaurantImageFav.setVisibility(View.INVISIBLE);

                        }

                        return itemView;
                }

                @Override
                public int getCount() {
                      return restaurants.size();
                }

                @Override
                public Restaurant getItem(int position) {
                        return restaurants.get(position);
                }

                @Override
                public long getItemId(int position) {
                        return position;
                }
        }

        private void saveData() {
                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(myFavouriteRestaurants.getFavouriteList());
                editor.putString("task list", json);
                editor.apply();
        }


                private void loadDataFavourite() {
                Log.e("YES","YESINLOAD");
                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedPreferences.getString("task list", null);
                Type type = new TypeToken<ArrayList<Restaurant>>() {}.getType();
                favouriteRestaurant = gson.fromJson(json, type);
                if (favouriteRestaurant == null) {
                        Log.e("YES","YESINNULL");
                        favouriteRestaurant = new ArrayList<Restaurant>();
                }

                else{
//                        Log.e("YES","YESINELSE");

                        for(int i = 0; i < favouriteRestaurant.size(); i++){

                                try {
//                                        Log.e("YES","ADDING");
                                        if(!favouriteRestaurantNames.contains(favouriteRestaurant.get(i).getRestaurantName()))
                                                favouriteRestaurantNames.add(favouriteRestaurant.get(i).getRestaurantName());
//                                        Log.e("YES", "SizeofR" + Integer.toString(favouriteRestaurant.size()));

                                }

                                catch(Exception e){
                                        Log.e("Nothing", "nothing found");
                                }
                        }
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
                                intent.putExtra("nameRestaurant", adapter.getItem(position).getRestaurantName());
                                intent.putExtra("fromMap", false);
                                startActivity(intent);
                        }
                });
        }

        @Override
        public void onRestart()
        {
                super.onRestart();
                finish();
                startActivity(getIntent());


        }




}

