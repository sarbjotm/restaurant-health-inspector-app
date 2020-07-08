/**
 * Icon IDs to store different icons for each violation.
 */
package com.example.restauranthealthinspector.model;

import android.content.Context;

import com.example.restauranthealthinspector.R;

public class IconID {
    private int code;
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

    public IconID(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIconID(Context context){

        if((code >= 101 && code <= 104)  || code == 311 || code == 312){
            iconID = savedIcon[0];
        }

        else if(code >= 201 && code <=204){
            iconID = savedIcon[1];
        }

        else if(code == 205 || code == 206 || code == 211 || code == 315){
            iconID = savedIcon[2];
        }

        else if((code >= 208 && code <= 210) || code == 212){
            iconID = savedIcon[3];
        }

        else if (code >= 301 && code <= 303){
            iconID = savedIcon[4];
        }

        else if (code == 304 || code == 305){
            iconID = savedIcon[5];
        }

        else if((code >= 306 && code <= 310) || code == 314 || (code >= 401 && code <= 404)){
            iconID = savedIcon[6];
        }

        else if(code == 313){
            iconID = savedIcon[7];
        }

        else{
            iconID = savedIcon[8];
        }

        return iconID;
    }
}
