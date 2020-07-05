package com.example.restauranthealthinspector.Model;

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
        Date date;
        scan.nextLine();

        while (scan.hasNextLine()) {
            line = scan.nextLine();
            line = line.replaceAll("\"", "");
            String[] lineArray = line.split(",");
            numCritical = Integer.parseInt(lineArray[3]);
            numNonCritical = Integer.parseInt(lineArray[4]);
            date = intToDate(lineArray[1]);

            if (lineArray.length == 6){
                inspection = new Inspection(lineArray[0], date, lineArray[2], numCritical, numNonCritical, lineArray[5], "");
            }
            else{
                inspection = new Inspection(lineArray[0], date, lineArray[2], numCritical, numNonCritical, lineArray[5], lineArray[6]);
            }
            instance.add(inspection);
        }
        scan.close();
    }

    private static Date intToDate(String inspectionDate) {
        String day = inspectionDate.substring(6, 8);
        String month = inspectionDate.substring(4, 6);
        String year = inspectionDate.substring(0, 4);
        Date date = new Date(day, month, year);
        return date;
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
