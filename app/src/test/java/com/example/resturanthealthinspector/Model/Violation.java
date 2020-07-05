package com.example.resturanthealthinspector.Model;

public class Violation {
    String briefDescription;
    String longDescription;
    String severity;
    int iconID;

    public Violation(String briefDescription, String longDescription, String severity, int iconID) {
        this.briefDescription = briefDescription;
        this.longDescription = longDescription;
        this.severity = severity;
        this.iconID = iconID;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getSeverity() {
        return severity;
    }

    public int getIconID() {
        return iconID;
    }
}
