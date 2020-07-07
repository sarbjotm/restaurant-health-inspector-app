package com.example.restauranthealthinspector.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An inspections class. Can implement enum classes for Inspection Type, Hazard Rating and Violation lLump.
 */
public class Inspection {
    private Date inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private ViolationManager violationManager;

    public Inspection(Date inspectionDate, String inspectionType, int numCritical, int numNonCritical, String hazardRating, ViolationManager violationManager) {
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violationManager = violationManager;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }


    public String getInspectionType() {
        return inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public ViolationManager getViolationManager() {
        return violationManager;
    }

    @Override
    public String toString() {
        return "Inspection{" +
                ", inspectionDate=" + inspectionDate +
                ", inspectionType='" + inspectionType + '\'' +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazardRating='" + hazardRating + '\'' +
                ", violationManager='" + violationManager + '\'' +
                '}';
    }
}
