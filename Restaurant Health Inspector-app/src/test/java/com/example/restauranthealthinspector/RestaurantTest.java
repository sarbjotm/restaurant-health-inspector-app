package com.example.restauranthealthinspector;

import org.junit.Test;

import java.io.FileNotFoundException;

import com.example.restauranthealthinspector.TextUI.TextUI;

public class RestaurantTest {
    @Test
    public void testRestaurant() throws FileNotFoundException {
        TextUI textUI = new TextUI();
        textUI.printRestaurants();
    }
}
