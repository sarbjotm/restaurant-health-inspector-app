/**
 * A manager to store inspections.
 */
package com.example.resturanthealthinspector.Model;

import java.util.ArrayList;

public class InspectionsManager {
    private ArrayList<Inspection> inspectionList = new ArrayList<>();
    private static InspectionsManager instance;

    private InspectionsManager(){

    }

    public static InspectionsManager getInstance(){
        if (instance == null){
            instance = new InspectionsManager();
        }
        return instance;
    }

    public void addInspection(Inspection inspection){
        inspectionList.add(inspection);
    }

    public Inspection getInspection(int i){
        return inspectionList.get(i);
    }
}
