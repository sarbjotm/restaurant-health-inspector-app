package com.example.restauranthealthinspector.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Date {
    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Date{" +
                "day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                '}';
    }

    public String showDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String currentDate = dtf.format(now);

        int currentDay = Integer.parseInt(currentDate.substring(8, 10));
        int day = this.day;
        int currentMonth = Integer.parseInt(currentDate.substring(5, 7));
        int month = this.month;
        int currentYear = Integer.parseInt(currentDate.substring(0, 4));
        int year = this.year;
        String realMonth = stringToMonth(Integer.toString(this.month));
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
