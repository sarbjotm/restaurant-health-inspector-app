package com.example.restauranthealthinspector.model;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.util.concurrent.TimeUnit;

/**
 * Date of inspection reports with attributes day, month and year.
 */
public class Date {
    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getNumberDate() {
        String dateString = String.valueOf(year);
        String dayString;
        String monthString;

        if (day >= 10) {
            dayString = String.valueOf(day);
        } else {
            dayString = "0" + day;
        }

        if (month >= 10) {
            monthString = String.valueOf(month);
        } else {
            monthString = "0" + month;
        }

        dateString += monthString + dayString;
        return Integer.parseInt(dateString);
    }

    public String getSmartDate() throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        java.util.Date currentDate = new java.util.Date();
        String dateString = String.valueOf(getNumberDate());
        java.util.Date date = dateFormat.parse(dateString);

        long timeDifference = currentDate.getTime() - date.getTime();
        long daysDifference = TimeUnit.DAYS.convert(timeDifference, TimeUnit.MILLISECONDS);

        if (daysDifference <= 30) {
            return daysDifference + " days";
        } else if (daysDifference < 365) {
            return getStringMonth() + " " + day;
        } else {
            return getStringMonth() + " " + year;
        }
    }

    private String getStringMonth() {
        return new DateFormatSymbols().getMonths()[month-1];
    }

    public String getFullDate() {
        return getStringMonth() + " " + day + ", " + year;
    }
}
