/**
 * An array list of violations.
 */
package com.example.restauranthealthinspector.model;

import java.util.ArrayList;

public class ViolationManager {
    private ArrayList<Violation> violationList = new ArrayList<>();

    public void convertVioLumpToViolations(String vioLump) {
        String[] vioLumpArray = vioLump.split("\\|");

        for (String violationString : vioLumpArray) {
            String[] violationArray = violationString.split(",");
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
    @Override
    public String toString() {
        return "ViolationManager{" +
                "violationList=" + violationList +
                '}';
    }
}
