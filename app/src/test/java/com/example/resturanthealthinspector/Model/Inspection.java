package com.example.resturanthealthinspector.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An inspections class. Can implement enum classes for Inspection Type, Hazard Rating and Violation lLump.
 */
public class Inspection {
    private String trackingNumber;
    private Date inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private ViolationManager violationManager;

    public Inspection(String trackingNumber, Date inspectionDate, String inspectionType, int numCritical, int numNonCritical, String hazardRating, String violLump) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violationManager = violationManager;
    }

    public String getTrackingNumber() {
        return trackingNumber;
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
                "trackingNumber='" + trackingNumber + '\'' +
                ", inspectionDate=" + inspectionDate +
                ", inspectionType='" + inspectionType + '\'' +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazardRating='" + hazardRating + '\'' +
                ", violationManager='" + violationManager + '\'' +
                '}';
    }

    public String showDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String currentDate = dtf.format(now);
        System.out.println(dtf.format(now));
        int currentDay = Integer.parseInt(currentDate.substring(8, 10));
        int day = Integer.parseInt(this.inspectionDate.getDay());
        int currentMonth = Integer.parseInt(currentDate.substring(5, 7));
        int month = Integer.parseInt(this.inspectionDate.getMonth());
        int currentYear = Integer.parseInt(currentDate.substring(0, 4));
        int year = Integer.parseInt(this.inspectionDate.getYear());
        String realMonth = stringToMonth(this.inspectionDate.getMonth());
        String str;

        if (currentYear == year){
            if (currentMonth - month == 1){
                if (currentDay < day){
                    str = (currentDay + 30 - day + " days");
                }
                else{
                    str = (realMonth + " " + day);
                }
            }
            else if(currentMonth == month){
                str = (currentDay - day + " days");
            }
            else{
                str = (realMonth + " " + day);
            }
        }
        else if (currentYear - year == 1){
            if ((currentMonth == 01) && (month == 12)){
                if(currentDay < day){
                    str = (currentDay + 30 - day + " days");
                }
                else{
                    str = (realMonth + " " + day);
                }
            }
            else if(currentMonth < month){
                str = (realMonth + " " + day);
            }
            else{
                str = (realMonth + " " + year);
            }
        }
        else{
            str = (realMonth + " " + year);
        }
        return str;
    }

    private String stringToMonth(String month) {
        switch(month) {
            case "01":
                month = "January";
                break;
            case "02":
                month = "February";
                break;
            case "03":
                month = "March";
                break;
            case "04":
                month = "April";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "August";
                break;
            case "09":
                month = "September";
                break;
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
                break;
            case "12":
                month = "December";
                break;
            default:
                month = "Unknown";
        }
        return month;
    }
}
