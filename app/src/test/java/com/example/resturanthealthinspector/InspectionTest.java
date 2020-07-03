package com.example.resturanthealthinspector;

import com.example.resturanthealthinspector.TextUI.TextUI;

import org.junit.Test;

import java.io.FileNotFoundException;

public class InspectionTest {
    @Test
    public void testInspections() throws FileNotFoundException {
        TextUI textUI = new TextUI();
        textUI.printInspections();
    }
}
