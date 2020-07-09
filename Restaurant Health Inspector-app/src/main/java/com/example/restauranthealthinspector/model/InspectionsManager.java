package com.example.restauranthealthinspector.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * An array list to store a list of inspection reports.
 */
public class InspectionsManager implements Iterable<Inspection> {
    private ArrayList<Inspection> inspectionList = new ArrayList<>();

    public void addFromLineArray(String[] lineArray) {
        Date date = intToDate(lineArray[1]);
        int numCritical = Integer.parseInt(lineArray[3]);
        int numNonCritical = Integer.parseInt(lineArray[4]);
        ViolationManager violationManager = vioLumpToViolationManager(lineArray);
        Inspection inspection = new Inspection(date, lineArray[2], numCritical, numNonCritical, lineArray[5], violationManager);
        inspectionList.add(inspection);
    }

    private Date intToDate(String inspectionDate) {
        int day = Integer.parseInt(inspectionDate.substring(6, 8));
        int month = Integer.parseInt(inspectionDate.substring(4, 6));
        int year = Integer.parseInt(inspectionDate.substring(0, 4));
        return new Date(day, month, year);
    }

    private ViolationManager vioLumpToViolationManager(String[] lineArray) {
        ViolationManager violationManager = new ViolationManager();

        if (lineArray.length != 6) {
            StringBuilder vioLump = new StringBuilder(lineArray[6]);
            for (int i = 7; i < lineArray.length; i++) {
                vioLump.append(",").append(lineArray[i]);
            }
            violationManager.convertVioLumpToViolations(vioLump.toString());
        }

        return violationManager;
    }

    public void sortInspections() {
        Collections.sort(inspectionList, new Comparator<Inspection>() {
            @Override
            public int compare(Inspection i1, Inspection i2) {
                return i2.getInspectionDate().getNumberDate() - i1.getInspectionDate().getNumberDate();
            }
        });
    }

    public Inspection get(int i){
        return inspectionList.get(i);
    }

    public ArrayList<Inspection> getInspectionList() {
        return inspectionList;
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspectionList.iterator();
    }
}
