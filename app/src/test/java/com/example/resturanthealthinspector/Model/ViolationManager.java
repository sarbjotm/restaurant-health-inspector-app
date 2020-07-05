package com.example.resturanthealthinspector.Model;

import java.util.ArrayList;

public class ViolationManager {
    private ArrayList<Violation> violationList = new ArrayList<>();
    private String[] descriptionList = new String[600];

    public ViolationManager(String vioLump) {
        convertVioLumpToViolations(vioLump);
    }

    private void convertVioLumpToViolations(String vioLump) {

    }

}
