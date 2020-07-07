package com.example.restauranthealthinspector.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Restaurant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
        private List<Restaurant> myRestaurant = new ArrayList<Restaurant>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                populateRestaurantList();
                populateListView();

        }

        private void populateRestaurantList() {
//                File file = new File("./src/main/res/raw/restaurants_itr1.csv");
//                Scanner scan = new Scanner(file);
//                String line;
//                double latitude;
//                double longitude;
//                scan.nextLine();
//
//                while (scan.hasNextLine()){
//                        line = scan.nextLine();
//                        line = line.replaceAll("\"", "");
//                        String[] lineArray = line.split(",");
//                        latitude = Double.parseDouble(lineArray[5]);
//                        longitude = Double.parseDouble(lineArray[6]);
//                        myRestaurant.add(new Restaurant(lineArray[0], lineArray[1], lineArray[2], lineArray[3], lineArray[4], latitude, longitude));
//                }
//                scan.close();

        myRestaurant.add(new Restaurant("123", "Walmart", "38277", "Squamish", "2", 3, 3));

        }

        private void populateListView() {
                ArrayAdapter<Restaurant> adapter = new MyListAdapter();
                ListView list = (ListView) findViewById(R.id.restaurantView);
                list.setAdapter(adapter);
        }

        private class MyListAdapter extends ArrayAdapter<Restaurant>{
                public MyListAdapter(){
                        super(MainActivity.this, R.layout.restaurant_view, myRestaurant);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        View itemView = convertView;
                        if (itemView == null){
                                itemView = getLayoutInflater().inflate(R.layout.restaurant_view, parent, false);
                        }

                        Restaurant currentRestaurant = myRestaurant.get(position);
                        TextView restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
                        restaurantName.setText(currentRestaurant.getRestaurantName());

                        return itemView;
                }
        }



}

