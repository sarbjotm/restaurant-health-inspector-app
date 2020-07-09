package com.example.restauranthealthinspector.model;

import android.content.Context;

/**
 * A class to store a violations's code, descriptions, severity and an icon.
 */
public class Violation {
    private int violationID;
    private String longDescription;
    private String severity;

    public Violation(int violationID, String longDescription, String severity) {
        this.violationID = violationID;
        this.longDescription = longDescription;
        this.severity = severity;
    }

    public int getViolationID() {
        return violationID;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getSeverity() {
        return severity;
    }

    public int getIconID(Context context) {

        ViolationIcon currentID = new ViolationIcon(context, violationID);

        return currentID.getIconID();
    }

}
