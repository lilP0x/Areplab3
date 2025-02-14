package org.example;

import org.example.controller.LabController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LabControllerTest {

    private LabController controller;

    @BeforeEach
    void setUp() {
        controller = new LabController();
    }

    @Test
    void testGr() {
        String response = controller.gr("Carlos");
        assertEquals("HolaCarlos", response, "El saludo debe incluir 'Hola' seguido del nombre.");
    }

    @Test
    void testGrNull() {
        String response = controller.gr(null);
        assertEquals("Holaworld", response, "Si el nombre es nulo, debe usar 'world'.");
    }

    @Test
    void testAdd() {
        String response = controller.add(5, 3);
        assertEquals("8", response, "5 + 3 debe ser 8.");
    }

    @Test
    void testAddNegative() {
        String response = controller.add(-2, 3);
        assertEquals("1", response, "-2 + 3 debe ser 1.");
    }

    @Test
    void testPrime() {
        String response = controller.prime(10, 4);
        assertEquals("6", response, "10 - 4 debe ser 6.");
    }

    @Test
    void testPrimeZero() {
        String response = controller.prime(10, 0);
        assertEquals("10", response, "10 - 0 debe ser 10.");
    }
}
