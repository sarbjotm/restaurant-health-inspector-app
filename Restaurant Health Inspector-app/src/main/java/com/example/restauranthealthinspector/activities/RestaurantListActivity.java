package com.example.restauranthealthinspector.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.restauranthealthinspector.model.SearchFilter;
import com.example.restauranthealthinspector.model.online.DataLoad;
import com.example.restauranthealthinspector.model.online.DataRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A list of restaurants with brief inspections report.
 */
public class RestaurantListActivity extends AppCompatActivity {
        private String favouriteDialogString = "";
        private RestaurantsManager myRestaurants;
        private FavouriteRestaurantManager myFavouriteRestaurants;
        private static final String TAG = "RestaurantListActivity";
        private static final int ERROR_DIALOG_REQUEST = 9001;
        private ArrayAdapter<Restaurant> adapter;
        private ArrayList<Restaurant> favouriteRestaurant;
        static ArrayList<String> favouriteRestaurantNames = new ArrayList<String>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_restaurant_list);
                loadDataFavourite();
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
                        setUpRestaurantImage();
                        return;
                }

                if (fromDialog) {
                        for(Restaurant r: favouriteRestaurant){
                                favouriteDialogString = favouriteDialogString + " " + getString(R.string.name) + " " + r.getRestaurantName();
                                if(r.getInspectionsManager().getInspectionList().size() == 0){
                                        favouriteDialogString = favouriteDialogString + "\n" + " " + getString(R.string.no_inspections_recorded) + "\n\n\n";
                                }
                                else{
                                        favouriteDialogString = favouriteDialogString + "\n" + " " + getString(R.string.date) + " " + r.getInspectionsManager().get(0).getInspectionDate().getFullDate();
                                        favouriteDialogString = favouriteDialogString + "\n " + " " + getString(R.string.hazard_level) + " " +  r.getInspectionsManager().get(0).getHazardRating();
                                        favouriteDialogString = favouriteDialogString + "\n\n\n ";
                                }


                        }
                        if(favouriteDialogString.equals("")){
                                favouriteDialogString = getString(R.string.no_favourite_restaurants);
                        }
                        openFavouriteDialog(favouriteDialogString);

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
                if(!fromDialog){
                        startMapActivity();
                }
        }


        private void openFavouriteDialog(String information){
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle(getString(R.string.favourite_restaurant_updates))
                                        .setCancelable(false)
                                        .setMessage(information)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                        startMapActivity();
                                                }
                                        });
                                builder.create().show();
                                builder.setCancelable(false);

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

        private void setUpSearch() {
                final SearchView searchView = findViewById(R.id.restlist_search);
                final TextView textView = findViewById(R.id.restlist_txtRestaurant);
                final SearchFilter searchFilter = SearchFilter.getInstance();

                String previousSearch = searchFilter.getSearch();
                if (!TextUtils.isEmpty(previousSearch)) {
                        searchView.onActionViewExpanded();
                        searchView.setQuery(previousSearch, true);
                        textView.setVisibility(View.INVISIBLE);
                        searchView.clearFocus();
                        Filter filter = adapter.getFilter();
                        filter.filter(previousSearch);
                }

                searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean isFocused) {
                                if (!isFocused && TextUtils.isEmpty(searchFilter.getSearch())) {
                                        textView.setVisibility(View.VISIBLE);
                                } else {
                                        textView.setVisibility(View.INVISIBLE);
                                }
                        }
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String text) {
                                Filter filter = adapter.getFilter();
                                if (TextUtils.isEmpty(text)) {
                                        filter.filter("");
                                } else {
                                        filter.filter(text);
                                }
                                searchView.clearFocus();
                                return true;
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
                });
        }

        private void setUpRestaurantImage() {
                for (Restaurant restaurant:myRestaurants) {
                        restaurant.setIconID(RestaurantListActivity.this);
                }
        }

        private class MyListAdapter extends ArrayAdapter<Restaurant> implements Filterable {
                private ArrayList<Restaurant> originalRestaurants;
                private ArrayList<Restaurant> restaurants;
                private SearchFilter searchFilter = SearchFilter.getInstance();

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
                                                searchFilter.setSearch(charSequence.toString());
                                                if (originalRestaurants != null && originalRestaurants.size() > 0) {
                                                        for (final Restaurant restaurant : originalRestaurants) {
                                                                if (searchFilter.inFilter(restaurant)) {
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
                        loadDataFavourite();
                        View itemView = convertView;
                        if (itemView == null){
                                itemView = getLayoutInflater().inflate(R.layout.list_restaurants, parent, false);
                        }

                        Restaurant currentRestaurant = restaurants.get(position);

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
                        


                        if ((favouriteRestaurantNames.contains(currentRestaurant.getTrackingNumber()))){
                                if(!(myFavouriteRestaurants.getFavouriteList().contains(currentRestaurant))){
                                        myFavouriteRestaurants.getFavouriteList().add(currentRestaurant);
                                }

                                currentRestaurant.setFavourite(true);
                                restaurantName.setTextColor(Color.parseColor("#FFFF00"));
                                restaurantImageFav.setVisibility(View.VISIBLE);
                                saveData();

                                

                        }

                        else{
                                try{
                                        favouriteRestaurantNames.remove(currentRestaurant.getTrackingNumber());
                                        saveData();


                                }catch(Exception e){
                                        Log.e("Exception", "Found Exception");
                                }

                                try{
                                        myFavouriteRestaurants.getFavouriteList().remove(currentRestaurant);
                                        saveData();



                                }catch(Exception e){
                                        Log.e("Exception", "Found Exception");
                                }
                                restaurantName.setTextColor(Color.parseColor("#FFFFFF"));
                                restaurantImageFav.setVisibility(View.INVISIBLE);


                        }

                        return itemView;
                }

                @Override
                public int getCount() {
                        if (restaurants == null ) {
                                return 0;
                        }
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
                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedPreferences.getString("task list", null);
                Type type = new TypeToken<ArrayList<Restaurant>>() {}.getType();
                favouriteRestaurant = gson.fromJson(json, type);
                if (favouriteRestaurant == null) {
                        favouriteRestaurant = new ArrayList<Restaurant>();
                }

                else{

                        for(int i = 0; i < favouriteRestaurant.size(); i++){

                                try {
                                        if(!favouriteRestaurantNames.contains(favouriteRestaurant.get(i).getTrackingNumber()))
                                                favouriteRestaurantNames.add(favouriteRestaurant.get(i).getTrackingNumber());

                                }

                                catch(Exception e){
                                        Log.e("Exception", "Found Exception");
                                }

                                favouriteRestaurant.get(i).setFavourite(true);

                        }
                }


                }



        private void hazard(View itemView, Inspection inspection) {
                String hazardRating = inspection.getHazardRating();

                TextView restaurantHazardLevel = itemView.findViewById(R.id.listR_txtHazardLevel);


                ImageView restaurantHazardImage = itemView.findViewById(R.id.listR_imgHazard);
                restaurantHazardImage.setVisibility(View.VISIBLE);

                if(hazardRating.equals("Low")){
                        restaurantHazardImage.setImageResource(R.drawable.hazard_low);
                        restaurantHazardLevel.setText(getString(R.string.low));
                        restaurantHazardLevel.setTextColor(Color.parseColor("#82F965"));
                }
                else if(hazardRating.equals("Moderate")){
                        restaurantHazardImage.setImageResource(R.drawable.hazard_moderate);
                        restaurantHazardLevel.setText(getString(R.string.moderate));
                        restaurantHazardLevel.setTextColor(Color.parseColor("#F08D47"));
                }
                else{
                        restaurantHazardImage.setImageResource(R.drawable.hazard_high);
                        restaurantHazardLevel.setText(getString(R.string.high));
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
                                intent.putExtra("restaurantName", adapter.getItem(position).getRestaurantName());
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
                loadDataFavourite();


        }




}

