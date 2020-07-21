package com.example.restauranthealthinspector.model;

import android.util.Log;

import java.util.ArrayList;

/**
 * An array list of violations.
 */
public class ViolationManager {
    private ArrayList<Violation> violationList = new ArrayList<>();

    public void convertVioLumpToViolations(String vioLump) {
        String[] vioLumpArray = vioLump.split("\\|");

        for (String violationString : vioLumpArray) {
            violationString = violationString.replaceAll("\"", "");
            String[] violationArray = violationString.split(",");
            //Log.i("violation", violationArray[0]);
            int violationID = Integer.parseInt(violationArray[0]);
            String severity = violationArray[1];
            String longDescription = violationArray[2];

            Violation violation = new Violation(violationID, longDescription, severity);
            violationList.add(violation);
        }

    }


    public ArrayList<Violation> getViolationList() {
        return violationList;
    }

}
