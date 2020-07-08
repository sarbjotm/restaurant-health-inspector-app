/**
 * A class to store a violations's code, descriptions, severity and an icon.
 */
package com.example.restauranthealthinspector.model;

import android.content.Context;

public class Violation {
    private int violationID;
    private String longDescription;
    private String severity;
    private String briefDescription;
    private int iconID;

    public Violation(int violationID, String longDescription, String severity) {
        this.violationID = violationID;
        this.longDescription = longDescription;
        this.severity = severity;
    }

    public int getViolationID() {
        return violationID;
    }

    public void setViolationID(int violationID) {
        this.violationID = violationID;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getBriefDescription(Context context) {
        BriefDescription message = new BriefDescription(violationID);

        return message.getBriefDescription(context);
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public int getIconID(Context context) {

        IconID currentID = new IconID(violationID);

        return currentID.getIconID(context);
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "violationID=" + violationID +
                ", longDescription='" + longDescription + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}
