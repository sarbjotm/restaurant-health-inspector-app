package com.example.restauranthealthinspector.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

/**
 * Details of an inspection report from a restaurant.
 */
public class InspectionActivity extends AppCompatActivity {
    private RestaurantsManager myRestaurants;
    private Inspection inspection;
    private List<Violation> violations;
    private int indexRestaurant;

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

        setUpBackButton();
        noViolationsMessage();
    }

    private void loadInspection() {
        Intent intent = getIntent();
        indexRestaurant = intent.getIntExtra("indexRestaurant", 0);
        int indexInspection = intent.getIntExtra("indexInspection", 0);
        Restaurant restaurant = myRestaurants.get(indexRestaurant);
        InspectionsManager inspectionsManager = restaurant.getInspectionsManager();
        inspection = inspectionsManager.get(indexInspection);
        ViolationManager violationManager = inspection.getViolationManager();
        violations = violationManager.getViolationList();
    }

    @SuppressLint("SetTextI18n")
    private void setupInspection() {
        TextView date = findViewById(R.id.inspect_txtDate);
        date.setText(inspection.getInspectionDate().getFullDate());

        TextView type = findViewById(R.id.inspect_txtType);
        type.setText(inspection.getInspectionType());

        TextView numOfCritical = findViewById(R.id.inspect_txtCriticalNum);
        numOfCritical.setText(Integer.toString(inspection.getNumCritical()));

        TextView numOfNonCritical = findViewById(R.id.inspect_txtNonCriticalNum);
        numOfNonCritical.setText(Integer.toString(inspection.getNumNonCritical()));

        TextView hazard = findViewById(R.id.inspect_txtHazard);
        String hazardString = inspection.getHazardRating();
        hazard.setText(hazardString);

        ImageView hazardLevel = findViewById(R.id.inspect_imgHazard);

        if(hazardString.equals("Low")){
            hazardLevel.setImageResource(R.drawable.hazard_low);
            hazard.setTextColor(Color.parseColor("#82F965"));
        }
        else if(hazardString.equals("Moderate")){
            hazardLevel.setImageResource(R.drawable.hazard_moderate);
            hazard.setTextColor(Color.parseColor("#F08D47"));
        }
        else {
            hazardLevel.setImageResource(R.drawable.hazard_high);
            hazard.setTextColor(Color.parseColor("#EC4A26"));
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
            String violationID = String.valueOf(currentViolation.getViolationID());
            briefDescription.setText(violationID);

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

    private void setUpBackButton() {
        ImageButton btn = findViewById(R.id.inspect_imgbtnBack);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InspectionActivity.this, RestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("indexRestaurant", indexRestaurant);
                startActivity(intent);
            }
        });
    }

    private void noViolationsMessage() {
        if (inspection.getViolationManager().getViolationList().size() != 0) {
            TextView textView = findViewById(R.id.inspect_txtNoViolations);
            textView.setVisibility(View.INVISIBLE);
        }
    }
}