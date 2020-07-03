/**
 * A manager to store inspections.
 */
package com.example.resturanthealthinspector.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class InspectionsManager implements Iterable<Inspection> {
    private ArrayList<Inspection> inspectionList = new ArrayList<>();
    private static InspectionsManager instance;

    private InspectionsManager(){

    }

    public static InspectionsManager getInstance() throws FileNotFoundException {
        if (instance == null){
            instance = new InspectionsManager();
            storeInspections();
        }
        return instance;
    }

    private static void storeInspections() throws FileNotFoundException {
        File file = new File("./src/main/res/raw/inspectionreports_itr1.csv");
        Scanner scan = new Scanner(file);
        Inspection inspection;
        String line = scan.nextLine();
        int inspectionDate;
        int numCritical;
        int numNonCritical;

        while (scan.hasNextLine()) {
            line = scan.nextLine();
            String[] lineArray = line.split(",");
            inspectionDate = Integer.parseInt(lineArray[1]);
            numCritical = Integer.parseInt(lineArray[3]);
            numNonCritical = Integer.parseInt(lineArray[4]);

            if (lineArray.length == 6){
                inspection = new Inspection(lineArray[0], inspectionDate, lineArray[2], numCritical, numNonCritical, lineArray[5], "");
            }
            else{
                inspection = new Inspection(lineArray[0], inspectionDate, lineArray[2], numCritical, numNonCritical, lineArray[5], lineArray[6]);
            }
            instance.add(inspection);
        }
        scan.close();
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
