package com.example.restauranthealthinspector.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.restauranthealthinspector.model.map.ClusterPin;
import com.example.restauranthealthinspector.model.map.CustomClusterRenderer;
import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

/**
 *  Map Activity displays the maps with restaurant pegs
 *  It stores the necessary things to get user location and displays these pegs
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private boolean resume = false;

    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private RestaurantsManager myRestaurants;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ClusterManager<ClusterPin> mClusterManger;
    private EditText mSearchText;
    private String userKeyboardInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mSearchText = (EditText) findViewById(R.id.input_search);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getLocationPermission();
        setupListButton();
        try {
            myRestaurants = RestaurantsManager.getInstance(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            Intent intent = getIntent();
            Boolean fromGPS = intent.getBooleanExtra("fromGPS", false);
            if (!fromGPS){
                getDeviceLocation();
            }
            else{
                double lat = intent.getDoubleExtra("latitude", 0);
                double lng = intent.getDoubleExtra("longitude", 0);
                LatLng latLng = new LatLng(lat, lng);
                moveCamera(latLng, DEFAULT_ZOOM);
            }
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            pinRestaurants();
            getUserInput();
        }
    }

    private void getUserInput(){
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == event.ACTION_DOWN
                        || event.getAction() == event.KEYCODE_ENTER){
                    //execute our method for searching
                    userKeyboardInput = String.valueOf(mSearchText.getText());
                    showText(userKeyboardInput);
                    pinRestaurants();
                }
                return false;
            }
        });
        return;
    }

    private void pinRestaurants(){
        mMap.clear();
        initClusterManager();

        for (Restaurant restaurant : myRestaurants) {
            int type;
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
            double lat = restaurant.getAddress().getLatitude();
            double lng = restaurant.getAddress().getLongitude();
            final LatLng latLng = new LatLng(lat, lng);
            String name = restaurant.getRestaurantName();
            String address = restaurant.getAddress().getStreetAddress();
            ArrayList<Inspection> inspections = restaurant.getInspectionsManager().getInspectionList();
            String hazardLevel = "No inspections recorded";
            if (inspections.size() != 0) {
                Inspection inspection = inspections.get(0);
                hazardLevel = inspection.getHazardRating();
            }
            String hazardMsg = getString(R.string.hazard_level);

            String snippet = name + "\n" + address + "\n" + hazardMsg + ": " + hazardLevel;
            float colour;
            if (hazardLevel.equals("High")) {
                colour = HUE_RED;
                type = 1;
            } else if (hazardLevel.equals("Moderate")) {
                colour = HUE_ORANGE;
                type = 2;
            } else {
                colour = HUE_GREEN;
                type = 3;
            }

            MarkerOptions options = new MarkerOptions().position(latLng).title(name).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(colour));
            Marker mMarker = mMap.addMarker(options
                .title(name)
                .snippet(address + "\n" + hazardLevel));
            mMarker.setVisible(false);

            if (userKeyboardInput.equals("")){
                mClusterManger.addItem(new ClusterPin(name, snippet, latLng, type));
            }

            else if (name.toLowerCase().indexOf(userKeyboardInput.toLowerCase()) != -1) {
                mClusterManger.addItem(new ClusterPin(name, snippet, latLng, type));
            }
        }
        mClusterManger.cluster();
    }

    public void initClusterManager(){
        mClusterManger = new ClusterManager<ClusterPin>(this,mMap);

        CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, mClusterManger);
        mClusterManger.setRenderer(renderer);

        mClusterManger.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterPin>() {
            @Override
            public boolean onClusterClick(Cluster<ClusterPin> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),100.0f));
                //Toast.makeText(MapsActivity.this, "Cluster click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mClusterManger.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterPin>() {
            @Override
            public boolean onClusterItemClick(ClusterPin clusterItem) {
                //Toast.makeText(MapsActivity.this, "Cluster item click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mClusterManger.getMarkerCollection().setInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(MapsActivity.this)));

        mClusterManger.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterPin>() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterPin clusterItem) {
                //Toast.makeText(MapsActivity.this, "Clicked info window: " + clusterItem.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapsActivity.this, RestaurantActivity.class);
                intent.putExtra("nameRestaurant", clusterItem.getTitle());
                intent.putExtra("fromMap", true);
                startActivity(intent);
            }
        });

        mMap.setOnCameraIdleListener(mClusterManger);
        mMap.setOnMarkerClickListener(mClusterManger);
        mMap.setInfoWindowAdapter(mClusterManger.getMarkerManager());
        mMap.setOnInfoWindowClickListener(mClusterManger);
    }

    private void showText(String userKeyboardInput){
        Toast.makeText(this, userKeyboardInput, Toast.LENGTH_SHORT).show();
    }

    private void setupListButton() {
        ImageButton btn = findViewById(R.id.map_imgbtnList);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, RestaurantListActivity.class);
                intent.putExtra("data", true);
                finish();
                startActivity(intent);
            }
        });
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            double lat = currentLocation.getLatitude();
                            double lng = currentLocation.getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            moveCamera(latLng, DEFAULT_ZOOM);
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, getString(R.string.no_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + "lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //get the location name from latitude and longitude
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addresses =
                                geocoder.getFromLocation(latitude, longitude, 1);
                        LatLng latLng = new LatLng(latitude, longitude);

                        mMap.setMaxZoomPreference(1);

                        if(resume == false){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                            resume = true;
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //locationManager.removeUpdates(locationListener);
    }
}