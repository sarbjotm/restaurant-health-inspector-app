package com.example.restauranthealthinspector;

import com.example.restauranthealthinspector.TextUI.TextUI;

import org.junit.Test;

import java.io.FileNotFoundException;

public class InspectionTest {
    @Test
    public void testInspections() throws FileNotFoundException {
        TextUI textUI = new TextUI();
        textUI.printInspections();
    }
}
