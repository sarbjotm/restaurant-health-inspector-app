package com.example.restauranthealthinspector.model;

public class Violation {
    int violationID;
    String longDescription;
    String severity;
    int briefDescriptionID;
    int iconID;

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

    public int getBriefDescriptionID() {
        return briefDescriptionID;
    }

    public void setBriefDescriptionID(int briefDescriptionID) {
        this.briefDescriptionID = briefDescriptionID;
    }

    public int getIconID() {
        return iconID;
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
