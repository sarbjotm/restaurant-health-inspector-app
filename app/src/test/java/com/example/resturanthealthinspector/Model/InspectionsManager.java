package com.example.resturanthealthinspector.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * A manager to store inspections.
 */
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
        String line;
        int inspectionDate;
        int numCritical;
        int numNonCritical;
        scan.nextLine();

        while (scan.hasNextLine()) {
            line = scan.nextLine();
            line = line.replaceAll("\"", "");
            String[] lineArray = line.split(",");
            inspectionDate = Integer.parseInt(lineArray[1]);
            numCritical = Integer.parseInt(lineArray[3]);
            numNonCritical = Integer.parseInt(lineArray[4]);

            if (lineArray.length == 6){
                inspection = new Inspection(lineArray[0], inspectionDate, lineArray[2], numCritical, numNonCritical, lineArray[5], null);
            }
            else{
                // Combine vioLump to 1 String
                String vioLump = lineArray[6];
                for (int i = 7; i < lineArray.length; i++) {
                    vioLump += "," + lineArray[i];
                }
                ViolationManager violationManager = new ViolationManager(vioLump);
                inspection = new Inspection(lineArray[0], inspectionDate, lineArray[2], numCritical, numNonCritical, lineArray[5], violationManager);
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
