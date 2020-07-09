package com.example.restauranthealthinspector.model;

import android.content.Context;

import com.example.restauranthealthinspector.R;

/**
 * ViolationIcon to store different icons for each violation.
 */
public class ViolationIcon {
    private int violationCode;
    private int iconID;
    private int[] savedIcon = {R.drawable.icon_violation_permit,
            R.drawable.icon_violation_cook,
            R.drawable.icon_violation_thermometer,
            R.drawable.icon_violation_food,
            R.drawable.icon_violation_equipment,
            R.drawable.icon_violation_pest,
            R.drawable.icon_violation_hygiene,
            R.drawable.icon_violation_animal,
            R.drawable.icon_violation_foodsafe};

    public ViolationIcon(Context context, int violationCode) {
        this.violationCode = violationCode;
        generateIconID(context);
    }

    public int getIconID() {
        return iconID;
    }

    public void generateIconID(Context context){

        if((violationCode >= 101 && violationCode <= 104)  || violationCode == 311 || violationCode == 312){
            iconID = savedIcon[0];
        }

        else if(violationCode >= 201 && violationCode <=204){
            iconID = savedIcon[1];
        }

        else if(violationCode == 205 || violationCode == 206 || violationCode == 211 || violationCode == 315){
            iconID = savedIcon[2];
        }

        else if((violationCode >= 208 && violationCode <= 210) || violationCode == 212){
            iconID = savedIcon[3];
        }

        else if (violationCode >= 301 && violationCode <= 303){
            iconID = savedIcon[4];
        }

        else if (violationCode == 304 || violationCode == 305){
            iconID = savedIcon[5];
        }

        else if((violationCode >= 306 && violationCode <= 310) || violationCode == 314 || (violationCode >= 401 && violationCode <= 404)){
            iconID = savedIcon[6];
        }

        else if(violationCode == 313){
            iconID = savedIcon[7];
        }

        else{
            iconID = savedIcon[8];
        }
    }
}
