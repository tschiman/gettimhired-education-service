package com.gettimhired.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainControllerTest {

    private MainController mainController;

    @BeforeEach
    public void init() {
        mainController = new MainController();
    }

    @Test
    public void testThatRootRouteReturnsTheIndexPage() {
        assertEquals("index", mainController.index());
    }
}