/**
 * Details of an inspection report from a restaurant.
 */
package com.example.restauranthealthinspector.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.Inspection;
import com.example.restauranthealthinspector.model.InspectionsManager;
import com.example.restauranthealthinspector.model.Restaurant;
import com.example.restauranthealthinspector.model.RestaurantsManager;
import com.example.restauranthealthinspector.model.Violation;
import com.example.restauranthealthinspector.model.ViolationManager;

import java.io.IOException;
import java.util.List;

public class InspectionActivity extends AppCompatActivity {
    private RestaurantsManager myRestaurants;
    private Inspection inspection;
    private List<Violation> violations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        try {
            myRestaurants = RestaurantsManager.getInstance(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadInspection();
        setupInspection();
        populateListView();
        setUpToastMessageOnclick();
    }

    private void loadInspection() {
        Intent intent = getIntent();
        int indexRestaurant = intent.getIntExtra("indexRestaurant", 0);
        int indexInspection = intent.getIntExtra("indexInspection", 0);
        Restaurant restaurant = myRestaurants.get(indexRestaurant);
        InspectionsManager inspectionsManager = restaurant.getInspectionsManager();
        inspection = inspectionsManager.get(indexInspection);
        ViolationManager violationManager = inspection.getViolationManager();
        violations = violationManager.getViolationList();
    }

    private void setupInspection() {
        TextView date = findViewById(R.id.inspect_txtDate);
        date.setText(inspection.getInspectionDate().getFullDate());

        TextView numOfCritical = findViewById(R.id.inspect_txtCriticalNum);
        numOfCritical.setText(Integer.toString(inspection.getNumCritical()));

        TextView numOfNonCritical = findViewById(R.id.inspect_txtNonCriticalNum);
        numOfNonCritical.setText(Integer.toString(inspection.getNumNonCritical()));

        ImageView hazardLevel = findViewById(R.id.inspect_imgHazard);

        if(inspection.getHazardRating().equals("Low")){
            hazardLevel.setImageResource(R.drawable.hazard_low);
        }
        else if(inspection.getHazardRating().equals("Moderate")){
            hazardLevel.setImageResource(R.drawable.hazard_moderate);
        }
        else {
            hazardLevel.setImageResource(R.drawable.hazard_high);
        }
    }

    private void populateListView(){
        ArrayAdapter<Violation> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.inspect_listViolations);

        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Violation> {
        public MyListAdapter(){
            super(InspectionActivity.this, R.layout.list_violations, violations);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listViolations = convertView;
            if (listViolations == null){
                listViolations = getLayoutInflater().inflate(R.layout.list_violations, parent,false);
            }

            //Find Violations to work with
            Violation currentViolation = violations.get(position);

            //Fill the description
            TextView briefDescription = listViolations.findViewById(R.id.listV_txtBriefDescription);
            briefDescription.setText(currentViolation.getBriefDescription(InspectionActivity.this));

            //Fill the severity
            TextView severity = listViolations.findViewById(R.id.listV_txtSeverity);
            severity.setText(currentViolation.getSeverity());

            //icon
            ImageView iconSelect = listViolations.findViewById(R.id.listV_imgViolation);
            iconSelect.setImageResource(currentViolation.getIconID(InspectionActivity.this));

            //icon for severity
            ImageView severityIcon = listViolations.findViewById(R.id.listV_imgSeverity);
            if(currentViolation.getSeverity().equals("Critical")){
                severityIcon.setImageResource(R.drawable.hazard_high);
            }
            else{
                severityIcon.setImageResource(R.drawable.hazard_low);
            }

            return listViolations;
        }
    }

    private void setUpToastMessageOnclick(){
        ListView list = findViewById(R.id.inspect_listViolations);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String message = violations.get(position).getLongDescription();

                Toast.makeText(InspectionActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}