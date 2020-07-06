package com.example.restauranthealthinspector.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A manager to store inspections.
 */
public class InspectionsManager implements Iterable<Inspection> {
    private ArrayList<Inspection> inspectionList = new ArrayList<>();

    public void addFromLineArray(String[] lineArray) {
        Date date = intToDate(lineArray[1]);
        int numCritical = Integer.parseInt(lineArray[3]);
        int numNonCritical = Integer.parseInt(lineArray[4]);
        ViolationManager violationManager = vioLumpToViolationManager(lineArray);
        Inspection inspection = new Inspection(lineArray[0], date, lineArray[2], numCritical, numNonCritical, lineArray[5], violationManager);
        inspectionList.add(inspection);
    }

    private Date intToDate(String inspectionDate) {
        int day = Integer.parseInt(inspectionDate.substring(6, 8));
        int month = Integer.parseInt(inspectionDate.substring(4, 6));
        int year = Integer.parseInt(inspectionDate.substring(0, 4));
        return new Date(day, month, year);
    }

    private ViolationManager vioLumpToViolationManager(String[] lineArray) {
        // No Violations
        if (lineArray.length == 6) {
            return null;
        }

        StringBuilder vioLump = new StringBuilder(lineArray[6]);
        for (int i = 7; i < lineArray.length; i++) {
            vioLump.append(",").append(lineArray[i]);
        }
        return new ViolationManager(vioLump.toString());
    }

    public void add(Inspection inspection){
        inspectionList.add(inspection);
    }

    public Inspection get(int i){
        return inspectionList.get(i);
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspectionList.iterator();
    }
}
